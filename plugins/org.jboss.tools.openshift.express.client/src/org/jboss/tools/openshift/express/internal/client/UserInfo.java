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
package org.jboss.tools.openshift.express.internal.client;

import java.util.List;

import org.jboss.tools.openshift.express.client.SSHPublicKey;


/**
 * @author André Dietisheim
 */
public class UserInfo {

	private String rhLogin;
	private String uuid;
	private SSHPublicKey sshPublicKey;
	private String rhcDomain;
	private String namespace;
	private List<ApplicationInfo> applicationInfos;

	public UserInfo(String rhLogin, String uuid, String sshPublicKey, String rhcDomain, String namespace, List<ApplicationInfo> applicationInfos) {
		this.rhLogin = rhLogin;
		this.uuid = uuid;
		this.sshPublicKey = new SSHPublicKey(sshPublicKey);
		this.rhcDomain = rhcDomain;
		this.namespace = namespace;
		this.applicationInfos = applicationInfos;
	}

	public String getUuid() {
		return uuid;
	}

	public SSHPublicKey getSshPublicKey() {
		return sshPublicKey;
	}

	public String getRhLogin() {
		return rhLogin;
	}

	public String getNamespace() {
		return namespace;
	}

	public List<ApplicationInfo> getApplicationInfos() {
		return applicationInfos;
	}

	public ApplicationInfo getApplicationInfoByName(String name) {
		ApplicationInfo matchingApplicationInfo = null;
		for (ApplicationInfo applicationInfo : applicationInfos) {
			if (name.equals(applicationInfo.getName())) {
				matchingApplicationInfo = applicationInfo;
				break;
			}
		}
		return matchingApplicationInfo;
	}
	
	public String getRhcDomain() {
		return rhcDomain;
	}

}
