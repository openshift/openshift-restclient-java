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

package com.openshift.internal.restclient.model.build;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.jboss.dmr.ModelNode;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.openshift.internal.restclient.model.BuildConfig;
import com.openshift.restclient.IClient;
import com.openshift.restclient.IResourceFactory;
import com.openshift.restclient.ResourceKind;
import com.openshift.restclient.model.IBuildConfig;
import com.openshift.restclient.model.IObjectReference;
import com.openshift.restclient.model.build.BuildTriggerType;
import com.openshift.restclient.model.build.IBuildTrigger;
import com.openshift.restclient.model.build.IGitBuildSource;
import com.openshift.restclient.model.build.ISourceBuildStrategy;
import com.openshift.restclient.model.build.IWebhookTrigger;

@RunWith(MockitoJUnitRunner.class)

public class BuildConfigBuilderTest {

    @Mock
    private IClient client;
    @Mock
    private IResourceFactory factory;
    private IBuildConfig bc;
    private BuildConfig bcImpl;

    @Before
    public void setUp() throws Exception {
        bcImpl = new BuildConfig(new ModelNode(), client, Collections.emptyMap());
        when(client.getResourceFactory()).thenReturn(factory);
        when(factory.stub(eq(ResourceKind.BUILD_CONFIG), anyString(), anyString())).thenReturn(bcImpl);

    }

    @Test
    public void testBuild() {
        bc = new BuildConfigBuilder(client).named("foo").inNamespace("aNamespace").buildOnConfigChange(true)
                .buildOnImageChange(true).buildOnSourceChange(true).fromGitSource()
                .fromGitUrl("https://foo/bar/repo.git").usingGitReference("branch").inContextDir("root/directory").end()
                .usingSourceStrategy().fromImageStreamTag("builder:latest").inNamespace("other").end()
                .toImageStreamTag("foo/target:latest").build();

        List<String> triggerTypes = Arrays.asList(BuildTriggerType.CONFIG_CHANGE, BuildTriggerType.GENERIC,
                BuildTriggerType.GITHUB, BuildTriggerType.IMAGE_CHANGE);
        List<IBuildTrigger> triggers = bc.getBuildTriggers();
        assertEquals("Exp. all the allowable triggers", triggerTypes.size(), triggers.size());
        triggers.stream()
                .forEach(t -> assertTrue(String.format("%s is not in expected types %s", t.getType(), triggerTypes),
                        triggerTypes.contains(t.getType())));
        triggers.stream().filter(
            t -> t.getType().equals(BuildTriggerType.GENERIC) || t.getType().equals(BuildTriggerType.GITHUB))
                .forEach(t -> assertTrue("Exp. the secret to not be blank",
                        StringUtils.isNotBlank(((IWebhookTrigger) t).getSecret())));
        IGitBuildSource source = bc.getBuildSource();
        assertEquals("https://foo/bar/repo.git", source.getURI());
        assertEquals("branch", source.getRef());
        assertEquals("root/directory", source.getContextDir());

        ISourceBuildStrategy strategy = bc.getBuildStrategy();
        assertEquals("builder:latest", strategy.getImage().toString());
        assertEquals("other", strategy.getFromNamespace());

        IObjectReference out = bc.getBuildOutputReference();
        assertEquals(ResourceKind.IMAGE_STREAM_TAG, out.getKind());
        assertEquals("target:latest", out.getName());
        assertEquals("foo", out.getNamespace());
    }

}
