/*******************************************************************************
 * Copyright (c) 2015 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package com.openshift.internal.restclient.model.authorization;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.jboss.dmr.ModelNode;
import org.jboss.dmr.ModelType;

import com.openshift.internal.restclient.model.KubernetesResource;
import com.openshift.internal.restclient.model.ObjectReference;
import com.openshift.restclient.IClient;
import com.openshift.restclient.model.IObjectReference;
import com.openshift.restclient.model.authorization.IRoleBinding;

public class RoleBinding extends KubernetesResource implements IRoleBinding {

	private static final String ROLE_REF = "roleRef";
	private static final String USER_NAMES = "userNames";
	private static final String GROUP_NAMES = "groupNames";

	public RoleBinding(ModelNode node, IClient client, Map<String, String[]> propertyKeys) {
		super(node, client, propertyKeys);
	}

	@Override
	public void setUserNames(Set<String> names) {
		if(names == null) {
			names = Collections.emptySet();
		}
		set(USER_NAMES, names);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Set<String> getUserNames() {
		return asSet(USER_NAMES, ModelType.STRING);
	}

	@Override
	public void addUserName(String name) {
		get(USER_NAMES).add(name);
	}

	@Override
	public void setGroupNames(Set<String> names) {
		if(names == null) {
			names = Collections.emptySet();
		}
		set(GROUP_NAMES, names);
 		
	}

	@SuppressWarnings("unchecked")
	@Override
	public Set<String> getGroupNames() {
		return asSet(GROUP_NAMES, ModelType.STRING);
	}

	@Override
	public void addGroupName(String name) {
		get(GROUP_NAMES).add(name);
	}

	@Override
	public void setSubjects(Set<IObjectReference> subjects) {
		if(subjects == null)
			subjects = Collections.emptySet();
		ModelNode node = get("subjects");
		node.clear();
		for (IObjectReference ref : subjects) {
			node.add(ModelNode.fromJSONString(ref.toJson()));
		}
	}

	@Override
	public Set<IObjectReference> getSubjects() {
		Set<IObjectReference> set = new HashSet<>();
		ModelNode node = get("subjects");
		if(node.isDefined()) {
			for (ModelNode ref : node.asList()) {
				set.add(new ObjectReference(ref));
			}
		}
		return set;
	}

	@Override
	public IObjectReference getRoleRef() {
		return new ObjectReference(get(ROLE_REF));
	}

	@Override
	public void setRoleRef(IObjectReference roleRef) {
		ModelNode node = get(ROLE_REF);
		node.set(ModelNode.fromJSONString(roleRef.toJson()));
	}
	
	
}
