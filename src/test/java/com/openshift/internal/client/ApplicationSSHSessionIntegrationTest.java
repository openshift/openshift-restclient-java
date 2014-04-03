/******************************************************************************* 
 * Copyright (c) 2013 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 *
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/
package com.openshift.internal.client;

import org.junit.Before;

import com.openshift.client.IDomain;
import com.openshift.client.IOpenShiftConnection;
import com.openshift.client.IUser;
import com.openshift.client.utils.DomainTestUtils;
import com.openshift.client.utils.TestConnectionFactory;

/**
 * @author Corey Daley
 */
public class ApplicationSSHSessionIntegrationTest extends TestTimer {

	private IUser user;
	private IDomain domain;

	@Before
	public void setUp() throws Exception {
		IOpenShiftConnection connection = new TestConnectionFactory().getConnection();
		this.user = connection.getUser();
		this.domain = DomainTestUtils.ensureHasDomain(user);
	}


}
