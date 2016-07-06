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

import static org.junit.Assert.*;

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

/**
 * 
 * @author jeff.cantrill
 *
 */
public class ImageStreamImportCapabilityIntegrationTest {

	private IImageStreamImportCapability cap;
	private IProject project;
	private IClient client;
	private IntegrationTestHelper helper = new IntegrationTestHelper();
	
	@Before
	public void setUp() throws Exception {
		client = helper.createClientForBasicAuth();
		project = helper.generateProject(client);
		cap = new ImageStreamImportCapability(project, client);
	}
	
	@After
	public void tearDown() {
		IntegrationTestHelper.cleanUpResource(client, project);
	}

	@Test
	public void testImportImageForExistingImage() {
		DockerImageURI image = new DockerImageURI("openshift/hello-openshift");
		IImageStreamImport imported = cap.importImageMetadata(image);
		assertNotNull(imported);
		IStatus status = imported.getImageStatus().iterator().next();
		assertTrue(status.isSuccess());
	}

	@Test
	public void testImportImageForUnknownImage() {
		DockerImageURI image = new DockerImageURI("openshift/hello-openshifts");
		IImageStreamImport imported = cap.importImageMetadata(image);
		Assert.assertNotNull(imported);
		IStatus status = imported.getImageStatus().iterator().next();
		assertEquals(IHttpConstants.STATUS_UNAUTHORIZED, status.getCode()); //exp code when image does not exist
	}

}
