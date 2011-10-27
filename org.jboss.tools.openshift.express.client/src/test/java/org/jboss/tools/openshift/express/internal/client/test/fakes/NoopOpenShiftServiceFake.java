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
package org.jboss.tools.openshift.express.internal.client.test.fakes;

import java.util.List;

import org.jboss.tools.openshift.express.client.IApplication;
import org.jboss.tools.openshift.express.client.ICartridge;
import org.jboss.tools.openshift.express.client.IDomain;
import org.jboss.tools.openshift.express.client.ISSHPublicKey;
import org.jboss.tools.openshift.express.client.OpenShiftException;
import org.jboss.tools.openshift.express.internal.client.Application;
import org.jboss.tools.openshift.express.internal.client.InternalUser;
import org.jboss.tools.openshift.express.internal.client.UserInfo;

/**
 * @author André Dietisheim
 */
public class NoopOpenShiftServiceFake extends OpenShiftTestService  {

	@Override
	public UserInfo getUserInfo(InternalUser user) throws OpenShiftException {
		throw new UnsupportedOperationException();
	}

	@Override
	public List<ICartridge> getCartridges(InternalUser user) throws OpenShiftException {
		throw new UnsupportedOperationException();
	}

	@Override
	public Application createApplication(String name, ICartridge cartridge, InternalUser user) throws OpenShiftException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void destroyApplication(String name, ICartridge cartridge, InternalUser user) throws OpenShiftException {
		throw new UnsupportedOperationException();
	}

	@Override
	public IApplication startApplication(String name, ICartridge cartridge, InternalUser user) throws OpenShiftException {
		throw new UnsupportedOperationException();
	}

	@Override
	public IApplication restartApplication(String name, ICartridge cartridge, InternalUser user) throws OpenShiftException {
		throw new UnsupportedOperationException();
	}

	@Override
	public IApplication stopApplication(String name, ICartridge cartridge, InternalUser user) throws OpenShiftException {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getStatus(String applicationName, ICartridge cartridge, InternalUser user) throws OpenShiftException {
		throw new UnsupportedOperationException();
	}

	@Override
	public IDomain changeDomain(String domainName, ISSHPublicKey sshKey, InternalUser user) throws OpenShiftException {
		throw new UnsupportedOperationException();
	}

	@Override
	public IDomain createDomain(String name, ISSHPublicKey keyPair, InternalUser user) throws OpenShiftException {
		throw new UnsupportedOperationException();
	}
}
