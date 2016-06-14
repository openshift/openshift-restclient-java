/*******************************************************************************
 * Copyright (c) 2015 Red Hat, Inc. Distributed under license by Red Hat, Inc.
 * All rights reserved. This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Red Hat, Inc.
 ******************************************************************************/
package com.openshift.internal.restclient.capability.server;

import static org.junit.Assert.*;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Collection;

import org.jboss.dmr.ModelNode;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.openshift.internal.restclient.IntegrationTestHelper;
import com.openshift.internal.restclient.model.template.Template;
import com.openshift.restclient.IClient;
import com.openshift.restclient.ResourceKind;
import com.openshift.restclient.capability.CapabilityVisitor;
import com.openshift.restclient.capability.server.ITemplateProcessing;
import com.openshift.restclient.model.IProject;
import com.openshift.restclient.model.IResource;
import com.openshift.restclient.model.template.ITemplate;
import com.openshift.restclient.utils.Samples;

/**
 * @author Jeff Cantrill
 */
public class ServerTemplateProcessingIntegrationTest {

	private static final Logger LOG = LoggerFactory.getLogger(ServerTemplateProcessingIntegrationTest.class);
	
	private IClient client;
	private IntegrationTestHelper helper = new IntegrationTestHelper();

	private IProject project;

	@Before
	public void setup () throws MalformedURLException{
		client = helper.createClientForBasicAuth();
		String namespace = helper.generateNamespace();
		client.create(client.getResourceFactory().stub(ResourceKind.PROJECT_REQUEST, namespace));
		project = client.get(ResourceKind.PROJECT, namespace, "");
	}
	
	@Test
	public void testProcessAndApplyTemplate() throws Exception{
		final Collection<IResource> results = new ArrayList<IResource>();
		ModelNode node = ModelNode.fromJSONString(Samples.V1_TEMPLATE.getContentAsString());
		final Template template = new Template(node, client, null);
		template.setNamespace(null);
		try {
			client.accept(new CapabilityVisitor<ITemplateProcessing, Object>() {
				
				@Override
				public Object visit(ITemplateProcessing capability) {
					
					LOG.debug("Processing template: {}", template.toJson());
					assertFalse("Exp. the template to have items for this test be interesting", template.getObjects().isEmpty());
					final int items = template.getObjects().size();
					ITemplate processedTemplate = capability.process(template, project.getName());
					
					LOG.debug("Applying template: {}", processedTemplate.toJson());
					LOG.debug("Applied template");
					assertEquals("Exp. the pre and post item count to be the same", items, template.getObjects().size());
					for (IResource resource : processedTemplate.getObjects()) {
						LOG.debug("creating: {}", resource);
						results.add(client.create(resource, project.getName()));
						LOG.debug("created: {}", resource.toJson());
					}
					return null;
				}
			}, new Object());
		} finally {
			IntegrationTestHelper.cleanUpResource(client, project);
		}
	}
}
