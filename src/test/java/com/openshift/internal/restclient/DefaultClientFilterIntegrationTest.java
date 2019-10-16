/*******************************************************************************
* Copyright (c) 2015-2019 Red Hat, Inc. Distributed under license by Red Hat, Inc.
* All rights reserved. This program is made available under the terms of the
* Eclipse Public License v1.0 which accompanies this distribution, and is
* available at http://www.eclipse.org/legal/epl-v10.html
* 
* Contributors: Red Hat, Inc.
******************************************************************************/

package com.openshift.internal.restclient;

import static com.openshift.restclient.ResourceKind.BUILD_CONFIG;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.openshift.restclient.IClient;
import com.openshift.restclient.model.IBuildConfig;
import com.openshift.restclient.model.IProject;
import com.openshift.restclient.model.IResource;

public class DefaultClientFilterIntegrationTest {

    private static final Logger LOG = LoggerFactory.getLogger(DefaultClientFilterIntegrationTest.class);

    private static IClient client;
    private static IProject project;
    private static Collection<IBuildConfig> bcs;
    private static IntegrationTestHelper helper = new IntegrationTestHelper();

    @BeforeClass
    public static void setup() {
        client = helper.createClientForBasicAuth();
        project = helper.getOrCreateIntegrationTestProject(client);
        bcs = helper.createResources(client, 
                helper.stubBuildConfig(client, project.getNamespaceName(), "build1", null, new HashMap<String, String>() {{
                        put("foo", "yes");
                        put("bar", "no");
                        put("baz", "no");
                    }
                }),
                helper.stubBuildConfig(client, project.getNamespaceName(), "build2", null, new HashMap<String, String>() {{
                        put("foo", "no");
                        put("bar", "yes");
                    }
                }),
                helper.stubBuildConfig(client, project.getNamespaceName(), "build3", null, new HashMap<String, String>() {{
                        put("foo", "yes");
                        put("bar", "yes");
                    }
                }),
        helper.stubBuildConfig(client, project.getNamespaceName(), "build4", null, new HashMap<>()));

    }

    @AfterClass
    public static void cleanup() {
        helper.cleanUpResources(client, bcs);
    }

    @Test
    public void testFilteringWithOneLabel() {
        List<IBuildConfig> list = client.list(BUILD_CONFIG, project.getNamespaceName(), new HashMap<String, String>() {
            {
                put("foo", "yes");
            }
        });

        assertEquals(2, list.size());
        Set<String> names = list.stream().map(IResource::getName).collect(Collectors.toSet());
        assertTrue("Should contain build1", names.contains("build1"));
        assertTrue("Should contain build3", names.contains("build3"));
    }

    @Test
    public void testFilteringWithTwoLabel() {
        List<IBuildConfig> list = client.list(BUILD_CONFIG, project.getNamespaceName(), new HashMap<String, String>() {
            {
                put("foo", "yes");
                put("bar", "no");
            }
        });

        assertEquals(1, list.size());
        IBuildConfig bc = list.get(0);
        assertEquals("build1", bc.getName());
    }

    @Test
    public void testFilteringWithLabelExist() {
        List<IBuildConfig> list = client.list(BUILD_CONFIG, project.getNamespaceName(), "baz");

        assertEquals(1, list.size());
        IBuildConfig bc = list.get(0);
        assertEquals("build1", bc.getName());
    }

    @Test
    public void testFilteringWithLabelNotExist() {
        List<IBuildConfig> list = client.list(BUILD_CONFIG, project.getNamespaceName(), "!baz");

        Set<String> names = list.stream().map(IResource::getName).collect(Collectors.toSet());

        assertThat(names).contains("build2", "build3", "build4").doesNotContain("build1");
    }

    @Test
    public void testFilteringWithLabelNotEqualTo() {
        List<IBuildConfig> list = client.list(BUILD_CONFIG, project.getNamespaceName(), "foo != yes");

        Set<String> names = list.stream().map(IResource::getName).collect(Collectors.toSet());

        assertThat(names).contains("build2", "build4").doesNotContain("build1", "build3");
    }

    @Test
    public void testFilteringWithLabelCombinedLabelQuery() {
        List<IBuildConfig> list = client.list(BUILD_CONFIG, project.getNamespaceName(), "foo,bar=no");

        assertThat(list).allMatch(bc -> "build1".equals(bc.getName()));
    }
}
