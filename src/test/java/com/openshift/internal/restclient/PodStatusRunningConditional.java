/******************************************************************************* 
 * Copyright (c) 2016 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/
package com.openshift.internal.restclient;

import com.openshift.internal.restclient.IntegrationTestHelper.ReadyConditional;
import com.openshift.restclient.model.IPod;
import com.openshift.restclient.model.IResource;

/**
 * Conditional to determin if a pod has acheived Running Status
 * @author jeff.cantrill
 *
 */
public class PodStatusRunningConditional implements ReadyConditional {

	@Override
	public boolean isReady(IResource resource) {
		if(resource == null) return false;
		if(!(resource instanceof IPod)) return false;
		IPod pod = (IPod) resource;
		return "Running".equals(pod.getStatus());
	}
}
