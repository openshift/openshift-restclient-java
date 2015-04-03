package com.openshift.internal.restclient.capability.resources;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.openshift.internal.restclient.capability.resources.TemplateTraceability;
import com.openshift.restclient.IClient;
import com.openshift.restclient.model.IResource;

@RunWith(MockitoJUnitRunner.class)
public class TemplateTraceabilityTest {

	private TemplateTraceability capability;
	@Mock private IClient client;
	@Mock private IResource resource;

	@Before
	public void setUp(){
		capability = new TemplateTraceability(resource);
		when(resource.getNamespace()).thenReturn("mynamespace");
	}
	
	@Test
	public void supportedWhenAnnotationHasTemplateKey(){
		when(resource.isAnnotatedWith("template")).thenReturn(true);
		when(resource.getAnnotation("template")).thenReturn("aTemplateName");
		
		assertTrue("Exp. the capability to be supported because it has the template annotation", capability.isSupported());
		assertEquals("Exp. to get the template name", "aTemplateName", capability.getTemplateName());
	}

	@Test
	public void unsupportedWhenAnnotationDoesNotHasTemplateKey(){
		assertFalse("Exp. the capability to not be supported because it does not have the template annotation", capability.isSupported());
		assertEquals("Exp. to get the template name", "", capability.getTemplateName());
	}
	
	@Test
	public void testGetName(){
		assertEquals("", TemplateTraceability.class.getSimpleName(), capability.getName());
	}
}
