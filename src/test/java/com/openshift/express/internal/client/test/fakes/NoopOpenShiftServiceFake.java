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
package com.openshift.express.internal.client.test.fakes;

import java.util.List;

import com.openshift.express.client.IApplication;
import com.openshift.express.client.ICartridge;
import com.openshift.express.client.IDomain;
import com.openshift.express.client.ISSHPublicKey;
import com.openshift.express.client.IUser;
import com.openshift.express.client.OpenShiftException;
import com.openshift.express.client.OpenShiftService;
import com.openshift.express.internal.client.Application;
import com.openshift.express.internal.client.UserInfo;

/**
 * @author André Dietisheim
 */
public class NoopOpenShiftServiceFake extends OpenShiftService  {

	public NoopOpenShiftServiceFake() {
		super(null, null);
	}

	public UserInfo getUserInfo(IUser user) throws OpenShiftException {
		throw new UnsupportedOperationException();
	}

	public List<ICartridge> getCartridges(IUser user) throws OpenShiftException {
		throw new UnsupportedOperationException();
	}

	public Application createApplication(String name, ICartridge cartridge, IUser user) throws OpenShiftException {
		throw new UnsupportedOperationException();
	}

	public void destroyApplication(String name, ICartridge cartridge, IUser user) throws OpenShiftException {
		throw new UnsupportedOperationException();
	}

	public IApplication startApplication(String name, ICartridge cartridge, IUser user) throws OpenShiftException {
		throw new UnsupportedOperationException();
	}

	public IApplication restartApplication(String name, ICartridge cartridge, IUser user) throws OpenShiftException {
		throw new UnsupportedOperationException();
	}

	public IApplication stopApplication(String name, ICartridge cartridge, IUser user) throws OpenShiftException {
		throw new UnsupportedOperationException();
	}

	public String getStatus(String applicationName, ICartridge cartridge, IUser user) throws OpenShiftException {
		throw new UnsupportedOperationException();
	}

	public IDomain changeDomain(String domainName, ISSHPublicKey sshKey, IUser user) throws OpenShiftException {
		throw new UnsupportedOperationException();
	}

	public IDomain createDomain(String name, ISSHPublicKey keyPair, IUser user) throws OpenShiftException {
		throw new UnsupportedOperationException();
	}
}

