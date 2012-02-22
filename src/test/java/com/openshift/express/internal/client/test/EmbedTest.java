/******************************************************************************* 
 * Copyright (c) 2007 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/
package com.openshift.express.internal.client.test;

import static org.junit.Assert.assertEquals;

import com.openshift.express.client.IEmbeddableCartridge;
import com.openshift.express.internal.client.request.EmbedAction;
import com.openshift.express.internal.client.request.EmbedRequest;
import com.openshift.express.internal.client.request.marshalling.EmbedRequestJsonMarshaller;
import com.openshift.express.internal.client.test.fakes.ApplicationResponseFake;
import org.junit.Test;

/**
 * @author André Dietisheim
 */
public class EmbedTest {

	@Test
	public void canMarshallAddEmbeddedCartridgeRequest() throws Exception {
		String applicationName = "test-application";
		String expectedRequestString =
				"{"
						+ "\"rhlogin\" : " + "\"" + ApplicationResponseFake.RHLOGIN + "\""
						+ ", \"debug\" : \"true\""
						+ ", \"cartridge\" : \"" + IEmbeddableCartridge.JENKINS_14.getName() + "\""
						+ ", \"action\" : \"" + EmbedAction.ADD.getCommand() + "\""
						+ ", \"app_name\" : \"" + applicationName + "\""
						+ "}";

		String addEmbeddedRequest = new EmbedRequestJsonMarshaller().marshall(
				new EmbedRequest(
						"test-application", IEmbeddableCartridge.JENKINS_14, EmbedAction.ADD,
						ApplicationResponseFake.RHLOGIN, true));

		assertEquals(expectedRequestString, addEmbeddedRequest);
	}
}
