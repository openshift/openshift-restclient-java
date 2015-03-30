/*******************************************************************************
 * Copyright (c) 2015 Red Hat, Inc. Distributed under license by Red Hat, Inc.
 * All rights reserved. This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Red Hat, Inc.
 ******************************************************************************/
package com.openshift3.internal.client.capability.server;

import java.net.MalformedURLException;
import java.util.Collection;

import org.jboss.dmr.ModelNode;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.openshift.client.utils.Samples;
import com.openshift3.client.IClient;
import com.openshift3.client.ResourceKind;
import com.openshift3.client.capability.CapabilityVisitor;
import com.openshift3.client.capability.server.ITemplateProcessing;
import com.openshift3.client.model.IConfig;
import com.openshift3.client.model.IResource;
import com.openshift3.client.model.template.ITemplate;
import com.openshift3.internal.client.IntegrationTestHelper;
import com.openshift3.internal.client.model.properties.ResourcePropertiesRegistry;
import com.openshift3.internal.client.model.template.Template;

public class ServerTemplateProcessingIntegrationTest {
	private static final Logger LOG = LoggerFactory.getLogger(ServerTemplateProcessingIntegrationTest.class);
	
	private IClient client;
	private IntegrationTestHelper helper = new IntegrationTestHelper();

	@Before
	public void setup () throws MalformedURLException{
		client = helper.createClient();
	}
	
	@Test
	public void testProcessAndApplyTemplate() throws Exception{

		ModelNode node = ModelNode.fromJSONString(Samples.V1BETA1_TEMPLATE.getContentAsString());
		final ITemplate template = new Template(node, client, ResourcePropertiesRegistry.getInstance().get("v1beta1", ResourceKind.Template));
		client.accept(new CapabilityVisitor<ITemplateProcessing>() {

			@Override
			public void visit(ITemplateProcessing capability) {
				LOG.debug("Processing template: " + template.toString());
				IConfig config = capability.process(template, helper.getDefaultNamespace());
				LOG.debug("Applying config: ", config.toString());
				
				Collection<IResource> results = client.create(config, helper.getDefaultNamespace());
				LOG.debug("applied template");
				for (IResource resource : results) {
					LOG.debug(resource.toString());
				}
			}
		});
	}

}
