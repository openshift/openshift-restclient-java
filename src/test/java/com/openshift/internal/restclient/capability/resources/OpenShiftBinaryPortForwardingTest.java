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
import static com.openshift.internal.restclient.capability.resources.testutils.BinaryCapabilityTestMocks.mockPortPair;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

import java.net.MalformedURLException;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import com.openshift.restclient.IClient;
import com.openshift.restclient.capability.IBinaryCapability.SkipTlsVerify;
import com.openshift.restclient.capability.resources.IPortForwardable.PortPair;
import com.openshift.restclient.model.IPod;

public class OpenShiftBinaryPortForwardingTest {

    private static final int LOCAL_PORT1 = 8080;
    private static final int REMOTE_PORT1 = 80;

    private static final int LOCAL_PORT2 = 8443;
    private static final int REMOTE_PORT2 = 43;

    private IPod pod;

    private OpenShiftBinaryPortForwarding binaryPortForwarding;

    @Before
    public void before() throws MalformedURLException {
        IClient client = mockClient();
        this.pod = mockPod();
        this.binaryPortForwarding = createBinaryPortForwarding(pod, client);
    }

    private OpenShiftBinaryPortForwarding createBinaryPortForwarding(IPod pod, IClient client) {
        OpenShiftBinaryPortForwarding portForwarding = spy(new OpenShiftBinaryPortForwarding(pod, client));
        doReturn(OC_LOCATION).when(portForwarding).getOpenShiftBinaryLocation();
        doReturn(null).when(portForwarding).startProcess(any(ProcessBuilder.class));
        return portForwarding;
    }

    @Test
    public void shouldBuildCommandLineWithoutSkipSSL() {
        // given
        ArgumentCaptor<ProcessBuilder> processBuilderArgument = ArgumentCaptor.forClass(ProcessBuilder.class);
        List<PortPair> ports = Arrays.asList(mockPortPair(LOCAL_PORT2, REMOTE_PORT2));
        // when
        binaryPortForwarding.forwardPorts(ports);
        // then
        verify(binaryPortForwarding).startProcess(processBuilderArgument.capture());
        ProcessBuilder builder = processBuilderArgument.getValue();
        assertThat(builder.command())
                .isEqualTo(Arrays.asList(OC_LOCATION, OpenShiftBinaryPortForwarding.PORT_FORWARD_COMMAND,
                        "--token=" + TOKEN, "--server=" + SERVER_URL.toString(), "-n", POD_NAMESPACE, POD_NAME,
                        LOCAL_PORT2 + ":" + REMOTE_PORT2));
    }

    @Test
    public void shouldBuildCommandLineWith2PortsSkipSSL() {
        // given
        ArgumentCaptor<ProcessBuilder> processBuilderArgument = ArgumentCaptor.forClass(ProcessBuilder.class);
        List<PortPair> ports = Arrays.asList(mockPortPair(LOCAL_PORT1, REMOTE_PORT1),
                mockPortPair(LOCAL_PORT2, REMOTE_PORT2));
        // when
        binaryPortForwarding.forwardPorts(ports, new SkipTlsVerify());
        // then
        verify(binaryPortForwarding).startProcess(processBuilderArgument.capture());
        ProcessBuilder builder = processBuilderArgument.getValue();
        assertThat(builder.command()).isEqualTo(
                Arrays.asList(OC_LOCATION, OpenShiftBinaryPortForwarding.PORT_FORWARD_COMMAND, "--token=" + TOKEN,
                        "--server=" + SERVER_URL.toString(), "--insecure-skip-tls-verify=true", "-n", POD_NAMESPACE,
                        POD_NAME, LOCAL_PORT1 + ":" + REMOTE_PORT1, LOCAL_PORT2 + ":" + REMOTE_PORT2));
    }
}
