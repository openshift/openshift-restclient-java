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
import java.util.Map;

import com.openshift.client.cartridge.IEmbeddableCartridge;
import com.openshift.client.cartridge.IStandaloneCartridge;

/**
 * @author André Dietisheim
 * @author Nicolas Spano
 * @author Syed Iqbal
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
	public void destroy(boolean force) throws OpenShiftException;

	/**
	 * Returns <code>true</code> if this domain can create application with environment variables. Returns <code>false</code> otherwise. 
	 * 
	 * @return true if can create an application with environment variables
	 * 
	 * @see IApplication#getEnvironmentVariables()
	 */
	public boolean canCreateApplicationWithEnvironmentVariables();

	public IApplication createApplication(String name, IStandaloneCartridge cartridge)
			throws OpenShiftException;

	public IApplication createApplication(String name, IStandaloneCartridge cartridge, String initialGitUrl)
			throws OpenShiftException;

	public IApplication createApplication(String name, IStandaloneCartridge cartridge, ApplicationScale scale)
			throws OpenShiftException;

	public IApplication createApplication(String name, IStandaloneCartridge cartridge, IGearProfile gearProfile)
			throws OpenShiftException;

	public IApplication createApplication(String name, IStandaloneCartridge cartridge, ApplicationScale scale,
			IGearProfile gearProfile)
			throws OpenShiftException;

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
	 *            available gear sizes from openshift api  or
	 *            null (will use default on openshift, ie, 'small')
	 * @param initialGitUrl
	 *            the git url for the initial template app to be used
	 * @return
	 * @throws OpenShiftException
	 */
	public IApplication createApplication(String name, IStandaloneCartridge cartridge, ApplicationScale scale,
			IGearProfile gearProfile, String initialGitUrl)
			throws OpenShiftException;

    /**
	 * Creates a new application with the given name and the given
	 * cartridge/framework. Optionally, adds scalability, a specific gear
	 * profile, a git url to use for the initial template, the timeout value and
	 * the embeddable (add-on) cartridges for it.
	 * 
	 * @param name
	 *            the name of the application
	 * @param cartridge
	 *            the cartridge (the application type, ex. jbossas-7,
	 *            jbossews-2, php.5.2, etc.
	 * @param scale
	 *            or null (will use default on openshift, ie, false)
	 * @param gearProfile
	 *            available gear sizes from openshift api  or
	 *            null (will use default on openshift, ie, 'small')
	 * @param initialGitUrl
	 *            the git url for the initial template app to be used
	 * @param timeout
	 *            the timeout value in milliseconds
	 * @param cartridges
	 *            the embeddable cartridges that shall get added to the new
	 *            application (the add-on cartridges mysql, mongodb, postgresql,
	 *            etc.)
	 * 
	 * @return IApplication created
	 * @throws OpenShiftException
	 */
	public IApplication createApplication(String name, IStandaloneCartridge cartridge, ApplicationScale scale,
			IGearProfile gearProfile, String initialGitUrl, int timeout, IEmbeddableCartridge... cartridges)
			throws OpenShiftException;
	
	/**
	 * Creates a new application with the given name and the given
	 * cartridge/framework. Optionally, adds scalability, a specific gear
	 * profile, a git url to use for the initial template, the timeout value, 
	 * environment variables  and
	 * the embeddable (add-on) cartridges for it.
	 * 
	 * @param name
	 *            the name of the application
	 * @param cartridge
	 *            the cartridge (the application type, ex. jbossas-7,
	 *            jbossews-2, php.5.2, etc.
	 * @param scale
	 *            or null (will use default on openshift, ie, false)
	 * @param gearProfile
	 *            available gear sizes from openshift api or
	 *            null (will use default on openshift, ie, 'small')
	 * @param initialGitUrl
	 *            the git url for the initial template app to be used
	 * @param timeout
	 *            the timeout value in milliseconds
	 * @param environmentVariable
	 *            the environment variables to be added to the application           
	 * @param cartridges
	 *            the embeddable cartridges that shall get added to the new
	 *            application (the add-on cartridges mysql, mongodb, postgresql,
	 *            etc.)
	 * 
	 * @return IApplication created
	 * @throws OpenShiftException
	 */
	public IApplication createApplication(String name, IStandaloneCartridge cartridge, ApplicationScale scale,
			IGearProfile gearProfile, String initialGitUrl, int timeout,Map<String,String> environmentVariable,
	        IEmbeddableCartridge... cartridges)
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