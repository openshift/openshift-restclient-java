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

import static com.openshift.express.internal.client.test.utils.ApplicationAsserts.assertApplication;
import static com.openshift.express.internal.client.test.utils.CartridgeAsserts.assertThatContainsCartridge;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import javax.xml.datatype.DatatypeConfigurationException;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import com.openshift.express.client.Cartridge;
import com.openshift.express.client.IApplication;
import com.openshift.express.client.ICartridge;
import com.openshift.express.client.IDomain;
import com.openshift.express.client.IOpenShiftService;
import com.openshift.express.client.ISSHPublicKey;
import com.openshift.express.client.IUser;
import com.openshift.express.client.OpenShiftException;
import com.openshift.express.client.utils.RFC822DateUtils;
import com.openshift.express.internal.client.ApplicationInfo;
import com.openshift.express.internal.client.EmbeddableCartridgeInfo;
import com.openshift.express.internal.client.InternalUser;
import com.openshift.express.internal.client.UserInfo;
import com.openshift.express.internal.client.test.fakes.CartridgeResponseFake;
import com.openshift.express.internal.client.test.fakes.UserInfoResponseFake;

/**
 * @author André Dietisheim
 */
public class UserTest {

	private IOpenShiftService openshiftService;
	private IUser user;
	private UserInfo userInfo;

	@Before
	public void setUp() throws OpenShiftException, DatatypeConfigurationException {
		userInfo = createUserInfo();
		this.openshiftService = mock(IOpenShiftService.class);
		when(openshiftService.getUserInfo(any(IUser.class))).thenReturn(userInfo);
		this.user = new InternalUser(UserInfoResponseFake.RHLOGIN, UserInfoResponseFake.PASSWORD, openshiftService);
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
		// pre-conditions
		ArrayList<ICartridge> cartridges = new ArrayList<ICartridge>();
		cartridges.add(new Cartridge(CartridgeResponseFake.CARTRIDGE_JBOSSAS70));
		cartridges.add(new Cartridge(CartridgeResponseFake.CARTRIDGE_PERL5));
		cartridges.add(new Cartridge(CartridgeResponseFake.CARTRIDGE_PHP53));
		cartridges.add(new Cartridge(CartridgeResponseFake.CARTRIDGE_RACK11));
		cartridges.add(new Cartridge(CartridgeResponseFake.CARTRIDGE_WSGI32));
		when(openshiftService.getCartridges(user)).thenReturn(cartridges);
		// operation
		Collection<ICartridge> userCartridges = user.getCartridges();
		// verifications
		assertNotNull(userCartridges);
		assertEquals(5, userCartridges.size());
		assertThatContainsCartridge(CartridgeResponseFake.CARTRIDGE_JBOSSAS70, userCartridges);
		assertThatContainsCartridge(CartridgeResponseFake.CARTRIDGE_PERL5, userCartridges);
		assertThatContainsCartridge(CartridgeResponseFake.CARTRIDGE_PHP53, userCartridges);
		assertThatContainsCartridge(CartridgeResponseFake.CARTRIDGE_RACK11, userCartridges);
		assertThatContainsCartridge(CartridgeResponseFake.CARTRIDGE_WSGI32, userCartridges);
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
				, Collections.singletonList(
						UserInfoResponseFake.toEmbeddableCartridge(
								UserInfoResponseFake.APP2_EMBEDDED_NAME,
								UserInfoResponseFake.APP2_EMBEDDED_URL))
				, UserInfoResponseFake.APP2_CREATION_TIME
				, application);
	}

	@Test
	public void shouldKeepApplicationsListInSync() throws OpenShiftException {
		// pre-conditions
		assertEquals(user.getApplications().size(), 2);
		final IApplication application = user.getApplicationByName(UserInfoResponseFake.APP1_NAME);
		assertNotNull(application);
		Mockito.doAnswer(new Answer<Object>() {
			public Object answer(InvocationOnMock invocation) throws Throwable {
				for (Iterator<ApplicationInfo> iterator = userInfo.getApplicationInfos().iterator(); iterator.hasNext();) {
					ApplicationInfo app = (ApplicationInfo) iterator.next();
					if (app.getName().equals(application.getName())) {
						iterator.remove();
					}
				}
				userInfo.getApplicationInfos();
				return null;
			}
		}).when(openshiftService).destroyApplication(application.getName(), application.getCartridge(), user);
		// operation
		application.destroy();
		// verifications
		assertEquals(user.getApplications().size(), 1);
	}

	private UserInfo createUserInfo() throws OpenShiftException, DatatypeConfigurationException {
		List<ApplicationInfo> applicationInfos = new ArrayList<ApplicationInfo>();
		applicationInfos.add(
				new ApplicationInfo(UserInfoResponseFake.APP1_NAME
						, UserInfoResponseFake.APP1_UUID
						, UserInfoResponseFake.APP1_EMBEDDED
						, Cartridge.valueOf(UserInfoResponseFake.APP1_CARTRIDGE)
						, RFC822DateUtils.getDate(UserInfoResponseFake.APP1_CREATION_TIME)));
		applicationInfos.add(
						new ApplicationInfo(UserInfoResponseFake.APP2_NAME
						, UserInfoResponseFake.APP2_UUID
						, Collections.singletonList(
								new EmbeddableCartridgeInfo(
										UserInfoResponseFake.APP2_EMBEDDED_NAME,
										UserInfoResponseFake.APP2_EMBEDDED_URL))
						, Cartridge.valueOf(UserInfoResponseFake.APP2_CARTRIDGE)
						, RFC822DateUtils.getDate(UserInfoResponseFake.APP2_CREATION_TIME)));
		
		return new UserInfo(
				UserInfoResponseFake.RHLOGIN
				, UserInfoResponseFake.UUID
				, UserInfoResponseFake.SSH_KEY
				, UserInfoResponseFake.RHC_DOMAIN
				, UserInfoResponseFake.NAMESPACE
				, applicationInfos
				, UserInfoResponseFake.SSH_KEY_TYPE);
	}

}