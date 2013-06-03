/*******************************************************************************
 * Copyright (c) 2011 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package com.openshift.client;

import java.util.List;

import com.openshift.client.cartridge.IStandaloneCartridge;

/**
 * @author André Dietisheim
 */
public interface IDomain extends IOpenShiftResource {
	
	/**
	 * @return the domain id (formerly known as 'namespace'). A unique litteral identifier on OpenShift.
	 */
	public String getId();

	/**
	 * The domain suffix is the host part eg: 'rhcloud.com')
	 * @return
	 */
	public String getSuffix();
	
	
	/**
	 * Rename the current domain with the given id....
	 * @param id
	 * @throws OpenShiftException
	 */
	public void rename(String id) throws OpenShiftException;
	
	/**
	 * Returns the currently connected user that manages this domain.
	 * 
	 * @return
	 * @throws OpenShiftException 
	 */
	public IUser getUser() throws OpenShiftException;

	
	/**
	 * Destroys the current domain. This method works only if it has not application.
	 * @throws OpenShiftException
	 */
	public void destroy() throws OpenShiftException;

	/**
	 * Destroys the current domain, using the 'force' parameter to also destroy the domain applications. The domain cannot
	 * be destroyed without setting 'force-true' if it still contains applications.
	 * 
	 * @param force
	 * @throws OpenShiftException
	 */
	public void destroy(final boolean force) throws OpenShiftException;

	/**
	 * Waits for the domain to become accessible. A domain is considered as accessible as soon as at least 1 application
	 * url in it resolves to a valid ip address.
	 * 
	 * @return boolean true if at least 1 application within this domain resolves
	 * @throws OpenShiftException
	 */
	public boolean waitForAccessible(long timeout) throws OpenShiftException;

	public IApplication createApplication(final String name, final IStandaloneCartridge cartridge)
			throws OpenShiftException;

	public IApplication createApplication(final String name, final IStandaloneCartridge cartridge, String initialGitUrl)
			throws OpenShiftException;

	public IApplication createApplication(final String name, final IStandaloneCartridge cartridge,
			final ApplicationScale scale) throws OpenShiftException;

	public IApplication createApplication(final String name, final IStandaloneCartridge cartridge,
			final IGearProfile gearProfile) throws OpenShiftException;

	public IApplication createApplication(final String name, final IStandaloneCartridge cartridge,
			final ApplicationScale scale, final IGearProfile gearProfile) throws OpenShiftException;

	/**
	 * Creates a new application with the given name and the given
	 * cartridge/framework. Optionally, adds scalability and a specific gear
	 * profile and a git url to use for the initial template.
	 * 
	 * @param name
	 *            the name of the application
	 * @param cartridge
	 *            the cartridge (the application type, ex. jbossas-7,
	 *            jbossews-2, php.5.2, etc.
	 * @param scale
	 *            or null (will use default on openshift, ie, false)
	 * @param gearProfile
	 *            ("small", "micro", "medium", "large", "exlarge", "jumbo") or
	 *            null (will use default on openshift, ie, 'small')
	 * @param initialGitUrl
	 *            the git url for the initial template app to be used
	 * @return
	 * @throws OpenShiftException
	 */
	public IApplication createApplication(final String name, final IStandaloneCartridge cartridge, 
			final ApplicationScale scale, final IGearProfile gearProfile, String initialGitUrl)
			throws OpenShiftException;

	public List<IApplication> getApplications() throws OpenShiftException;
	
	/**
	 * Returns the list of cartridges that can be used to create a new application.
	 * @return the list of cartridges that can be used to create a new application.
	 * @throws OpenShiftException
	 */
	public List<String> getAvailableCartridgeNames() throws OpenShiftException;

	/**
	 * Returns the application identified by the given name.
	 * @param name
	 * @return
	 * @throws OpenShiftException
	 */
	public IApplication getApplicationByName(String name) throws OpenShiftException;

	/**
	 * Returns true if the application identified by the given name exists in the domain.
	 * @param name
	 * @return
	 * @throws OpenShiftException
	 */
	public boolean hasApplicationByName(String name) throws OpenShiftException;

	public List<IApplication> getApplicationsByCartridge(IStandaloneCartridge cartridge) throws OpenShiftException;

	public boolean hasApplicationByCartridge(IStandaloneCartridge cartridge) throws OpenShiftException;
	
	/**
	 * Returns the list of available gear size that the user can choose when creating a new application (application's gear size can't be changed after creation).
	 * @return
	 * @throws OpenShiftException
	 */
	public List<IGearProfile> getAvailableGearProfiles() throws OpenShiftException;
	
}