/*******************************************************************************
 * Copyright (c) 2015 Red Hat, Inc. Distributed under license by Red Hat, Inc.
 * All rights reserved. This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Red Hat, Inc.
 ******************************************************************************/
package com.openshift3.internal.client.model.build;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import com.openshift3.client.model.build.BuildTriggerType;
import com.openshift3.client.model.build.IWebhookTrigger;

public class WebhookTriggerTest {

	private IWebhookTrigger trigger;
	
	@Before
	public void setup(){
	}
	@Test
	public void testGetWebhookUrlWhenResourceHasBaseURL() {
		trigger = new WebhookTrigger(BuildTriggerType.generic, "secret101","foo","https://localhost:8443","v1beta1","test");
		assertEquals("https://localhost:8443/osapi/v1beta1/buildConfigHooks/foo/secret101/generic?namespace=test", trigger.getWebhookURL());
	}
	
	@Test
	public void testGetWebhookUrlWhenResourceDoesNotHaveBaseURL(){
		trigger = new WebhookTrigger(BuildTriggerType.generic, "secret101","foo"," ","v1beta1","test");
		assertEquals("",trigger.getWebhookURL());
	}
}
