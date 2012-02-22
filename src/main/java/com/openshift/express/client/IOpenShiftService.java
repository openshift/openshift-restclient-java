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
package com.openshift.express.client;

import java.net.MalformedURLException;
import java.util.List;

import com.openshift.express.internal.client.ApplicationInfo;
import com.openshift.express.internal.client.UserInfo;

/**
 * @author André Dietisheim
 */
public interface IOpenShiftService {

	public static final String ID = "com.openshift.express.client";

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
	public boolean isValid(IUser user) throws OpenShiftException;

	/**
	 * List all cartridges that are available on the OpenShift Express platform.
	 * 
	 * @param user
	 *            the user account that shall be used
	 * @return the list of cartridges available on the platform
	 * @throws OpenShiftException
	 * @throws MalformedURLException
	 * 
	 * @see IUser
	 */
	public List<ICartridge> getCartridges(IUser user) throws OpenShiftException;
	/**
	 * Lists all cartridges that may be embedded into applications.
	 * 
	 * @param user
	 *            the user to authenticate with
	 * @return a list of embeddable cartridges
	 * @throws OpenShiftException
	 * 
	 * @see #addEmbeddedCartridge(IApplication, IEmbeddableCartridge,
	 *      IUser)
	 */
	public List<IEmbeddableCartridge> getEmbeddableCartridges(IUser user) throws OpenShiftException;

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
	 * @see IUser
	 * @see IApplication
	 */
	public IApplication createApplication(String name, ICartridge cartridge, IUser user)
			throws OpenShiftException;
	
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
	 * @param nodeProfile
	 *            Applications node profile (e.g. std, large)
	 * @return the application that was created
	 * @throws OpenShiftException
	 * 
	 * @see ICartridge
	 * @see IUser
	 * @see IApplication
	 */
	public IApplication createApplication(String name, ICartridge cartridge, IUser user, String nodeProfile)
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
	 * @see IUser
	 */
	public void destroyApplication(String name, ICartridge cartridge, IUser user) throws OpenShiftException;

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
	 * @see IUser
	 * @see IApplication
	 */
	public IApplication startApplication(String name, ICartridge cartridge, IUser user)
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
	 * @see IUser
	 * @see IApplication
	 */
	public IApplication restartApplication(String name, ICartridge cartridge, IUser user)
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
	 * @see IUser
	 * @see IApplication
	 */
	public IApplication stopApplication(String name, ICartridge cartridge, IUser user) throws OpenShiftException;

	
	/**
	 * Trigger a thread dump for the application with the given name and cartridge for the given
	 * user account. 
	 * 
	 * @param name
	 *            of the application that shall be started
	 * @param cartridge
	 *            the cartridge the application is running on
	 * @param user
	 *            the user account to use
	 * @return the application 
	 * @throws OpenShiftException
	 * 
	 * @see ICartridge
	 * @see IUser
	 * @see IApplication
	 */
	public IApplication threadDumpApplication(String name, ICartridge cartridge, IUser user)
			throws OpenShiftException;
	
	/**
	 * Adds the given embeddable cartridge to the application with the name
	 * using the given user.
	 * 
	 * @param applicationName
	 *            the name of the application to add the embeddable cartridge to
	 * @param cartridge
	 *            the cartridge to embed
	 * @param user
	 *            the user that's used to authenticate to the service
	 * @throws OpenShiftException
	 *             the open shift exception
	 * @see #getEmbeddableCartridges(IUser)
	 */
	public IEmbeddableCartridge addEmbeddedCartridge(String applicationName, IEmbeddableCartridge cartridge,
			IUser user)
			throws OpenShiftException;

	/**
	 * Removes the given embeddable cartridge from the application with the
	 * given name using the given user.
	 * 
	 * @param applicationName the name of the application to remove the embedded cartridge
	 *            from
	 * @param cartridge the cartridge to remmove
	 * @param user the user that's used to authenticate to the service
	 * @throws OpenShiftException occurrs if the cartridge could not be removed
	 * @see #getEmbeddableCartridges(IUser)
	 */
	public void removeEmbeddedCartridge(String applicationName, IEmbeddableCartridge cartridge,
			IUser user) throws OpenShiftException;

	
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
	 * @see IUser
	 */
	public String getStatus(String name, ICartridge cartridge, IUser user) throws OpenShiftException;
	
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
	 * @param logFile
	 * 				the log file           
	 * @return the log of the application
	 * @throws OpenShiftException
	 * 
	 * @see ICartridge
	 * @see IUser
	 */
	public String getStatus(String name, ICartridge cartridge, IUser user, String logFile, int numLines) throws OpenShiftException;


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
	 * @see IUser
	 * @see SSHKeyPair
	 * @see ISSHPublicKey
	 * @see IDomain
	 */
	public IDomain changeDomain(String name, ISSHPublicKey sshKey, IUser user) throws OpenShiftException;

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
	 * @see IUser
	 * @see SSHKeyPair
	 * @see ISSHPublicKey
	 * @see IDomain
	 */
	public IDomain createDomain(String name, ISSHPublicKey sshKey, IUser user) throws OpenShiftException;

	/**
	 * Destroys the OpenShift domain associated to the given User.
	 * If there is any application deployed OpenShiftException is thrown.
	 * 
	 * @param name
	 * 				domain namespace to destroy
	 * @param user
	 *             the user account to use
	 * 
	 * @throws OpenShiftException
	 * 
	 * @see IUser
	 */
	public void destroyDomain(String name, IUser user) throws OpenShiftException;
	
	
	/**
	 * Returns all informations for the given user and its applications.
	 * 
	 * @param user
	 *            the user account to use
	 * @return all user informations (user related info and applications)
	 * @throws OpenShiftException
	 * 
	 * @see IUser
	 * @see IUserInfo
	 * @see ApplicationInfo
	 */
	public UserInfo getUserInfo(IUser user) throws OpenShiftException;
	
	/**
	 * Waits for the given application to become accessible on it's public url.
	 * 
	 * @param healthCheckUrl
	 *            the url at which the application may be queried for it's accessibility
	 * @param timeout
	 *            the max time that shall be waited for.
	 * @return true, if successful
	 * @throws OpenShiftException
	 *             the open shift exception
	 * @see IApplication#getApplicationUrl()
	 */
	public boolean waitForApplication(String applicationHealthCheckUrl, long timeout) throws OpenShiftException;
	
	public boolean waitForHostResolves(String url, long timeout) throws OpenShiftException;
	
	/**
	 * Sets flag for enabling SSL certificate checks (i.e. self-signed SSL certificates)
	 * 
	 * @param doSSLChecks
	 */
	public void setEnableSSLCertChecks(boolean doSSLChecks);
	
	/**
	 * Sets flag for using an HTTP proxy
	 * 
	 * @param proxySet
	 */
	public void setProxySet(boolean proxySet);
	
	/**
	 * Sets the HTTP proxy hostname
	 * 
	 * @param proxyHost
	 */
	public void setProxyHost(String proxyHost);
	
	/**
	 * Sets the HTTP proxy port
	 * 
	 * @param proxyPort
	 */
	public void setProxyPort(String proxyPort);
	
}
