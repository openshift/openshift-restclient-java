package com.openshift.client;

import com.jcraft.jsch.Session;

public interface IApplicationPortForwarding {

	/**
	 * Start the binding, ie, open a SSH tunnel between local address:port and remote address:port.
	 * 
	 * @throws OpenShiftSSHOperationException
	 */
	public abstract void start(final Session session) throws OpenShiftSSHOperationException;

	/**
	 * Stop the SSH tunnel.
	 * 
	 * @throws OpenShiftSSHOperationException
	 */
	public abstract void stop(final Session session) throws OpenShiftSSHOperationException;

	/**
	 * @return true if the SSH tunnel is open, false otherwise.
	 * @throws OpenShiftSSHOperationException 
	 */
	public abstract boolean isStarted(final Session session) throws OpenShiftSSHOperationException;

	/**
	 * @return the name
	 */
	public abstract String getName();

	/**
	 * @return the localAddress
	 */
	public abstract String getLocalAddress();

	/**
	 * @param localAddress
	 *            the localAddress to set
	 */
	public abstract void setLocalAddress(final String localAddress);

	/**
	 * @return the localPort
	 */
	public abstract int getLocalPort();

	/**
	 * @param localPort
	 *            the localPort to set
	 */
	public abstract void setLocalPort(final int localPort);

	/**
	 * @return the remoteIp
	 */
	public abstract String getRemoteAddress();

	/**
	 * @return the remotePort
	 */
	public abstract int getRemotePort();

}