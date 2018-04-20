/*******************************************************************************
 * Copyright (c) 2018 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/

package com.openshift.internal.restclient.capability.resources;

import static com.openshift.internal.restclient.capability.resources.testutils.BinaryCapabilityTestMocks.OC_LOCATION;
import static com.openshift.internal.restclient.capability.resources.testutils.BinaryCapabilityTestMocks.POD_NAME;
import static com.openshift.internal.restclient.capability.resources.testutils.BinaryCapabilityTestMocks.POD_NAMESPACE;
import static com.openshift.internal.restclient.capability.resources.testutils.BinaryCapabilityTestMocks.SERVER_URL;
import static com.openshift.internal.restclient.capability.resources.testutils.BinaryCapabilityTestMocks.TOKEN;
import static com.openshift.internal.restclient.capability.resources.testutils.BinaryCapabilityTestMocks.mockClient;
import static com.openshift.internal.restclient.capability.resources.testutils.BinaryCapabilityTestMocks.mockPod;
import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

import java.net.MalformedURLException;
import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import com.openshift.restclient.IClient;
import com.openshift.restclient.capability.IBinaryCapability.SkipTlsVerify;
import com.openshift.restclient.capability.resources.IRSyncable.Exclude;
import com.openshift.restclient.capability.resources.IRSyncable.GitFolderExclude;
import com.openshift.restclient.capability.resources.IRSyncable.LocalPeer;
import com.openshift.restclient.capability.resources.IRSyncable.NoPerms;
import com.openshift.restclient.capability.resources.IRSyncable.Peer;
import com.openshift.restclient.capability.resources.IRSyncable.PodPeer;
import com.openshift.restclient.model.IPod;

public class OpenShiftBinaryRSyncTest {

    private static final String LOCAL_PATH = "/local/42";
    private static final String POD_PATH = "/deployment/42";

    private IPod pod;

    private OpenShiftBinaryRSync binaryRsync;

    @Before
    public void before() throws MalformedURLException {
        IClient client = mockClient();
        this.pod = mockPod();
        this.binaryRsync = createBinaryRSync(client);
    }

    private OpenShiftBinaryRSync createBinaryRSync(IClient client) {
        OpenShiftBinaryRSync binaryRsync = spy(new OpenShiftBinaryRSync(client));
        doReturn(OC_LOCATION).when(binaryRsync).getOpenShiftBinaryLocation();
        doReturn(null).when(binaryRsync).startProcess(any(ProcessBuilder.class));
        return binaryRsync;
    }

    @Test
    public void shouldBuildCommandLineWithoutSkipSSL() {
        // given
        ArgumentCaptor<ProcessBuilder> processBuilderArgument = ArgumentCaptor.forClass(ProcessBuilder.class);
        Peer localPeer = new LocalPeer(LOCAL_PATH);
        PodPeer podPeer = new PodPeer(POD_PATH, pod);
        // when
        binaryRsync.sync(localPeer, podPeer);
        // then
        verify(binaryRsync).startProcess(processBuilderArgument.capture());
        ProcessBuilder builder = processBuilderArgument.getValue();
        assertThat(builder.command()).isEqualTo(Arrays.asList(OC_LOCATION, OpenShiftBinaryRSync.RSYNC_COMMAND,
                "--token=" + TOKEN, "--server=" + SERVER_URL.toString(), "-n", POD_NAMESPACE, LOCAL_PATH,
                POD_NAME + ":" + POD_PATH));
    }

    @Test
    public void shouldBuildCommandLineWithSkipSSLNoPermsGitExclude() {
        // given
        ArgumentCaptor<ProcessBuilder> processBuilderArgument = ArgumentCaptor.forClass(ProcessBuilder.class);
        Peer localPeer = new LocalPeer(LOCAL_PATH);
        PodPeer podPeer = new PodPeer(POD_PATH, pod);
        // when
        binaryRsync.sync(localPeer, podPeer, new SkipTlsVerify(), new NoPerms(), new GitFolderExclude());
        // then
        verify(binaryRsync).startProcess(processBuilderArgument.capture());
        ProcessBuilder builder = processBuilderArgument.getValue();
        assertThat(builder.command())
                .isEqualTo(Arrays.asList(OC_LOCATION, OpenShiftBinaryRSync.RSYNC_COMMAND, "--token=" + TOKEN,
                        "--server=" + SERVER_URL.toString(), "-n", POD_NAMESPACE, "--insecure-skip-tls-verify=true",
                        "--no-perms=true", "--exclude=.git", LOCAL_PATH, POD_NAME + ":" + POD_PATH));
    }

    @Test
    public void shouldBuildCommandLineWithExcludeDotGitDotNpm() {
        // given
        ArgumentCaptor<ProcessBuilder> processBuilderArgument = ArgumentCaptor.forClass(ProcessBuilder.class);
        Peer localPeer = new LocalPeer(LOCAL_PATH);
        PodPeer podPeer = new PodPeer(POD_PATH, pod);
        // when
        binaryRsync.sync(localPeer, podPeer, new Exclude(".git", ".npm"));
        // then
        verify(binaryRsync).startProcess(processBuilderArgument.capture());
        ProcessBuilder builder = processBuilderArgument.getValue();
        assertThat(builder.command()).isEqualTo(Arrays.asList(OC_LOCATION, OpenShiftBinaryRSync.RSYNC_COMMAND,
                "--token=" + TOKEN, "--server=" + SERVER_URL.toString(), "-n", POD_NAMESPACE, "--exclude=.git",
                "--exclude=.npm", LOCAL_PATH, POD_NAME + ":" + POD_PATH));
    }
}
