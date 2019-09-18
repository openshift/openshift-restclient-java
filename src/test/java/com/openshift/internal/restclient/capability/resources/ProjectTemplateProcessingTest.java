/*******************************************************************************
 * Copyright (c) 2015-2019 Red Hat, Inc. Distributed under license by Red Hat, Inc.
 * All rights reserved. This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: Red Hat, Inc. - initial API and implementation
 ******************************************************************************/

package com.openshift.internal.restclient.capability.resources;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collection;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.openshift.internal.restclient.ResourceFactory;
import com.openshift.restclient.IApiTypeMapper;
import com.openshift.restclient.IApiTypeMapper.IVersionedApiResource;
import com.openshift.restclient.IApiTypeMapper.IVersionedType;
import com.openshift.restclient.IClient;
import com.openshift.restclient.ResourceKind;
import com.openshift.restclient.capability.resources.IProjectTemplateProcessing;
import com.openshift.restclient.capability.server.ITemplateProcessing;
import com.openshift.restclient.model.IList;
import com.openshift.restclient.model.IProject;
import com.openshift.restclient.model.IResource;
import com.openshift.restclient.model.template.ITemplate;
import com.openshift.restclient.utils.Samples;

@RunWith(MockitoJUnitRunner.class)
public class ProjectTemplateProcessingTest {

    private static final String NAMESPACE = "aProjectNamespace";
    private IProjectTemplateProcessing capability;
    @Mock
    private ITemplate template;
    @Mock
    private IClient client;
    @Mock
    private ITemplateProcessing serverCapability;
    @Mock
    private IProject project;

    @Before
    public void setUp() throws Exception {
        when(project.getNamespaceName()).thenReturn(NAMESPACE);
        when(client.supports(eq(ITemplateProcessing.class))).thenReturn(true);
        when(client.getCapability(eq(ITemplateProcessing.class))).thenReturn(serverCapability);

        capability = new ProjectTemplateProcessing(project, client);
    }

    @Test
    public void isSupportedShouldBeFalseForNullClient() {
        capability = new ProjectTemplateProcessing(project, null);
        assertFalse(capability.isSupported());
    }

    @Test
    public void isSupportedShouldBeFalseIfTheClientDoesntSupportTemplates() {
        when(client.supports(eq(ITemplateProcessing.class))).thenReturn(false);
        capability = new ProjectTemplateProcessing(project, client);
        assertFalse(capability.isSupported());
    }

    @Test
    public void isSupportedShouldBeTrueIfTheClientSupportTemplates() {
        assertTrue(capability.isSupported());
    }

    @Test
    public void processTemplateShouldUseTheClientsCapability() {
        when(serverCapability.process(any(ITemplate.class), anyString())).thenReturn(template);

        assertEquals(template, capability.process(template));
        verify(serverCapability).process(eq(template), eq(NAMESPACE));
    }

    @Test
    public void applyTemplateShouldUseTheClientToCreateTheResources() {
        @SuppressWarnings("unchecked")
        Collection<IResource> resources = mock(Collection.class);
        when(client.create(any(IList.class), anyString())).thenReturn(resources);
        when(client.getResourceFactory()).thenReturn(new ResourceFactory(client) {
        });
        IApiTypeMapper mapper = mock(IApiTypeMapper.class);
        when(client.adapt(IApiTypeMapper.class)).thenReturn(mapper);
        when(mapper.getType(anyString(), eq(ResourceKind.TEMPLATE))).thenReturn(new IVersionedType() {
            
            @Override
            public String getVersion() {
                return "v1";
            }
            
            @Override
            public String getPrefix() {
                return null;
            }
            
            @Override
            public String getKind() {
                return ResourceKind.TEMPLATE;
            }
            
            @Override
            public String getApiGroupName() {
                return null;
            }
        });
        when(mapper.getType(anyString(), eq(ResourceKind.DEPLOYMENT_CONFIG))).thenReturn(new IVersionedType() {
            
            @Override
            public String getVersion() {
                return "v1";
            }
            
            @Override
            public String getPrefix() {
                return null;
            }
            
            @Override
            public String getKind() {
                return ResourceKind.DEPLOYMENT_CONFIG;
            }
            
            @Override
            public String getApiGroupName() {
                return null;
            }
        });
        when(mapper.getEndpointFor(anyString(), eq(ResourceKind.DEPLOYMENT_CONFIG))).thenReturn(new IVersionedApiResource() {
            
            @Override
            public boolean isSupported(String capability) {
                return true;
            }
            
            @Override
            public boolean isNamespaced() {
                return true;
            }
            
            @Override
            public String getVersion() {
                return "v1";
            }
            
            @Override
            public String getPrefix() {
                return null;
            }
            
            @Override
            public String getName() {                // TODO Auto-generated method stub
                return null;
            }
            
            @Override
            public String getKind() {
                return ResourceKind.DEPLOYMENT_CONFIG;
            }
            
            @Override
            public String getApiGroupName() {
                return "v1";
            }
        });

        
        ITemplate template = new ResourceFactory(client) {
        }.create(Samples.V1_TEMPLATE.getContentAsString());

        assertEquals(resources, capability.apply(template));
        verify(client).create(any(IList.class), eq(NAMESPACE));
    }

}
