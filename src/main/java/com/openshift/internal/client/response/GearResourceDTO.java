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

/**
 * The Class ApplicationDTO.
 *
 * @author Xavier Coulon
 */
public class GearResourceDTO extends BaseResourceDTO {

	/** uuid the Gear UUID. */
	private final String uuid;
	
	/** state the Gear state. */
	private final String state;
	
	/** the URL to connect with SSH. */
	private final String sshUrl;

	/**
	 * Constructor
	 * @param uuid the Gear UUID
	 * @param state the Gear state
	 * @param sshUrl the URL to connect with SSH
	 */
	GearResourceDTO(String uuid, String state, String sshUrl) {
		super(new HashMap<String, Link>(), null);
		this.uuid = uuid;
		this.sshUrl = sshUrl;
		this.state = state;
	}
	
	@Override
	public String toString() {
		return "GearResourceDTO";
	}
	
	public String getUuid() {
		return uuid;
	}
	
	public String getState() {
		return state;
	}

	/**
	 * @return the sshUrl
	 */
	public String getSshUrl() {
		return sshUrl;
	}

}
