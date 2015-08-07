/*******************************************************************************
 * Copyright (c) 2015 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package com.openshift.internal.restclient.model.properties;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.openshift.internal.restclient.KubernetesAPIVersion;
import com.openshift.internal.restclient.OpenShiftAPIVersion;

/**
 * @author jeff.cantrill
 */
public class ResourcePropertiesRegistryTest {
	
	private ResourcePropertiesRegistry registry;

	@Before
	public void setUp() throws Exception {
		registry = spy(ResourcePropertiesRegistry.getInstance());
	}
	
	@Test
	public void theClientShouldUseTheSameVersionWhenTheyAreTheSame() {
		List<KubernetesAPIVersion> serverVersions = Arrays.asList(new KubernetesAPIVersion[] {KubernetesAPIVersion.v1, KubernetesAPIVersion.v1beta3});
		assertEquals(KubernetesAPIVersion.v1, registry.getMaxSupportedKubernetesVersion(serverVersions));
	}
	
	@Test
	public void theClientShouldUseTheServerVersionWhenTheServerIsBehindTheClient() {
		when(registry.getSupportedKubernetesVersions()).thenReturn(new KubernetesAPIVersion [] {KubernetesAPIVersion.v1, KubernetesAPIVersion.v1beta3});
		List<KubernetesAPIVersion> serverVersions = Arrays.asList(new KubernetesAPIVersion[] {KubernetesAPIVersion.v1beta3});
		
		assertEquals(KubernetesAPIVersion.v1beta3, registry.getMaxSupportedKubernetesVersion(serverVersions));
	}

	@Test
	public void theClientShouldUseTheClientVersionVersionWhenTheClientIsBehindTheServer() {
		List<KubernetesAPIVersion> serverVersions = Arrays.asList(new KubernetesAPIVersion[] {KubernetesAPIVersion.v1, KubernetesAPIVersion.v1beta3});
		when(registry.getSupportedKubernetesVersions()).thenReturn(new KubernetesAPIVersion [] {KubernetesAPIVersion.v1beta3});
		
		assertEquals(KubernetesAPIVersion.v1beta3, registry.getMaxSupportedKubernetesVersion(serverVersions));
	}

	@Test
	public void theClientShouldUseTheSameOpenShiftAPIVersionWhenTheyAreTheSame() {
		List<OpenShiftAPIVersion> serverVersions = Arrays.asList(new OpenShiftAPIVersion[] {OpenShiftAPIVersion.v1, OpenShiftAPIVersion.v1beta3});
		assertEquals(OpenShiftAPIVersion.v1, registry.getMaxSupportedOpenShiftVersion(serverVersions));
	}
	
	@Test
	public void theClientShouldUseTheOpenShiftAPIServerVersionWhenTheServerIsBehindTheClient() {
		when(registry.getSupportedOpenShiftVersions()).thenReturn(new OpenShiftAPIVersion [] {OpenShiftAPIVersion.v1, OpenShiftAPIVersion.v1beta3});
		List<OpenShiftAPIVersion> serverVersions = Arrays.asList(new OpenShiftAPIVersion[] {OpenShiftAPIVersion.v1beta3});
		
		assertEquals(OpenShiftAPIVersion.v1beta3, registry.getMaxSupportedOpenShiftVersion(serverVersions));
	}
	
	@Test
	public void theClientShouldUseTheOpenShiftAPIClientVersionVersionWhenTheClientIsBehindTheServer() {
		List<OpenShiftAPIVersion> serverVersions = Arrays.asList(new OpenShiftAPIVersion[] {OpenShiftAPIVersion.v1, OpenShiftAPIVersion.v1beta3});
		when(registry.getSupportedOpenShiftVersions()).thenReturn(new OpenShiftAPIVersion [] {OpenShiftAPIVersion.v1beta3});
		
		assertEquals(OpenShiftAPIVersion.v1beta3, registry.getMaxSupportedOpenShiftVersion(serverVersions));
	}

}
