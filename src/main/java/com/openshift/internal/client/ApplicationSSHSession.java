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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.openshift.client.IApplication;
import com.openshift.client.IApplicationPortForwarding;
import com.openshift.client.IApplicationSSHSession;
import com.openshift.client.OpenShiftException;
import com.openshift.client.OpenShiftSSHOperationException;
import com.openshift.client.utils.TarFileUtils;
import com.openshift.internal.client.ssh.ApplicationPortForwarding;
import com.openshift.internal.client.utils.StreamUtils;

/**
 * @author Xavier Coulon
 * @author André Dietisheim
 * @author Corey Daley
 */
public class ApplicationSSHSession implements IApplicationSSHSession {

	private static final Logger LOGGER = LoggerFactory.getLogger(ApplicationSSHSession.class);

	private static final int CONNECT_TIMEOUT = 10 * 60 * 1000;
	private static final String JSCH_EXEC_CHANNEL = "exec";

	/** SSH Session to use for all methods */
	private Session session;

	/** Application that is associated with this SSH Session */
	private IApplication application;

	/** List of ports available for port forwarding */
	private List<IApplicationPortForwarding> ports = null;

	/**
	 * Sets the SSH session that this application will use to connect to
	 * OpenShift to perform some operations. This SSH session must be
	 * initialized out of the library, since the user's SSH settings may depend
	 * on the runtime environment (Eclipse, etc.).
	 *
	 * @param application
	 *            The application that this SSH session is connecting to
	 * @param session
	 *            The SSH session that is connected to the application
	 */
	public ApplicationSSHSession(IApplication application, Session session) {
		this.application = application;
		this.session = session;
	}

	/**
	 * Set the current SSH session
	 * 
	 * @param session
	 *            A new SSH session to use for the ApplicationSSHSession object
	 */
	public void setSSHSession(final Session session) {
		this.session = session;
	}

	/**
	 * Get the application associated with this ApplicationSSHSession
	 * 
	 * @return The application associated with this ssh session
	 */
	public IApplication getApplication() {
		return this.application;
	}

	/**
	 * Check if the current SSH session is connected
	 * 
	 * @return True if the SSH session is connected
	 */
	public boolean isConnected() {
		return this.session.isConnected();
	}

	/**
	 * Check if port forwarding has been started
	 * 
	 * @return true if port forwarding is started, false otherwise
	 * @throws OpenShiftSSHOperationException
	 */
	public boolean isPortFowardingStarted() throws OpenShiftSSHOperationException {
		try {
			return isConnected()
					&& session.getPortForwardingL().length > 0;
		} catch (JSchException e) {
			throw new OpenShiftSSHOperationException(e,
					"Unable to verify if port-forwarding has been started for application \"{0}\"",
					application.getName());
		}
	}

	/**
	 * Start forwarding available ports to this application
	 * 
	 * @return Current list of ports
	 * @throws OpenShiftSSHOperationException
	 */
	public List<IApplicationPortForwarding> startPortForwarding() throws OpenShiftSSHOperationException {
		assertLiveSSHSession();

		for (IApplicationPortForwarding port : ports) {
			try {
				port.start(session);
			} catch (OpenShiftSSHOperationException oss) {
				/*
				 * ignore for now FIXME: should store this error on the forward
				 * to let user know why it could not start/stop
				 */
			}
		}
		return ports;
	}

	/**
	 * Stop forwarding of all ports to this application
	 * 
	 * @return The current list of ports
	 * @throws OpenShiftSSHOperationException
	 */
	public List<IApplicationPortForwarding> stopPortForwarding() throws OpenShiftSSHOperationException {
		assertLiveSSHSession();
		for (IApplicationPortForwarding port : ports) {
			try {
				port.stop(session);
			} catch (OpenShiftSSHOperationException oss) {
				/*
				 * ignore for now should store this error on the forward to let
				 * user know why it could not start/stop
				 */
			}
		}
		// make sure port forwarding is stopped by closing session...
		session.disconnect();
		return ports;
	}

	/**
	 * Refresh the list of forwardable ports for an application
	 * 
	 * @return List of forwardable ports for your application
	 * @throws OpenShiftSSHOperationException
	 */
	public List<IApplicationPortForwarding> refreshForwardablePorts() throws OpenShiftSSHOperationException {
		assertLiveSSHSession();
		this.ports = loadPorts();
		return getForwardablePorts();
	}

