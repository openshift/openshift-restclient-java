/*******************************************************************************
 * Copyright (c) 2015 Red Hat, Inc. Distributed under license by Red Hat, Inc.
 * All rights reserved. This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: Red Hat, Inc.
 ******************************************************************************/

package com.openshift.internal.restclient.model.v1;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;

import org.jboss.dmr.ModelNode;
import org.junit.Before;
import org.junit.Test;

import com.openshift.internal.restclient.model.ImageStream;
import com.openshift.internal.restclient.model.properties.ResourcePropertiesRegistry;
import com.openshift.restclient.IClient;
import com.openshift.restclient.PredefinedResourceKind;
import com.openshift.restclient.images.DockerImageURI;
import com.openshift.restclient.model.IImageStream;
import com.openshift.restclient.model.image.ITagReference;
import com.openshift.restclient.utils.Samples;

public class ImageStreamTest {
    private static final String VERSION = "v1";
    private static IClient client;
    private IImageStream stream;
    private ModelNode node;

    @Before
    public void setup() {
        client = mock(IClient.class);
        node = ModelNode.fromJSONString(Samples.V1_IMAGE_STREAM.getContentAsString());
        stream = new ImageStream(node, client,
                ResourcePropertiesRegistry.getInstance().get(VERSION, PredefinedResourceKind.IMAGE_STREAM.getIdentifier()));
    }

    @Test
    public void testGetTags() {
        Collection<String> tags = stream.getTags().stream().map(tr -> tr.getName()).collect(Collectors.toList());
        assertArrayEquals(new Object[] { "8.1", "latest" }, tags.toArray());
    }

    @Test
    public void testAddTag() {
        ITagReference tag = stream.addTag("1234", PredefinedResourceKind.IMAGE_STREAM_TAG.getIdentifier(), "foo/bar");
        Optional<ITagReference> actTag = stream.getTags().stream().filter(t -> "1234".equals(t.getName())).findFirst();
        assertTrue("Exp. the tag to have been added", actTag.isPresent());
        assertEquals(tag.toJson(), actTag.get().toJson());
    }

    @Test
    public void testAddTagWithNamespace() {
        ITagReference tag = stream.addTag("1234", PredefinedResourceKind.IMAGE_STREAM_TAG.getIdentifier(), "foo/bar", "fromNmspc");
        Optional<ITagReference> actTag = stream.getTags().stream().filter(t -> "1234".equals(t.getName())).findFirst();
        assertTrue("Exp. the tag to have been added", actTag.isPresent());
        assertEquals(tag.toJson(), actTag.get().toJson());
    }

    @Test
    public void getDockerImageRepository() {
        DockerImageURI uri = new DockerImageURI("172.30.224.48:5000/openshift/wildfly:latest");
        assertEquals(uri, stream.getDockerImageRepository());
        node.get("spec").clear();
        assertEquals(uri, stream.getDockerImageRepository());
    }

    @Test
    public void setDockerImageRepository() {
        DockerImageURI newUri = new DockerImageURI("172.30.244.213:5000/tests/origin-ruby-sample");
        stream.setDockerImageRepository(newUri);
        assertEquals(newUri, stream.getDockerImageRepository());
    }

}
