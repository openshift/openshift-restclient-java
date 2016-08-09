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

public interface IContext {
	
	/**
	 * The name of the cluster
	 * @return
	 */
	String getCluster();

	/**
	 * Returns the user info in the form of:
	 * <username>/<url>
	 * @return the user info
	 */
	String getUser();
	
	/**
	 * default namespace to use
	 * on unspecified requests
	 * @return
	 */
	String getNamespace();

	/**
	 * The name of the context
	 * @return
	 */
	String getName();
	
}