/******************************************************************************* 
 * Copyright (c) 2011 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/ 
package org.jboss.tools.openshift.express.internal.client.request;

/**
 * @author André Dietisheim
 */
public class ListCartridgesRequest extends AbstractOpenShiftRequest {

	private static final String CART_TYPE_STANDALONE = "standalone";

	public ListCartridgesRequest(String username) {
		this(username, false);
	}

	public ListCartridgesRequest(String username, boolean debug) {
		super(username, debug);
	}
	
	public String getCartType() {
		return CART_TYPE_STANDALONE;
	}
	
	@Override
	protected String getResourcePath() {
		return "cartlist";
	}

}
