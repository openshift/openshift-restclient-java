package com.openshift.internal.restclient.model.template;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.fest.assertions.Assertions;
import org.jboss.dmr.ModelNode;
import org.junit.Test;

import com.openshift.restclient.model.template.IParameter;

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
	
	@Test
	public void testIsRequiredUndefined() {
		assertFalse(param.isRequired());
	}
	@Test
	public void testIsRequiredFalse() {
		ModelNode node = new ModelNode();
		node.get("required").set(false);
		param = new Parameter(node);
		assertFalse(param.isRequired());
	}

	@Test
	public void testIsRequired() {
		ModelNode node = new ModelNode();
		node.get("required").set(true);
		param = new Parameter(node);
		assertTrue(param.isRequired());
	}
	
	@Test
	public void shouldNotEqualsIfFromIsDifferent() {
		// pre-requisistes
		ModelNode node = new ModelNode();
		node.get("from").set("42");
		IParameter parameter = new Parameter(node);

		ModelNode otherNode = new ModelNode();
		otherNode.get("from").set("84");
		IParameter otherParameter = new Parameter(otherNode);
		
		// operation
		// verification
		Assertions.assertThat(parameter).isNotEqualTo(otherParameter);
		Assertions.assertThat(otherParameter).isNotEqualTo(parameter);
	}

	@Test
	public void shouldNotEqualsIfGeneratorNameIsDifferent() {
		// pre-requisistes
		ModelNode node = new ModelNode();
		node.get("generate").set("42");
		IParameter parameter = new Parameter(node);

		ModelNode otherNode = new ModelNode();
		otherNode.get("generate").set("84");
		IParameter otherParameter = new Parameter(otherNode);
		
		// operation
		// verification
		Assertions.assertThat(parameter).isNotEqualTo(otherParameter);
		Assertions.assertThat(otherParameter).isNotEqualTo(parameter);
	}

	@Test
	public void shouldNotEqualsIfNameIsDifferent() {
		// pre-requisistes
		ModelNode node = new ModelNode();
		node.get("name").set("42");
		IParameter parameter = new Parameter(node);

		ModelNode otherNode = new ModelNode();
		otherNode.get("name").set("84");
		IParameter otherParameter = new Parameter(otherNode);
		
		// operation
		// verification
		Assertions.assertThat(parameter).isNotEqualTo(otherParameter);
		Assertions.assertThat(otherParameter).isNotEqualTo(parameter);
	}

	@Test
	public void shouldNotEqualsIfValueIsDifferent() {
		// pre-requisistes
		ModelNode node = new ModelNode();
		node.get("value").set("42");
		IParameter parameter = new Parameter(node);

		ModelNode otherNode = new ModelNode();
		otherNode.get("value").set("84");
		IParameter otherParameter = new Parameter(otherNode);
		
		// operation
		// verification
		Assertions.assertThat(parameter).isNotEqualTo(otherParameter);
		Assertions.assertThat(otherParameter).isNotEqualTo(parameter);
	}

	@Test
	public void shouldNotEqualsIfIsRequiredIsDifferent() {
		// pre-requisistes
		ModelNode node = new ModelNode();
		node.get("required").set(true);
		IParameter parameter = new Parameter(node);

		ModelNode otherNode = new ModelNode();
		otherNode.get("required").set(false);
		IParameter otherParameter = new Parameter(otherNode);
		
		// operation
		// verification
		Assertions.assertThat(parameter).isNotEqualTo(otherParameter);
		Assertions.assertThat(otherParameter).isNotEqualTo(parameter);
	}

}
