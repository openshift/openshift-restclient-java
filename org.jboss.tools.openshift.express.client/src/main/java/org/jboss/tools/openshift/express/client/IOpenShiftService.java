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

import java.net.MalformedURLException;
import java.util.List;

import org.jboss.tools.openshift.express.internal.client.ApplicationInfo;
import org.jboss.tools.openshift.express.internal.client.InternalUser;
import org.jboss.tools.openshift.express.internal.client.UserInfo;

/**
 * @author André Dietisheim
 */
public interface IOpenShiftService {

	/**
	 * The platform base url
	 */
	public static final String BASE_URL = "https://openshift.redhat.com";

	/**
	 * The path (url path addition) to the service
	 */
	public static final String SERVICE_PATH = "/broker";

	/**
	 * Returns the url at which the service is reachable.
	 * 
	 * @return
	 */
	public String getServiceUrl();

	/**
	 * Returns the url at which the platform is reachable.
	 * 
	 * @return
	 */
	public String getPlatformUrl();

	/**
	 * Returns <code>true</code> if given user has valid credentials. Returns
	 * <code>false</code> otherwise.
	 * 
	 * @return
	 * @throws OpenShiftException
	 * @throws MalformedURLException
	 */
	public boolean isValid(InternalUser user) throws OpenShiftException;

	/**
	 * List all cartridges that are available on the OpenShift Express platform.
	 * 
	 * @param user
	 *            the user account that shall be used
	 * @return the list of cartridges available on the platform
	 * @throws OpenShiftException
	 * @throws MalformedURLException
	 * 
	 * @see InternalUser
	 */
	public List<ICartridge> getCartridges(InternalUser user) throws OpenShiftException;

	/**
	 * Creates an application with the given name and cartridge for the given
	 * user.
	 * 
	 * @param name
	 *            the application name
	 * @param cartridge
	 *            the cartridge to use
	 * @param user
	 *            the user account
	 * @return the application that was created
	 * @throws OpenShiftException
	 * 
	 * @see ICartridge
	 * @see InternalUser
	 * @see IApplication
	 */
	public IApplication createApplication(String name, ICartridge cartridge, InternalUser user)
			throws OpenShiftException;

	/**
	 * Destroys the application with the given name and cartridge for the given
	 * user.
	 * 
	 * @param name
	 *            the name of the application that shall be destroyed
	 * @param cartridge
	 *            the cartridge that the application is running on
	 * @param user
	 *            the user account
	 * @throws OpenShiftException
	 * 
	 * @see ICartridge
	 * @see InternalUser
	 */
	public void destroyApplication(String name, ICartridge cartridge, InternalUser user) throws OpenShiftException;

	/**
	 * Starts the application with the given name and cartridge for the given
	 * user account. Starting an application that is already started has no
	 * effect.
	 * 
	 * @param name
	 *            of the application that shall be started
	 * @param cartridge
	 *            the cartridge the application is running on
	 * @param user
	 *            the user account to use
	 * @return the application that was started
	 * @throws OpenShiftException
	 * 
	 * @see ICartridge
	 * @see InternalUser
	 * @see IApplication
	 */
	public IApplication startApplication(String name, ICartridge cartridge, InternalUser user)
			throws OpenShiftException;

	/**
	 * Restarts the application with the given name and cartridge for the given
	 * user account.
	 * 
	 * @param name
	 *            the name of the application that shall be restarted
	 * @param cartridge
	 *            the cartridge the application is running on
	 * @param user
	 *            the user account to use
	 * @return the application that was started
	 * @throws OpenShiftException
	 * 
	 * @see ICartridge
	 * @see InternalUser
	 * @see IApplication
	 */
	public IApplication restartApplication(String name, ICartridge cartridge, InternalUser user)
			throws OpenShiftException;

	/**
	 * Stops the application with the given name and cartridge for the given
	 * user account. Stopping an application that is already stopped has no
	 * effect.
	 * 
	 * @param name
	 *            the name of the application that shall be restarted
	 * @param cartridge
	 *            the cartridge the application is running on
	 * @param user
	 *            the user account to use
	 * @return the application that was stopped
	 * @throws OpenShiftException
	 * 
	 * @see ICartridge
	 * @see InternalUser
	 * @see IApplication
	 */
	public IApplication stopApplication(String name, ICartridge cartridge, InternalUser user) throws OpenShiftException;

	/**
	 * Returns the log of the application with the given name and cartridge.
	 * Returns the whole log if no new log entry was created since the last
	 * call. Returns the new entries otherwise.
	 * 
	 * @param name
	 *            of the application that the log shall be returned of
	 * @param cartridge
	 *            the cartridge the application is running on
	 * @param user
	 *            the user account to use
	 * @return the log of the application
	 * @throws OpenShiftException
	 * 
	 * @see ICartridge
	 * @see InternalUser
	 */
	public String getStatus(String name, ICartridge cartridge, InternalUser user) throws OpenShiftException;

	/**
	 * Changes the current domain (namespace) to the given name.
	 * 
	 * @param name
	 *            the new domain name(-space)
	 * @param sshKey
	 *            the ssh key that shall be used.
	 * @param user
	 *            the user account to use
	 * @return the domain that was changed
	 * @throws OpenShiftException
	 * 
	 * @see ICartridge
	 * @see InternalUser
	 * @see SSHKeyPair
	 * @see ISSHPublicKey
	 * @see IDomain
	 */
	public IDomain changeDomain(String name, ISSHPublicKey sshKey, InternalUser user) throws OpenShiftException;

	/**
	 * Creates a domain (namespace) with the given name for the given user
	 * account with the given ssh public key. If a domain already exists an
	 * OpenShiftEndpointException is thrown.
	 * 
	 * @param name
	 *            the new domain name(-space)
	 * @param sshKey
	 *            the ssh key that shall be used.
	 * @param user
	 *            the user account to use
	 * @return the domain that was changed
	 * @throws OpenShiftException
	 * 
	 * @see ICartridge
	 * @see InternalUser
	 * @see SSHKeyPair
	 * @see ISSHPublicKey
	 * @see IDomain
	 */
	public IDomain createDomain(String name, ISSHPublicKey sshKey, InternalUser user) throws OpenShiftException;

	/**
	 * Returns all informations for the given user and its applications.
	 * 
	 * @param user
	 *            the user account to use
	 * @return all user informations (user related info and applications)
	 * @throws OpenShiftException
	 * 
	 * @see InternalUser
	 * @see InternalUserInfo
	 * @see ApplicationInfo
	 */
	public UserInfo getUserInfo(InternalUser user) throws OpenShiftException;
}
