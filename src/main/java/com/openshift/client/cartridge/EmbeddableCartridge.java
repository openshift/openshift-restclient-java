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
package com.openshift.client.cartridge;

import java.net.URL;

import com.openshift.internal.client.APIResource;
import com.openshift.internal.client.CartridgeType;
import com.openshift.internal.client.cartridge.BaseCartridge;

/**
 * An cartridge that may be embedded (added) into an application. Add-on
 * cartridge is an equivalent name for embedded cartridge.
 * 
 * @author Xavier Coulon
 * 
 * @see IEmbeddableCartridge for cartridges that have already been added and
 *      configured to an application.
 */
public class EmbeddableCartridge extends BaseCartridge implements IEmbeddableCartridge {

	public EmbeddableCartridge(final String name) {
		super(name);
	}

	public EmbeddableCartridge(final URL url) {
		super(url);
	}

	public EmbeddableCartridge(final String name, final URL url) {
		super(name, url);
	}

	@Override
	public CartridgeType getType() {
		return CartridgeType.EMBEDDED;
	}

	/**
	 * Constructor used when available cartridges are loaded from OpenShift
	 * 
	 * @see APIResource#getEmbeddableCartridges()
	 */
	public EmbeddableCartridge(final String name, String displayName, String description) {
		super(name, displayName, description);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof IEmbeddableCartridge)) {
			return false;
		}
		IEmbeddableCartridge other = (IEmbeddableCartridge) obj;
		// shortcut: downloadable cartridges get their name only when
		// they're deployed thus should equal on url only
		if (isDownloadable()) {
			if (other.isDownloadable()) {
				if (getUrl() == null) {
					return other.getUrl() == null;
				}
				return getUrl().equals(other.getUrl());
			}
		}
		if (getName() == null) {
			if (other.getName() != null) {
				return false;
			}
		} else if (!getName().equals(other.getName())) {
			return false;
		}
		return true;
	}

}
