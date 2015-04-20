/*******************************************************************************
 * Copyright (c) 2015 Red Hat, Inc. Distributed under license by Red Hat, Inc.
 * All rights reserved. This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package com.openshift.internal.restclient.capability.resources;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.util.Collection;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.openshift.restclient.IClient;
import com.openshift.restclient.capability.resources.IProjectTemplateProcessing;
import com.openshift.restclient.capability.server.ITemplateProcessing;
import com.openshift.restclient.model.IConfig;
import com.openshift.restclient.model.IList;
import com.openshift.restclient.model.IProject;
import com.openshift.restclient.model.IResource;
import com.openshift.restclient.model.template.ITemplate;

@RunWith(MockitoJUnitRunner.class)
public class ProjectTemplateProcessingTest {
	
	private static final String NAMESPACE = "aProjectNamespace";
	private IProjectTemplateProcessing capability;
	@Mock
	private ITemplate template;
	@Mock
	private IClient client;
	@Mock
	private ITemplateProcessing serverCapability;
	@Mock
	private IProject project;
	@Mock
	IConfig config;
	
	@Before
	public void setUp() throws Exception {
		when(project.getNamespace()).thenReturn(NAMESPACE);
		when(client.supports(eq(ITemplateProcessing.class))).thenReturn(true);
		when(client.getCapability(eq(ITemplateProcessing.class))).thenReturn(serverCapability);
		
		capability = new ProjectTemplateProcessing(project, client);
	}
	
	@Test
	public void isSupportedShouldBeFalseForNullClient() {
		capability = new ProjectTemplateProcessing(project, null);
		assertFalse(capability.isSupported());
	}
	
	@Test
	public void isSupportedShouldBeFalseIfTheClientDoesntSupportTemplates() {
		when(client.supports(eq(ITemplateProcessing.class))).thenReturn(false);
		capability = new ProjectTemplateProcessing(project, client);
		assertFalse(capability.isSupported());
	}

	@Test
	public void isSupportedShouldBeTrueIfTheClientSupportTemplates() {
		assertTrue(capability.isSupported());
	}
	
	@Test
	public void processTemplateShouldUseTheClientsCapability() {
		when(serverCapability.process(any(ITemplate.class), anyString())).thenReturn(config);
		
		assertEquals(config, capability.process(template));
		verify(serverCapability).process(eq(template), eq(NAMESPACE));
	}

	@Test
	public void applyTemplateShouldUseTheClientToCreateTheResources() {
		@SuppressWarnings("unchecked")
		Collection<IResource> resources = mock(Collection.class);
		when(client.create(any(IList.class), anyString())).thenReturn(resources);
		
		assertEquals(resources, capability.apply(config));
		verify(client).create(eq(config), eq(NAMESPACE));
	}
	
}
