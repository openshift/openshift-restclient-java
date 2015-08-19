/*******************************************************************************
 * Copyright (c) 2015 Red Hat, Inc. Distributed under license by Red Hat, Inc.
 * All rights reserved. This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Red Hat, Inc.
 ******************************************************************************/
package com.openshift.internal.restclient.model.build;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import com.openshift.internal.restclient.model.build.WebhookTrigger;
import com.openshift.restclient.model.build.BuildTriggerType;
import com.openshift.restclient.model.build.IWebhookTrigger;

/**
 * @author Jeff Cantrill
 */
public class WebhookTriggerTest {

	private IWebhookTrigger trigger;
	
	@Before
	public void setup(){
	}
	@Test
	public void testGetWebhookUrlWhenResourceHasBaseURL() {
		trigger = new WebhookTrigger(BuildTriggerType.GENERIC, "secret101","https://localhost:8443/oapi/v1/namespaces/test/buildconfigs/foo");
		assertEquals("https://localhost:8443/oapi/v1/namespaces/test/buildconfigs/foo/webhooks/secret101/generic", trigger.getWebhookURL());
	}
	
	@Test
	public void testGetWebhookUrlWhenResourceDoesNotHaveBaseURL(){
		trigger = new WebhookTrigger(BuildTriggerType.GENERIC, "secret101"," ");
		assertEquals("",trigger.getWebhookURL());
	}
}
