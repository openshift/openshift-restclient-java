/*******************************************************************************
 * Copyright (c) 2016 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package com.openshift.internal.restclient.model.kubeclient;

import java.util.HashMap;
import java.util.Map;
import com.openshift.restclient.model.kubeclient.ICluster;

public class Cluster implements ICluster {

	private static final String SERVER = "server";
	private static final String INSECURE_SKIP_TLS_VERIFY = "insecure-skip-tls-verify";
	private String name;
	private Map<String, Object> cluster = new HashMap<>();

	public void setCluster(Map<String, Object> cluster) {
		this.cluster.clear();
		this.cluster.putAll(cluster);
	}
	
	@Override
	public String getName() {
		return this.name;
	}
	
	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String getServer() {
		return (String) cluster.get(SERVER);
	}
	
	@Override
	public boolean isInsecureSkipTLSVerify() {
		if(cluster.containsKey(INSECURE_SKIP_TLS_VERIFY)) {
			return (Boolean) cluster.get(INSECURE_SKIP_TLS_VERIFY);
		}
		return false;
	}

}
