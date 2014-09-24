/******************************************************************************* 
 * Copyright (c) 2013 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/
package com.openshift.internal.client.cartridge;

import java.net.URL;

import com.openshift.client.cartridge.EmbeddableCartridge;
import com.openshift.client.cartridge.ICartridge;
import com.openshift.client.cartridge.StandaloneCartridge;
import com.openshift.internal.client.CartridgeType;
import com.openshift.internal.client.utils.StringUtils;

/**
 * A (base) cartridge for an OpenShift application.
 * 
 * @author Andre Dietisheim
 * 
 * @see EmbeddableCartridge
 * @see StandaloneCartridge
 */
public class BaseCartridge implements ICartridge {

	private final String name;
	private String displayName;
	private String description;
	private URL url;
	private boolean obsolete;

	public BaseCartridge(final String name) {
		this(name, null, null, null);
	}

	public BaseCartridge(final URL url) {
		this(null, url, null, null);
	}

	protected BaseCartridge(final String name, URL url) {
		this(name, url, null, null);
	}

	/**
	 * Constructor used when available cartridges are loaded from OpenShift
	 * 
	 * @see APIResource#getEmbeddableCartridges()
	 */
	protected BaseCartridge(final String name, String displayName, String description, boolean obsolete) {
		this(name, null, displayName, description, obsolete);
	}

	protected BaseCartridge(final String name, URL url, String displayName, String description) {
		this(name, url, displayName, description, false);
	}
	
	protected BaseCartridge(final String name, URL url, String displayName, String description, boolean obsolete) {
		this.url = url;
		this.name = name;
		this.displayName = getDisplayName(displayName, url);
		this.description = description;
		this.obsolete = obsolete;
	}

	protected String getDisplayName(final String displayName, URL url) {
		if (!StringUtils.isEmpty(displayName)) {
			return displayName;
		} else if (url != null){
			return url.getRef();
		}
		return null;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public String getDisplayName() {
		return displayName;
	}

	@Override
	public String getDescription() {
		return description;
	}

	@Override
	public boolean isDownloadable() {
		return url != null;
	}

	@Override
	public URL getUrl() {
		return url;
	}
	
	@Override
	public CartridgeType getType() {
		return CartridgeType.UNDEFINED;
	}
	
	@Override
	public boolean isObsolete() {
		return obsolete;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof BaseCartridge)) {
			return false;
		}

		BaseCartridge other = (BaseCartridge) obj;
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

	@Override
	public String toString() {
		return getClass().getSimpleName() + "[ "
				+ "name=" + name
				+ ", url=" + url
				+ ", displayName = " + displayName
				+ ", description=" + description
				+ ", obsolete=" + obsolete
				+ " ]";
	}
}
