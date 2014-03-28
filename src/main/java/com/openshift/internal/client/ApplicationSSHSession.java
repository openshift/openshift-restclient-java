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
package com.openshift.internal.client;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.openshift.client.*;
import com.openshift.internal.client.ssh.ApplicationPortForwarding;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author André Dietisheim
 * @author Syed Iqbal
 * @author Martes G Wigglesworth
 * @author Corey Daley
 */
public class ApplicationSSHSession implements IApplicationSSHSession {

	/** SSH Session to use for all methods */
	private Session session;

	/** Application that is associated with this SSH Session */
	private IApplication application;

	/** List of ports available for port forwarding */
	private List<IApplicationPortForwarding> ports = null;

	/** Regex for port forwarding */
	private static final Pattern REGEX_FORWARDED_PORT = Pattern.compile("([^ ]+) -> ([^:]+):(\\d+)");

	/**
	 * Sets the SSH session that this application will use to connect to
	 * OpenShift to perform some operations. This SSH session must be
	 * initialized out of the library, since the user's SSH settings may depend
	 * on the runtime environment (Eclipse, etc.).
	 *
	 * @param application The application that this SSH session is connecting to
	 * @param session The SSH session that is connected to the application
	 */
	public ApplicationSSHSession(IApplication application, Session session) {
		this.application = application;
		this.session = session;
	}

	/**
	 * Set the current SSH session
	 * @param session A new SSH session to use for the ApplicationSSHSession object
	 */
	public void setSSHSession(final Session session) {this.session = session;}

	/**
	 * Get the application associated with this ApplicationSSHSession
	 * @return The application associated with this ssh session
	 */
	public IApplication getApplication() {return this.application;}

	/**
	 * Check if the current SSH session is connected
	 * @return True if the SSH session is connected
	 */
	public boolean isSSHSessionConnected() {
		return this.session.isConnected();
	}

	/**
	 * Check if port forwarding has been started
	 * @return true if port forwarding is started, false otherwise
	 * @throws OpenShiftSSHOperationException
	 */
	public boolean isPortFowardingStarted() throws OpenShiftSSHOperationException {
		try {
			return isSSHSessionConnected() && this.session.getPortForwardingL().length > 0;
		} catch (JSchException e) {
			throw new OpenShiftSSHOperationException(e,
					"Unable to verify if port-forwarding has been started for application \"{0}\"", application.getName());
		}
	}

	/**
	 * Start forwarding available ports to this application
	 * @return Current list of ports
	 * @throws OpenShiftSSHOperationException
	 */
	public List<IApplicationPortForwarding> startPortForwarding() throws OpenShiftSSHOperationException {
		if (!isSSHSessionConnected()) {
			throw new OpenShiftSSHOperationException(
					"SSH session for application ''{0}'' is closed. Cannot start port forwarding",
					application.getName());
		}
		for (IApplicationPortForwarding port : ports) {
			try {
				port.start(session);
			} catch (OpenShiftSSHOperationException oss) {
				/*
				 * ignore for now
				 * FIXME: should store this error on the forward to let user
				 * know why it could not start/stop
				 */
			}
		}
		return ports;
	}

	/**
	 * Stop forwarding of all ports to this application
	 * @return The current list of ports
	 * @throws OpenShiftSSHOperationException
	 */
	public List<IApplicationPortForwarding> stopPortForwarding() throws OpenShiftSSHOperationException {
		for (IApplicationPortForwarding port : ports) {
			try {
				port.stop(session);
			} catch (OpenShiftSSHOperationException oss) {
				/* ignore for now
				 *  should store this error on the forward to let user know why
				 *  it could not start/stop
				 */
			}
		}
		// make sure port forwarding is stopped by closing session...
		session.disconnect();
		return ports;
	}

	/**
	 * Refresh the list of forwardable ports for an application
	 * @return List of forwardable ports for your application
	 * @throws OpenShiftSSHOperationException
	 */
	public List<IApplicationPortForwarding> refreshForwardablePorts() throws OpenShiftSSHOperationException {
		this.ports = loadPorts();
		return getForwardablePorts();
	}

	/**
	 * Gets a list of forwardable ports for your application
	 * @return List of forwardable ports for your application
	 * @throws OpenShiftSSHOperationException
	 */
	public List<IApplicationPortForwarding> getForwardablePorts() throws OpenShiftSSHOperationException {
		if (ports == null) {
			this.ports = loadPorts();
		}
		return ports;
	}

	/**
	 * Get a list of properties from your OpenShift Application
	 * @return List of properties from your OpenShift application
	 * @throws OpenShiftSSHOperationException
	 */
	@Override
	public List<String> getEnvironmentProperties() throws OpenShiftSSHOperationException {
		List<String> openshiftProps = new ArrayList<String>();
		List<String> allEnvProps = sshExecCmd("set", SshStreams.INPUT);
		for (String line : allEnvProps) {
			openshiftProps.add(line);
		}
		return openshiftProps;
	}

