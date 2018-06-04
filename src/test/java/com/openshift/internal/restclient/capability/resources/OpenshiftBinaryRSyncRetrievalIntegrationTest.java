package com.openshift.internal.restclient.capability.resources;
/*******************************************************************************
 * Copyright (c) 2015-2018 Red Hat, Inc.
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
import static org.junit.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.IOUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.openshift.internal.restclient.IntegrationTestHelper;
import com.openshift.internal.restclient.api.capabilities.PodExecIntegrationTest;
import com.openshift.internal.restclient.model.Pod;
import com.openshift.restclient.IClient;
import com.openshift.restclient.PredefinedResourceKind;
import com.openshift.restclient.api.capabilities.IPodExec;
import com.openshift.restclient.capability.CapabilityVisitor;
import com.openshift.restclient.capability.IBinaryCapability;
import com.openshift.restclient.capability.IBinaryCapability.SkipTlsVerify;
import com.openshift.restclient.capability.IStoppable;
import com.openshift.restclient.capability.resources.IRSyncable;
import com.openshift.restclient.capability.resources.IRSyncable.LocalPeer;
import com.openshift.restclient.capability.resources.IRSyncable.Peer;
import com.openshift.restclient.capability.resources.IRSyncable.PodPeer;
import com.openshift.restclient.model.IResource;

/**
 * 
 * @author Xavier Coulon
 *
 */
public class OpenshiftBinaryRSyncRetrievalIntegrationTest {
    private static final String TARGET_FOLDER_WITH_SPECIAL_CHARS = "/tmp/OpenshiftBinaryRSyncRetrievalIntegrationTest/with sp@cé/";
    private static final String FILE_NAME_SPECIAL_CHARS = "test with spéci@l characters and spaces";
    private static final String SOURCE_FOLDER_WITH_SPECIAL_CHARS = "with sp@cé in path";

    private static final String NORMAL_TARGET_TMP = "/tmp/OpenshiftBinaryRSyncRetrievalIntegrationTest/withoutspace/";
    private static final String NORMAL_FILE_NAME = "normalFileName";
    private static final String NORMAL_FOLDER_NAME = "normalFolderName";

    private static final Logger LOG = LoggerFactory.getLogger(OpenshiftBinaryRSyncRetrievalIntegrationTest.class);

    private IntegrationTestHelper helper = new IntegrationTestHelper();
    private IClient client;

    @Rule
    public TemporaryFolder tmpFolder = new TemporaryFolder();

    private Pod pod;
    private String podFolderToClean = null;

    @Before
    public void setUp() throws Exception {
        // given
        System.setProperty(IBinaryCapability.OPENSHIFT_BINARY_LOCATION, helper.getOpenShiftLocation());
        client = helper.createClientForBasicAuth();

        List<IResource> pods = client.list(PredefinedResourceKind.POD.getIdentifier(), "default");
        pod = (Pod) pods.stream().filter(p -> p.getName().startsWith("docker-registry")).findFirst().orElse(null);
        assertNotNull("Did not find the registry pod to which to rsync", pod);
    }

    @After
    public void tearDown() throws Exception {
        if (podFolderToClean != null) {
            execOnPod(new String[] { "rm", "-r", podFolderToClean });
        }
    }

    @Test
    public void testRSyncLogRetrieval() throws IOException, InterruptedException {
        testRsyncLogRetrieval(NORMAL_FOLDER_NAME, NORMAL_FILE_NAME, NORMAL_TARGET_TMP);
    }

    @Test
    public void testRSyncLogRetrievalWithSpaceInFolderToSynchronize() throws Exception {
        testRsyncLogRetrieval(SOURCE_FOLDER_WITH_SPECIAL_CHARS, NORMAL_FILE_NAME, NORMAL_TARGET_TMP);
    }

    @Test
    public void testRSyncLogRetrievalWithSpaceInFileToSynchronize() throws Exception {
        testRsyncLogRetrieval(NORMAL_FOLDER_NAME, FILE_NAME_SPECIAL_CHARS, NORMAL_TARGET_TMP);
    }

