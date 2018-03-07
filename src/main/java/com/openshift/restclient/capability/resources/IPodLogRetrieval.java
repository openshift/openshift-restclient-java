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
package com.openshift.restclient.capability.resources;

import java.io.InputStream;

import com.openshift.restclient.capability.IBinaryCapability.OpenShiftBinaryOption;
import com.openshift.restclient.capability.ICapability;

/**
 * 
 * @author Jeff Cantrill
 *
 */
public interface IPodLogRetrieval extends ICapability {
	
	
	/**
	 * Return the logs from the pod, optionally following them.
	 * 
	 * @param follow
	 *            <code>true</code> if following. Default: <code>false</code>
	 * @param options
	 *            the options to pass to the underlying {@code oc} command
	 * @return the log output stream
	 */
	InputStream getLogs(boolean follow, OpenShiftBinaryOption... options);

	/**
	 * Return the logs from the pod, optionally following them
	 * 
	 * @param follow
	 *            true; if following, Default: false
	 * @param container
	 *            the name of the container in the pod to get logs uses the
	 *            first container if empty
	 * @param options
	 *            the options to pass to the underlying {@code oc} command
	 * @return the log output stream
	 */
	InputStream getLogs(boolean follow, String container, OpenShiftBinaryOption... options);
	
	/**
	 * Stop retrieving logs for all containers
	 */
	void stop();

	/**
	 * Stop retrieving logs for a specific container
	 * @param container the name of the container
	 */
	void stop(String container);

}
