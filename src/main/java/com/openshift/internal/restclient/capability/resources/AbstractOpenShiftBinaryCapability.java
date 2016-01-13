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
import com.openshift.restclient.OpenShiftContext;
import com.openshift.restclient.OpenShiftException;
import com.openshift.restclient.authorization.BasicAuthorizationStrategy;
import com.openshift.restclient.authorization.IAuthorizationStrategyVisitor;
import com.openshift.restclient.authorization.TokenAuthorizationStrategy;
import com.openshift.restclient.capability.IBinaryCapability;
import com.openshift.restclient.capability.resources.LocationNotFoundException;

/**
 * Capability that wraps the OpenShift binary
 * 
 * @author Jeff Cantrill
 *
 */
public abstract class AbstractOpenShiftBinaryCapability implements IBinaryCapability {
	
	private static final Logger LOG = LoggerFactory.getLogger(AbstractOpenShiftBinaryCapability.class);

	private Process process;

	private IClient client;
	
	protected AbstractOpenShiftBinaryCapability(IClient client) {
		this.client = client;
	}

	/**
	 * Cleanup required when stopping the process
	 */
	protected abstract void cleanup();

	/**
	 * Validate arguments before starting process
	 * @return true if start should continue; false otherwise;
	 */
	protected abstract boolean validate();
	
	/**
	 * Callback for building args to be sent to the oc command
	 * @return
	 */
	protected abstract String buildArgs();

	protected IClient getClient() {
		return client;
	}

	protected AbstractOpenShiftBinaryCapability() {
		addShutdownHook();
	}

	protected Process getProcess() {
		return process;
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
	
	protected StringBuilder addUser(final StringBuilder builder) {
		builder.append("--user=")
				.append(client.getCurrentUser().getName())
				.append(" ");
		return builder;
	}

	protected StringBuilder addServer(final StringBuilder builder) {
		builder.append("--server=")
				.append(client.getBaseURL())
				.append(" ");
		return builder;
	}

	protected StringBuilder addToken(final StringBuilder builder) {
		builder.append("--token=");
		client.getAuthorizationStrategy().accept(new IAuthorizationStrategyVisitor() {
			
			@Override
			public void visit(TokenAuthorizationStrategy strategy) {
				builder.append(strategy.getToken());
			}
			
			@Override
			public void visit(BasicAuthorizationStrategy strategy) {
				builder.append(strategy.getToken());
			}
		});
		builder.append(" ");
		return builder;
	}
	
	protected StringBuilder addSkipTlsVerify(StringBuilder args) {
		return args.append("--insecure-skip-tls-verify=true ");
	}

	public final void start() {
		String location = getOpenShiftBinaryLocation();
		if(!validate()) {
			return;
		}
		startProcess(location);
	}
	
	private void startProcess(String location) {
		String cmdLine = new StringBuilder(location).append(' ').append(buildArgs()).toString();
		String[] args = StringUtils.split(cmdLine, " ");
		ProcessBuilder builder = new ProcessBuilder(args);
		LOG.debug("OpenShift binary args: {}", builder.command());
		try {
			process = builder.start();
			checkProcessIsAlive();
		} catch (IOException e) {
			LOG.error("Could not start process for {}.", new Object[]{ getName(), e });
			throw new OpenShiftException(e, "Does your OpenShift binary location exist? Error starting process: %s", 
					e.getMessage());
		}
	}
	
	private void checkProcessIsAlive() throws IOException {
		try {
			// TODO: replace fixed wait with wait for process to be running
			Thread.sleep(1000);
			if(!process.isAlive() && process.exitValue() != 0) {
				throw new OpenShiftException("OpenShiftBinaryCapability process exited: %s", 
						IOUtils.toString(process.getErrorStream()));
			}
		} catch (InterruptedException e) {
			if(!process.isAlive() && process.exitValue() != 0) {
				throw new OpenShiftException("OpenShiftBinaryCapability process exited: %s", 
						IOUtils.toString(process.getErrorStream()));
			}
		}
	}

	public final synchronized void stop() {
		if(process == null) return;
		cleanup();
		if(!process.isAlive()) {
			LOG.debug("OpenShiftBinaryCapability process exit code {}", process.exitValue());
			try {
				LOG.debug("OpenShiftBinaryCapability process error stream", IOUtils.toString(process.getErrorStream()));
			} catch (IOException e) {
				LOG.debug("IOException trying to debug the process error stream", e);
			}
			process = null;
			return;
		}
		process.destroyForcibly();
	}

	protected String getOpenShiftBinaryLocation() {
		//Check the ThreadLocal for oc binary
		String location = OpenShiftContext.get().get(OPENSHIFT_BINARY_LOCATION);
		if (StringUtils.isBlank(location)) {
			//Fall back to System property
			location = System.getProperty(OPENSHIFT_BINARY_LOCATION);
		}
		if(StringUtils.isBlank(location)) {
			throw new LocationNotFoundException(
					String.format("The OpenShift 'oc' binary location was not specified. Set the property %s", 
							OPENSHIFT_BINARY_LOCATION));
		}
		return location;
	}
}
