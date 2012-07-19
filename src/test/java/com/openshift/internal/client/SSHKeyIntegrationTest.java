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

import static com.openshift.client.utils.FileUtils.createRandomTempFile;
import static org.fest.assertions.Assertions.assertThat;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.jcraft.jsch.JSchException;
import com.openshift.client.IOpenShiftConnection;
import com.openshift.client.IOpenShiftSSHKey;
import com.openshift.client.ISSHPublicKey;
import com.openshift.client.IUser;
import com.openshift.client.InvalidCredentialsOpenShiftException;
import com.openshift.client.OpenShiftConnectionFactory;
import com.openshift.client.OpenShiftException;
import com.openshift.client.SSHKeyPair;
import com.openshift.client.SSHKeyType;
import com.openshift.client.SSHPublicKey;
import com.openshift.client.utils.OpenShiftTestConfiguration;
import com.openshift.client.utils.SSHKeyTestUtils;
import com.openshift.client.utils.TestConnectionFactory;
import com.openshift.internal.client.httpclient.HttpClientException;

/**
 * @author Andre Dietisheim
 */
public class SSHKeyIntegrationTest {

	private IUser user;
	
	@Before
	public void setUp() throws SocketTimeoutException, HttpClientException, Throwable {
		final OpenShiftTestConfiguration configuration = new OpenShiftTestConfiguration();
		final IOpenShiftConnection connection = new OpenShiftConnectionFactory().getConnection(
				configuration.getClientId(), 
				configuration.getRhlogin(), 
				configuration.getPassword(),
				configuration.getLibraServer());
		this.user = connection.getUser();
	}

	@Test(expected = InvalidCredentialsOpenShiftException.class)
	public void shouldThrowIfInvalidCredentials() throws Exception {
		new TestConnectionFactory().getConnection(
				new OpenShiftTestConfiguration().getClientId(), "bogus-password").getUser();	
	}
	
	@Test
	public void shouldReturnExistingKeys() throws HttpClientException, Throwable {
		// pre-conditions
		// operation
		List<IOpenShiftSSHKey> sshKeys = user.getSSHKeys();
		// verifications
		assertThat(sshKeys).isNotNull();
	}

	@Test
	public void shouldAddKey() throws SocketTimeoutException, HttpClientException, Throwable {
		IOpenShiftSSHKey key = null;
		try {
			// pre-conditions
			String keyName = String.valueOf(System.currentTimeMillis());
			String publicKeyPath = SSHKeyTestUtils.createDsaKeyPair();
			ISSHPublicKey publicKey = new SSHPublicKey(publicKeyPath);
			int numOfKeys = user.getSSHKeys().size();

			// operation
			key = user.putSSHKey(keyName, publicKey);

			// verifications
			assertThat(
					new SSHKeyTestUtils.SSHPublicKeyAssertion(key))
					.hasName(keyName)
					.hasPublicKey(publicKey.getPublicKey())
					.isType(publicKey.getKeyType());
			List<IOpenShiftSSHKey> keys = user.getSSHKeys();
			assertThat(keys.size()).isEqualTo(numOfKeys + 1);
			IOpenShiftSSHKey keyInList = SSHKeyTestUtils.getKey(keyName, keys);
			assertThat(key).isEqualTo(keyInList);
		} finally {
			SSHKeyTestUtils.silentlyDestroyKey(key);
		}
	}

	@Test
	public void shouldUpdatePublicKey() throws SocketTimeoutException, HttpClientException, Throwable {
		IOpenShiftSSHKey key = null;
		try {
			// pre-conditions
			String keyName = String.valueOf(System.currentTimeMillis());
			String publicKeyPath = createRandomTempFile().getAbsolutePath();
			String privateKeyPath = createRandomTempFile().getAbsolutePath();
			SSHKeyPair keyPair = SSHKeyPair.create(
					SSHKeyType.SSH_RSA,
					SSHKeyTestUtils.DEFAULT_PASSPHRASE,
					privateKeyPath,
					publicKeyPath);
			key = user.putSSHKey(keyName, keyPair);

			// operation
			String publicKey = SSHKeyPair.create(
					SSHKeyType.SSH_RSA,
					SSHKeyTestUtils.DEFAULT_PASSPHRASE,
					privateKeyPath,
					publicKeyPath).getPublicKey();
			key.setPublicKey(publicKey);

			// verification
			assertThat(key.getPublicKey()).isEqualTo(publicKey);
			IOpenShiftSSHKey openshiftKey = user.getSSHKeyByName(keyName);
			assertThat(
					new SSHKeyTestUtils.SSHPublicKeyAssertion(openshiftKey))
					.hasName(keyName)
					.hasPublicKey(publicKey)
					.isType(openshiftKey.getKeyType());
		} finally {
			SSHKeyTestUtils.silentlyDestroyKey(key);
		}

	}

