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
 * OpenShift object
 * @author Jeff Cantrill
 *
 */
public interface IObjectReference {

	/**
	 * Returns the resource kind
	 * @return
	 */
	String getKind();
	
	/**
	 * The obj ref kind
	 * @param kind
	 */
	void setKind(String kind);

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
	 * The name of the obj ref
	 * @param name
	 */
	void setName(String name);

	/**
	 * Returns the scope of this resource
	 * @return
	 */
	String getNamespace();
	
	/**
	 * The namespace for the object ref
	 * @param namespace
	 */
	void setNamespace(String namespace);

	String getFieldPath();

	String getUID();

	String toJson();
}
