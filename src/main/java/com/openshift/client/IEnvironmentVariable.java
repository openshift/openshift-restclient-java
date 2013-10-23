/******************************************************************************* 
 * Copyright (c) 2013 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/
package com.openshift.client;

/**
 * @author Syed Iqbal
 */
public interface IEnvironmentVariable extends IOpenShiftResource {

	/**
	 * @return Name of the environment variable
	 */
	public String getName();

	/**
	 * @return Value of the environment variable
	 */
	public String getValue();

	/**
	 * Updates this environment variable to the given value.
	 * 
	 * @param value
	 *            new value for this environment variable
	 * @throws OpenShiftException
	 */
	public void update(String value) throws OpenShiftException;

	/**
	 * Destroys this environment variable
	 * 
	 * @throws OpenShiftException
	 */
	public void destroy() throws OpenShiftException;

	
	/**
	 * Returns the application for this environment variable
	 * 
	 * @return
	 */
	public IApplication getApplication();

}