	@Test
	public void shouldReturnKeyForName() throws SocketTimeoutException, HttpClientException, Throwable {
		IOpenShiftSSHKey key = null;
		try {
			// pre-conditions
			String keyName = String.valueOf(System.currentTimeMillis());
			String publicKeyPath = SSHKeyTestUtils.createDsaKeyPair();
			ISSHPublicKey publicKey = new SSHPublicKey(publicKeyPath);

			// operation
			key = user.putSSHKey(keyName, publicKey);
			IOpenShiftSSHKey keyByName = user.getSSHKeyByName(keyName);

			// verifications
			assertThat(key).isEqualTo(keyByName);
		} finally {
			SSHKeyTestUtils.silentlyDestroyKey(key);
		}
	}

	@Test
	public void shouldReturnKeyForPublicKey() throws SocketTimeoutException, HttpClientException, Throwable {
		IOpenShiftSSHKey key = null;
		try {
			// pre-conditions
			String keyName = String.valueOf(System.currentTimeMillis());
			String publicKeyPath = SSHKeyTestUtils.createDsaKeyPair();
			ISSHPublicKey publicKey = new SSHPublicKey(publicKeyPath);

			// operation
			key = user.putSSHKey(keyName, publicKey);
			IOpenShiftSSHKey keyByPublicKey = user.getSSHKeyByPublicKey(publicKey.getPublicKey());

			// verifications
			assertThat(key).isEqualTo(keyByPublicKey);
		} finally {
			SSHKeyTestUtils.silentlyDestroyKey(key);
		}

	}

	@Test
	public void shouldUpdateKeyTypeAndPublicKey() throws SocketTimeoutException, HttpClientException, Throwable {
		IOpenShiftSSHKey key = null;
		try {
			// pre-conditions
			String keyName = String.valueOf(System.currentTimeMillis());
			String publicKeyPath = createRandomTempFile().getAbsolutePath();
			String privateKeyPath = createRandomTempFile().getAbsolutePath();
			SSHKeyTestUtils.createDsaKeyPair(publicKeyPath, privateKeyPath);
			ISSHPublicKey publicKey = new SSHPublicKey(publicKeyPath);
			assertThat(publicKey.getKeyType()).isEqualTo(SSHKeyType.SSH_DSA);
			key = user.putSSHKey(keyName, publicKey);
			SSHKeyPair keyPair = SSHKeyPair.create(
					SSHKeyType.SSH_RSA, SSHKeyTestUtils.DEFAULT_PASSPHRASE, privateKeyPath, publicKeyPath);

			// operation
			key.setKeyType(SSHKeyType.SSH_RSA, keyPair.getPublicKey());

			// verification
			assertThat(key.getKeyType()).isEqualTo(SSHKeyType.SSH_RSA);
			assertThat(key.getPublicKey()).isEqualTo(keyPair.getPublicKey());
		} finally {
			SSHKeyTestUtils.silentlyDestroyKey(key);
		}
	}

	@Test
	public void shouldRemoveKey() throws IOException, JSchException, OpenShiftException {
		IOpenShiftSSHKey key = null;
		try {
			// pre-conditions
			String keyName = String.valueOf(System.currentTimeMillis());
			String publicKeyPath = createRandomTempFile().getAbsolutePath();
			String privateKeyPath = createRandomTempFile().getAbsolutePath();
			SSHKeyTestUtils.createDsaKeyPair(publicKeyPath, privateKeyPath);
			key = user.putSSHKey(keyName, new SSHPublicKey(publicKeyPath));
			
			// operation
			key.destroy();
			key = null;
			
			// verification
			assertThat(user.getSSHKeyByName(keyName)).isNull();
		} finally {
			SSHKeyTestUtils.silentlyDestroyKey(key);
		}
	}
	
}
