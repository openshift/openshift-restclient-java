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
package org.jboss.tools.openshift.express.client;

import org.jboss.tools.openshift.express.internal.client.OpenShiftCoreActivator;
import org.jboss.tools.openshift.express.internal.client.utils.Base64Encoder;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.KeyPair;

/**
 * @author André Dietisheim
 */
public class SSHKeyPair implements ISSHPublicKey {

	/**
	 * the length of the key that is created when using #create. ssh-keygen uses
	 * a default of 2048
	 * 
	 * @see #create(String, String, String)
	 * @see http://en.wikipedia.org/wiki/Ssh-keygen
	 */
	private static final int KEYLENGTH = 2048;

	private KeyPair keyPair;
	private String privateKeyPath;

	private String publicKeyPath;

	private SSHKeyPair(KeyPair keyPair, String privateKeyPath, String publicKeyPath) throws OpenShiftException {
		this.keyPair = keyPair;
		this.privateKeyPath = privateKeyPath;
		this.publicKeyPath = publicKeyPath;
	}

	/**
	 * Creates private and public ssh-rsa keys and stores them to the given
	 * paths. The key is created while using the given pass phrase.
	 * 
	 * @param passPhrase
	 *            the pass phrase to set to the new key
	 * @param privateKeyPath
	 *            the path where the new private key gets stored
	 * @param publicKeyPath
	 *            the path where the new public key gets stored
	 * @return
	 * @throws OpenShiftException
	 *             if the key could not be created
	 */
	public static SSHKeyPair create(String passPhrase, String privateKeyPath, String publicKeyPath)
			throws OpenShiftException {
		try {
			KeyPair keyPair = KeyPair.genKeyPair(new JSch(), KeyPair.RSA, KEYLENGTH);
			keyPair.setPassphrase(passPhrase);
			keyPair.writePublicKey(publicKeyPath, "created by " + OpenShiftCoreActivator.PLUGIN_ID);
			keyPair.writePrivateKey(privateKeyPath);
			return new SSHKeyPair(keyPair, privateKeyPath, publicKeyPath);
		} catch (Exception e) {
			throw new OpenShiftException(e, "Could not create new rsa key", e);
		}
	}

	/**
	 * Loads existing private and public ssh key from the given paths.
	 * 
	 * @param privateKeyPath
	 *            the path to the private key
	 * @param publicKeyPath
	 *            the path to the public key
	 * @return
	 * @throws OpenShiftException
	 */
	public static SSHKeyPair load(String privateKeyPath, String publicKeyPath)
			throws OpenShiftException {
		try {
			KeyPair keyPair = KeyPair.load(new JSch(), privateKeyPath, publicKeyPath);
			return new SSHKeyPair(keyPair, privateKeyPath, publicKeyPath);
		} catch (JSchException e) {
			throw new OpenShiftException(e, "Could not create new rsa key");
		}
	}

	public String getPublicKey() throws OpenShiftException {
		return new String(Base64Encoder.encode(keyPair.getPublicKeyBlob()));
	}

	public String getPrivateKeyPath() {
		return privateKeyPath;
	}

	public String getPublicKeyPath() {
		return publicKeyPath;
	}
}
