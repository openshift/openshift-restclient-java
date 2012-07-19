/******************************************************************************* 
 * Copyright (c) 2012 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/
package com.openshift.client;

import java.util.List;

public interface IApplicationGear {
	
	public static final String COMPONENT_NAME = "proxy_port";
	
	public static final String COMPONENT_PROXY_PORT = "proxy_port";
	
	public static final String COMPONENT_PROXY_HOST = "proxy_host";
	
	public static final String COMPONENT_INTERNAL_PORT = "internal_port";
	
	/**
	 * Returns the uuid of this application.
	 * 
	 * @return the uuid of this application.
	 */
	public String getUuid();

	/**
	 * Returns the url at which the git repository of this application may be reached at.
	 * 
	 * @return the url of the git repo of this application.
	 */
	public String getGitUrl();

	/**
	 * Returns the gear's components.
	 *
	 * @return the components
	 */
	public List<IApplicationGearComponent> getComponents(); 
	

}
