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
package com.openshift.internal.client;

import java.util.List;
import java.util.Map;

import com.openshift.client.ApplicationScale;
import com.openshift.client.ICartridge;
import com.openshift.client.IGearProfile;
import com.openshift.client.IJBossASApplication;
import com.openshift.client.OpenShiftException;
import com.openshift.internal.client.response.Link;
import com.openshift.internal.client.response.Message;

/**
 * @author William DeCoste
 * @author Andre Dietisheim
 */
public class JBossASApplication extends ApplicationResource implements IJBossASApplication {

	public JBossASApplication(String name, String uuid, String creationTime, List<Message> creationLog,
			String applicationUrl, String gitUrl, String healthCheckPath, IGearProfile gearProfile,
			ApplicationScale scalable, ICartridge cartridge, List<String> aliases,
			Map<String, String> embeddedCartridgesInfos, Map<String, Link> links, DomainResource domain) {
		super(name, uuid, creationTime, creationLog, applicationUrl, gitUrl, healthCheckPath, gearProfile, scalable,
				cartridge, aliases, embeddedCartridgesInfos, links, domain);
	}

	public String threadDump() throws OpenShiftException {
		throw new UnsupportedOperationException();
		// service.threadDumpApplication(name, cartridge, getInternalUser());
		//
		// return "stdout.log";
	}

	public String getHealthCheckSuccessResponse() throws OpenShiftException {
		return "1";
	}

}
