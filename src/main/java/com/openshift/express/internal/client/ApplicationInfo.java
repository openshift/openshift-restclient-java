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
package com.openshift.express.internal.client;

import java.util.Date;
import java.util.List;

import com.openshift.express.client.ICartridge;

/**
 * @author André Dietisheim
 */
public class ApplicationInfo {

	private String name;
	private String uuid;
	private List<EmbeddableCartridgeInfo> embeddedCartridges;
	private ICartridge cartridge;
	private Date creationTime;

	public ApplicationInfo(String name, String uuid, List<EmbeddableCartridgeInfo> embeddedCartridges, ICartridge cartridge, Date creationTime) {
		this.name = name;
		this.uuid = uuid;
		this.embeddedCartridges = embeddedCartridges;
		this.cartridge = cartridge;
		this.creationTime = creationTime;
	}

	public String getName() {
		return name;
	}

	public List<EmbeddableCartridgeInfo> getEmbeddedCartridges() {
		return embeddedCartridges;
	}

	public EmbeddableCartridgeInfo getEmbeddedCartridge(String name) {
		EmbeddableCartridgeInfo embeddableCartridge = null;
		for(EmbeddableCartridgeInfo cartridge : embeddedCartridges) {
			if (cartridge.getName().equals(name)) {
				embeddableCartridge = cartridge;
				break;
			}
		}
		return embeddableCartridge;
	}
	
	public String getUuid() {
		return uuid;
	}

	public ICartridge getCartridge() {
		return cartridge;
	}

	public Date getCreationTime() {
		return creationTime;
	}

}
