/*******************************************************************************
 * Copyright (c) 2015 Red Hat, Inc.
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
 *
 */
public class OpenShiftBinaryPortForwarding extends AbstractOpenShiftBinaryCapability implements IPortForwardable {
	
	private final IPod pod;
	private final Collection<PortPair> pairs = new ArrayList<>();

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
		if (ports != null && !ports.isEmpty()) {
			this.pairs.addAll(ports);
		} else {
			throw new OpenShiftException("Port-forwarding was invoked but not port was specified.");
		}
		start(options);
	}

	@Override
	protected String buildArgs(final List<OpenShiftBinaryOption> options) {
		final StringBuilder argBuilder = new StringBuilder();
		argBuilder.append("port-forward ");
		if(options.contains(OpenShiftBinaryOption.SKIP_TLS_VERIFY)) {
			argBuilder.append(getSkipTlsVerifyFlag());
		}
		argBuilder.append(getServerFlag()).append(getTokenFlag()).append("-n ").append(pod.getNamespace()).append(" ")
				.append("-p ").append(pod.getName()).append(" ");
		for (PortPair pair : pairs) {
			argBuilder.append(pair.getLocalPort()).append(":").append(pair.getRemotePort()).append(" ");
		}
		return argBuilder.toString();
	}
	
}
