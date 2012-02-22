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

import java.util.Iterator;
import java.util.List;

import com.openshift.express.client.OpenShiftUnknonwSSHKeyTypeException;
import com.openshift.express.client.SSHPublicKey;

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

	public UserInfo(String rhLogin, String uuid, String sshPublicKey, String rhcDomain, String namespace,
			List<ApplicationInfo> applicationInfos, String sshKeyType) throws OpenShiftUnknonwSSHKeyTypeException {
		this.rhLogin = rhLogin;
		this.uuid = uuid;
		this.sshPublicKey = new SSHPublicKey(sshPublicKey, sshKeyType);
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

	public boolean hasDomain() {
		return namespace != null;
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

	public void removeApplicationInfo(String name) {
		for (Iterator<ApplicationInfo> iterator = applicationInfos.iterator(); iterator.hasNext();) {
			ApplicationInfo appInfo = iterator.next();
			if (appInfo.getName().equals(name)) {
				iterator.remove();
				break;
			}

		}
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

	protected void clearNameSpace() {
		namespace = null;
	}
}