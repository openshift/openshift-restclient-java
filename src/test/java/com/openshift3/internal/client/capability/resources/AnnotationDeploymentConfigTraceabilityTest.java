package com.openshift3.internal.client.capability.resources;

import static org.junit.Assert.*;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.openshift3.client.IClient;
import com.openshift3.client.ResourceKind;
import com.openshift3.client.model.IDeploymentConfig;
import com.openshift3.client.model.IPod;

@RunWith(MockitoJUnitRunner.class)
public class AnnotationDeploymentConfigTraceabilityTest {

	private AnnotationDeploymentConfigTraceability capability;
	
	@Mock private IDeploymentConfig config;
	@Mock private IPod resource;
	@Mock private IClient client;
	
	@Before
	public void setUp(){
		capability = new AnnotationDeploymentConfigTraceability(resource, client);
		
		when(resource.getNamespace()).thenReturn("mynamespace");
		
		when(client.get(eq(ResourceKind.DeploymentConfig), eq("foobar"), eq("mynamespace")))
			.thenReturn(config);
	}
	
	@Test
	public void supportedWhenAnnotationsHaveADeploymentKey(){
		when(resource.isAnnotatedWith(eq("deploymentconfig"))).thenReturn(true);
		when(resource.getAnnotation("deploymentconfig")).thenReturn("foobar");

		assertEquals("Exp. to get the deploymentConfig", config, capability.getDeploymentConfig());
		
		verify(client).get(eq(ResourceKind.DeploymentConfig), eq("foobar"), eq("mynamespace"));
	}

	@Test
	public void unsupportedWhenAnnotationsDoNotHaveADeploymentKey(){
		assertNull("Exp. to get the deploymentConfig", capability.getDeploymentConfig());
	}

}
