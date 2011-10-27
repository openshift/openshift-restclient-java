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
package org.jboss.tools.openshift.express.internal.client.test;

import static org.jboss.tools.openshift.express.internal.client.test.utils.CartridgeAsserts.assertThatContainsCartridge;
import static org.junit.Assert.assertEquals;

import java.net.URLEncoder;
import java.util.List;

import org.jboss.tools.openshift.express.client.ICartridge;
import org.jboss.tools.openshift.express.client.OpenShiftException;
import org.jboss.tools.openshift.express.internal.client.request.ListCartridgesRequest;
import org.jboss.tools.openshift.express.internal.client.request.OpenShiftEnvelopeFactory;
import org.jboss.tools.openshift.express.internal.client.request.marshalling.ListCartridgesRequestJsonMarshaller;
import org.jboss.tools.openshift.express.internal.client.response.OpenShiftResponse;
import org.jboss.tools.openshift.express.internal.client.response.unmarshalling.JsonSanitizer;
import org.jboss.tools.openshift.express.internal.client.response.unmarshalling.ListCartridgesResponseUnmarshaller;
import org.junit.Test;

/**
 * @author André Dietisheim
 */
public class ListCartridgesTest {

	private static final String USERNAME = "toolsjboss@gmail.com";
	private static final String PASSWORD = "1q2w3e";

	@Test
	public void canMarshallListCartridgesRequest() throws Exception {
		String expectedRequestString = "password=" + PASSWORD + "&json_data=%7B%22rhlogin%22+%3A+%22"
				+ URLEncoder.encode(USERNAME, "UTF-8")
				+ "%22%2C+%22debug%22+%3A+%22true%22%2C+%22cart_type%22+%3A+%22standalone%22%7D";

		String listCartridgeRequest = new ListCartridgesRequestJsonMarshaller().marshall(
				new ListCartridgesRequest(USERNAME, true));
		String effectiveRequest = new OpenShiftEnvelopeFactory(PASSWORD, listCartridgeRequest).createString();

		assertEquals(expectedRequestString, effectiveRequest);
	}

	@Test
	public void canUnmarshallCartridgeListResponse() throws OpenShiftException {
		String cartridgeListResponse =
				"{"
						+ "\"messages\":\"\","
						+ "\"debug\":\"\","
						+ "\"data\":"
						+ "\"{\\\"carts\\\":[\\\"perl-5.10\\\",\\\"jbossas-7.0\\\",\\\"wsgi-3.2\\\",\\\"rack-1.1\\\",\\\"php-5.3\\\"]}\","
						+ "\"api\":\"1.1.1\","
						+ "\"api_c\":[\"placeholder\"],"
						+ "\"result\":null,"
						+ "\"broker\":\"1.1.1\","
						+ "\"broker_c\":["
						+ "\"namespace\","
						+ "\"rhlogin\","
						+ "\"ssh\","
						+ "\"app_uuid\","
						+ "\"debug\","
						+ "\"alter\","
						+ "\"cartridge\","
						+ "\"cart_type\","
						+ "\"action\","
						+ "\"app_name\","
						+ "\"api"
						+ "\"],"
						+ "\"exit_code\":0}";

		cartridgeListResponse = JsonSanitizer.sanitize(cartridgeListResponse);
		OpenShiftResponse<List<ICartridge>> response =
				new ListCartridgesResponseUnmarshaller().unmarshall(cartridgeListResponse);
		assertEquals("", response.getMessages());
		assertEquals(false, response.isDebug());

		List<ICartridge> cartridges = response.getOpenShiftObject();
		assertEquals(5, cartridges.size());
		assertThatContainsCartridge("perl-5.10", cartridges);
		assertThatContainsCartridge("jbossas-7.0", cartridges);
		assertThatContainsCartridge("wsgi-3.2", cartridges);
		assertThatContainsCartridge("rack-1.1", cartridges);
		assertThatContainsCartridge("php-5.3", cartridges);
		assertEquals(null, response.getResult());
		assertEquals(0, response.getExitCode());
	}

}
