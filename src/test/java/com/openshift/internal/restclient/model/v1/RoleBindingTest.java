/*******************************************************************************
 * Copyright (c) 2015 Red Hat, Inc. Distributed under license by Red Hat, Inc.
 * All rights reserved. This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Red Hat, Inc.
 ******************************************************************************/
package com.openshift.internal.restclient.model.v1;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.jboss.dmr.ModelNode;
import org.junit.BeforeClass;
import org.junit.Test;

import com.openshift.internal.restclient.model.ObjectReference;
import com.openshift.internal.restclient.model.authorization.RoleBinding;
import com.openshift.internal.restclient.model.properties.ResourcePropertiesRegistry;
import com.openshift.restclient.IClient;
import com.openshift.restclient.ResourceKind;
import com.openshift.restclient.model.IObjectReference;
import com.openshift.restclient.model.authorization.IRoleBinding;
import com.openshift.restclient.utils.Samples;

/**
 * @author Jeff Cantrill
 */
public class RoleBindingTest {

	private static final String VERSION = "v1";
	private static IRoleBinding binding;
	
	@BeforeClass
	public static void setup(){
		IClient client = mock(IClient.class);
		ModelNode node = ModelNode.fromJSONString(Samples.V1_ROLE_BINDING.getContentAsString());
		binding = new RoleBinding(node, client, ResourcePropertiesRegistry.getInstance().get(VERSION, ResourceKind.ROLE_BINDING));
	}
	
	@Test
	public void testUserNames(){
		Set<String> users = new HashSet<>(Arrays.asList("alpha","beta"));
		binding.setUserNames(users);
		binding.addUserName("omega");
		
		users.add("omega");
		assertArrayEquals(users.toArray(), binding.getUserNames().toArray());
	}

	@Test
	public void testGroupNames(){
		Set<String> groups = new HashSet<>(Arrays.asList("phi","zeta"));
		binding.setGroupNames(groups);
		binding.addGroupName("pi");
		
		groups.add("pi");
		assertArrayEquals(groups.toArray(), binding.getGroupNames().toArray());
	}

	@Test
	public void testSubjects(){
		ModelNode node = new ModelNode();
		node.get("name").set("bar");
		IObjectReference ref = new ObjectReference(node);
		Set<IObjectReference> subjects = new HashSet<>();
		subjects.add(ref);
		
		binding.setSubjects(subjects);

		assertArrayEquals(subjects.toArray(), binding.getSubjects().toArray());
	}
	
	@Test
	public void testRoleRefs() {
		ModelNode node = new ModelNode();
		node.get("name").set("bar");
		IObjectReference ref = new ObjectReference(node);
		binding.setRoleRef(ref);
		assertEquals("bar", binding.getRoleRef().getName());
	}
	
}
