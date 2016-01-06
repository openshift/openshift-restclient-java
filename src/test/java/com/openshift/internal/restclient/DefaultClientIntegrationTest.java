 /*******************************************************************************
 * Copyright (c) 2015 Red Hat, Inc. Distributed under license by Red Hat, Inc.
 * All rights reserved. This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Red Hat, Inc.
 ******************************************************************************/
package com.openshift.internal.restclient;

import static org.junit.Assert.assertEquals;

import java.net.MalformedURLException;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.openshift.internal.restclient.model.Project;
import com.openshift.internal.restclient.model.Service;
import com.openshift.internal.restclient.model.template.Template;
import com.openshift.restclient.IClient;
import com.openshift.restclient.IResourceFactory;
import com.openshift.restclient.ResourceKind;
import com.openshift.restclient.model.IProject;
import com.openshift.restclient.model.IResource;
import com.openshift.restclient.model.template.ITemplate;
import com.openshift.restclient.utils.Samples;

/**
 * @author Jeff Cantrill
 */
public class DefaultClientIntegrationTest {

	private static final String VERSION = "v1";

	private static final Logger LOG = LoggerFactory.getLogger(DefaultClientIntegrationTest.class);
	
	private IClient client;
	private IntegrationTestHelper helper = new IntegrationTestHelper();

	private IResourceFactory factory;

	@Before
	public void setup () {
		client = helper.createClientForBasicAuth();
		factory = new ResourceFactory(client);
	}
	
	@Test
	public void testListTemplates(){
		Template template = null; 
		Project project = null;
		
		try {
			project = factory.create(VERSION, ResourceKind.PROJECT);
			project.setName(helper.generateNamespace());
			template = factory.create(Samples.V1_TEMPLATE.getContentAsString());
			template.setNamespace(project.getName());
			
			project = client.create(project);
			template = client.create(template, project.getNamespace());
			
			List<ITemplate> list = client.list(ResourceKind.TEMPLATE, project.getName());
			assertEquals(1, list.size());
			for (ITemplate t : list) {
				LOG.debug(t.toString());
			}
		} finally {
			cleanUpResource(client, template);
			cleanUpResource(client, project);
		}
	}
	
	@Test
	public void testResourceLifeCycle() throws MalformedURLException {
		
		
		IProject project = factory.create(VERSION, ResourceKind.PROJECT);
		((Project) project).setName(helper.generateNamespace());
		LOG.debug(String.format("Stubbing project: %s", project));
		
		IProject other = factory.create(VERSION, ResourceKind.PROJECT);
		((Project) other).setName(helper.generateNamespace());
		LOG.debug(String.format("Stubbing project: %s", project));
		
		Service service = factory.create(VERSION, ResourceKind.SERVICE);
		service.setNamespace(project.getName()); //this will be the project's namespace
		service.setName("some-service");
		service.setTargetPort(6767);
		service.setPort(6767);
		service.setSelector("name", "barpod");
		LOG.debug(String.format("Stubbing service: %s", service));

		Service otherService = factory.create(VERSION, ResourceKind.SERVICE);
		otherService.setNamespace(other.getName()); //this will be the project's namespace
		otherService.setName("some-other-service");
		otherService.setTargetPort(8787);
		otherService.setPort(8787);
		otherService.setSelector("name", "foopod");
		
		LOG.debug(String.format("Stubbing service: %s", otherService));
		
		try{
			project = client.create(project);
			LOG.debug(String.format("Created project: %s", project));

			other = client.create(other);
			LOG.debug(String.format("Created project: %s", project));
			
			LOG.debug(String.format("Creating service: %s", service));
			service = client.create(service);
			LOG.debug(String.format("Created service: %s", service));
			
			LOG.debug(String.format("Creating service: %s", otherService));
			otherService = client.create(otherService);
			LOG.debug(String.format("Created service: %s", otherService));
			
			LOG.debug("Listing projects");
			List<Project> projects = client.list(ResourceKind.PROJECT);
			LOG.debug(String.format("Listed projects: %s", projects));
			
			LOG.debug(String.format("Listing services with namespace: %s", project.getNamespace()));
			List<Service> services = client.list(ResourceKind.SERVICE, project.getNamespace());
			LOG.debug(String.format("Listed services: %s", services));
			
			LOG.debug(String.format("Getting service: %s", otherService.getName()));
			Service s = client.get(ResourceKind.SERVICE, otherService.getName(), otherService.getNamespace());
			LOG.debug(String.format("Retrieved service: %s", s.getName()));
			
			assertEquals("Expected there to be only one service returned", 1, services.size());
			assertEquals("Expected to get the service with the correct name", service.getName(), services.get(0).getName());
		}finally{
			cleanUpResource(client, project);
			cleanUpResource(client, other);
			cleanUpResource(client, service);
			cleanUpResource(client, otherService);
		}
		
	}

	private void cleanUpResource(IClient client, IResource resource){
		try{
			Thread.sleep(1000);
			LOG.debug(String.format("Deleting resource: %s", resource));
//			client.delete(resource);
		}catch(Exception e){
			LOG.error("Exception deleting", e);
		}
	}

}