	/**
	 * Gets a list of forwardable ports for your application
	 * 
	 * @return List of forwardable ports for your application
	 * @throws OpenShiftSSHOperationException
	 */
	public List<IApplicationPortForwarding> getForwardablePorts() throws OpenShiftSSHOperationException {
		assertLiveSSHSession();
		if (ports == null) {
			this.ports = loadPorts();
		}
		return ports;
	}

	/**
	 * Get a list of properties from your OpenShift Application
	 * 
	 * @return List of properties from your OpenShift application
	 * @throws OpenShiftSSHOperationException
	 */
	@Override
	public List<String> getEnvironmentProperties() throws OpenShiftSSHOperationException {
		assertLiveSSHSession();
		List<String> openshiftProps = new ArrayList<String>();
		InputStream in = execCommand("set", ChannelInputStreams.DATA, session);
		try {
			for (String line : new SshCommandResponse(in).getLines()) {
				openshiftProps.add(line);
			}
			return openshiftProps;
		} catch (IOException e) {
			throw new OpenShiftSSHOperationException(e,
					"Could not execute \"set\" command via ssh on application {0}", application.getName());
		}
	}

	public InputStream saveFullSnapshot() {
		assertLiveSSHSession();

		return new FullSnapshotCommand(session).save();
	}

	public InputStream restoreFullSnapshot(InputStream inputStream) {
		return restoreFullSnapshot(inputStream, true);
	}

	/**
	 * Restores the given full snapshot to the application that this session is
	 * bound to. Providing <code>true</code> for includeGit will also activate
	 * the snapshot (and having the page reflecting the changes in the
	 * snapshot). It only works though if the snapshot has a /git/ folder with
	 * content.
	 * 
	 * @param inputStream
	 *            the snapshot
	 * @param includeGit
	 *            will activate the new snapshot given the snapshot includes a
	 *            /git folder
	 * @return
	 * 
	 * @see TarFileUtils#hasGitFolder(InputStream)
	 * @see #saveFullSnapshot()
	 */
	public InputStream restoreFullSnapshot(InputStream inputStream, boolean includeGit) {
		assertLiveSSHSession();

		return new FullSnapshotCommand(session).restore(inputStream, includeGit);
	}

	public InputStream saveDeploymentSnapshot() {
		assertLiveSSHSession();

		return new DeploymentSnapshotCommand(session).save();
	}

	/**
	 * Restores the given snapshot to the application that this session is bound
	 * to.
	 * 
	 * @param inputStream
	 *            the snapshot
	 * @param hotDeploy
	 *            will not restart the application if <code>true</code>
	 * @return
	 * @throws OpenShiftException
	 * 
	 * @see #saveDeploymentSnapshot()
	 */
	public InputStream restoreDeploymentSnapshot(InputStream inputStream, boolean hotDeploy)
			throws OpenShiftException {
		return new DeploymentSnapshotCommand(session).restore(inputStream, hotDeploy);
	}

	/**
	 * List all forwardable ports for a given application. saveSnapshot
	 * 
	 * @return the forwardable ports in an unmodifiable collection
	 * @throws OpenShiftSSHOperationException
	 */
	private List<IApplicationPortForwarding> loadPorts() throws OpenShiftSSHOperationException {
		assertLiveSSHSession();
		this.ports = new ArrayList<IApplicationPortForwarding>();
		InputStream in = execCommand("rhc-list-ports", ChannelInputStreams.EXTENDED_DATA, session);
		try {
			return this.ports =
					new RhcListPortsCommandResponse(application, in).getPortForwardings();
		} catch (IOException e) {
			throw new OpenShiftSSHOperationException("Could not execute \"rhc-list-ports\" via ssh in application {0}",
					application.getName());
		} finally {
			try {
				StreamUtils.close(in);
			} catch (IOException e) {
				LOGGER.error("Could not close channel to ssh server", e);
			}
		}

	}

	/**
	 * Refreshes the list of forwardable ports
	 * 
	 * @throws OpenShiftException
	 */
	public void refresh() throws OpenShiftException {
		if (this.ports != null) {
			this.ports = loadPorts();
		}
	}

