/******************************************************************************* 
 * Copyright (c) 2012 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/
package com.openshift.internal.client.test;

import java.io.IOException;

import javax.xml.datatype.DatatypeConfigurationException;

import org.junit.Before;
import org.junit.Test;

import com.openshift.client.IUser;
import com.openshift.client.InvalidCredentialsOpenShiftException;
import com.openshift.client.OpenShiftException;
import com.openshift.client.utils.OpenShiftTestConfiguration;
import com.openshift.client.utils.TestConnectionFactory;

/**
 * @author André Dietisheim
 */
public class UserResourceIntegrationTest {

	private IUser user;

	@Before
	public void setUp() throws OpenShiftException, DatatypeConfigurationException, IOException {
		this.user = new TestConnectionFactory().getConnection().getUser();
	}

	@Test(expected = InvalidCredentialsOpenShiftException.class)
	public void shouldThrowIfInvalidCredentials() throws Exception {
		new TestConnectionFactory().getConnection(
				new OpenShiftTestConfiguration().getClientId(), "bogus-password").getUser();	
	}
}
