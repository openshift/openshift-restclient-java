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

import com.openshift.express.client.IEmbeddableCartridge;

/**
 * @author André Dietisheim
 */
public class EmbedRequest extends AbstractOpenShiftRequest {

	private String name;
	private IEmbeddableCartridge cartridge ;
	private EmbedAction action;

	public EmbedRequest(String name, IEmbeddableCartridge cartridge, EmbedAction action, String username) {
		this(name, cartridge, action, username, false);
	}

	public EmbedRequest(String name, IEmbeddableCartridge cartridge, EmbedAction action, String username, boolean debug) {
		super(username, debug);
		this.name = name;
		this.cartridge = cartridge;
		this.action = action;
	}

	public EmbedAction getAction() {
		return action;
	}

	public String getName() {
		return name;
	}

	public IEmbeddableCartridge getEmbeddableCartridge() {
		return cartridge;
	}

	public String getResourcePath() {
		return "embed_cartridge";
	}
}
