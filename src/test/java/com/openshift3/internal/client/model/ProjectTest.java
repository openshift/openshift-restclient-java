package com.openshift3.internal.client.model;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.openshift3.client.IClient;
import com.openshift3.client.ResourceKind;
import com.openshift3.client.model.IService;
import com.openshift3.internal.client.OpenShiftAPIVersion;
import com.openshift3.internal.client.ResourceFactory;

@RunWith(MockitoJUnitRunner.class)
public class ProjectTest {

	@Mock private IClient client;
	private Project project;
	
	@Before
	public void setup(){
		project = new ResourceFactory(client).create(OpenShiftAPIVersion.v1beta1.toString(), ResourceKind.Project);
		project.setName("aprojectname");
	}
	
	@Test
	public void getResourcesShouldUseProjectNameForNamespaceWhenGettingResources() {
		ArrayList<IService> services = new ArrayList<IService>();
		when(client.<IService>list(eq(ResourceKind.Service), anyString())).thenReturn(services);
		List<IService> resources = project.getResources(ResourceKind.Service);
		
		assertEquals("Exp. a list of services", services, resources);
		verify(client).list(eq(ResourceKind.Service), eq(project.getName()));
	}

}
