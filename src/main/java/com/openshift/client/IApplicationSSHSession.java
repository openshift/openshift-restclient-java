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

import com.jcraft.jsch.Session;

import java.util.List;

/**
 * @author André Dietisheim
 * @author Syed Iqbal
 * @author Martes G Wigglesworth
 * @author Corey Daley
 */
public interface IApplicationSSHSession {

	/**
	 * Refreshes the list of port-forwarding. Started ones are kept as-is.
	 *
	 * @return the list of forwardable ports on OpenShift for this application.
	 * @throws OpenShiftSSHOperationException
	 */
	public List<IApplicationPortForwarding> refreshForwardablePorts() throws OpenShiftSSHOperationException;

	/**
	 * Returns true if the SSH session provided to the application
	 * is still valid (connected).
	 *
	 * @return true if the SSH session provided to the application
	 *         is still valid (connected).
	 */
	public boolean isConnected();

	/**
	 * Returns the list of forwardable ports on OpenShift for this application.
	 *
	 * @return the list of forwardable ports on OpenShift for this application.
	 * @throws OpenShiftSSHOperationException
	 */
	public List<IApplicationPortForwarding> getForwardablePorts() throws OpenShiftSSHOperationException;

	/**
	 * Stop the port-forwarding for all ports.
	 *
	 * @return the list of forwardable ports on OpenShift for this application.
	 * @throws OpenShiftSSHOperationException
	 */
	public List<IApplicationPortForwarding> stopPortForwarding() throws OpenShiftSSHOperationException;

	/**
	 * @ * Starts the port-forwarding for all ports.
	 *
	 * @return the list of forwardable ports on OpenShift for this application.
	 * @throws com.jcraft.jsch.JSchException
	 */
	public List<IApplicationPortForwarding> startPortForwarding() throws OpenShiftSSHOperationException;

	/**
	 * Sets the SSH session that this application will use to connect to
	 * OpenShift to perform some operations. This SSH session must be
	 * initialized out of the library, since the user's SSH settings may depend
	 * on the runtime environment (Eclipse, etc.).
	 *
	 * @param session
	 *            the SSH session
	 */
	public void setSSHSession(Session session);

	/**
	 * Retrieves the list of environment properties.
	 *
	 * @return the list of environment properties.
	 * @throws OpenShiftSSHOperationException
	 */
	public List<String> getEnvironmentProperties() throws OpenShiftSSHOperationException;

	/**
	 * Returns true if the port-forwarding has been started, false otherwise.
	 *
	 * @return true if the port-forwarding has been started, false otherwise.
	 * @throws OpenShiftSSHOperationException
	 */
	public boolean isPortFowardingStarted() throws OpenShiftSSHOperationException;


}
