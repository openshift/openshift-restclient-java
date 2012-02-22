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
import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.openshift.express.client.ICartridge;
import com.openshift.express.client.IOpenShiftService;
import com.openshift.express.client.ISSHPublicKey;
import com.openshift.express.client.NotFoundOpenShiftException;
import com.openshift.express.client.OpenShiftException;
import com.openshift.express.client.OpenShiftService;
import com.openshift.express.client.configuration.DefaultConfiguration;
import com.openshift.express.client.configuration.SystemConfiguration;
import com.openshift.express.client.configuration.UserConfiguration;
import com.openshift.express.internal.client.ApplicationInfo;
import com.openshift.express.internal.client.InternalUser;
import com.openshift.express.internal.client.UserInfo;
import com.openshift.express.internal.client.test.fakes.TestUser;
import com.openshift.express.internal.client.test.utils.ApplicationInfoAsserts;

/**
 * @author André Dietisheim
 */
public class UserInfoIntegrationTest {

	private OpenShiftService openShiftService;
	private TestUser user;

	@Before
	public void setUp() throws OpenShiftException, IOException {
		UserConfiguration userConfiguration = new UserConfiguration(new SystemConfiguration(new DefaultConfiguration()));
		this.openShiftService = new OpenShiftService(TestUser.ID, userConfiguration.getLibraServer());
		openShiftService.setEnableSSLCertChecks(Boolean.parseBoolean(System.getProperty("enableSSLCertChecks")));
		this.user = new TestUser(openShiftService);
	}

	@Test
	public void canGetUserInfo() throws Exception {
		UserInfo userInfo = openShiftService.getUserInfo(user);
		assertNotNull(userInfo);

		assertEquals(user.getRhlogin(), userInfo.getRhLogin());
	}

	//@Test(expected = InvalidCredentialsOpenShiftException.class)
	public void getUserInfoForInexistantUserThrowsException() throws Exception {
		TestUser inexistantUser = new TestUser("inexistantUsername", "bogusPassword", openShiftService);
		openShiftService.getUserInfo(inexistantUser);
	}

	/**
	 * {@link OpenShiftService#getUserInfo(InternalUser)} for a user without
	 * domain throws {@link NotFoundOpenShiftException}
	 */
	@Test(expected = NotFoundOpenShiftException.class)
	public void canGetUserInfoForUserWithoutDomain() throws Exception {
		TestUser inexistantUser = new TestUser(TestUser.RHLOGIN_USER_WITHOUT_DOMAIN, TestUser.PASSWORD_USER_WITHOUT_DOMAIN, openShiftService);
		openShiftService.getUserInfo(inexistantUser);
	}

	@Test
	public void userInfoContainsOneMoreApplicationAfterCreatingNewApplication() throws Exception {
		UserInfo userInfo = openShiftService.getUserInfo(user);
		assertNotNull(userInfo);

		List<ApplicationInfo> applicationInfos = userInfo.getApplicationInfos();
		assertNotNull(applicationInfos);
		int numberOfApplicationInfos = applicationInfos.size();

		String applicationName = createRandomName();
		try {
			openShiftService.createApplication(applicationName, ICartridge.JBOSSAS_7, user);

			UserInfo userInfo2 = openShiftService.getUserInfo(user);
			assertEquals(numberOfApplicationInfos + 1, userInfo2.getApplicationInfos().size());
			ApplicationInfoAsserts.assertThatContainsApplicationInfo(applicationName, userInfo2.getApplicationInfos());
		} finally {
			silentlyDestroyAS7Application(applicationName, openShiftService);
		}
	}

	@Test
	public void canUseReturnedSSHKeyToChangeDomain() throws Exception {
		UserInfo userInfo = openShiftService.getUserInfo(user);
		assertNotNull(userInfo);

		ISSHPublicKey sshKey = userInfo.getSshPublicKey();
		openShiftService.changeDomain(createRandomName(), sshKey, user);
	}

	private String createRandomName() {
		return String.valueOf(System.currentTimeMillis());
	}

	private void silentlyDestroyAS7Application(String name, IOpenShiftService service) {
		try {
			service.destroyApplication(name, ICartridge.JBOSSAS_7, user);
		} catch (OpenShiftException e) {
			e.printStackTrace();
		}
	}
}
