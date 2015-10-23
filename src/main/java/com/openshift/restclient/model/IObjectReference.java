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
package com.openshift.restclient.model;

/**
 * An OpenShift object reference to an
 * Openshift object
 * @author jeff.cantrill
 *
 */
public interface IObjectReference {

	/**
	 * Returns the resource kind
	 * @return
	 */
	String getKind();
	
	/**
	 * returns the api version of this resource
	 * @return
	 */
	String getApiVersion();

	/**
	 * returns the resource version of this resource
	 * @return
	 */
	String getResourceVersion();
	
	/**
	 * Returns the identifier for this resource
	 * @return
	 */
	String getName();
	
	/**
	 * Returns the scope of this resource
	 * @return
	 */
	String getNamespace();
	
	String getFieldPath();
	
	String getUID();
	
	String toJson();
}
