/*******************************************************************************
 * Copyright (c) 2015 Red Hat, Inc. Distributed under license by Red Hat, Inc.
 * All rights reserved. This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Red Hat, Inc.
 ******************************************************************************/
package com.openshift3.internal.client;

import static org.junit.Assert.*;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import org.jboss.dmr.ModelNode;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.openshift.client.NoopSSLCertificateCallback;
import com.openshift.client.utils.Samples;
import com.openshift3.client.ResourceKind;
import com.openshift3.client.authorization.AuthorizationClientFactory;
import com.openshift3.client.authorization.OAuthStrategy;
import com.openshift3.client.capability.CapabilityVisitor;
import com.openshift3.client.capability.server.ITemplateProcessing;
import com.openshift3.client.model.IConfig;
import com.openshift3.client.model.template.ITemplate;
import com.openshift3.internal.client.DefaultClient;
import com.openshift3.internal.client.model.properties.ResourcePropertiesRegistry;
import com.openshift3.internal.client.model.template.Template;

public class ProcessAndApplyTemplateIntegrationTest {
	private static final Logger LOG = LoggerFactory.getLogger(ProcessAndApplyTemplateIntegrationTest.class);
	
	private static final String URL = "https://localhost:8443";
	private static final String NAMESPACE = "test";
	private DefaultClient client;

	@Before
	public void setup () throws MalformedURLException{
		client = new DefaultClient(new URL(URL), new NoopSSLCertificateCallback());
		client.setAuthorizationStrategy(new OAuthStrategy(URL, new AuthorizationClientFactory().create(), "jcantril", "abcd"));
	}
	
	@Test
	public void testProcessAndApplyTemplate() throws Exception{

		ModelNode node = ModelNode.fromJSONString(Samples.V1BETA1_TEMPLATE.getContentAsString());
		final ITemplate template = new Template(node, client, ResourcePropertiesRegistry.getInstance().get("v1beta1", ResourceKind.Template));
		client.accept(new CapabilityVisitor<ITemplateProcessing>() {

			@Override
			public void visit(ITemplateProcessing capability) {
				IConfig config = capability.process(template, NAMESPACE);
				LOG.debug(config.toString());
			}
		});
	}

}
