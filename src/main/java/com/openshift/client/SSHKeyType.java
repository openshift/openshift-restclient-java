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
package com.openshift.client;

import com.jcraft.jsch.KeyPair;
import com.openshift.internal.client.utils.Assert;

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

	/**
	 * Returns the JSch key pair constant for a given SSHKeyType.
	 * 
	 * @param type
	 *            the SSHKeyType that the JSch KeyPair constant will get
	 *            returned for
	 * @return
	 */
	public int toJSchKeyType() {
		switch (this) {
		case SSH_RSA:
			return KeyPair.RSA;
		case SSH_DSA:
			return KeyPair.DSA;
		default:
			return KeyPair.UNKNOWN;
		}
	}

	public static SSHKeyType getByTypeId(String keyTypeId) throws OpenShiftUnknonwSSHKeyTypeException {
		Assert.notNull(keyTypeId);

		for (SSHKeyType sSHKeyType : values()) {
			if (keyTypeId.equals(sSHKeyType.getTypeId())) {
				return sSHKeyType;
			}
		}
		throw new OpenShiftUnknonwSSHKeyTypeException("OpenShift does not support keys of type \"{0}\"", keyTypeId);
	}

	public static SSHKeyType getByJSchKeyType(KeyPair keyPair) throws OpenShiftUnknonwSSHKeyTypeException {
		Assert.notNull(keyPair);

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
