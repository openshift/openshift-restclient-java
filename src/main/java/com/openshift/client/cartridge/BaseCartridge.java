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
package com.openshift.client.cartridge;

import java.util.regex.Pattern;

/**
 * A (base) cartridge for an OpenShift application. 
 * 
 * @author Andre Dietisheim
 * 
 * @see EmbeddableCartridge 
 * @see StandaloneCartridge
 */
public abstract class BaseCartridge implements ICartridge {

	private static final Pattern CARTRIDGE_URL_PATTERN = Pattern.compile("https?|ftp)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]");

	private final String name;
	private String displayName;
	private String description;
	private String url;

	protected BaseCartridge(final String name) {
		this(name, null, null);
	}

	protected BaseCartridge(final String name, String version) {
		this(name + NAME_VERSION_DELIMITER + version, null, null);
	}

	protected BaseCartridge(final String name, String displayName, String description) {
		this.name = name;
		if (isUrl(name)) {
			this.url = name;
		}
		this.displayName = displayName;
		this.description = description;
	}

	public String getName() {
		return name;
	}

	public String getDisplayName() {
		return displayName;
	}

	public String getDescription() {
		return description;
	}

	public boolean isDownloadable() {
		return url != null;
	}
	
	public String getUrl() {
		return url;
	}
	
	private boolean isUrl(String url) {
		return CARTRIDGE_URL_PATTERN.matcher(url).matches();
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
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		BaseCartridge other = (BaseCartridge) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + "[ "
				+ "name=" + name  
				+ " ]";
	}

}
