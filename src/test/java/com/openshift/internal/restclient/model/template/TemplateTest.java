/*******************************************************************************
 * Copyright (c) 2015 Red Hat, Inc. Distributed under license by Red Hat, Inc.
 * All rights reserved. This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package com.openshift.internal.restclient.model.template;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.jboss.dmr.ModelNode;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import com.openshift.restclient.IClient;
import com.openshift.restclient.model.template.IParameter;
import com.openshift.restclient.model.template.ITemplate;

public class TemplateTest {
	@Mock private IClient client;
	private ITemplate template;
	private ModelNode node = new ModelNode();
	@Before
	public void setUp() throws Exception {
		ModelNode params = node.get("parameters");
		params.add(param("foo","bar"));
		params.add(param("abc","xyz"));
		params.add(param("123","456"));
		template = new Template(node, client, null);
	}
	
	@Test
	public void testGetParametersWhenNotDefined() {
		template = new Template(new ModelNode(), client, null);
		assertNotNull(template.getParameters());
	}
	
	@Test
	public void addObjectLabel() {
		final String key = "objectLabelKey";
		final String value = "objectLabelValue";
		template.addObjectLabel(key, value);
		Map<String, String> labels = template.getObjectLabels();
		assertEquals(value, labels.get(key));
	}

	@Test
	public void updateParameter() {
		template.updateParameter("foo", "newbar");
		List<String> updatedParams = new ArrayList<String>();
		for (IParameter param : template.getParameters().values()) {
			updatedParams.add(String.format("%s:%s", param.getName(), param.getValue()));
		}

		String [] exp = new String [] {"foo:newbar","abc:xyz","123:456"};
		String [] act = updatedParams.toArray(new String [] {});
		Arrays.sort(exp);
		Arrays.sort(act);
		assertArrayEquals(exp, act);
	}

	@Test
	public void updateParameters() {
		Collection<IParameter> parameters = new ArrayList<IParameter>();
		parameters.add(new Parameter(param("foo", "newbar")));
		parameters.add(new Parameter(param("123", "anewvalue")));
		
		template.updateParameterValues(parameters);
		
		List<String> updatedParams = new ArrayList<String>();
		for (IParameter param : template.getParameters().values()) {
			updatedParams.add(String.format("%s:%s", param.getName(), param.getValue()));
		}
		String [] exp = new String [] {"foo:newbar", "123:anewvalue","abc:xyz"};
		String [] act = updatedParams.toArray(new String [] {});
		Arrays.sort(exp);
		Arrays.sort(act);
		assertArrayEquals(exp, act);
	}
	
	private ModelNode param(String name, String value) {
		ModelNode node = new ModelNode();
		node.get("name").set(name);
		node.get("value").set(value);
		return node;
	}
}
