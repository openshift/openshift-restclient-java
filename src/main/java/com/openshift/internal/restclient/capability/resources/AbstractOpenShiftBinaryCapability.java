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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.openshift.internal.util.StringSplitter;
import com.openshift.restclient.IClient;
import com.openshift.restclient.OpenShiftContext;
import com.openshift.restclient.OpenShiftException;
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
	
	
	private static final boolean IS_MAC = StringUtils.isNotEmpty(System.getProperty("os.name"))
			&& System.getProperty("os.name").toLowerCase().contains("mac");

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
	 * Callback for building args to be sent to the {@code oc} command.
	 * @return the String representation of all the arguments to use when running the {@code oc} command. 
	 */
	protected abstract String buildArgs(final List<OpenShiftBinaryOption> options);

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
	
	protected String getUserFlag() {
		final StringBuilder argBuilder = new StringBuilder();
		argBuilder.append("--user=").append(client.getAuthorizationContext().getUserName()).append(" ");
		return argBuilder.toString();
	}

	/**
	 * @return
	 */
	protected String getServerFlag() {
		final StringBuilder argBuilder = new StringBuilder();
		argBuilder.append("--server=").append(client.getBaseURL()).append(" ");
		return argBuilder.toString();
	}

	/**
	 * Adds the authentication token
	 * @return the command-line argument to use the current token 
	 */
	protected String getTokenFlag() {
		return new StringBuilder("--token=")
			.append(client.getAuthorizationContext().getToken())
			.append(" ").toString();
	}
	
	/**
	 * @return the command-line flag to use insecure connection (skip TLS verification)
	 */
	protected String getSkipTlsVerifyFlag() {
		return "--insecure-skip-tls-verify=true ";
	}
	
	/**
	 * Starts the {@link Process} to run the {@code oc} command.
	 * @param options the command line options
	 */
	public final void start(final OpenShiftBinaryOption... options) {
		String location = getOpenShiftBinaryLocation();
		if(!validate()) {
			return;
		}
		ProcessBuilder processBuilder = initProcessBuilder(location, options);
		startProcess(processBuilder);
	}
	
	private void startProcess(ProcessBuilder builder) {
		try {
			process = builder.start();
			checkProcessIsAlive();
		} catch (IOException e) {
			LOG.error("Could not start process for {}.", new Object[]{ getName(), e });
			throw new OpenShiftException(e, "Does your OpenShift binary location exist? Error starting process: %s", 
					e.getMessage());
		}
	}
	
	private ProcessBuilder initProcessBuilder(String location, final OpenShiftBinaryOption... options) {
		List<String> args = new ArrayList<String>();
		ProcessBuilder builder = null;
		// the condition is made in order to solve mac problem 
		// with launching binaries containing spaces in its path
		// https://issues.jboss.org/browse/JBIDE-23862 - see the latest comments
		if (IS_MAC) {
			args.add(location);
			StringSplitter.split(buildArgs(Arrays.asList(options)), args);
			builder = new ProcessBuilder(args);
		} else {
			File oc = new File(location);
			args.add(location);
			StringSplitter.split(buildArgs(Arrays.asList(options)), args);
			builder = new ProcessBuilder(args);
			builder.directory(oc.getParentFile());
		}		
		builder.environment().remove("KUBECONFIG");
		LOG.debug("OpenShift binary args: {}", builder.command());
		return builder;
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

	/**
	 * Stops the {@link Process} running the {@code oc} command.
	 */
	public final synchronized void stop() {
		if(process == null) return;
		cleanup();
		if(!process.isAlive()) {
			final int exitValue = process.exitValue();
			LOG.debug("OpenShiftBinaryCapability process exit code {}", exitValue);
			if(exitValue != 0) {
				try {
					LOG.debug("OpenShiftBinaryCapability process error stream", IOUtils.toString(process.getErrorStream()));
				} catch (IOException e) {
					LOG.debug("IOException trying to debug the process error stream", e);
				}
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
