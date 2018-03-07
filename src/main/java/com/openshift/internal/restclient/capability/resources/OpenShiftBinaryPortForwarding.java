/*******************************************************************************
 * Copyright (c) 2015-2018 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package com.openshift.internal.restclient.capability.resources;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.openshift.restclient.IClient;
import com.openshift.restclient.OpenShiftException;
import com.openshift.restclient.capability.resources.IPortForwardable;
import com.openshift.restclient.model.IPod;

/**
 * Port forwarding implementation that wraps the OpenShift binary
 * 
 * @author Jeff Cantrill
 * @author Andre Dietisheim
 *
 */
public class OpenShiftBinaryPortForwarding extends AbstractOpenShiftBinaryCapability implements IPortForwardable {
	
	public static final String PORT_FORWARD_COMMAND = "port-forward";
	private final IPod pod;
	private final Collection<PortPair> pairs = new ArrayList<>();

	static class PodName implements OpenShiftBinaryOption {

		private IPod pod;

		public PodName(IPod pod) {
			this.pod = pod;
		}

		@Override
		public void append(StringBuilder commandLine) {
			commandLine.append(" -p ").append(pod.getName());
		}
	}

	static class PortPairs implements OpenShiftBinaryOption {

		private Collection<PortPair> pairs;

		public PortPairs(Collection<PortPair> pairs) {
			this.pairs = pairs;
		}

		@Override
		public void append(StringBuilder commandLine) {
			if (pairs == null) {
				return;
			}
			for (PortPair pair : pairs) {
				append(pair, commandLine);
			}
		}

		protected void append(PortPair pair, StringBuilder commandLine) {
			commandLine
				.append(" ")
				.append(pair.getLocalPort())
				.append(":")
				.append(pair.getRemotePort()).append(" ");
		}
	}

	public OpenShiftBinaryPortForwarding(IPod pod, IClient client) {
		super(client);
		this.pod = pod;
	}

	@Override
	protected void cleanup() {
		this.pairs.clear();
	}

	@Override
	protected boolean validate() {
		return !pairs.isEmpty();
	}

	@Override
	public boolean isForwarding() {
		return getProcess() != null && getProcess().isAlive();
	}

	@Override
	public boolean isSupported() {
		return true;
	}

	@Override
	public String getName() {
		return OpenShiftBinaryPortForwarding.class.getSimpleName();
	}

	@Override
	public Collection<PortPair> getPortPairs() {
		return pairs;
	}

	@Override
	public synchronized void forwardPorts(final Collection<PortPair> ports, final OpenShiftBinaryOption... options) {
		if (ports == null || ports.isEmpty()) {
			throw new OpenShiftException("Port-forwarding was invoked but no ports were specified.");
		}

		this.pairs.addAll(ports);
		start(options);
	}

	@Override
	protected String buildArgs(final List<OpenShiftBinaryOption> options) {
		return new CommandLineBuilder(PORT_FORWARD_COMMAND)
				.append(	new Token(getClient()))
				.append(new Server(getClient()))
				.append(options)
				.append(new Namespace(pod))
				.append(new PodName(pod))
				.append(new PortPairs(pairs))
				.build();
	}
	
}
