/*******************************************************************************
 * Copyright (c) 2016 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/

package com.openshift.internal.restclient.capability.resources;

import static com.openshift.restclient.utils.ResourceTestHelper.givenDeployConfigIsVersion;
import static com.openshift.restclient.utils.ResourceTestHelper.givenResourceIsAnnotatedWith;
import static com.openshift.restclient.utils.ResourceTestHelper.thenResourceShouldBeUpdated;
import static com.openshift.restclient.utils.ResourceTestHelper.thenResourceShouldNotBeUpdated;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.openshift.restclient.IClient;
import com.openshift.restclient.NotFoundException;
import com.openshift.restclient.OpenShiftException;
import com.openshift.restclient.capability.resources.IDeployCapability;
import com.openshift.restclient.http.IHttpConstants;
import com.openshift.restclient.model.IDeploymentConfig;
import com.openshift.restclient.model.IReplicationController;
import com.openshift.restclient.model.IStatus;

@RunWith(MockitoJUnitRunner.class)
public class DeployCapabilityTest {

    private static final String NAMESPACE = "aNamespace";
    private static final int VERSION = 1986;
    private static final String NAME = "aDCName";

    private IDeployCapability cap;
    @Mock
    private IDeploymentConfig config;
    @Mock
    private IReplicationController deployment;
    @Mock
    private IClient client;

    @Before
    public void setUp() throws Exception {
        cap = new DeployCapability(config, client);
        when(config.getNamespaceName()).thenReturn(NAMESPACE);
        when(config.getName()).thenReturn(NAME);

        givenDeployConfigIsVersion(config, VERSION);
    }

    @Test
    public void testIsSupported() {
        assertTrue("Exp. the capability to be supported", cap.isSupported());
    }

    @SuppressWarnings("unchecked")
    @Test(expected = OpenShiftException.class)
    public void testThrowsErrorWhenUnableToFindLatestDeployment() {
        when(client.get(anyString(), anyString(), anyString())).thenThrow(OpenShiftException.class);
        whenDeploying();
    }

    @Test
    public void testWhenLatestDeploymentNotFound() {
        givenTheLatestDeploymentIsNotFound();
        whenDeploying();
        thenVersionShouldIncrease(config);
        thenResourceShouldBeUpdated(client, config);
    }

    @Test
    public void testConfigNotUpdatedWhenAlreadyInProgress() {
        givenTheDeploymentIsRetrieved();
        givenDeploymentStatusIs("New");
        whenDeploying();
        thenResourceShouldNotBeUpdated(client, config);
        thenVersionShouldNotBeIncreased(config);
    }

    @Test
    public void testStartsNewDeploymentWhenPreviousFailed() {
        givenTheDeploymentIsRetrieved();
        givenDeploymentStatusIs("Failed");
        whenDeploying();
        thenVersionShouldIncrease(config);
        thenResourceShouldBeUpdated(client, config);
    }

    @Test
    public void testStartsNewDeploymentWhenPreviousComplete() {
        givenTheDeploymentIsRetrieved();
        givenDeploymentStatusIs("Complete");
        whenDeploying();
        thenVersionShouldIncrease(config);
        thenResourceShouldBeUpdated(client, config);
    }

    private void thenVersionShouldIncrease(IDeploymentConfig config) {
        int newVersion = config.getLatestVersionNumber() + 1;
        verify(config, times(1)).setLatestVersionNumber(newVersion);
    }

    private void thenVersionShouldNotBeIncreased(IDeploymentConfig config) {
        int newVersion = config.getLatestVersionNumber() + 1;
        verify(config, times(0)).setLatestVersionNumber(newVersion);
    }

    private void whenDeploying() {
        cap.deploy();
    }

    private void givenDeploymentStatusIs(String status) {
        givenResourceIsAnnotatedWith(deployment, IReplicationController.DEPLOYMENT_PHASE, status);
    }

    private void givenTheDeploymentIsRetrieved() {
        when(client.get(anyString(), anyString(), anyString())).thenReturn(deployment);
    }

    private void givenTheLatestDeploymentIsNotFound() {
        IStatus status = mock(IStatus.class);
        when(status.getCode()).thenReturn(IHttpConstants.STATUS_NOT_FOUND);
        NotFoundException e = new NotFoundException(null, status, "Not Found");
        when(client.get(anyString(), anyString(), anyString())).thenThrow(e);
    }
}
