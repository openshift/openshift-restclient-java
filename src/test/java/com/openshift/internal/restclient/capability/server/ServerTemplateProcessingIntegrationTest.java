/*******************************************************************************
 * Copyright (c) 2015-2019 Red Hat, Inc. Distributed under license by Red Hat, Inc.
 * All rights reserved. This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Red Hat, Inc.
 ******************************************************************************/

package com.openshift.internal.restclient.capability.server;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;

import org.jboss.dmr.ModelNode;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.openshift.internal.restclient.IntegrationTestHelper;
import com.openshift.internal.restclient.model.KubernetesResource;
import com.openshift.internal.restclient.model.template.Template;
import com.openshift.restclient.IClient;
import com.openshift.restclient.OpenShiftException;
import com.openshift.restclient.capability.CapabilityVisitor;
import com.openshift.restclient.capability.server.ITemplateProcessing;
import com.openshift.restclient.model.IProject;
import com.openshift.restclient.model.IResource;
import com.openshift.restclient.model.template.ITemplate;
import com.openshift.restclient.utils.Samples;

public class ServerTemplateProcessingIntegrationTest {

    private static final Logger LOG = LoggerFactory.getLogger(ServerTemplateProcessingIntegrationTest.class);

    private IntegrationTestHelper helper = new IntegrationTestHelper();
    private IClient client;
    private IProject project;
    private Collection<IResource> resources = new ArrayList<IResource>();

    @Before
    public void before() throws MalformedURLException {
        this.client = helper.createClientForBasicAuth();
        this.project = helper.getOrCreateIntegrationTestProject(client);
    }

    @After
    public void after() {
        helper.cleanUpResources(client, resources);
    }

    @Test
    public void testProcessAndApplyTemplate() throws Exception {
        ModelNode node = ModelNode.fromJSONString(Samples.V1_TEMPLATE.getContentAsString());
        final Template template = new Template(node, client, null);
        node = ModelNode.fromJSONString(Samples.GROUP_TEMPLATE.getContentAsString());
        final Template groupTemplate = new Template(node, client, null);
        template.setNamespace(null);
        client.accept(new CapabilityVisitor<ITemplateProcessing, Object>() {

            @Override
            public Object visit(ITemplateProcessing capability) {

                try {
                    return processTemplate(template, capability);
                } catch (OpenShiftException e) {
                    return processTemplate(groupTemplate, capability);
                }
            }
        }, new Object());
    }

    private Object processTemplate(final Template template, ITemplateProcessing capability) {
        LOG.debug("Processing template: {}", template.toJson());
        assertFalse("Exp. the template to have items for this test be interesting",
                template.getObjects().isEmpty());
        final int items = template.getObjects().size();
        ITemplate processedTemplate = capability.process(template, project.getName());

        LOG.debug("Applying template: {}", processedTemplate.toJson());
        LOG.debug("Applied template");
        assertEquals("Exp. the pre and post item count to be the same", 
                items,
                template.getObjects().size());
        Collection<IResource> stubs = processedTemplate.getObjects();
        ServerTemplateProcessingIntegrationTest.this.resources = 
                stubs.stream()
                    .map(stub -> {
                        // resources as in template dont have a namespace
                        ((KubernetesResource) stub).setNamespace(project.getNamespaceName());
                        LOG.debug("creating: {}", stub);
                        IResource resource = client.create(stub);
                        LOG.debug("created: {}", resource.toJson());
                        return resource;
                    })
                    .collect(Collectors.toList());
        return null;
    }
}
