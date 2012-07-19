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
package com.openshift.internal.client.response;

import java.util.HashMap;
import java.util.List;

public class GearComponentDTO extends BaseResourceDTO {

	/** the component name. */
	private final String name;

	/** the component internal port. */
	private final String internalPort;

	/** the component proxy host. */
	private final String proxyHost;

	/** the component proxy port. */
	private final String proxyPort;

	/**
	 * Instantiates a new gear component dto.
	 * 
	 * @param name
	 *            the name
	 * @param internalPort
	 *            the internal port
	 * @param proxyPort
	 *            the proxy port
	 * @param proxyHost
	 *            the proxy host
	 */
	public GearComponentDTO(final String name, final String internalPort, final String proxyHost,
			final String proxyPort, final List<Message> creationLog) {
		super(new HashMap<String, Link>(), creationLog);
		this.name = name;
		this.internalPort = internalPort;
		this.proxyHost = proxyHost;
		this.proxyPort = proxyPort;
	}


	/**
	 * @return the name
	 */
	public final String getName() {
		return name;
	}

	/**
	 * @return the internalPort
	 */
	public final String getInternalPort() {
		return internalPort;
	}

	/**
	 * @return the proxyHost
	 */
	public final String getProxyHost() {
		return proxyHost;
	}

	/**
	 * @return the proxyPort
	 */
	public final String getProxyPort() {
		return proxyPort;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((internalPort == null) ? 0 : internalPort.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((proxyHost == null) ? 0 : proxyHost.hashCode());
		result = prime * result + ((proxyPort == null) ? 0 : proxyPort.hashCode());
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
		if (getClass() != obj.getClass()) {
			return false;
		}
		GearComponentDTO other = (GearComponentDTO) obj;
		if (internalPort == null) {
			if (other.internalPort != null) {
				return false;
			}
		} else if (!internalPort.equals(other.internalPort)) {
			return false;
		}
		if (name == null) {
			if (other.name != null) {
				return false;
			}
		} else if (!name.equals(other.name)) {
			return false;
		}
		if (proxyHost == null) {
			if (other.proxyHost != null) {
				return false;
			}
		} else if (!proxyHost.equals(other.proxyHost)) {
			return false;
		}
		if (proxyPort == null) {
			if (other.proxyPort != null) {
				return false;
			}
		} else if (!proxyPort.equals(other.proxyPort)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "GearsComponent [ " +
				"name=" + name + ", "
				+ "internalPort=" + internalPort + ", "
				+ "proxyHost=" + proxyHost + ", "
				+ "proxyPort=" + proxyPort
				+ " ]";
	}
}
