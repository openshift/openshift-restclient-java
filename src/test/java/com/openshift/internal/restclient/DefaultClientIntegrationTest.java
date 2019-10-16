/*******************************************************************************
* Copyright (c) 2015-2019 Red Hat, Inc. Distributed under license by Red Hat, Inc.
* All rights reserved. This program is made available under the terms of the
* Eclipse Public License v1.0 which accompanies this distribution, and is
* available at http://www.eclipse.org/legal/epl-v10.html
* 
* Contributors: Red Hat, Inc.
******************************************************************************/

package com.openshift.internal.restclient;

import static com.openshift.internal.restclient.IntegrationTestHelper.MILLISECONDS_PER_SECOND;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Collections;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.openshift.internal.restclient.model.template.Template;
import com.openshift.restclient.IClient;
import com.openshift.restclient.IResourceFactory;
import com.openshift.restclient.ResourceKind;
import com.openshift.restclient.authorization.UnauthorizedException;
import com.openshift.restclient.model.IBuildConfig;
import com.openshift.restclient.model.IProject;
import com.openshift.restclient.model.IResource;
import com.openshift.restclient.model.IService;
import com.openshift.restclient.model.template.ITemplate;

public class DefaultClientIntegrationTest {

    private static final Logger LOG = LoggerFactory.getLogger(DefaultClientIntegrationTest.class);

    private IntegrationTestHelper helper = new IntegrationTestHelper();
    private IClient client;
    private IResourceFactory factory;
    private IProject project;

    @Before
    public void setup() {
        this.client = helper.createClientForBasicAuth();
        this.factory = new ResourceFactory(client);
        this.project = helper.getOrCreateIntegrationTestProject(client);
    }

    @Test
    public void testAuthContextIsAuthorizedWithValidUserNameAndPassword() {
        client = helper.createClient();
        client.getAuthorizationContext().setUserName(helper.getDefaultClusterAdminUser());
        client.getAuthorizationContext().setPassword(helper.getDefaultClusterAdminPassword());
        assertThat(client.getAuthorizationContext().isAuthorized()).isTrue();
    }

    @Test(expected = UnauthorizedException.class)
    public void testAuthContextIsAuthorizedWithoutPasswordThrows() {
        client = helper.createClient();
        client.getAuthorizationContext().setUserName(helper.getDefaultClusterAdminUser());
        client.getAuthorizationContext().isAuthorized();
    }

    @Test
    public void testListprojects() {
        assertTrue(client.list(ResourceKind.PROJECT, IntegrationTestHelper.getDefaultNamespace()).size() > 0);
    }

    @Test
    public void testReady() {
        client.getServerReadyStatus();
    }

    @Test
    public void testListTemplates() {
        Template template = null;
        IProject project = helper.getOrCreateIntegrationTestProject(client);

        try {
            template = factory.stub(ResourceKind.TEMPLATE, "mytemplate");
            template = client.create(template, project.getNamespaceName());

            assertNotNull("Exp. the template to be found but was not", helper.waitForResource(client, ResourceKind.TEMPLATE,
                    project.getName(), template.getName(), 5 * MILLISECONDS_PER_SECOND));

            List<ITemplate> list = client.list(ResourceKind.TEMPLATE, project.getName());
            assertEquals(1, list.size());
            for (ITemplate t : list) {
                LOG.debug(t.toString());
            }
        } finally {
            helper.cleanUpResource(client, template);
        }
    }

    @Test
    public void testResourceLifeCycle() {
        IService service = null;
        IService otherService = null;
        IBuildConfig bc = null;
        try {
            IService stub = helper.stubService(client,
                    project.getNamespaceName(), 
                    IntegrationTestHelper.appendRandom("some-service"),
                    6767, 6767,
                    "barpod");
            service = createAndAssert(stub);
            assertServiceEquals(stub, service);

            IService otherStub = helper.stubService(client,
                    project.getNamespaceName(),
                    IntegrationTestHelper.appendRandom("some-other-service"),
                    8787, 8787,
                    "foopod");
            otherService = createAndAssert(otherStub);

            bc = client.create(
                    helper.stubBuildConfig(client,
                            project.getNamespaceName(),
                            "test",
                            "https://github.com/openshift/origin.git",
                            Collections.emptyMap()));
            LOG.debug(String.format("Created bc: %s", bc.getName()));
            LOG.debug(String.format("Trying to delete bc: %s", bc.getName()));
            client.delete(bc);
            bc = null;
        } finally {
            helper.cleanUpResources(client, service, otherService, bc);
        }
    }

    private <R extends IResource> R createAndAssert(R stub) {
        // given
        int numberOfServices = client.list(stub.getKind(), project.getNamespaceName()).size();
        // when
        R resource = client.create(stub);
        // then
        List<R> resources = client.list(stub.getKind(), project.getNamespaceName());
        assertNotNull(resources);
        int newNumberOfServices = resources.size();
        assertEquals(numberOfServices + 1, newNumberOfServices);
        assertTrue(resources.contains(resource));
        R queriedResource = client.get(stub.getKind(), stub.getName(), stub.getNamespaceName());
        assertEquals("Expected to get the service with the correct name", 
                stub.getName(),
                queriedResource.getName());
        return resource;
    }

    private void assertServiceEquals(IService stub, IService service) {
        assertEquals(stub.getName(), service.getName());
        assertEquals(stub.getNamespace(), service.getNamespace());
        assertEquals(stub.getSelector(), service.getSelector());
        assertEquals(stub.getPort(), service.getPort());
    }
    
    @Test
    public void shouldHaveOpenShiftMasterVersion() {
        // given
        // when
        int osMajorVersion = client.getOpenShiftMajorVersion();
        // then
        assertThat(osMajorVersion).isNotEqualTo(KubernetesVersion.NO_VERSION);
    }

    @Test
    public void shouldDetectCorrectOpenShiftMasterVersion() {
        // given
        String osVersion = client.getOpenshiftMasterVersion();
        String k8Version = client.getKubernetesMasterVersion();
        // when
        int osMajorVersion = client.getOpenShiftMajorVersion();
        // then
        assertOpenShiftVersion(osMajorVersion, osVersion, k8Version);
    }

    private void assertOpenShiftVersion(int osMajorVersion, String osVersion, String k8Version) {
        if (StringUtils.length(osVersion) > 1) {
            int guessedOSMajorVersion = Integer.parseInt(osVersion.charAt(1) + "");
            assertThat(osMajorVersion).isEqualTo(guessedOSMajorVersion);
        } else if (!StringUtils.isEmpty(k8Version)) {
            int guessedOSMajorVersion = KubernetesVersion.NO_VERSION;
            int guessedK8MinorVersion = Integer.parseInt(k8Version.split("\\.")[1]);
            if (guessedK8MinorVersion <= 11) {
                guessedOSMajorVersion = 3;
            } else {
                guessedOSMajorVersion = 4;
            }
            assertThat(osMajorVersion).isEqualTo(guessedOSMajorVersion);
        } else {
            fail("Could not guess OpenShift version, neither /version/openshift nor /version are available.");
        }
    }

}
