package com.openshift.internal.restclient.capability.resources;
/*******************************************************************************
 * Copyright (c) 2015 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/

import static org.fest.assertions.Assertions.assertThat;
import static org.junit.Assert.assertNotNull;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.openshift.internal.restclient.IntegrationTestHelper;
import com.openshift.internal.restclient.model.Pod;
import com.openshift.restclient.IClient;
import com.openshift.restclient.ResourceKind;
import com.openshift.restclient.capability.CapabilityVisitor;
import com.openshift.restclient.capability.IBinaryCapability;
import com.openshift.restclient.capability.IBinaryCapability.OpenShiftBinaryOption;
import com.openshift.restclient.capability.resources.IRSyncable;
import com.openshift.restclient.capability.resources.IRSyncable.LocalPeer;
import com.openshift.restclient.capability.resources.IRSyncable.PodPeer;
import com.openshift.restclient.model.IResource;

/**
 * 
 * @author Xavier Coulon
 *
 */
public class OpenshiftBinaryRSyncRetrievalIntegrationTest {

	private static final String TARGET_FOLDER_WITH_SPECIAL_CHARS = "/tmp/with sp@cé";
	private static final String FILE_NAME_SPECIAL_CHARS = "test with spéci@l characters and spaces";
	private static final String SOURCE_FOLDER_WITH_SPECIAL_CHARS = "with sp@cé in path";

	private static final String NORMAL_TARGET_TMP = "/tmp";
	private static final String NORMAL_FILE_NAME = "normalFileName";
	private static final String NORMAL_FOLDER_NAME = "normalFolderName";

	private static final Logger LOG = LoggerFactory.getLogger(OpenshiftBinaryRSyncRetrievalIntegrationTest.class); 
	
	private IntegrationTestHelper helper = new IntegrationTestHelper();
	private IClient client;
	
	@Rule
	public TemporaryFolder tmpFolder = new TemporaryFolder();
	
	private Pod pod;

	@Before
	public void setUp() throws Exception {
		// given
		System.setProperty(IBinaryCapability.OPENSHIFT_BINARY_LOCATION, helper.getOpenShiftLocation());
		client = helper.createClientForBasicAuth();

		List<IResource> pods = client.list(ResourceKind.POD, "default");
		pod = (Pod) pods.stream().filter(p->p.getName().startsWith("docker-registry")).findFirst().orElse(null);
		assertNotNull("Did not find the registry pod to which to rsync", pod);
	}
	
	@Test
	public void testRSyncLogRetrieval() throws IOException {
		testRsyncLogRetrieval(NORMAL_FOLDER_NAME, NORMAL_FILE_NAME, NORMAL_TARGET_TMP);
	}
	
	@Test
	public void testRSyncLogRetrievalWithSpaceInFolderToSynchronize() throws IOException {
		testRsyncLogRetrieval(SOURCE_FOLDER_WITH_SPECIAL_CHARS, NORMAL_FILE_NAME, NORMAL_TARGET_TMP);
	}
	
	@Test
	public void testRSyncLogRetrievalWithSpaceInFileToSynchronize() throws IOException {
		testRsyncLogRetrieval(NORMAL_FOLDER_NAME, FILE_NAME_SPECIAL_CHARS, NORMAL_TARGET_TMP);
	}
	
	@Test
	public void testRSyncLogRetrievalWithSpaceInTargetDirectory() throws IOException {
		testRsyncLogRetrieval(NORMAL_FOLDER_NAME, NORMAL_FILE_NAME, TARGET_FOLDER_WITH_SPECIAL_CHARS);
	}
	
	@Test
	public void testRSyncLogRetrievalWithSpaceEverywhere() throws IOException {
		testRsyncLogRetrieval(SOURCE_FOLDER_WITH_SPECIAL_CHARS, FILE_NAME_SPECIAL_CHARS, TARGET_FOLDER_WITH_SPECIAL_CHARS);
	}

	protected void testRsyncLogRetrieval(String folderToSynchronizeName, String fileNameToSynchronize, String targetFolderPath) throws IOException {
		File localTempDir = tmpFolder.newFolder(folderToSynchronizeName);
		// create a dummy file to be sure there will be something to rsync
		final String fileName = File.createTempFile(fileNameToSynchronize, ".txt", localTempDir).getName();
		
		// run the rsync and collect the logs
		 List<String> logs = pod.accept(new CapabilityVisitor<IRSyncable, List<String>>() {

			@Override
			public List<String> visit(IRSyncable cap) {
				try {
					final BufferedReader reader = new BufferedReader(new InputStreamReader(
							cap.sync(new LocalPeer(localTempDir.getAbsolutePath()), new PodPeer(targetFolderPath, pod), OpenShiftBinaryOption.SKIP_TLS_VERIFY)));
					List<String> logs = IOUtils.readLines(reader);
					// wait until end of 'rsync'
					cap.await();
					return logs;
				} catch (Exception e) {
					LOG.error("Exception rsyncing to pod:",e);
				}
				return new ArrayList<>();
			}

		}, new ArrayList<>());
		if(LOG.isDebugEnabled()) {
			LOG.debug("**** RSync Logs ****");
			logs.forEach(l->LOG.debug(l));
		}
		// then
		// verify that the logs contain a message about the dummy file
		assertThat(logs).isNotEmpty();
		assertThat(logs.stream().anyMatch(line -> line.contains(fileName))).isTrue();
	}
}
