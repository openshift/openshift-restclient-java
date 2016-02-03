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
package com.openshift.restclient.utils;

import static org.mockito.Mockito.*;

import com.openshift.restclient.IClient;
import com.openshift.restclient.model.IDeploymentConfig;
import com.openshift.restclient.model.IResource;

/**
 * Helper for BDD driven unit tests
 * @author jeff.cantrill
 *
 */
public class ResourceTestHelper {

	public static void givenResourceIsAnnotatedWith(IResource resource, String annotation, String value) {
		when(resource.isAnnotatedWith(annotation)).thenReturn(true);
		when(resource.getAnnotation(annotation)).thenReturn(value);
	}

	public static void givenDeployConfigIsVersion(IDeploymentConfig config, int version) {
		when(config.getLatestVersionNumber()).thenReturn(version);
	}

	public static void thenResourceShouldBeUpdated(IClient client, IResource config) {
		verify(client, times(1)).update(config);
	}

	public static void thenResourceShouldNotBeUpdated(IClient client, IResource config) {
		verify(client, times(0)).update(config);
	}
	
	public static void thenResourceShouldBeRetrieved(IClient client, String namespace, String kind, String name) {
		verify(client, times(1)).get(kind, name, namespace);
	}
}
