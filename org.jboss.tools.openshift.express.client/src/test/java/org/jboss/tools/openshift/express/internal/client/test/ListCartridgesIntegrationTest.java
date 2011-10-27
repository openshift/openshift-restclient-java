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
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.jboss.tools.openshift.express.client.ICartridge;
import org.jboss.tools.openshift.express.client.OpenShiftService;
import org.jboss.tools.openshift.express.internal.client.test.fakes.TestUser;
import org.junit.Before;
import org.junit.Test;

/**
 * @author André Dietisheim
 */
public class ListCartridgesIntegrationTest {

	private OpenShiftService openShiftService;
	private TestUser user;

	@Before
	public void setUp() {
		this.openShiftService = new OpenShiftService(TestUser.ID);
		this.user = new TestUser();
	}

	@Test
	public void canListCartridges() throws Exception {
		List<ICartridge> cartridges = openShiftService.getCartridges(user);
		assertNotNull(cartridges);
		assertTrue(cartridges.size() > 0);
		assertThatContainsCartridge("jbossas-7.0", cartridges);
	}
}
