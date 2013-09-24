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
package com.openshift.client.cartridge;

import java.net.URL;

import com.openshift.internal.client.cartridge.BaseCartridge;


/**
 * A cartridge that is available on the openshift server. This class is no enum
 * since we dont know all available types and they may change at any time.
 * 
 * @author André Dietisheim
 */
public class StandaloneCartridge extends BaseCartridge implements IStandaloneCartridge {

	public StandaloneCartridge(String name) {
		super(name);
	}

	public StandaloneCartridge(URL url) {
		super(url);
	}

	public StandaloneCartridge(String name, URL url) {
		super(name, url);
	}

	public StandaloneCartridge(String name, String displayName, String description) {
		super(name, null, displayName, description);
	}

	public StandaloneCartridge(String name, URL url, String displayName, String description) {
		super(name, url, displayName, description);
	}
}