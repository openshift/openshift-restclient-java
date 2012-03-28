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
package com.openshift.express.client;

import com.jcraft.jsch.KeyPair;
import com.openshift.express.internal.client.utils.StringUtils;

/**
 * @author Andre Dietisheim
 */
public enum SSHKeyType {

	SSH_RSA("ssh-rsa"), SSH_DSA("ssh-dss");

	private final String typeId;

	SSHKeyType(String typeId) {
		this.typeId = typeId;
	}

	public String getTypeId() {
		return typeId;
	}

	public static SSHKeyType getByTypeId(String keyTypeId) throws OpenShiftUnknonwSSHKeyTypeException {
		if (StringUtils.isEmpty(keyTypeId)) {
			return null;
		}
		for (SSHKeyType sSHKeyType : values()) {
			if (keyTypeId.equals(sSHKeyType.getTypeId())) {
				return sSHKeyType;
			}
		}
		throw new OpenShiftUnknonwSSHKeyTypeException("OpenShift does not support keys of type \"{0}\"", keyTypeId);
	}

	
	public static SSHKeyType getByJSchKeyType(KeyPair keyPair) throws OpenShiftUnknonwSSHKeyTypeException {
		return getByJSchKeyType(keyPair.getKeyType());
	}

	public static SSHKeyType getByJSchKeyType(int jschKeyType) throws OpenShiftUnknonwSSHKeyTypeException {
		if (jschKeyType == KeyPair.RSA) {
			return SSH_RSA;
		} else if (jschKeyType == KeyPair.DSA) {
			return SSH_DSA;
		} else {
			throw new OpenShiftUnknonwSSHKeyTypeException("Unknown jsch key type \"{0}\"", jschKeyType);
		}
	}
}
