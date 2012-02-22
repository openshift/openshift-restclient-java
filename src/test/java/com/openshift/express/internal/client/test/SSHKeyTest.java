/******************************************************************************* 
 * Copyright (c) 2007 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/
package com.openshift.express.internal.client.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;

import org.junit.Test;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.KeyPair;
import com.openshift.express.client.IOpenShiftService;
import com.openshift.express.client.ISSHPublicKey;
import com.openshift.express.client.OpenShiftUnknonwSSHKeyTypeException;
import com.openshift.express.client.SSHKeyPair;
import com.openshift.express.client.SSHKeyType;
import com.openshift.express.client.SSHPublicKey;

public class SSHKeyTest {

	private static final String SSH_RSA = "ssh-rsa";
	private static final String SSH_DSA = "ssh-dss";
	private static final String PASSPHRASE = "12345";

	@Test
	public void canCreatePublicKey() throws Exception {
		String publicKeyPath = createTempFile().getAbsolutePath();
		String privateKeyPath = createTempFile().getAbsolutePath();
		SSHKeyPair sshKey = SSHKeyPair.create(PASSPHRASE, privateKeyPath, publicKeyPath);
		String publicKey = sshKey.getPublicKey();
		assertNotNull(sshKey.getKeyType());
		String keyType = sshKey.getKeyType().getTypeId();
		
		assertNotNull(publicKey);
		assertTrue(!publicKey.contains(SSH_RSA)); // no identifier
		assertTrue(!publicKey.contains(" ")); // no comment
		assertEquals(SSHKeyType.SSH_RSA.getTypeId(), keyType);
	}

	@Test
	public void canLoadKeyPair() throws Exception {
		String publicKeyPath = createTempFile().getAbsolutePath();
		String privateKeyPath = createTempFile().getAbsolutePath();
		SSHKeyPair.create(PASSPHRASE, privateKeyPath, publicKeyPath);

		SSHKeyPair sshKey = SSHKeyPair.load(privateKeyPath, publicKeyPath);
		String publicKey = sshKey.getPublicKey();
		assertNotNull(sshKey.getKeyType());
		String keyType = sshKey.getKeyType().getTypeId();

		assertNotNull(publicKey);
		assertTrue(!publicKey.contains(SSH_RSA)); // no identifier
		assertTrue(!publicKey.contains(" ")); // no comment
		assertEquals(SSHKeyType.SSH_RSA.getTypeId(), keyType);
	}

	@Test
	public void canLoadPublicKey() throws Exception {
		String publicKeyPath = createTempFile().getAbsolutePath();
		String privateKeyPath = createTempFile().getAbsolutePath();
		SSHKeyPair.create(PASSPHRASE, privateKeyPath, publicKeyPath);

		ISSHPublicKey sshKey = new SSHPublicKey(new File(publicKeyPath));
		String publicKey = sshKey.getPublicKey();
		assertNotNull(sshKey.getKeyType());
		String keyType = sshKey.getKeyType().getTypeId();

		assertNotNull(publicKey);
		assertTrue(!publicKey.contains(SSH_RSA)); // no identifier
		assertTrue(!publicKey.contains(" ")); // no comment

		SSHKeyPair keyPair = SSHKeyPair.load(privateKeyPath, publicKeyPath);
		assertEquals(publicKey, keyPair.getPublicKey());
		assertEquals(SSHKeyType.SSH_RSA.getTypeId(), keyType);
	  }
	
	  @Test
	  public void canLoadKeyPairDsa() throws Exception {
	    String publicKeyPath = createTempFile().getAbsolutePath();
	    String privateKeyPath = createTempFile().getAbsolutePath();
	    createDsaKeyPair(publicKeyPath, privateKeyPath);
	
	    SSHKeyPair sshKey = SSHKeyPair.load(privateKeyPath, publicKeyPath);
	    String publicKey = sshKey.getPublicKey();
		assertNotNull(sshKey.getKeyType());
		String keyType = sshKey.getKeyType().getTypeId();
	
	    assertNotNull(publicKey);
	    assertTrue(!publicKey.contains(SSH_DSA)); // no identifier
	    assertTrue(!publicKey.contains(" ")); // no comment
	    assertEquals(SSHKeyType.SSH_DSA.getTypeId(), keyType);
	  }

	  @Test
	  public void canLoadPublicKeyDsa() throws Exception {
	    String publicKeyPath = createTempFile().getAbsolutePath();
	    String privateKeyPath = createTempFile().getAbsolutePath();
	    createDsaKeyPair(publicKeyPath, privateKeyPath);
	
	    ISSHPublicKey sshKey = new SSHPublicKey(new File(publicKeyPath));
	    String publicKey = sshKey.getPublicKey();
		assertNotNull(sshKey.getKeyType());
		String keyType = sshKey.getKeyType().getTypeId();
	
	    assertNotNull(publicKey);
	    assertTrue(!publicKey.contains(SSH_DSA)); // no identifier
	    assertTrue(!publicKey.contains(" ")); // no comment
	
	    SSHKeyPair keyPair = SSHKeyPair.load(privateKeyPath, publicKeyPath);
	    assertEquals(publicKey, keyPair.getPublicKey());
	    assertEquals(SSHKeyType.SSH_DSA.getTypeId(), keyType);
	  }
	
	  @Test
	  public void canGetKeyTypeByTypeId() throws OpenShiftUnknonwSSHKeyTypeException {
		  assertTrue(SSHKeyType.SSH_DSA == SSHKeyType.getByTypeId(SSH_DSA));
		  assertTrue(SSHKeyType.SSH_RSA == SSHKeyType.getByTypeId(SSH_RSA));
	  }
	  
	  @Test(expected=OpenShiftUnknonwSSHKeyTypeException.class)
	  public void getKeyTypeByTypeIdReturnsNullIfNoMatchingType() throws OpenShiftUnknonwSSHKeyTypeException {
		  SSHKeyType.getByTypeId("dummy");
	  }

	  private void createDsaKeyPair(String publicKeyPath, String privateKeyPath) throws IOException, JSchException {
	    KeyPair keyPair = KeyPair.genKeyPair(new JSch(), KeyPair.DSA, 1024);
	    keyPair.setPassphrase(PASSPHRASE);
	    keyPair.writePublicKey(publicKeyPath, "created by " + IOpenShiftService.ID);
	    keyPair.writePrivateKey(privateKeyPath);
	   }
	 
	   private File createTempFile() throws IOException {
		return File.createTempFile(String.valueOf(System.currentTimeMillis()), null);
	}
}
