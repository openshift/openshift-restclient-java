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
package com.openshift.restclient.apis.autoscaling.models;

import com.openshift.restclient.api.models.INameSetable;
import com.openshift.restclient.api.models.INamespaceSetable;
import com.openshift.restclient.api.models.IObjectMeta;
import com.openshift.restclient.api.models.ITypeMeta;

/**
 * Scale object payload to scalable resources
 * @author jeff.cantrill
 *
 */
public interface IScale extends ITypeMeta, IObjectMeta, INamespaceSetable, INameSetable {

	/**
	 * The number of desired replicas
	 * @return
	 */
	int getSpecReplicas();
	
	/**
	 * Set the number of desired replicas
	 * @param replicas
	 */
	void setSpecReplicas(int replicas);
}
