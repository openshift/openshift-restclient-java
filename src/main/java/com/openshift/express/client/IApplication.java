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

import java.util.Date;
import java.util.List;

/**
 * @author André Dietisheim
 */
public interface IApplication {

	/**
	 * Returns the name of this application.
	 * 
	 * @return
	 */
	public String getName();

	/**
	 * Returns the uuid of this application.
	 * 
	 * @return the uuid of this application.
	 * @throws OpenShiftException
	 */
	public String getUUID() throws OpenShiftException;

	/**
	 * Returns the uri at which the git repository of this application may be reached at.
	 * 
	 * @return the uri of the git repo of this application.
	 * @throws OpenShiftException
	 */
	public String getGitUri() throws OpenShiftException;

	/**
	 * Returns the url at which this application may be reached at.
	 * 
	 * @return the url of this application.
	 * @throws OpenShiftException
	 */
	public String getApplicationUrl() throws OpenShiftException;

	/**
	 * Returns the url at which this application may be checked for its health state.
	 * 
	 * @return the url at which the health state may be queried.
	 * @throws OpenShiftException
	 */
	public String getHealthCheckUrl() throws OpenShiftException;
	
	public String getHealthCheckResponse() throws OpenShiftException;

	/**
	 * Returns the cartridge (application type) that this app is running on.
	 * 
	 * @return the cartridge of this application
	 * 
	 * @see ICartridge
	 * @see User#getCartridges()
	 */
	public ICartridge getCartridge();

	/**
	 * Adds the given embeddable cartridge to this app.
	 * 
	 * @param embeddedCartridge
	 * @throws OpenShiftException
	 */
	public void addEmbbedCartridge(IEmbeddableCartridge embeddedCartridge) throws OpenShiftException;

	/**
	 * Adds all given embeddable cartridges from this app.
	 * 
	 * @param embeddedCartridges
	 * @throws OpenShiftException
	 * 
	 * @see IEmbeddableCartridge
	 * @see #addEmbbedCartridge(IEmbeddableCartridge)
	 * @see #removeEmbbedCartridge(IEmbeddableCartridge)
	 */
	public void addEmbbedCartridges(List<IEmbeddableCartridge> embeddedCartridges) throws OpenShiftException;

	/**
	 * Removes the given cartridge from this app.
	 * 
	 * @param embeddedCartridge
	 * @throws OpenShiftException
	 * 
	 * @see IEmbeddableCartridge
	 * @see #addEmbbedCartridge(IEmbeddableCartridge)
	 * @see #removeEmbbedCartridge(IEmbeddableCartridge)
	 */
	public void removeEmbbedCartridge(IEmbeddableCartridge embeddedCartridge) throws OpenShiftException;

	/**
	 * Removes all given cartridges from this app.
	 * 
	 * @param embeddedCartridges all cartridges that shall be removed.
	 * @throws OpenShiftException
	 * 
	 * @see IEmbeddableCartridge
	 * @see #addEmbbedCartridge(IEmbeddableCartridge)
	 * @see #removeEmbbedCartridge(IEmbeddableCartridge)
	 */
	public void removeEmbbedCartridges(List<IEmbeddableCartridge> embeddedCartridges) throws OpenShiftException;;

	/**
	 * Returns all embedded cartridges.
	 * 
	 * @return all embedded cartridges.
	 * @throws OpenShiftException
	 * 
	 * @see IEmbeddableCartridge
	 * @see #addEmbbedCartridge(IEmbeddableCartridge)
	 * @see #removeEmbbedCartridge(IEmbeddableCartridge)
	 */
	public List<IEmbeddableCartridge> getEmbeddedCartridges() throws OpenShiftException;

	/**
	 * Returns <code>true</code> if this app has an embedded cartridge with the given name. Returns <code>false</code>
	 * otherwise.
	 * 
	 * @param the name of the cartridge to look for
	 * @return true if there's an embedded cartridge with the given name
	 * @throws OpenShiftException
	 * 
	 * @see IEmbeddableCartridge
	 * @see #addEmbbedCartridge(IEmbeddableCartridge)
	 * @see #removeEmbbedCartridge(IEmbeddableCartridge)
	 */
	public boolean hasEmbeddedCartridge(String cartridgeName) throws OpenShiftException;

	/**
	 * Returns the embedded cartridge with the given name. Returns <code>null</code> if none was found.
	 * 
	 * @param cartridgeName
	 * @return the embedded cartridge with the given name
	 * @throws OpenShiftException
	 */
	public IEmbeddableCartridge getEmbeddedCartridge(String cartridgeName) throws OpenShiftException;

	/**
	 * Returns the timestamp at which this app was created.
	 * 
	 * @return the creation time
	 * 
	 * @throws OpenShiftException
	 */
	public Date getCreationTime() throws OpenShiftException;

	/**
	 * Destroys this application (and removes it from the list of available applications)
	 * 
	 * @throws OpenShiftException
	 * 
	 * @see IUser#getApplications()
	 */
	public void destroy() throws OpenShiftException;

	/**
	 * Starts this application. Has no effect if this app is already running.
	 * 
	 * @throws OpenShiftException
	 */
	public void start() throws OpenShiftException;

	/**
	 * Restarts this application.
	 * 
	 * @throws OpenShiftException
	 */
	public void restart() throws OpenShiftException;

	/**
	 * Stops this application.
	 * 
	 * @throws OpenShiftException
	 */
	public void stop() throws OpenShiftException;

	/**
	 * Gets the log that was created when the application was created.
	 * 
	 * @return the log which reported the creation of this app
	 */
	public String getCreationLog();

	/**
	 * Returns a reader that will allow you to read from the application log.
	 * 
	 * @return a reader that you can read the log from
	 * @throws OpenShiftException
	 * 
	 * @see ApplicationLogReader
	 */
	public ApplicationLogReader getLogReader() throws OpenShiftException;
	
	/**
	 * Returns a reader that will allow you to read from the application log.
	 * 
	 * @param logFile
	 * 			the log file
	 * @return a reader that you can read the log from
	 * @throws OpenShiftException
	 * 
	 * @see ApplicationLogReader
	 */
	public ApplicationLogReader getLogReader(String logFile) throws OpenShiftException;
	
	/**
	 * Waits for this application to become accessible on its public url. 
	 * 
	 * @param timeout
	 * @return
	 * @throws OpenShiftException
	 * 
	 * @see IApplication#getApplicationUrl()
	 */
	public boolean waitForAccessible(long timeout) throws OpenShiftException;

	public IUser getUser();
	
}