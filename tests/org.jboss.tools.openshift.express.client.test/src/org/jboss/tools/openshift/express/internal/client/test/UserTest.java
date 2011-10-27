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

import static org.jboss.tools.openshift.express.internal.client.test.utils.ApplicationAsserts.assertApplication;
import static org.jboss.tools.openshift.express.internal.client.test.utils.CartridgeAsserts.assertThatContainsCartridge;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import javax.xml.datatype.DatatypeConfigurationException;

import org.jboss.tools.openshift.express.client.Cartridge;
import org.jboss.tools.openshift.express.client.IApplication;
import org.jboss.tools.openshift.express.client.ICartridge;
import org.jboss.tools.openshift.express.client.IDomain;
import org.jboss.tools.openshift.express.client.ISSHPublicKey;
import org.jboss.tools.openshift.express.client.OpenShiftException;
import org.jboss.tools.openshift.express.client.OpenShiftService;
import org.jboss.tools.openshift.express.client.utils.RFC822DateUtils;
import org.jboss.tools.openshift.express.internal.client.ApplicationInfo;
import org.jboss.tools.openshift.express.internal.client.InternalUser;
import org.jboss.tools.openshift.express.internal.client.UserInfo;
import org.jboss.tools.openshift.express.internal.client.test.fakes.CartridgeResponseFake;
import org.jboss.tools.openshift.express.internal.client.test.fakes.NoopOpenShiftServiceFake;
import org.jboss.tools.openshift.express.internal.client.test.fakes.UserInfoResponseFake;
import org.junit.Before;
import org.junit.Test;

/**
 * @author André Dietisheim
 */
public class UserTest {

	private OpenShiftService userInfoservice;
	private InternalUser user;

	@Before
	public void setUp() throws OpenShiftException, DatatypeConfigurationException {
		UserInfo userInfo = createUserInfo();
		this.userInfoservice = createUserInfoService(userInfo);
		this.user = new InternalUser(UserInfoResponseFake.RHLOGIN, UserInfoResponseFake.PASSWORD, userInfoservice);
	}

	@Test
	public void canGetUserUUID() throws OpenShiftException {
		assertEquals(UserInfoResponseFake.UUID, user.getUUID());
	}
	
	@Test
	public void canGetPublicKey() throws OpenShiftException {
		ISSHPublicKey key = user.getSshKey();
		assertNotNull(key);
		assertEquals(UserInfoResponseFake.SSH_KEY, key.getPublicKey());
	}

	@Test
	public void canGetDomain() throws OpenShiftException {
		IDomain domain = user.getDomain();
		assertNotNull(domain);
		assertEquals(UserInfoResponseFake.RHC_DOMAIN, domain.getRhcDomain());
		assertEquals(UserInfoResponseFake.NAMESPACE, domain.getNamespace());
	}

	@Test
	public void canGetCartridges() throws OpenShiftException {
		OpenShiftService cartridgeListService = new NoopOpenShiftServiceFake() {

			@Override
			public List<ICartridge> getCartridges(InternalUser user) throws OpenShiftException {
				ArrayList<ICartridge> cartridges = new ArrayList<ICartridge>();
				cartridges.add(new Cartridge(CartridgeResponseFake.CARTRIDGE_JBOSSAS70));
				cartridges.add(new Cartridge(CartridgeResponseFake.CARTRIDGE_PERL5));
				cartridges.add(new Cartridge(CartridgeResponseFake.CARTRIDGE_PHP53));
				cartridges.add(new Cartridge(CartridgeResponseFake.CARTRIDGE_RACK11));
				cartridges.add(new Cartridge(CartridgeResponseFake.CARTRIDGE_WSGI32));
				return cartridges;
			}
		};
		InternalUser user = new InternalUser(UserInfoResponseFake.RHLOGIN, UserInfoResponseFake.PASSWORD, cartridgeListService);
		Collection<ICartridge> cartridges = user.getCartridges();
		assertNotNull(cartridges);
		assertEquals(5, cartridges.size());
		assertThatContainsCartridge(CartridgeResponseFake.CARTRIDGE_JBOSSAS70, cartridges);
		assertThatContainsCartridge(CartridgeResponseFake.CARTRIDGE_PERL5, cartridges);
		assertThatContainsCartridge(CartridgeResponseFake.CARTRIDGE_PHP53, cartridges);
		assertThatContainsCartridge(CartridgeResponseFake.CARTRIDGE_RACK11, cartridges);
		assertThatContainsCartridge(CartridgeResponseFake.CARTRIDGE_WSGI32, cartridges);
	}

	@Test
	public void canGetApplications() throws OpenShiftException {
		/** response is UserInfoResponseFake */
		Collection<IApplication> applications = user.getApplications();
		assertNotNull(applications);
		assertEquals(2, applications.size());
	}

	@Test
	public void canGetApplicationByName() throws OpenShiftException, DatatypeConfigurationException {
		IApplication application = user.getApplicationByName(UserInfoResponseFake.APP2_NAME);
		assertApplication(
				UserInfoResponseFake.APP2_NAME
				, UserInfoResponseFake.APP2_UUID
				, UserInfoResponseFake.APP2_CARTRIDGE
				, UserInfoResponseFake.APP2_EMBEDDED
				, UserInfoResponseFake.APP2_CREATION_TIME
				, application);
	}

	private UserInfo createUserInfo() throws OpenShiftException, DatatypeConfigurationException {
		ApplicationInfo[] applicationInfos = new ApplicationInfo[] {
				new ApplicationInfo(UserInfoResponseFake.APP1_NAME
						, UserInfoResponseFake.APP1_UUID
						, UserInfoResponseFake.APP1_EMBEDDED
						, Cartridge.valueOf(UserInfoResponseFake.APP1_CARTRIDGE)
						, RFC822DateUtils.getDate(UserInfoResponseFake.APP1_CREATION_TIME))
				, new ApplicationInfo(UserInfoResponseFake.APP2_NAME
						, UserInfoResponseFake.APP2_UUID
						, UserInfoResponseFake.APP2_EMBEDDED
						, Cartridge.valueOf(UserInfoResponseFake.APP2_CARTRIDGE)
						, RFC822DateUtils.getDate(UserInfoResponseFake.APP2_CREATION_TIME))
		};
		return new UserInfo(
				UserInfoResponseFake.RHLOGIN
				, UserInfoResponseFake.UUID
				, UserInfoResponseFake.SSH_KEY
				, UserInfoResponseFake.RHC_DOMAIN
				, UserInfoResponseFake.NAMESPACE
				, Arrays.asList(applicationInfos));
	}

	private OpenShiftService createUserInfoService(final UserInfo userInfo) {
		return new NoopOpenShiftServiceFake() {

			@Override
			public UserInfo getUserInfo(InternalUser user) throws OpenShiftException {
				return userInfo;
			}
		};
	}
}
