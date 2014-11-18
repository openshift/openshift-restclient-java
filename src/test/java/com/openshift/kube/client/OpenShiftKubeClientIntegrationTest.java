package com.openshift.kube.client;

import static org.junit.Assert.*;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.openshift.internal.kube.Resource;
import com.openshift.internal.kube.factories.ProjectFactory;
import com.openshift.kube.Client;
import com.openshift.kube.OpenShiftKubeClient;
import com.openshift.kube.Project;
import com.openshift.kube.ResourceKind;
import com.openshift.kube.Service;

public class OpenShiftKubeClientIntegrationTest {

	private static final Logger LOG = LoggerFactory.getLogger(OpenShiftKubeClientIntegrationTest.class);
	
	@Test
	public void testResourceLifeCycle() throws MalformedURLException {
		
		Client client = new OpenShiftKubeClient(new URL("http://localhost:8080"));
		ProjectFactory factory = new ProjectFactory("v1beta1");
		Project project = factory.create();
		project.setName("firstproject");
		LOG.debug(String.format("Stubbing project: %s", project));
		
		Project other = factory.create();
		other.setName("other");
		LOG.debug(String.format("Stubbing project: %s", project));
		
		Service service = new Service(client);
		service.setNamespace(project.getName()); //this will be the project's namespace
		service.setName("some-service");
		service.setContainerPort(6767);
		service.setPort(6767);
		service.setSelector("barpod");
		LOG.debug(String.format("Stubbing service: %s", service));

		Service otherService = new Service(client);
		otherService.setNamespace("someothernamespace"); //this will be the project's namespace
		otherService.setName("some-other-service");
		otherService.setContainerPort(8787);
		otherService.setPort(8787);
		otherService.setSelector("foopod");
		
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
			
			LOG.debug(String.format("Listing projects with namespace: %s", project.getNamespace()));
			List<Project> projects = client.list(ResourceKind.Project, project.getNamespace());
			LOG.debug(String.format("Listed projects: %s", projects));
			
			assertEquals("Expected there to be only one project returned", 1, projects.size());
			assertEquals("Expected to get the project with the correct namespace", project.getName(), projects.get(0).getName());
			
			LOG.debug(String.format("Listing services with namespace: %s", project.getNamespace()));
			List<Service> services = client.list(ResourceKind.Service, project.getNamespace());
			LOG.debug(String.format("Listed services: %s", services));
			
			LOG.debug(String.format("Getting service: %s", otherService.getName()));
			Service s = client.get(ResourceKind.Service, otherService.getName(), otherService.getNamespace());
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

	private void cleanUpResource(Client client, Resource resource){
		try{
			Thread.sleep(1000);
			LOG.debug(String.format("Deleting resource: %s", resource));
			client.delete(resource);
		}catch(Exception e){
			LOG.error("Exception deleting", e);
		}
	}

}
