/******************************************************************************* 
 * Copyright (c) 2012 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/
package com.openshift.client;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author Xavier Coulon
 * @author Andre Dietisheim
 */
public interface IOpenShiftConnection {

	/**
	 * Returns the server this connection is bound to.
	 * 
	 * @return the server
	 */
	public String getServer();
	
	/**
	 * Returns the user associated with the current OpenShift connection.
	 * 
	 * @return the user
	 * @throws OpenShiftException
	 */
	public IUser getUser() throws OpenShiftException;

	/**
	 * Returns the domains associated with the current OpenShift connection.
	 * 
	 * @return the domains
	 * @throws OpenShiftException
	 */
	public List<IDomain> getDomains() throws OpenShiftException;

	/**
	 * Returns the available standalone cartridges associated with the current
	 * OpenShift connection.
	 * 
	 * @return the available standalone cartridges
	 * @throws OpenShiftException
	 */
	public List<ICartridge> getStandaloneCartridges() throws OpenShiftException;

	/**
	 * Returns the available embeddable cartridges associated with the current
	 * OpenShift connection.
	 * 
	 * @return the available embeddable cartridges
	 * @throws OpenShiftException
	 */
	public List<IEmbeddableCartridge> getEmbeddableCartridges() throws OpenShiftException;

	/**
	 * Sets flag for enabling SSL certificate checks (i.e. self-signed SSL
	 * certificates)
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

	/**
	 * Returns the executor service instance that's available in this
	 * connection.
	 * 
	 * @return the executor service instance for this connection
	 * 
	 * @see ExecutorService
	 * @see Executors
	 */
	public ExecutorService getExecutorService();

}
