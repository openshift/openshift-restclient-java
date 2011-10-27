/******************************************************************************* 
 * Copyright (c) 2011 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/
package org.jboss.tools.openshift.express.internal.client;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.jboss.tools.openshift.express.client.IApplication;
import org.jboss.tools.openshift.express.client.ICartridge;
import org.jboss.tools.openshift.express.client.IDomain;
import org.jboss.tools.openshift.express.client.IOpenShiftService;
import org.jboss.tools.openshift.express.client.ISSHPublicKey;
import org.jboss.tools.openshift.express.client.IUser;
import org.jboss.tools.openshift.express.client.InvalidCredentialsOpenShiftException;
import org.jboss.tools.openshift.express.client.NotFoundOpenShiftException;
import org.jboss.tools.openshift.express.client.OpenShiftException;
import org.jboss.tools.openshift.express.client.OpenShiftService;
import org.jboss.tools.openshift.express.client.UserConfiguration;

/**
 * @author André Dietisheim
 */
public class InternalUser implements IUser {

	private String rhlogin;
	private String password;
	private ISSHPublicKey sshKey;
	private IDomain domain;
	private UserInfo userInfo;
	private List<ICartridge> cartridges;
	private List<IApplication> applications = new ArrayList<IApplication>();

	private IOpenShiftService service;

	public InternalUser(String password, String id) throws OpenShiftException, IOException {
		this(new UserConfiguration(), password, id);
	}

	public InternalUser(UserConfiguration configuration, String password, String id) {
		this(configuration.getRhlogin(), password, (ISSHPublicKey) null, new OpenShiftService(id));
	}

	public InternalUser(String rhlogin, String password, String id) {
		this(rhlogin, password, (ISSHPublicKey) null, new OpenShiftService(id));
	}

	public InternalUser(String rhlogin, String password, String id, String url) {
		this(rhlogin, password, (ISSHPublicKey) null, new OpenShiftService(id, url));
	}

	public InternalUser(String rhlogin, String password, IOpenShiftService service) {
		this(rhlogin, password, (ISSHPublicKey) null, service);
	}

	public InternalUser(String rhlogin, String password, ISSHPublicKey sshKey, IOpenShiftService service) {
		this.rhlogin = rhlogin;
		this.password = password;
		this.sshKey = sshKey;
		this.service = service;
	}

	@Override
	public boolean isValid() throws OpenShiftException {
		try {
			return service.isValid(this);
		} catch (InvalidCredentialsOpenShiftException e) {
			return false;
		}
	}

	@Override
	public IDomain createDomain(String name, ISSHPublicKey key) throws OpenShiftException {
		setSshKey(key);
		this.domain = getService().createDomain(name, key, this);
		return domain;
	}

	@Override
	public IDomain getDomain() throws OpenShiftException {
		if (domain == null) {
			try {
				this.domain = new Domain(
						getUserInfo().getNamespace()
						, getUserInfo().getRhcDomain()
						, this
						, service);
			} catch (NotFoundOpenShiftException e) {
				return null;
			}
		}
		return domain;
	}

	public boolean hasDomain() throws OpenShiftException {
		return getDomain() != null;
	}
	
	private void setSshKey(ISSHPublicKey key) {
		this.sshKey = key;
	}

	@Override
	public ISSHPublicKey getSshKey() throws OpenShiftException {
		if (sshKey == null) {
			this.sshKey = getUserInfo().getSshPublicKey();
		}
		return sshKey;
	}

	@Override
	public String getRhlogin() {
		return rhlogin;
	}

	@Override
	public String getPassword() {
		return password;
	}

	@Override
	public String getUUID() throws OpenShiftException {
		return getUserInfo().getUuid();
	}

	@Override
	public List<ICartridge> getCartridges() throws OpenShiftException {
		if (cartridges == null) {
			this.cartridges = service.getCartridges(this);
		}
		return Collections.unmodifiableList(cartridges);
	}

	@Override
	public ICartridge getCartridgeByName(String name) throws OpenShiftException {
		ICartridge matchingCartridge = null;
		for(ICartridge cartridge : getCartridges()) {
			if (name.equals(cartridge.getName())) {
				matchingCartridge = cartridge;
				break;
			}
		}
		return matchingCartridge;
	}
	
	@Override
	public IApplication createApplication(String name, ICartridge cartridge) throws OpenShiftException {
		IApplication application = service.createApplication(name, cartridge, this);
		add(application);
		return application;
	}

	@Override
	public Collection<IApplication> getApplications() throws OpenShiftException {
		if (getUserInfo().getApplicationInfos().size() > applications.size()) {
			update(getUserInfo().getApplicationInfos());
		}
		return applications;
	}

	@Override
	public IApplication getApplicationByName(String name) throws OpenShiftException {
		return getApplicationByName(name, getApplications());
	}

	private IApplication getApplicationByName(String name, Collection<IApplication> applications) {
		IApplication matchingApplication = null;
		for (IApplication application : applications) {
			if (name.equals(application.getName())) {
				matchingApplication = application;
			}
		}
		return matchingApplication;
	}

	public void add(IApplication application) {
		applications.add(application);
	}

	public void remove(IApplication application) {
		applications.remove(application);
	}

	public void setSshPublicKey(ISSHPublicKey key) {
		this.sshKey = key;
	}

	protected UserInfo refreshUserInfo() throws OpenShiftException {
		this.userInfo = null;
		return getUserInfo();
	}
	
	protected UserInfo getUserInfo() throws OpenShiftException {
		if (userInfo == null) {
			this.userInfo = service.getUserInfo(this);
		}
		return userInfo;
	}

	@Override
	public void refresh() throws OpenShiftException {
		this.domain = null;
		this.sshKey = null;
		getUserInfo();
	}

	private void update(List<ApplicationInfo> applicationInfos) {
		for (ApplicationInfo applicationInfo : applicationInfos) {
			IApplication application = getApplicationByName(applicationInfo.getName(), applications);
			if (application == null) {
				applications.add(createApplication(applicationInfo));
			}
		}
	}

	private Application createApplication(ApplicationInfo applicationInfo) {
		return new Application(applicationInfo.getName()
				, applicationInfo.getCartridge()
				, applicationInfo
				, this, service);
	}

	protected IOpenShiftService getService() {
		return service;
	}
}
