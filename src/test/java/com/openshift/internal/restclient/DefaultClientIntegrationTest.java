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
import static org.junit.Assert.assertNotNull;
import static com.openshift.internal.restclient.IntegrationTestHelper.*;

import java.net.MalformedURLException;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.openshift.internal.restclient.model.Project;
import com.openshift.internal.restclient.model.Service;
import com.openshift.internal.restclient.model.project.OpenshiftProjectRequest;
import com.openshift.internal.restclient.model.template.Template;
import com.openshift.restclient.IClient;
import com.openshift.restclient.IResourceFactory;
import com.openshift.restclient.OpenShiftException;
import com.openshift.restclient.ResourceKind;
import com.openshift.restclient.authorization.UnauthorizedException;
import com.openshift.restclient.model.IBuildConfig;
import com.openshift.restclient.model.IProject;
import com.openshift.restclient.model.build.IBuildConfigBuilder;
import com.openshift.restclient.model.project.IProjectRequest;
import com.openshift.restclient.model.template.ITemplate;

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
	public void testAuthContextIsAuthorizedWithValidUserNameAndPassword() {
		client = helper.createClient();
		client.getAuthorizationContext().setUserName(helper.getDefaultClusterAdminUser());
		client.getAuthorizationContext().setPassword(helper.getDefaultClusterAdminPassword());
		client.getAuthorizationContext().isAuthorized();
	}
	@Test(expected=UnauthorizedException.class)
	public void testAuthContextIsAuthorizedWithoutPasswordThrows() {
		client = helper.createClient();
		client.getAuthorizationContext().setUserName(helper.getDefaultClusterAdminUser());
		client.getAuthorizationContext().isAuthorized();
	}
	
	@Test
	public void testReady() {
		client.getServerReadyStatus();
	}
	
	@Test
	public void testListTemplates(){
		Template template = null; 
		IProject project = null;
		
		try {
			OpenshiftProjectRequest projectRequest = factory.create(VERSION, ResourceKind.PROJECT_REQUEST);
			projectRequest.setName(helper.generateNamespace());
			template = factory.stub(ResourceKind.TEMPLATE, "mytemplate");
			
			project = (IProject) client.create(projectRequest);
			template = client.create(template, project.getNamespace());
			
			assertNotNull("Exp. the template to be found but was not", waitForResource(client, ResourceKind.TEMPLATE, project.getName(), template.getName(), 5 * MILLISECONDS_PER_SECOND));
			
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
	public void testResourceLifeCycle() { 
		
		
		IProjectRequest projectRequest = factory.create(VERSION, ResourceKind.PROJECT_REQUEST);
		((OpenshiftProjectRequest) projectRequest).setName(helper.generateNamespace());
		LOG.debug(String.format("Stubbing project request: %s", projectRequest));
		
		IProjectRequest otherProjectRequest = factory.create(VERSION, ResourceKind.PROJECT_REQUEST);
		((OpenshiftProjectRequest) otherProjectRequest).setName(helper.generateNamespace());
		LOG.debug(String.format("Stubbing project request: %s", otherProjectRequest));
		
		Service service = factory.create(VERSION, ResourceKind.SERVICE);
		service.setNamespace(projectRequest.getName()); //this will be the project's namespace
		service.setName("some-service");
		service.setTargetPort(6767);
		service.setPort(6767);
		service.setSelector("name", "barpod");
		LOG.debug(String.format("Stubbing service: %s", service));

		Service otherService = factory.create(VERSION, ResourceKind.SERVICE);
		otherService.setNamespace(otherProjectRequest.getName()); //this will be the project's namespace
		otherService.setName("some-other-service");
		otherService.setTargetPort(8787);
		otherService.setPort(8787);
		otherService.setSelector("name", "foopod");
		
		LOG.debug(String.format("Stubbing service: %s", otherService));
		
		IProject project = null;
		IProject other = null;
		try{
			project = (IProject) client.create(projectRequest);
			LOG.debug(String.format("Created project: %s", project));

			other = (IProject) client.create(otherProjectRequest);
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
			
			IBuildConfigBuilder builder = client.adapt(IBuildConfigBuilder.class);
			IBuildConfig bc = builder.named("test")
				.fromGitSource()
					.fromGitUrl("https://github.com/openshift/origin.git")
					.inContextDir("examples/hello-openshift")
				.end()
				.usingSourceStrategy()
					.fromDockerImage("foo/bar")
				.end()
				.toImageStreamTag("foo/bar:latest")
				.build();
			bc = client.create(bc, project.getNamespace());
			LOG.debug(String.format("Created bc: %s", bc.getName()));
			LOG.debug(String.format("Trying to delete bc: %s", bc.getName()));
			client.delete(bc);
		}finally{
			cleanUpResource(client, project);
			cleanUpResource(client, other);
			cleanUpResource(client, service);
			cleanUpResource(client, otherService);
		}
		
	}

}
