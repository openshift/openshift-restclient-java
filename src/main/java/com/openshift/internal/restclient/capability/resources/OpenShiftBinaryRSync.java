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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.SequenceInputStream;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.IOUtils;
import org.eclipse.jetty.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.openshift.restclient.IClient;
import com.openshift.restclient.OpenShiftException;
import com.openshift.restclient.capability.resources.IRSyncable;

/**
 * Port forwarding implementation that wraps the OpenShift binary
 * 
 * @author Andre Dietisheim
 *
 */
public class OpenShiftBinaryRSync extends AbstractOpenShiftBinaryCapability implements IRSyncable {
	
	private static final Logger LOG = LoggerFactory.getLogger(OpenShiftBinaryRSync.class);

	private static final long WAIT_FOR_EXIT_TIMEOUT = 5; // mins

	private Peer source;
	private Peer destination;

	private final Executor executor = Executors.newCachedThreadPool(); 
	
	/**
	 * Constructor.
	 * @param client the client to connect to OpenShift. 
	 */
	public OpenShiftBinaryRSync(final IClient client) {
		super(client);
	}

	@Override
	public InputStream sync(Peer source, Peer destination) throws OpenShiftException {
		this.source = source;
		this.destination = destination;
		start();
		// monitor the process completion in a separate thread
		this.executor.execute(() -> {
			try {
				this.getProcess().waitFor();
			} catch (Exception e) {
			}
			
		});
		final SequenceInputStream is = new SequenceInputStream(getProcess().getInputStream(), getProcess().getErrorStream());
		return is;
	}

	@Override
	public boolean isDone() {
		return !getProcess().isAlive();
	}
	
	@Override
	public int exitValue() {
		return getProcess().exitValue();
	}
	
	@Override
	public void await() throws InterruptedException {
		try {
			if (getProcess() == null) {
				throw new OpenShiftException("Could not sync %s to %s, no process was launched.", 
						destination);
			}
			if (!getProcess().waitFor(WAIT_FOR_EXIT_TIMEOUT, TimeUnit.MINUTES)) {
				throw new OpenShiftException("Syncing %s to %s did not terminate within %d minutes.", 
						source, destination, WAIT_FOR_EXIT_TIMEOUT);
			}
			
			if (getProcess().exitValue() != 0) {
				String errorMessage = getErrorMessage(getProcess().getErrorStream());
				throw new OpenShiftException("Syncing %s to %s failed"
						+ (StringUtil.isBlank(errorMessage) ? "" : ":%s"),
						source, destination, errorMessage);
			}
		} catch (InterruptedException e) {
			throw new OpenShiftException(e, "Syncing %s to %s was interrupted.",
					source, destination);
		}
	}
	
	private static String getErrorMessage(InputStream errorStream) {
		try {
			return IOUtils.toString(errorStream);
		} catch (IOException e) {
			LOG.error("Could not retrieve error message from process", e);
			return null;
		}
	}
		
	@Override
	protected void cleanup() {
		this.source = null;
		this.destination = null;
	}

	@Override
	protected boolean validate() {
		return source != null
				&& destination != null
				&& hasPodPeer(source, destination);
	}

	private static boolean hasPodPeer(Peer source, Peer destination) {
		return source.isPod()
				|| destination.isPod();
	}
	
	@Override
	public boolean isSupported() {
		return true;
	}

	@Override
	public String getName() {
		return OpenShiftBinaryRSync.class.getSimpleName();
	}
	
	@Override
	protected String buildArgs() {
		final StringBuilder argsBuilder = new StringBuilder("rsync ");
		argsBuilder.append(getUserFlag()).append(getTokenFlag()).append(getServerFlag()).append(getSkipTlsVerifyFlag())
				.append(getExclusionFlags()).append(getNoPermsFlags()).append(source.getParameter()).append(" ")
				.append(destination.getParameter());
		return argsBuilder.toString();
	}
	
}
