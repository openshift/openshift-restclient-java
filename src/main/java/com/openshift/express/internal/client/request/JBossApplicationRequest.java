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
 * @author William DeCoste
 */
public class JBossApplicationRequest extends ApplicationRequest {

	public JBossApplicationRequest(String name, ICartridge cartridge, ApplicationAction action, String username) {
		super(name, cartridge, action, username, false);
	}

	public JBossApplicationRequest(String name, ICartridge cartridge, ApplicationAction action, String username, boolean debug) {
		super(name, cartridge, action, username, debug);
	}
}
