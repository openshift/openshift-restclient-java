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

import com.openshift.restclient.OpenShiftException;
import com.openshift.restclient.capability.ICapability;
import com.openshift.restclient.capability.resources.LocationNotFoundException;

/**
 * Capability that wraps the OpenShift binary
 * 
 * @author jeff.cantrill
 *
 */
public abstract class AbstractOpenShiftBinaryCapability implements ICapability {
	
	private static final Logger LOG = LoggerFactory.getLogger(AbstractOpenShiftBinaryCapability.class);
	
	private Process process;

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
	 * @param location
	 * @return
	 */
	protected abstract String [] buildArgs(String location);
	

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
	
	public final void start() {
		String location = System.getProperty(OPENSHIFT_BINARY_LOCATION);
		if(StringUtils.isBlank(location)) {
			throw new LocationNotFoundException(String.format("The OpenShift 'oc' binary location was not specified. Set the property %s", OPENSHIFT_BINARY_LOCATION));
		}
		if(!validate()) {
			return;
		}
		startProcess(location);
		
	}

	private void startProcess(String location) {
		ProcessBuilder builder = new ProcessBuilder(buildArgs(location));
		LOG.debug("OpenShift binary args: {}", builder.command());
		try {
			process = builder.start();
			checkProcessIsAlive();
		} catch (IOException e) {
			LOG.error("Exception starting process", e);
			throw new OpenShiftException(e, "Does your OpenShift binary location exist? Error starting process: %s", e.getMessage());
		}
	}
	
	private void checkProcessIsAlive() throws IOException {
		try {
			Thread.sleep(1000);
			if(!process.isAlive() && process.exitValue() != 0) {
				throw new OpenShiftException("OpenShiftBinaryCapability process exited: %s", IOUtils.toString(process.getErrorStream()));
			}
		} catch (InterruptedException e) {
			if(!process.isAlive() && process.exitValue() != 0) {
				throw new OpenShiftException("OpenShiftBinaryCapability process exited: %s", IOUtils.toString(process.getErrorStream()));
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
	
}
