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

/**
 * A cartridge info that is reported by {@link ApplicationInfo#getEmbeddedCartridge(String)} and holds the data reported
 * by the paas.
 * 
 * @author André Dietisheim
 */
public class EmbeddableCartridgeInfo {

	private String name;
	private String url;

	public EmbeddableCartridgeInfo(String name, String url) {
		this.name = name;
		this.url = url;
	}

	public String getName() {
		return name;
	}

	public String getUrl() {
		return url;
	}

}
