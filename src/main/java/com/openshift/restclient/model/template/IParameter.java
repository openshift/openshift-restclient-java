/*******************************************************************************
 * Copyright (c) 2015 Red Hat, Inc. Distributed under license by Red Hat, Inc.
 * All rights reserved. This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Red Hat, Inc.
 ******************************************************************************/
package com.openshift.restclient.model.template;

/**
 * A parameter for a template
 */
public interface IParameter {
	
	/**
	 * The name of the parameter that is substituted
	 * in the template
	 * @return
	 */
	String getName();
	
	/**
	 * The description
	 * @return
	 */
	String getDescription();
	
	/**
	 * The value to use
	 * @return
	 */
	String getValue();
	
	/**
	 * Set the value to use
	 * @param value
	 */
	void setValue(String value);
	
	/**
	 * The generator name which will use the value
	 * from 'getFrom' if set
	 * @return
	 */
	String getGeneratorName();
	
	/**
	 * The input to the generator
	 * @return
	 */
	String getFrom();
}
