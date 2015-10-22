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
package com.openshift.restclient.model.build;


import com.openshift.restclient.model.IResource;

/**
 * Resource payload for triggering a build
 * @author jeff.cantrill
 *
 */
public interface IBuildRequest extends IResource {
	/**
	 * Set the commit level for the git clone extraction
	 * of the source code the build operates against
	 * @param commitId  the specific hexadecimal commit ID associated with a git log level
	 */
	void setCommitId(String commitId);
}
