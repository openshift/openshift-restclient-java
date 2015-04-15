package com.openshift.internal.restclient.model.template;

import static org.junit.Assert.*;

import org.jboss.dmr.ModelNode;
import org.junit.Test;

public class ParameterTest {

	private Parameter param = new Parameter(new ModelNode());
	
	@Test
	public void testGetNameWhenUndefined() {
		assertEquals("", param.getValue());
	}

	@Test
	public void testGetDescriptionWhenUndefined() {
		assertEquals("", param.getDescription());
	}
	
	@Test
	public void testGetFromWhenUndefined() {
		assertEquals("", param.getFrom());
	}

	@Test
	public void testGetGeneratorNameWhenUndefined() {
		assertEquals("", param.getGeneratorName());
	}
}
