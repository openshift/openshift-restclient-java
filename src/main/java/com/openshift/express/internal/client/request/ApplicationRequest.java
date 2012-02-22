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

import com.openshift.express.client.ICartridge;

/**
 * @author André Dietisheim
 */
public class ApplicationRequest extends AbstractOpenShiftRequest {

	private String name;
	private ICartridge cartridge ;
	private ApplicationAction action;
	private String nodeProfile;

	public ApplicationRequest(String name, ICartridge cartridge, ApplicationAction action, String username) {
		this(name, cartridge, action, username, false);
	}
	
	public ApplicationRequest(String name, ICartridge cartridge, ApplicationAction action, String username, boolean debug) {
		this(name, cartridge, action, username, debug, (String)null);
	}
	
	public ApplicationRequest(String name, ICartridge cartridge, ApplicationAction action, String username, boolean debug, String nodeProfile) {
		super(username, debug);
		this.name = name;
		this.cartridge = cartridge;
		this.action = action;
		this.nodeProfile = nodeProfile;
	}

	public ApplicationAction getAction() {
		return action;
	}

	public String getName() {
		return name;
	}

	public ICartridge getCartridge() {
		return cartridge;
	}
	
	public String getNodeProfile() {
		return nodeProfile;
	}

	public String getResourcePath() {
		return "cartridge";
	}
}
