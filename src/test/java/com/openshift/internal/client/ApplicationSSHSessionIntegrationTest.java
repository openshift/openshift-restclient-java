/******************************************************************************* 
 * Copyright (c) 2014 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 *
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/
package com.openshift.internal.client;

import static org.fest.assertions.Assertions.assertThat;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.zip.GZIPInputStream;

import org.apache.commons.compress.archivers.ArchiveException;
import org.apache.commons.compress.compressors.CompressorException;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.openshift.client.DeploymentTypes;
import com.openshift.client.IApplication;
import com.openshift.client.IDomain;
import com.openshift.client.IUser;
import com.openshift.client.cartridge.query.LatestVersionOf;
import com.openshift.client.utils.ApplicationAssert;
import com.openshift.client.utils.ApplicationTestUtils;
import com.openshift.client.utils.DomainTestUtils;
import com.openshift.client.utils.FileUtils;
import com.openshift.client.utils.SSHKeyTestUtils;
import com.openshift.client.utils.StringUtils;
import com.openshift.client.utils.TarFileTestUtils;
import com.openshift.client.utils.TestConnectionBuilder;
import com.openshift.internal.client.utils.StreamUtils;

/**
 * @author Andre Dietisheim
 */
public class ApplicationSSHSessionIntegrationTest extends TestTimer {

	private static IUser user;

	private IApplication application;
	private Session session;

	@BeforeClass
	public static void createSSHKeys() throws IOException, JSchException {
		user = new TestConnectionBuilder().defaultCredentials().disableSSLCertificateChecks().create().getUser();
		SSHKeyTestUtils.addTestKeyToOpenShift(user);
	}

	@Before
	public void setUp() throws Exception {
		IDomain domain = DomainTestUtils.ensureHasDomain(user);
		this.application = ApplicationTestUtils.getOrCreateApplication(domain, LatestVersionOf.php().get(user));
		this.session = createSSHSession(application.getSshUrl());
	}

	@After
	public void tearDown() {
		this.session.disconnect();
	}

	@Test
	public void shouldSaveDeploymentSnapshot() throws IOException, CompressorException {
		// pre-conditions
		File snapshotFile = FileUtils.createRandomTempFile(".tar.gz");
		FileOutputStream snapshotFileOut = new FileOutputStream(snapshotFile);

		// operations
		InputStream in = new ApplicationSSHSession(application, session).saveDeploymentSnapshot();
		writeTo(in, snapshotFileOut);

		// verifications
		List<String> allPaths = TarFileTestUtils.getAllPaths(new FileInputStream(snapshotFile));
		assertThat(allPaths).contains("./build-dependencies/", "./dependencies/", "./repo/");
		assertThat(allPaths).excludes("./git/");
	}

	@Test
	public void shouldRestoreDeploymentSnapshot() throws IOException {
		// pre-conditions
		File snapshotFile = FileUtils.createRandomTempFile(".tar.gz");
		ApplicationSSHSession applicationSession = new ApplicationSSHSession(application, session);
		InputStream snapshot = applicationSession.saveDeploymentSnapshot();
		writeTo(snapshot, new FileOutputStream(snapshotFile));
		String title = StringUtils.createRandomString();
		File newArchive = TarFileTestUtils.fakeReplaceFile(
				"index.php", "<html><body><h1>" + title + "</h1></body></html>", new FileInputStream(snapshotFile));
		assertThat(newArchive).isNotNull();
		assertThat(newArchive.length()).isGreaterThan(0);
		application.setDeploymentType(DeploymentTypes.binary());
		
		// operations
		InputStream restoreOutput = applicationSession.restoreDeploymentSnapshot(new FileInputStream(newArchive), true);
		StreamUtils.writeTo(restoreOutput, System.out);

		// verifications
		new ApplicationAssert(application).pageContains("", title);
	}

	@Test
	public void shouldRestoreFullSnapshot() throws IOException, ArchiveException, CompressorException {
		// pre-conditions
		File snapshotFile = FileUtils.createRandomTempFile(".tar.gz");
		FileOutputStream snapshotFileOut = new FileOutputStream(snapshotFile);
		ApplicationSSHSession applicationSession = new ApplicationSSHSession(application, session);
		InputStream in = applicationSession.saveFullSnapshot();
		writeTo(in, snapshotFileOut);
		assertThat(snapshotFile.length()).isGreaterThan(0);
		
		String title = StringUtils.createRandomString();
		File newArchive = TarFileTestUtils.fakeReplaceFile(
				"index.php", "<html><body><h1>" + title + "</h1></body></html>", new FileInputStream(snapshotFile));
		assertThat(newArchive).isNotNull();
		assertThat(newArchive.length()).isGreaterThan(0);
		
		// operations
		InputStream restoreResponse =
				applicationSession.restoreFullSnapshot(new FileInputStream(newArchive), true);
		StreamUtils.writeTo(restoreResponse, System.out);
		
		// verification
		new ApplicationAssert(application).pageContains("", title);
	}

	@Test
	public void shouldSaveFullSnapshot() throws IOException {
		// pre-conditions
		File snapshotFile = FileUtils.createRandomTempFile(".tar.gz");
		FileOutputStream snapshotFileOut = new FileOutputStream(snapshotFile);

		// operations
		// use gzip inputStream to assert valid gzip file
		InputStream in = new GZIPInputStream(
				new ApplicationSSHSession(application, session).saveFullSnapshot());
		writeTo(in, snapshotFileOut);
		// verifications
		assertThat(snapshotFile.length()).isGreaterThan(0);
	}

	private void writeTo(InputStream inputStream, FileOutputStream fileOut) throws IOException {
		try {
			StreamUtils.writeTo(inputStream, fileOut);
		} finally {
			StreamUtils.close(inputStream);
			fileOut.flush();
			StreamUtils.close(fileOut);
		}
	}

	private Session createSSHSession(String sshUrl) throws JSchException, URISyntaxException {
		JSch.setConfig("StrictHostKeyChecking", "no");
		JSch jsch = new JSch();
		jsch.addIdentity(SSHKeyTestUtils.SSH_TEST_PRIVATEKEY, SSHKeyTestUtils.DEFAULT_PASSPHRASE);
		URI sshUri = new URI(sshUrl);
		Session session = jsch.getSession(sshUri.getUserInfo(), sshUri.getHost());
		session.connect();
		return session;
	}
}
