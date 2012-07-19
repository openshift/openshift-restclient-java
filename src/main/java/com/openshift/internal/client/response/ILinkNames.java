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
package com.openshift.internal.client.response;

/**
 * The Interface ILinkNames.
 */
public interface ILinkNames {

	/** The Constant GET. */
	public static final String GET = "GET";

	/** The Constant UPDATE. */
	public static final String UPDATE = "UPDATE";

	/** The Constant CREATE. */
	public static final String CREATE = "CREATE";

	/** The Constant DELETE. */
	public static final String DELETE = "DELETE";

	/** The Constant LIST_APPLICATIONS. */
	public static final String LIST_APPLICATIONS = "LIST_APPLICATIONS";

	/** The Constant ADD_APPLICATION. */
	public static final String ADD_APPLICATION = "ADD_APPLICATION";

	/** The Constant ADD_APPLICATION_FROM_TEMPLATE. */
	public static final String ADD_APPLICATION_FROM_TEMPLATE = "ADD_APPLICATION_FROM_TEMPLATE";

	/** The Constant SHOW_PORT. */
	public static final String SHOW_PORT = "SHOW_PORT";
	
	/** The Constant ADD_ALIAS. */
	public static final String ADD_ALIAS = "ADD_ALIAS";
	
	/** The Constant REMOVE_ALIAS. */
	public static final String REMOVE_ALIAS = "REMOVE_ALIAS";
	
	/** The Constant START. */
	public static final String START = "START";
	
	/** The Constant STOP. */
	public static final String STOP = "STOP";
	
	/** The Constant FORCE_STOP. */
	public static final String FORCE_STOP = "FORCE_STOP";
	
	/** The Constant RESTART. */
	public static final String RESTART = "RESTART";
	
	/** The Constant CONCEAL_PORT. */
	public static final String CONCEAL_PORT = "CONCEAL_PORT";
	
	/** The Constant EXPOSE_PORT. */
	public static final String EXPOSE_PORT = "EXPOSE_PORT";
	
	/** The Constant LIST_CARTRIDGES. */
	public static final String LIST_CARTRIDGES = "LIST_CARTRIDGES";
	
	/** The Constant ADD_CARTRIDGE. */
	public static final String ADD_CARTRIDGE = "ADD_CARTRIDGE";
	
	/** The Constant GET_DESCRIPTOR. */
	public static final String GET_DESCRIPTOR = "GET_DESCRIPTOR";
	
	/** The Constant GET_GEARS. */
	public static final String GET_GEARS = "GET_GEARS";
}