	/**
	 * Extract the named forwardable port from the 'rhc-list-ports' command
	 * result line, with the following format:
	 * <code>java -> 127.10.187.1:4447</code>.
	 *
	 * @param portValue The raw port data to parse
	 * @return the forwardable port.
	 */
	private ApplicationPortForwarding extractForwardablePortFrom(final String portValue) {
		Matcher matcher = REGEX_FORWARDED_PORT.matcher(portValue);
		if (!matcher.find()
				|| matcher.groupCount() != 3) {
			return null;
		}
		try {
			final String name = matcher.group(1);
			final String host = matcher.group(2);
			final int remotePort = Integer.parseInt(matcher.group(3));
			return new ApplicationPortForwarding(application, name, host, remotePort);
		} catch(NumberFormatException e) {
			throw new OpenShiftSSHOperationException(e,
					"Couild not determine forwarded port in application {0}", application.getName());
		}
	}

	/**
	 * List all forwardable ports for a given application.
	 *
	 * @return the forwardable ports in an unmodifiable collection
	 * @throws OpenShiftSSHOperationException
	 */
	private List<IApplicationPortForwarding> loadPorts() throws OpenShiftSSHOperationException {
		this.ports = new ArrayList<IApplicationPortForwarding>();
		List<String> lines = sshExecCmd("rhc-list-ports", SshStreams.EXT_INPUT);
		for (String line : lines) {
			ApplicationPortForwarding port = extractForwardablePortFrom(line);
			if (port != null) {
				ports.add(port);
			}
		}
		return ports;
	}

	/**
	 * Refreshes the list of ports
	 * @throws OpenShiftException
	 */
	public void refresh() throws OpenShiftException {
		if (this.ports != null) {
			this.ports = loadPorts();
		}
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}
		if (object == null) {
			return false;
		}
		if (getClass() != object.getClass()) {
			return false;
		}
		ApplicationSSHSession other = (ApplicationSSHSession) object;
		ApplicationResource otherapp = (ApplicationResource) ((ApplicationSSHSession) object).getApplication();
		if (application.getUUID() == null) {
			if (otherapp.getUUID() != null) {
				return false;
			}
		} else if (!application.getUUID().equals(otherapp.getUUID())) {
			return false;
		} else if (this.isSSHSessionConnected() != other.isSSHSessionConnected()) {
			return false;
		} else if (isPortFowardingStarted() != other.isPortFowardingStarted()) {
			return false;
		} else if (this.application != otherapp) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "ApplicationSSHSession ["
				+ "applicationuuid=" + application.getUUID()
				+ ", applicationname=" + application.getName()
				+ ", isconnected=" + isSSHSessionConnected()
				+ ", isportforwardingstarted=" + isPortFowardingStarted()
				+ "]";
	}

	protected enum SshStreams {
		EXT_INPUT {
			protected InputStream getInputStream(Channel channel) throws IOException {
				return channel.getExtInputStream();
			}

		}, INPUT {
			protected InputStream getInputStream(Channel channel) throws IOException {
				return channel.getInputStream();
			}
		};

		public List<String> getLines(Channel channel) throws IOException {
			BufferedReader reader = new BufferedReader(new InputStreamReader(getInputStream(channel)));
			List<String> lines = new ArrayList<String>();
			String line = null;
			// Read File Line By Line
			while ((line = reader.readLine()) != null) {
				lines.add(line);
			}
			return lines;
		}
		protected abstract InputStream getInputStream(Channel channel) throws IOException;
	}

	/**
	 *
	 * @param command The remote command to run on the server
	 * @param sshStream The ssh stream to use
	 * @return The output of the command that is run on the server
	 * @throws OpenShiftSSHOperationException
	 */
	protected List<String> sshExecCmd(final String command, final SshStreams sshStream)
			throws OpenShiftSSHOperationException {
		final Session session = this.session;
		if (session == null) {
			throw new OpenShiftSSHOperationException(
					"No SSH session available for application ''{0}''.  Please supply an SSH session using ApplicationResource@setSSHSession.",
					application.getName());
		}
		Channel channel = null;
		try {
			session.openChannel("exec");
			channel = session.openChannel("exec");
			((ChannelExec) channel).setCommand(command);
			channel.connect();
			return sshStream.getLines(channel);
		} catch (JSchException e) {
			throw new OpenShiftSSHOperationException(e, "Failed to execute remote ssh command \"{0}\"",
					application.getName());
		} catch (IOException e) {
			throw new OpenShiftSSHOperationException(e, "Failed to execute remote ssh command \"{0}\"",
					application.getName());
		} finally {

			if (channel != null && channel.isConnected()) {
				channel.disconnect();
			}
		}
	}
}
