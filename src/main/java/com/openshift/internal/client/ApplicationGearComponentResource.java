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
package com.openshift.internal.client;

import com.openshift.client.IApplicationGearComponent;
import com.openshift.client.OpenShiftException;
import com.openshift.internal.client.response.GearComponentDTO;

/**
 * @author Xavier Coulon
 */
public class ApplicationGearComponentResource extends AbstractOpenShiftResource implements IApplicationGearComponent {

	private final String name;
	private final String internalPort;
	private final String proxyHost;
	private final String proxyPort;

	protected ApplicationGearComponentResource(GearComponentDTO dto) {
		this(dto.getName(), dto.getInternalPort(), dto.getProxyHost(), dto.getProxyPort());
	}

	protected ApplicationGearComponentResource(final String name, final String internalPort, final String proxyHost,
			final String proxyPort) {
		super(null);
		this.name = name;
		this.internalPort = internalPort;
		this.proxyHost = proxyHost;
		this.proxyPort = proxyPort;
	}

	protected final String getName() {
		return name;
	}

	protected final String getInternalPort() {
		return internalPort;
	}

	protected final String getProxyHost() {
		return proxyHost;
	}

	protected final String getProxyPort() {
		return proxyPort;
	}
	
	@Override
	public void refresh() throws OpenShiftException {
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
		ApplicationGearComponentResource other = (ApplicationGearComponentResource) obj;
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
		return "GearsComponent ["
				+ "name=" + name
				+ ", internalPort=" + internalPort
				+ ", proxyHost=" + proxyHost
				+ ", proxyPort=" + proxyPort
				+ "]";
	}
}
