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
package com.openshift.restclient.model.volume;

import java.util.Set;

import com.openshift.restclient.model.IResource;

/**
 * 
 * @author Jeff Cantrill
 *
 */
public interface IPersistentVolumeClaim extends IResource {

	/**
	 * 
	 * @return the {@link PVCAccessModes}
	 */
	Set<String> getAccessModes();

	/**
	 * Set the access modes for this claim.
	 *
	 * @param accessModes The access modes to set
     */
	void setAccessModes(Set<String> accessModes);

	/**
	 * The requested storage
	 * @return
	 */
	String getRequestedStorage();

	/**
	 * Set the requested storage of the claim
	 *
	 * @param requestedStorage The requested storage capacity
     */
	void setRequestedStorage(String requestedStorage);

	/**
	 * The status of the claim
	 * @return
	 */
	String getStatus();
}
