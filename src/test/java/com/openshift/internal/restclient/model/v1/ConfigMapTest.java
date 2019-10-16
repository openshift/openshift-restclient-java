/*******************************************************************************
 * Copyright (c) 2015-2019 Red Hat, Inc. Distributed under license by Red Hat, Inc.
 * All rights reserved. This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Red Hat, Inc.
 ******************************************************************************/

package com.openshift.internal.restclient.model.v1;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Collections;

import org.jboss.dmr.ModelNode;
import org.junit.Before;
import org.junit.Test;

import com.openshift.internal.restclient.ResourceFactory;
import com.openshift.internal.restclient.model.ConfigMap;
import com.openshift.internal.restclient.model.properties.ResourcePropertiesRegistry;
import com.openshift.restclient.IApiTypeMapper;
import com.openshift.restclient.IApiTypeMapper.IVersionedType;
import com.openshift.restclient.IClient;
import com.openshift.restclient.ResourceKind;
import com.openshift.restclient.model.IConfigMap;
import com.openshift.restclient.utils.Samples;

/**
 * @author Ulf Lilleengen
 */
public class ConfigMapTest {

    private static final String VERSION = "v1";
    private IConfigMap configMap;
    private IClient client;

    @Before
    public void setUp() {
        client = mock(IClient.class);
        IApiTypeMapper mapper = mock(IApiTypeMapper.class);
        when(client.adapt(IApiTypeMapper.class)).thenReturn(mapper);
        when(mapper.getType(anyString(), eq(ResourceKind.CONFIG_MAP))).thenReturn(new IVersionedType() {
            
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
                return ResourceKind.CONFIG_MAP;
            }
            
            @Override
            public String getApiGroupName() {
                return null;
            }
        });
        ModelNode node = ModelNode.fromJSONString(Samples.V1_CONFIG_MAP.getContentAsString());
        configMap = new ConfigMap(node, client,
                ResourcePropertiesRegistry.getInstance().get(VERSION, ResourceKind.CONFIG_MAP));
    }

    @Test
    public void testIsRegisteredWithFactory() {
        configMap = new ResourceFactory(client).create(Samples.V1_CONFIG_MAP.getContentAsString());
    }

    @Test
    public void testGetData() {
        assertEquals(Collections.singletonMap("key1", "config1"), configMap.getData());
    }
}
