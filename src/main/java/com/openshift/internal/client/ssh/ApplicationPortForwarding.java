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
package com.openshift.internal.client.ssh;

import java.util.Arrays;

import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.openshift.client.IApplication;
import com.openshift.client.IApplicationPortForwarding;
import com.openshift.client.OpenShiftSSHOperationException;
import com.openshift.internal.client.utils.Assert;

/**
 * @author Xavier Coulon
 */
public class ApplicationPortForwarding implements IApplicationPortForwarding {

	private final IApplication application;

	private final String name;

	private final String remoteAddress;

	private final int remotePort;

	/** the local binding address, or null if not configured yet. */
	private String localAddress;

	/** the local binding port number, or null if not configured yet. */
	private int localPort = -1;

	public ApplicationPortForwarding(final IApplication application, final String name, final String remoteAddress,
			final int remotePort) {
		super();
		this.application = application;
		this.name = name;
		this.remoteAddress = remoteAddress;
		this.remotePort = remotePort;
	}

	public void start(final Session session) throws OpenShiftSSHOperationException {
		if (localAddress == null || localAddress.isEmpty()) {
			throw new IllegalArgumentException("Cannot enable port-forwarding from an undefined local address");
		}
		if (localPort == -1 ) {
			throw new IllegalArgumentException("Cannot enable port-forwarding from an undefined local port");
		}
		// don't start it twice
		if (!isStarted(session)) {
			try {
				session.setPortForwardingL(localAddress, this.localPort, this.remoteAddress, this.remotePort);
			} catch (Exception e) {
				throw new OpenShiftSSHOperationException(e, "Failed to start port forwarding on {0}:{1}",
						this.localAddress, this.localPort);
			}
		}
	}

	public void stop(final Session session) throws OpenShiftSSHOperationException {		
		if (isStarted(session)) {
			try {
				session.delPortForwardingL(localAddress, localPort);
			} catch (Exception e) {
				throw new OpenShiftSSHOperationException(e, "Failed to stop port forwarding on {0}:{1}",
						this.localAddress, this.localPort);
			}
		}
	}

	public boolean isStarted(final Session session) throws OpenShiftSSHOperationException {
		if (session == null || !session.isConnected()) {
			return false;
		}
		try {
			// returned format : localPort:remoteHost:remotePort
			final String[] portForwardingL = session.getPortForwardingL();
			final String key = this.localPort + ":" + this.remoteAddress + ":" + this.remotePort;
			Arrays.sort(portForwardingL);
			final int r = Arrays.binarySearch(portForwardingL, key);
			return r >= 0;
		} catch (JSchException e) {
			throw new OpenShiftSSHOperationException(e, "Failed to retrieve SSH ports forwarding");
		}
	}

	protected final IApplication getApplication() {
		return application;
	}

	public final String getName() {
		return name;
	}

	public final String getLocalAddress() {
		return localAddress;
	}

	public final void setLocalAddress(String localAddress) {
		Assert.notNull(localAddress);
		this.localAddress = localAddress;
	}

	public final int getLocalPort() {
		return localPort;
	}

	public final void setLocalPort(final int localPort) {
		Assert.notNull(localPort);
		this.localPort = localPort;
	}

	public final String getRemoteAddress() {
		return remoteAddress;
	}

	public final int getRemotePort() {
		return remotePort;
	}

	@Override
	public String toString() {
		return "ApplicationForwardablePort [" 
				+ name + ": " + localAddress + ":" + localPort + " -> " + remoteAddress
				+ ":" + remotePort + "]";
	}

}