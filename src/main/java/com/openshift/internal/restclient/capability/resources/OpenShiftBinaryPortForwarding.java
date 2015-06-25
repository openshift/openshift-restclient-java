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

import java.io.IOException;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.openshift.restclient.IClient;
import com.openshift.restclient.OpenShiftException;
import com.openshift.restclient.authorization.BasicAuthorizationStrategy;
import com.openshift.restclient.authorization.IAuthorizationStrategyVisitor;
import com.openshift.restclient.authorization.KerbrosBrokerAuthorizationStrategy;
import com.openshift.restclient.authorization.TokenAuthorizationStrategy;
import com.openshift.restclient.capability.resources.IPortForwardable;
import com.openshift.restclient.capability.resources.LocationNotFoundException;
import com.openshift.restclient.model.IPod;

/**
 * Port forwarding implementation that wraps the OpenShift binary
 * 
 * @author jeff.cantrill
 *
 */
public class OpenShiftBinaryPortForwarding implements IPortForwardable {
	
	private static final Logger LOG = LoggerFactory.getLogger(IPortForwardable.class);
	
	private IClient client;
	private IPod pod;
	private PortPair[] pairs;
	private Process process;

	public OpenShiftBinaryPortForwarding(IPod pod, IClient client) {
		this.pod = pod;
		this.client = client;
		addShutdownHook();
	}
	
	private void addShutdownHook() {
		Runnable runnable = new Runnable() {
			@Override
			public void run() {
				stop();
			}
		};
		Runtime.getRuntime().addShutdownHook(new Thread(runnable));
	}
	
	@Override
	public boolean isForwarding() {
		return process != null && process.isAlive();
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
	public PortPair[] getPortPairs() {
		return pairs;
	}

	@Override
	public synchronized void forwardPorts(PortPair... ports) {
		stop();
		if(ports.length == 0) return;
		String location = System.getProperty(IPortForwardable.OPENSHIFT_BINARY_LOCATION);
		if(StringUtils.isBlank(location)) {
			throw new LocationNotFoundException(String.format("The OpenShift 'oc' binary location was not specified. Set the property %s", IPortForwardable.OPENSHIFT_BINARY_LOCATION));
		}
		startProcess(location, ports);
	}
	
	private void startProcess(String location, PortPair [] ports) {
		ProcessBuilder builder = new ProcessBuilder(buildArgs(location, ports));
		LOG.debug("PortForwarding args: {}", builder.command());
		try {
			process = builder.start();
			checkProcessIsAlive();
			this.pairs = ports;
		} catch (IOException e) {
			LOG.error("Exception starting process", e);
			throw new OpenShiftException(e, "Does your OpenShift binary location exist? Unable to foward ports to pod %s because of: %s", pod.getName(), e.getMessage());
		}
	}
	
	private void checkProcessIsAlive() throws IOException {
		Thread.yield();
		if(!process.isAlive() && process.exitValue() != 0) {
			throw new OpenShiftException("Port forwarding process exited: %s", IOUtils.toString(process.getErrorStream()));
		}
	}
	
	private String[] buildArgs(String location, PortPair [] pairs) {
		StringBuilder args = new StringBuilder(location);
		args.append(" port-forward ")
			.append("--insecure-skip-tls-verify=true ")
			.append("--server=").append(client.getBaseURL()).append(" ");
			addToken(args)
			.append("-n ").append(pod.getNamespace()).append(" ")
			.append("-p ").append(pod.getName()).append(" ");
		for (PortPair pair : pairs) {
			args.append(pair.getLocalPort()).append(":").append(pair.getRemotePort()).append(" ");
		}
		return StringUtils.split(args.toString(), " ");
	}
	
	private StringBuilder addToken(final StringBuilder builder) {
		builder.append("--token=");
		client.getAuthorizationStrategy().accept(new IAuthorizationStrategyVisitor() {
			
			@Override
			public void visit(TokenAuthorizationStrategy strategy) {
				builder.append(strategy.getToken());
			}
			
			@Override
			public void visit(KerbrosBrokerAuthorizationStrategy strategy) {
			}
			
			@Override
			public void visit(BasicAuthorizationStrategy strategy) {
				builder.append(strategy.getToken());
			}
		});
		builder.append(" ");
		return builder;
	}

	@Override
	public synchronized void stop() {
		if(process == null) return;
		this.pairs = null;
		if(!process.isAlive()) {
			LOG.debug("PortForward exit code {}", process.exitValue());
			try {
				LOG.debug("Process error stream", IOUtils.toString(process.getErrorStream()));
			} catch (IOException e) {
				LOG.debug("IOException trying to debug the process error stream", e);
			}
			process = null;
			return;
		}
		process.destroyForcibly();
	}
	
}