    @Test
    public void testRSyncLogRetrievalWithSpaceInTargetDirectory() throws Exception {
        testRsyncLogRetrieval(NORMAL_FOLDER_NAME, NORMAL_FILE_NAME, TARGET_FOLDER_WITH_SPECIAL_CHARS);
    }

    @Test
    public void testRSyncLogRetrievalWithSpaceEverywhere() throws Exception {
        testRsyncLogRetrieval(SOURCE_FOLDER_WITH_SPECIAL_CHARS, FILE_NAME_SPECIAL_CHARS,
                TARGET_FOLDER_WITH_SPECIAL_CHARS);
    }

    protected void testRsyncLogRetrieval(String folderToSynchronizeName, String fileNameToSynchronize,
            String targetFolderPath) throws IOException, InterruptedException {
        podFolderToClean = targetFolderPath;
        execOnPod(new String[] { "mkdir", "-p", targetFolderPath });
        File localTempDir = tmpFolder.newFolder(folderToSynchronizeName);
        // Create a dummy file locally to be sure there will be something to rsync from
        // Local to Remote
        File tmpFile = File.createTempFile(fileNameToSynchronize, ".txt", localTempDir);
        final String fileName = tmpFile.getName();
        LocalPeer localPeer = new LocalPeer(localTempDir.getAbsolutePath() + File.separator);
        PodPeer podPeer = new PodPeer(targetFolderPath, pod);

        // Check Local to Remote
        rsyncAndCheck(fileName, localPeer, podPeer);
        tmpFile.delete();

        // Create a dummy file locally to be sure there will be something to rsync from
        // Remote to Local
        execOnPod(new String[] { "touch", targetFolderPath + "/fileToSynchronizeBackFromPodToLocal.txt" });

        // Check Remote to Local
        rsyncAndCheck("fileToSynchronizeBackFromPodToLocal", podPeer, localPeer);
        assertThat(new File(localTempDir, "fileToSynchronizeBackFromPodToLocal.txt").exists()).isTrue();
    }

    protected void execOnPod(String[] commands) throws InterruptedException {
        PodExecIntegrationTest.TestExecListener execListener = new PodExecIntegrationTest.TestExecListener();
        final String container = pod.getContainers().iterator().next().getName();
        IPodExec.Options options = new IPodExec.Options();
        options.container(container);

        pod.accept(new CapabilityVisitor<IPodExec, IStoppable>() {

            @Override
            public IStoppable visit(IPodExec capability) {
                return capability.start(execListener, options, commands);
            }

        }, null);
        execListener.testDone.await(10, TimeUnit.SECONDS);
        assertTrue(execListener.openCalled.get());
        assertTrue(execListener.closeCalled.get());
        assertTrue(!execListener.failureCalled.get());
        assertTrue(!execListener.execErrCalled.get());
    }

    protected void rsyncAndCheck(final String fileName, Peer source, Peer destination) {
        List<String> logs = pod.accept(new CapabilityVisitor<IRSyncable, List<String>>() {

            @Override
            public List<String> visit(IRSyncable cap) {
                try {
                    final BufferedReader reader = new BufferedReader(
                            new InputStreamReader(cap.sync(source, destination, new SkipTlsVerify())));
                    List<String> logs = IOUtils.readLines(reader);
                    // wait until end of 'rsync'
                    cap.await();
                    return logs;
                } catch (Exception e) {
                    LOG.error("Exception rsyncing to pod:", e);
                }
                return new ArrayList<>();
            }

        }, new ArrayList<>());
        if (LOG.isDebugEnabled()) {
            LOG.debug("**** RSync Logs ****");
            logs.forEach(l -> LOG.debug(l));
        }
        // then verify that the logs contain a message about the dummy file
        assertThat(logs).isNotEmpty();
        assertThat(logs.stream().anyMatch(line -> line.contains(fileName))).isTrue();
    }
}
