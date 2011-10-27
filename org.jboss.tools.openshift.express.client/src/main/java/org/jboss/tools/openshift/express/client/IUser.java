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
package org.jboss.tools.openshift.express.client;

import java.util.Collection;
import java.util.List;

/**
 * @author André Dietisheim
 */
public interface IUser {

	public String getRhlogin();

	public String getPassword();

	public boolean isValid() throws OpenShiftException;

	public String getUUID() throws OpenShiftException;

	public IDomain createDomain(String name, ISSHPublicKey key) throws OpenShiftException;

	/**
	 * Returns the domain that this user created previously. Returns
	 * <code>null</code> if no domain was created.
	 * 
	 * @return the domain that this user created
	 * @throws OpenShiftException
	 * 
	 * @see #createDomain
	 */
	public IDomain getDomain() throws OpenShiftException;

	public boolean hasDomain() throws OpenShiftException;

	public ISSHPublicKey getSshKey() throws OpenShiftException;

	public List<ICartridge> getCartridges() throws OpenShiftException;

	public ICartridge getCartridgeByName(String name) throws OpenShiftException;

	public IApplication createApplication(String name, ICartridge cartridge) throws OpenShiftException;

	public Collection<IApplication> getApplications() throws OpenShiftException;

	public IApplication getApplicationByName(String name) throws OpenShiftException;

	public void refresh() throws OpenShiftException;

}