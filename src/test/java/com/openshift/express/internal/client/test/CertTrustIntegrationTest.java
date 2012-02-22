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


import static org.junit.Assert.fail;

import java.io.IOException;

import org.junit.Before;

import com.openshift.express.client.IOpenShiftService;
import com.openshift.express.client.IUser;
import com.openshift.express.client.OpenShiftException;
import com.openshift.express.client.OpenShiftService;
import com.openshift.express.client.configuration.DefaultConfiguration;
import com.openshift.express.client.configuration.SystemConfiguration;
import com.openshift.express.client.configuration.UserConfiguration;
import com.openshift.express.internal.client.test.fakes.TestUser;

/**
 * @author William DeCoste
 */
public class CertTrustIntegrationTest {
	
	private IOpenShiftService service;
	private IUser user;
	
	@Before
	public void setUp() throws OpenShiftException, IOException {
		UserConfiguration userConfiguration = new UserConfiguration(new SystemConfiguration(new DefaultConfiguration()));
		service = new OpenShiftService(TestUser.ID, userConfiguration.getLibraServer());
		service.setEnableSSLCertChecks(Boolean.parseBoolean(System.getProperty("enableSSLCertChecks")));
		
		user = new TestUser(System.getProperty("RHLOGIN"), System.getProperty("PASSWORD"), service);
	}

	//@Test
	public void testValidationSwitch() throws Exception {
		
		try {
			service.setEnableSSLCertChecks(true);
			service.getUserInfo(user);
			fail("Expected cert validation exception");
		} catch (Exception e) {
		} 
		
		service.setEnableSSLCertChecks(false);
		service.getUserInfo(user);
		
		try {
			service.setEnableSSLCertChecks(true);
			service.getUserInfo(user);
			fail("Expected cert validation exception");
		} catch (Exception e) {
		} 
		
		service.setEnableSSLCertChecks(false);
		service.getUserInfo(user);
	}
}