	@Override
	public boolean equals(Object object) {
		if (object == null) {
			return false;
		} else if (getClass() != object.getClass()) {
			return false;
		} else if (object == this) {
			return true;
		}

		ApplicationSSHSession other = (ApplicationSSHSession) object;
		ApplicationResource otherapp = (ApplicationResource) ((ApplicationSSHSession) object).getApplication();
		if (application.getUUID() == null) {
			if (otherapp.getUUID() != null) {
				return false;
			}
		} else if (!application.getUUID().equals(otherapp.getUUID())) {
			return false;
		} else if (isConnected() != other.isConnected()) {
			return false;
		} else if (isPortFowardingStarted() != other.isPortFowardingStarted()) {
			return false;
		} else if (!application.equals(otherapp)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "ApplicationSSHSession ["
				+ "applicationuuid=" + application.getUUID()
				+ ", applicationname=" + application.getName()
				+ ", isconnected=" + isConnected()
				+ ", isportforwardingstarted=" + isPortFowardingStarted()
				+ "]";
	}

	protected void assertLiveSSHSession() {
		if (!isConnected()) {
			throw new OpenShiftSSHOperationException(
					"SSH session for application \"{0}\" is closed.",
					application.getName());
		}
	}

	protected InputStream execCommand(final String command, ChannelInputStreams factory, Session session)
			throws OpenShiftSSHOperationException {
		return execCommand(command, null, factory, session);
	}

	/**
	 *
	 * @param command
	 *            The remote command to run on the server
	 * @param sshMsgChannelData
	 * @param sshStream
	 *            The ssh stream to use
	 * @return The output of the command that is run on the server
	 * @throws OpenShiftSSHOperationException
	 */
	protected InputStream execCommand(final String command, InputStream forStdIn,
			ChannelInputStreams channelInputStream, Session session) throws OpenShiftSSHOperationException {
		assertLiveSSHSession();

		ChannelExec channel = null;
		try {
			channel = (ChannelExec) session.openChannel(JSCH_EXEC_CHANNEL);
			((ChannelExec) channel).setCommand(command);
			final OutputStream remoteStdIn = channel.getOutputStream();

			InputStream in = channel.getInputStream();
			ChannelResponse channelResponse = new ChannelResponse(in, channel);
			channel.connect(CONNECT_TIMEOUT);
			if (forStdIn != null) {
				writeToRemoteStdInput(forStdIn, remoteStdIn);
			}
			return channelResponse;
		} catch (JSchException e) {
			channel.disconnect();
			throw new OpenShiftSSHOperationException(e,
					"Could no execute remote ssh command \"{0}\" on application {1}",
					command, application.getName());
		} catch (IOException e) {
			channel.disconnect();
			throw new OpenShiftSSHOperationException(e,
					"Could not get response channel for remote ssh command \"{0}\" on application {1}",
					command, application.getName());
		}
	}

	private void writeToRemoteStdInput(InputStream forStdInput, OutputStream remoteStdIn) throws IOException {
		for (int data = -1; (data = forStdInput.read()) != -1;) {
			remoteStdIn.write(data);
		}
		remoteStdIn.close();
		forStdInput.close();
	}

	public abstract class AbstractSnapshotType {

		private String saveCommand;
		private String restoreCommand;

		private AbstractSnapshotType(String saveCommand, String restoreCommand) {
			this.saveCommand = saveCommand;
			this.restoreCommand = restoreCommand;
		}

		public String getSaveCommand() {
			return saveCommand;
		}

		public String getRestoreCommand() {
			return restoreCommand;
		}
	}

	protected abstract class AbstractSnapshotSshCommand {

		protected Session session;

		AbstractSnapshotSshCommand(Session session) {
			this.session = session;
		}

	}

	class FullSnapshotCommand extends AbstractSnapshotSshCommand {

		FullSnapshotCommand(Session session) {
			super(session);
		}

		public InputStream save() {
			/* rhc snapshot save -a <application> */
			return execCommand(
					"snapshot", ChannelInputStreams.DATA, session);
		}

		public InputStream restore(InputStream in, boolean includeGit) {
			return execCommand(
					MessageFormat.format("restore{0}", includeGit ? " INCLUDE_GIT" : ""),
					in,
					ChannelInputStreams.DATA,
					session);
		}
	}

	class DeploymentSnapshotCommand extends AbstractSnapshotSshCommand {

		DeploymentSnapshotCommand(Session session) {
			super(session);
		}

		public InputStream save() {
			/* rhc snapshot save -a <application> */
			return execCommand(
					"gear archive-deployment", ChannelInputStreams.DATA, session);
		}

		public InputStream restore(InputStream inputStream, boolean hotDeploy) {
			return execCommand(
					MessageFormat.format("oo-binary-deploy{0}", hotDeploy ? " --hot-deploy" : ""),
					inputStream,
					ChannelInputStreams.DATA,
					session);
		}
	}

	protected static class SshCommandResponse {

		private InputStream inputStream;

		SshCommandResponse(InputStream inputStream) {
			this.inputStream = inputStream;
		}

		public List<String> getLines() throws IOException {
			BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
			List<String> lines = new ArrayList<String>();
			String line = null;
			while ((line = reader.readLine()) != null) {
				lines.add(line);
			}
			return lines;
		}
	}

	private static class RhcListPortsCommandResponse extends SshCommandResponse {

		/** Regex for port forwarding */
		private static final Pattern REGEX_FORWARDED_PORT = Pattern.compile("([^ ]+) -> ([^:]+):(\\d+)");

		private IApplication application;

		RhcListPortsCommandResponse(IApplication application, InputStream inputStream) {
			super(inputStream);
			this.application = application;
		}

		public List<IApplicationPortForwarding> getPortForwardings() throws IOException {
			List<IApplicationPortForwarding> ports = new ArrayList<IApplicationPortForwarding>();
			for (String line : getLines()) {
				ApplicationPortForwarding port = extractForwardablePortFrom(line);
				if (port != null) {
					ports.add(port);
				}
			}
			return ports;
		}

		/**
		 * Extracts the named forwardable port from the 'rhc-list-ports' command
		 * result line, with the following format:
		 * <code>java -> 127.10.187.1:4447</code>.
		 *
		 * @param rhcListPortsOutput
		 *            The raw port data to parse
		 * @return the forwardable port.
		 */
		private ApplicationPortForwarding extractForwardablePortFrom(final String rhcListPortsOutput) {
			Matcher matcher = REGEX_FORWARDED_PORT.matcher(rhcListPortsOutput);
			if (!matcher.find()
					|| matcher.groupCount() != 3) {
				return null;
			}
			try {
				final String name = matcher.group(1);
				final String host = matcher.group(2);
				final int remotePort = Integer.parseInt(matcher.group(3));
				return new ApplicationPortForwarding(application, name, host, remotePort);
			} catch (NumberFormatException e) {
				throw new OpenShiftSSHOperationException(e,
						"Couild not determine forwarded port in application {0}", application.getName());
			}
		}
	}

	enum ChannelInputStreams {
		DATA {

			@Override
			public InputStream get(Channel channel) throws IOException, JSchException {
				return channel.getInputStream();
			}

		},
		EXTENDED_DATA {
			@Override
			public InputStream get(Channel channel) throws IOException, JSchException {
				return channel.getExtInputStream();
			}
		};

		public abstract InputStream get(Channel channel) throws IOException, JSchException;
	}

	class ChannelResponse extends InputStream {

		/** the delay to wait for further data from the remote **/
		private static final int WAIT_DELAY = 1000;

		private ChannelExec channel;
		private InputStream channelInputStream;
		private InputStream channelErrorStream;

		protected ChannelResponse(InputStream response, ChannelExec channel)
				throws IOException, JSchException {
			this.channel = channel;
			// ATTENTION: stream must be get before connecting
			this.channelInputStream = response;
			this.channelErrorStream = channel.getErrStream();
		}

		@Override
		public int read() throws IOException {
			if (channel.isClosed()
					&& channel.getExitStatus() != 0) {
				throw new IOException(StreamUtils.readToString(channelErrorStream));
			}
			while (!(channel.isClosed()
			&& channelInputStream.available() == 0)) {
				if (channelInputStream.available() > 0) {
					int data = channelInputStream.read();
					if (data == -1) {
						continue;
					}
					return data;
				}
				try {
					Thread.sleep(WAIT_DELAY);
				} catch (InterruptedException e) {
					break;
				}
			}
			return -1;
		}

		@Override
		public void close() throws IOException {
			channel.disconnect();
			channelInputStream.close();
		}

		@Override
		public int available() throws IOException {
			return channelInputStream.available();
		}
	}

}
