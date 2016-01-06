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
package com.openshift.restclient.model;

import java.util.Map;

public interface Annotatable {
	
	/**
	 * Returns <code>true</code> if the resource is annotated with
	 * the given key
	 * @param key
	 * @return true if the annotation key exists
	 */
	boolean isAnnotatedWith(String key);
	
	/**
	 * Retrieves the annotated value for the given key
	 * @param key
	 * @return
	 */
	String getAnnotation(String key);
	
	/**
	 * Set the resource annotation
	 * @param key
	 * @param value
	 */
	void setAnnotation(String key, String value);
	
	/**
	 * Retrieves the annotations associated with the resource
	 * @return
	 */
	Map<String, String> getAnnotations();

}
