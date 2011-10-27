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
import static org.junit.Assert.assertNotNull;

import java.util.List;

import org.jboss.tools.openshift.express.client.ICartridge;
import org.jboss.tools.openshift.express.client.OpenShiftException;
import org.jboss.tools.openshift.express.internal.client.response.OpenShiftResponse;
import org.jboss.tools.openshift.express.internal.client.response.unmarshalling.JsonSanitizer;
import org.jboss.tools.openshift.express.internal.client.response.unmarshalling.ListCartridgesResponseUnmarshaller;
import org.jboss.tools.openshift.express.internal.client.test.fakes.CartridgeResponseFake;
import org.junit.Test;

/**
 * @author André Dietisheim
 */
public class CartridgeTest {

	@Test
	public void canUnmarshallApplicationResponse() throws OpenShiftException {
		String response = JsonSanitizer.sanitize(CartridgeResponseFake.RESPONSE);
		OpenShiftResponse<List<ICartridge>> openshiftResponse =
				new ListCartridgesResponseUnmarshaller().unmarshall(response);
		List<ICartridge> cartridges = openshiftResponse.getOpenShiftObject();
		assertNotNull(cartridges);
		assertThatContainsCartridge(CartridgeResponseFake.CARTRIDGE_JBOSSAS70, cartridges);
		assertThatContainsCartridge(CartridgeResponseFake.CARTRIDGE_PERL5, cartridges);
		assertThatContainsCartridge(CartridgeResponseFake.CARTRIDGE_PHP53, cartridges);
		assertThatContainsCartridge(CartridgeResponseFake.CARTRIDGE_RACK11, cartridges);
		assertThatContainsCartridge(CartridgeResponseFake.CARTRIDGE_WSGI32, cartridges);
	}
}
