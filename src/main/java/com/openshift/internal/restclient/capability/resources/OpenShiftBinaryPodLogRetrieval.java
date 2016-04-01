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

import java.io.InputStream;
import java.io.SequenceInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.openshift.restclient.IClient;
import com.openshift.restclient.capability.IBinaryCapability.OpenShiftBinaryOption;
import com.openshift.restclient.capability.resources.IPodLogRetrieval;
import com.openshift.restclient.model.IPod;

public class OpenShiftBinaryPodLogRetrieval implements IPodLogRetrieval {
	
	private static final Logger LOG = LoggerFactory.getLogger(IPodLogRetrieval.class);
	private IPod pod;
	private IClient client;
	private Map<String, PodLogs> cache = new HashMap<>();

	public OpenShiftBinaryPodLogRetrieval(IPod pod, IClient client) {
		this.pod = pod;
		this.client = client;
	}
	
	@Override
	public boolean isSupported() {
		return true;
	}

	@Override
	public String getName() {
		return OpenShiftBinaryPodLogRetrieval.class.getSimpleName();
	}
	
	@Override
	public InputStream getLogs(final boolean follow, final OpenShiftBinaryOption... options) {
		return getLogs(follow, null, options);
	}

	@Override
	public InputStream getLogs(final boolean follow, final String container, final OpenShiftBinaryOption... options) {
		final String normalizedContainer = StringUtils.defaultIfBlank(container, "");
		synchronized (cache) {
			if(cache.containsKey(normalizedContainer)) {
				return cache.get(normalizedContainer).getLogs();
			}
			PodLogs logs = null;
			try {
				logs = new PodLogs(client, follow, normalizedContainer, options);
				return logs.getLogs();
			}catch(Exception e) {
				throw e;
			}finally {
				if(logs != null) {
					cache.put(normalizedContainer, logs);
				}
			}
		}
	}
	
	@Override
	public void stop() {
		new ArrayList<>(cache.keySet()).forEach(c->stop(c));
	}

	@Override
	public synchronized void stop(String container) {
		if(!cache.containsKey(container)) return;
		try {
			PodLogs logs = cache.remove(container);
			logs.stop();
		}catch(Exception e) {
			LOG.warn("Unable to stop pod logs",e);
		}
	}


	private class PodLogs extends AbstractOpenShiftBinaryCapability{
		
		private String container;
		private boolean follow;
		private SequenceInputStream is;
		private OpenShiftBinaryOption[] options;

		PodLogs(IClient client, boolean follow, String container, OpenShiftBinaryOption... options){
			super(client);
			this.follow = follow;
			this.container = container;
			this.options = options;
		}

		public synchronized InputStream getLogs() {
			if(is == null) {
				start(options);
				is = new SequenceInputStream(getProcess().getInputStream(), getProcess().getErrorStream());
			}
			return is;
		}

		@Override
		public boolean isSupported() {
			return true;
		}

		@Override
		public String getName() {
			return "";
		}

		@Override
		protected void cleanup() {
			follow = false;
			if(getProcess() != null) {
				IOUtils.closeQuietly(getProcess().getInputStream());
				IOUtils.closeQuietly(getProcess().getErrorStream());
			}
			synchronized (cache) {
				cache.remove(this.container);
			}
		}

		@Override
		protected boolean validate() {
			return true;
		}

		@Override
		protected String buildArgs(final List<OpenShiftBinaryOption> options) {
			final StringBuilder argsBuilder = new StringBuilder();
			argsBuilder.append("logs ");
			if(options.contains(OpenShiftBinaryOption.SKIP_TLS_VERIFY)) {
				argsBuilder.append(getSkipTlsVerifyFlag());
			}
			argsBuilder.append(getServerFlag()).append(" ")
					.append(pod.getName()).append(" ").append("-n ").append(pod.getNamespace()).append(" ")
					.append(getTokenFlag());
			if(follow) {
				argsBuilder.append(" -f ");
			}
			if(StringUtils.isNotBlank(container)) {
				argsBuilder.append( " -c ").append(container);
			}
			return argsBuilder.toString();
		}
	}
	
}
