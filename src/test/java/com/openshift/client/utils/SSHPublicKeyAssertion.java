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
package com.openshift.client.utils;

import static org.fest.assertions.Assertions.assertThat;

import org.fest.assertions.AssertExtension;

import com.openshift.client.IOpenShiftSSHKey;
import com.openshift.client.SSHKeyType;

/**
 * @author Andre Dietisheim
 */
public class SSHPublicKeyAssertion implements AssertExtension {

		private IOpenShiftSSHKey sshKey;

		public SSHPublicKeyAssertion(IOpenShiftSSHKey key) {
			this.sshKey = key;
		}

		public SSHPublicKeyAssertion hasName(String name) {
			assertThat(name).isEqualTo(sshKey.getName());
			return this;
		}

		public SSHPublicKeyAssertion hasPublicKey(String publicKey) {
			assertThat(publicKey).isEqualTo(sshKey.getPublicKey());
			return this;
		}

		public SSHPublicKeyAssertion isType(String type) {
			assertThat(type).isEqualTo(sshKey.getKeyType().getTypeId());
			return this;
		}

		public SSHPublicKeyAssertion isType(SSHKeyType type) {
			assertThat(type).isEqualTo(sshKey.getKeyType());
			return this;
		}
	}