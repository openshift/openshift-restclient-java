package com.openshift.internal.restclient.capability.resources;

import static org.junit.Assert.*;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.openshift.internal.restclient.capability.resources.AnnotationCapability;
import com.openshift.restclient.model.IResource;

@RunWith(MockitoJUnitRunner.class)
public class AnnotationCapabilityTest {
	
	private AnnotationCapability capability;
	@Mock private IResource resource;
	
	@Before
	public void setUp(){
		capability = newCapability();
	}
	
	private AnnotationCapability newCapability(){
		return new AnnotationCapability("MyCapability", resource) {
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
}
