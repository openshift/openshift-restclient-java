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
import java.util.Map;
import java.util.function.Consumer;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.openshift.restclient.IClient;
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
	public InputStream getLogs(boolean follow) {
		return getLogs(follow, null);
	}

	@Override
	public InputStream getLogs(boolean follow, String container) {
		container = StringUtils.defaultIfBlank(container, "");
		synchronized (cache) {
			if(cache.containsKey(container)) {
				return cache.get(container).getLogs();
			}
			PodLogs logs = null;
			try {
				logs = new PodLogs(client, follow, container);
				return logs.getLogs();
			}catch(Exception e) {
				throw e;
			}finally {
				if(logs != null) {
					cache.put(container, logs);
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
		private Consumer<String> cacheFlush;
		private SequenceInputStream is;

		PodLogs(IClient client, boolean follow, String container){
			super(client);
			this.follow = follow;
			this.container = container;
		}

		public synchronized InputStream getLogs() {
			if(is == null) {
				start();
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
		protected String buildArgs() {
			StringBuilder args = new StringBuilder();
			args.append("logs ");
			addSkipTlsVerify(args);
			addServer(args)
			.append(" ").append(pod.getName()).append(" ")
			.append("-n ").append(pod.getNamespace()).append(" ");
			addToken(args);
			if(follow) {
				args.append(" -f ");
			}
			if(StringUtils.isNotBlank(container)) {
				args.append( " -c ").append(container);
			}
			return args.toString();
		}
	}
	
}
