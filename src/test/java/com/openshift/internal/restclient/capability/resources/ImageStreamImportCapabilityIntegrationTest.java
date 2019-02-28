/*******************************************************************************
 * Copyright (c) 2016-2019 Red Hat, Inc.
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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.openshift.internal.restclient.IntegrationTestHelper;
import com.openshift.restclient.IClient;
import com.openshift.restclient.capability.resources.IImageStreamImportCapability;
import com.openshift.restclient.http.IHttpConstants;
import com.openshift.restclient.images.DockerImageURI;
import com.openshift.restclient.model.IProject;
import com.openshift.restclient.model.IStatus;
import com.openshift.restclient.model.image.IImageStreamImport;

import junit.framework.Assert;

public class ImageStreamImportCapabilityIntegrationTest {

    private IImageStreamImportCapability cap;
    private IProject project;
    private IClient client;
    private IntegrationTestHelper helper = new IntegrationTestHelper();
    private IImageStreamImport imageStreamImport;

    @Before
    public void setUp() throws Exception {
        this.client = helper.createClientForBasicAuth();
        this.project = helper.getOrCreateIntegrationTestProject(client);
        this.cap = new ImageStreamImportCapability(project, client);
    }

    @After
    public void tearDown() {
        helper.cleanUpResource(client, imageStreamImport);
    }

    @Test
    public void testImportImageForExistingImage() {
        DockerImageURI image = new DockerImageURI("openshift/hello-openshift");
        this.imageStreamImport = cap.importImageMetadata(image);
        assertNotNull(imageStreamImport);
        IStatus status = imageStreamImport.getImageStatus().iterator().next();
        assertTrue(status.isSuccess());
    }

    @Test
    public void testImportImageForUnknownImage() {
        DockerImageURI image = new DockerImageURI("openshift/hello-openshifts");
        this.imageStreamImport = cap.importImageMetadata(image);
        Assert.assertNotNull(imageStreamImport);
        IStatus status = imageStreamImport.getImageStatus().iterator().next();
        assertEquals(IHttpConstants.STATUS_UNAUTHORIZED, status.getCode()); // exp code when image does not exist
    }

}
