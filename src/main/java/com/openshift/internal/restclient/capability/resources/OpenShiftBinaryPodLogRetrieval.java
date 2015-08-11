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

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;

import com.openshift.restclient.IClient;
import com.openshift.restclient.capability.resources.IPodLogRetrieval;
import com.openshift.restclient.model.IPod;

public class OpenShiftBinaryPodLogRetrieval extends AbstractOpenShiftBinaryCapability implements IPodLogRetrieval {
	

	private IPod pod;
	private boolean follow;

	public OpenShiftBinaryPodLogRetrieval(IPod pod, IClient client) {
		super(client);
		this.pod = pod;
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
		this.follow = follow;
		start();
		SequenceInputStream is = new SequenceInputStream(getProcess().getInputStream(), getProcess().getErrorStream());
		return is;
	}

	@Override
	protected void cleanup() {
		follow = false;
		if(getProcess() != null) {
			IOUtils.closeQuietly(getProcess().getInputStream());
			IOUtils.closeQuietly(getProcess().getErrorStream());
		}
	}

	@Override
	protected boolean validate() {
		return true;
	}

	@Override
	protected String[] buildArgs(String location) {
		StringBuilder args = new StringBuilder(location);
		args.append(" logs ")
			.append("--insecure-skip-tls-verify=true ")
			.append("--server=").append(getClient().getBaseURL()).append(" ")
			.append(" ").append(pod.getName()).append(" ")
			.append("-n ").append(pod.getNamespace()).append(" ");
		addToken(args);
		if(follow) {
			args.append(" -f ");
		}
		return StringUtils.split(args.toString(), " ");
	}
	
}
