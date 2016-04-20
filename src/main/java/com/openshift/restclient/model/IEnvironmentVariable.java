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

import com.openshift.internal.restclient.model.JSONSerializeable;

/**
 * Environment variable representation to
 * allow more complex values then
 * name/value pairs.  An environmentVariable
 * will have either a value or valueFrom
 * but not both.
 * 
 * @author jeff.cantrill
 *
 */
public interface IEnvironmentVariable extends JSONSerializeable{
	
	/**
	 * The name of the env var
	 * @return
	 */
	String getName();
	
	/**
	 * The value of the environment variable or null if not
	 * defined.
	 * @return
	 */
	String getValue();
	
	/**
	 * The ref value or null if not defined
	 * @return
	 */
	IEnvVarSource getValueFrom();
	
	/**
	 * Marker interface for sources of environment variables
	 * @author jeff.cantrill
	 *
	 */
	static interface IEnvVarSource{
		
	}

	@Override
	String toJson();
	
}
