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
import com.openshift3.client.model.IResource;

@RunWith(MockitoJUnitRunner.class)
public class AnnotationCapabilityTest {
	
	private AnnotationCapability capability;
	@Mock private IResource resource;
	@Mock private IClient client;
	
	@Before
	public void setUp(){
		capability = newCapability(client);
	}
	
	private AnnotationCapability newCapability(IClient client){
		return new AnnotationCapability("MyCapability", resource, client) {
			@Override
			protected String getAnnotationKey() {
				return "foobar";
			}
		};
		
	}
	@Test
	public void supportedWhenAnnotationsHasKey(){
		when(resource.isAnnotatedWith(eq("foobar"))).thenReturn(true);
		assertTrue("Exp. the capability to be supported when the annotation key exists", capability.isSupported());
	}

	@Test
	public void unsupportedWhenAnnotationsDoNotHaveADeploymentKey(){
		assertFalse("Exp. the capability to not be supported when annotation key does not exists", capability.isSupported());
	}
	
	@Test
	public void unsupportedWhenTheClientIsNull(){
		capability = newCapability(null);
		assertFalse("Exp. the capability to be unsupported because the IClient is null", capability.isSupported());
	}

}
