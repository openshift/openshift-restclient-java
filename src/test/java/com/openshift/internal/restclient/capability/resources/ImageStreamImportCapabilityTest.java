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

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.openshift.restclient.IClient;
import com.openshift.restclient.IResourceFactory;
import com.openshift.restclient.capability.resources.IImageStreamImportCapability;
import com.openshift.restclient.images.DockerImageURI;
import com.openshift.restclient.model.IProject;
import com.openshift.restclient.model.IStatus;
import com.openshift.restclient.model.image.IImageStreamImport;

@RunWith(MockitoJUnitRunner.class)
public class ImageStreamImportCapabilityTest {

    private IImageStreamImportCapability cap;
    @Mock
    private IProject project;
    @Mock
    private IClient client;
    @Mock
    private IResourceFactory factory;
    @Mock
    private IImageStreamImport streamImport;
    @Mock
    private IStatus status;

    @Before
    public void setUp() throws Exception {
        when(project.getName()).thenReturn("aProjectName");
        when(client.getResourceFactory()).thenReturn(factory);
        when(factory.stub(anyString(), anyString(), anyString())).thenReturn(streamImport);
        when(client.create(any(IImageStreamImport.class))).thenReturn(streamImport);

        when(status.getStatus()).thenReturn("Success");
        when(streamImport.getImageStatus()).thenReturn(Arrays.asList(status));

        cap = new ImageStreamImportCapability(project, client);
    }

    @Test
    public void testImportImageInfo() {
        DockerImageURI image = new DockerImageURI("foo/hello-world");
        IImageStreamImport imported = cap.importImageMetadata(image);
        assertEquals(imported, streamImport);

        verify(client).create(streamImport);
        verify(streamImport).addImage("DockerImage", image);

    }

}
