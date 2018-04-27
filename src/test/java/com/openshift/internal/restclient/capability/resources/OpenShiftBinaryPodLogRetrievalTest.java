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

import com.openshift.internal.restclient.capability.resources.OpenShiftBinaryPodLogRetrieval.PodLogs;
import com.openshift.restclient.IClient;
import com.openshift.restclient.capability.IBinaryCapability;
import com.openshift.restclient.capability.IBinaryCapability.OpenShiftBinaryOption;
import com.openshift.restclient.model.IPod;

public class OpenShiftBinaryPodLogRetrievalTest {

    private static final String CONTAINER_NAME = "smurfland";

    private IClient client;
    private IPod pod;

    @Before
    public void before() throws MalformedURLException {
        this.client = mockClient();
        this.pod = mockPod();
    }

    private OpenShiftBinaryPodLogRetrieval.PodLogs createPodLogs(boolean follow, IPod pod, IClient client,
            OpenShiftBinaryOption... options) {
        OpenShiftBinaryPodLogRetrieval.PodLogs podLogs = spy(
                new OpenShiftBinaryPodLogRetrieval(pod, client).new PodLogs(client, true, CONTAINER_NAME, options));
        doReturn(OC_LOCATION).when(podLogs).getOpenShiftBinaryLocation();
        doReturn(null).when(podLogs).startProcess(any(ProcessBuilder.class));
        return podLogs;
    }

    @Test
    public void shouldBuildCommandLineWithoutSkipTlsVerify() {
        // given
        ArgumentCaptor<ProcessBuilder> processBuilderArgument = ArgumentCaptor.forClass(ProcessBuilder.class);
        PodLogs podLogs = createPodLogs(false, pod, client);
        // when
        podLogs.getLogs();
        // then
        verify(podLogs).startProcess(processBuilderArgument.capture());
        ProcessBuilder builder = processBuilderArgument.getValue();
        assertThat(builder.command()).isEqualTo(Arrays.asList(OC_LOCATION, PodLogs.LOGS_COMMAND, "--token=" + TOKEN,
                "--server=" + SERVER_URL.toString(), POD_NAME, "-n", POD_NAMESPACE, "-f", "-c", CONTAINER_NAME));
    }

    @Test
    public void shouldBuildCommandLineWithSkipTlsVerify() {
        // given
        ArgumentCaptor<ProcessBuilder> processBuilderArgument = ArgumentCaptor.forClass(ProcessBuilder.class);
        PodLogs podLogs = createPodLogs(false, pod, client, IBinaryCapability.SKIP_TLS_VERIFY);
        // when
        podLogs.getLogs();
        // then
        verify(podLogs).startProcess(processBuilderArgument.capture());
        ProcessBuilder builder = processBuilderArgument.getValue();
        assertThat(builder.command()).isEqualTo(Arrays.asList(OC_LOCATION, PodLogs.LOGS_COMMAND, "--token=" + TOKEN,
                "--server=" + SERVER_URL.toString(), "--insecure-skip-tls-verify=true", POD_NAME, "-n", POD_NAMESPACE,
                "-f", "-c", CONTAINER_NAME));
    }

}
