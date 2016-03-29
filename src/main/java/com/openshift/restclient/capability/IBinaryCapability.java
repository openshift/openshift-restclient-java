/*******************************************************************************
 * Copyright (c) 2015 Red Hat, Inc. Distributed under license by Red Hat, Inc.
 * All rights reserved. This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Red Hat, Inc.
 ******************************************************************************/
package com.openshift.restclient.capability;

/**
 * @author Andre Dietisheim
 */
public interface IBinaryCapability extends ICapability {
	
	/**
	 * Optional arguments to pass when running the {@code oc} command.
	 */
	public enum OpenShiftBinaryOption {
		/** option to skip verifying the certificates during TLS connection establishment. */
		SKIP_TLS_VERIFY,
		/** option to exclude the {@code .git} folder in the list of files/folders to synchronize. */
		EXCLUDE_GIT_FOLDER,
		/** option to not transfer file permissions. */
		NO_PERMS;
	}
	
	static final String OPENSHIFT_BINARY_LOCATION = "openshift.restclient.oc.location";

}
