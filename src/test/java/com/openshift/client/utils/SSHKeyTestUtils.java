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

import static com.openshift.client.utils.FileUtils.createRandomTempFile;
import static org.fest.assertions.Assertions.assertThat;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.KeyPair;
import com.openshift.client.IOpenShiftSSHKey;
import com.openshift.client.IUser;
import com.openshift.client.IOpenShiftConnection;
import com.openshift.client.OpenShiftException;
import com.openshift.client.SSHPublicKey;

/**
 * @author Andre Dietisheim
 */
public class SSHKeyTestUtils {

	public static final String SSH_TEST_KEY_NAME = "OSJC_SSHKEY";
	public static final String SSH_TEST_PUBLICKEY = FileUtils.getTempDirFilePath(SSH_TEST_KEY_NAME + ".pub");
	public static final String SSH_TEST_PRIVATEKEY = FileUtils.getTempDirFilePath(SSH_TEST_KEY_NAME);
	
	public static final String DEFAULT_PASSPHRASE = "12345";

	public static final String SSH_RSA = "ssh-rsa";
	public static final String SSH_DSA = "ssh-dss";

	/**
	 * Returns the key with the given name out of the keys in the given list of
	 * keys. Uses plain java means to look for the key (so that tests may limit
	 * theirself to test single bits of functionality).
	 * 
	 * @param name
	 * @param keys
	 * @return
	 */
	public static IOpenShiftSSHKey getKey(String name, List<IOpenShiftSSHKey> keys) {
		IOpenShiftSSHKey matchingKey = null;
		for (IOpenShiftSSHKey key : keys) {
			if (name.equals(key.getName())) {
				matchingKey = key;
				break;
			}
		}
		return matchingKey;
	}

	/**
	 * Creates a dsa key pair at the given paths for the public and private key.
	 * Uses external means (jsch KeyPair) and no internal functionality to
	 * create the keys (so that tests can limit theirselves to test single
	 * functionality bits).
	 * 
	 * @param publicKeyPath
	 * @param privateKeyPath
	 * @throws IOException
	 * @throws JSchException
	 */
	public static KeyPair createDsaKeyPair(String publicKeyPath, String privateKeyPath) throws IOException, JSchException {
		KeyPair keyPair = KeyPair.genKeyPair(new JSch(), KeyPair.DSA, 1024);
		keyPair.setPassphrase(DEFAULT_PASSPHRASE);
		keyPair.writePublicKey(publicKeyPath, "created by " + IOpenShiftConnection.DEFAULT_CLIENT_ID);
		keyPair.writePrivateKey(privateKeyPath);
		return keyPair;
	}

	public static String createDsaKeyPair() throws IOException, JSchException {
		String publicKeyPath = createRandomTempFile().getAbsolutePath();
		String privateKeyPath = createRandomTempFile().getAbsolutePath();
		createDsaKeyPair(publicKeyPath, privateKeyPath);
		return publicKeyPath;
	}

	public static void silentlyDestroyKey(IOpenShiftSSHKey key) {
		if (key == null) {
			return;
		}
		try {
			key.destroy();
		} catch (Exception e) {
//			e.printStackTrace();
		}
	}

	public static String createRandomKeyName() {
		return String.valueOf(System.currentTimeMillis());
	}

	public static KeyPair createSSHTestKeys() throws IOException, JSchException {
		new File(SSH_TEST_PUBLICKEY).createNewFile();
		new File(SSH_TEST_PRIVATEKEY).createNewFile();
		return createDsaKeyPair(SSH_TEST_PUBLICKEY, SSH_TEST_PRIVATEKEY);
	}

	public static void addTestKeyToOpenShift(IUser user) throws OpenShiftException, FileNotFoundException, IOException, JSchException {
		assertThat(user).isNotNull();
		createSSHTestKeys();
		user.removeSSHKey(SSH_TEST_KEY_NAME);
		user.addSSHKey(SSH_TEST_KEY_NAME, new SSHPublicKey(SSH_TEST_PUBLICKEY));
	}
	
}
