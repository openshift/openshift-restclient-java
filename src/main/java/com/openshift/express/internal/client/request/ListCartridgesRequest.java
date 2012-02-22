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
package com.openshift.express.internal.client.request;


/**
 * @author André Dietisheim
 */
public class ListCartridgesRequest extends AbstractOpenShiftRequest {

	public enum CartridgeType {
		STANDALONE, EMBEDDED;

		public String toString() {
			return name().toLowerCase();
		}
	}
	
	private CartridgeType cartridgeType;

	public ListCartridgesRequest(String username) {
		this(username, false);
	}

	public ListCartridgesRequest(String username, boolean debug) {
		this(CartridgeType.STANDALONE, username, debug);
	}

	public ListCartridgesRequest(CartridgeType cartridgeType, String username, boolean debug) {
		super(username, debug);
		this.cartridgeType = cartridgeType;
	}
	
	public CartridgeType getCartType() {
		return cartridgeType;
	}
	
	protected String getResourcePath() {
		return "cartlist";
	}
}
