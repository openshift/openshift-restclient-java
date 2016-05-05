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
package com.openshift.restclient.model.kubeclient;

import java.util.Collection;

/**
 * Configuration type for connecting to a Kubernetes client config
 * ref: client/unversioned/clientcmd/api/types.go
 * 
 * @author jeff.cantrill
 *
 */
public interface IKubeClientConfig {
	
	/**
	 * A map of userinfo to cluster info where the key
	 * is <namespace>/<url>/<username>
	 * @return
	 */
	Collection<ICluster> getClusters();

	Collection<IContext> getContexts();
	
	/**
	 * The name of the current cluster context
	 * @return
	 */
	String getCurrentContext();
	
	Collection<IUser> getUsers();
}
