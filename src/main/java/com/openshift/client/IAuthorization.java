/*******************************************************************************
 * Copyright (c) 2014 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Sean Kavanagh - initial API and implementation
 ******************************************************************************/
package com.openshift.client;

/**
 * Operations to manage and view authorization resources
 * 
 * @link http://openshift.github.io/documentation/rest_api/rest-api-1-6.html#
 *       authorization
 * 
 * @author Sean Kavanagh
 */
public interface IAuthorization extends IOpenShiftResource {

	public static String SCOPE_SESSION = "session";
	public static String SCOPE_SESSION_READ = "session read";
	public static int NO_EXPIRES_IN = -1;

	/**
	 * Returns the unique id for this authorization.
	 *
	 * @return
	 */
	public String getId();

	/**
	 * authorization note
	 *
	 * @return
	 */
	public String getNote();

	/**
	 * returns the scope of the authorization token to determine type of access.
	 *
	 * @return
	 */
	public String getScopes();

	/**
	 * Returns authorization string that contains user credentials.
	 *
	 * @return
	 */
	public String getToken();

	/**
	 * Returns the total time in seconds before this authorization expires.
	 * 
	 * @return
	 */
	public int getExpiresIn();

	/**
	 * Destroys this authorization
	 *
	 * @throws OpenShiftException
	 */
	public void destroy() throws OpenShiftException;

	/**
	 * Refreshes the authorization by reloading its content from OpenShift.
	 *
	 * @throws OpenShiftException
	 */
	public void refresh() throws OpenShiftException;
}
