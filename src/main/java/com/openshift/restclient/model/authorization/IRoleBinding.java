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
package com.openshift.restclient.model.authorization;

import java.util.Set;

import com.openshift.restclient.model.IObjectReference;
import com.openshift.restclient.model.IResource;

/**
 * @author Jeff Cantrill
 */
public interface IRoleBinding extends IResource {

	void setUserNames(Set<String> names);
	Set<String> getUserNames();
	void addUserName(String name);

	void setGroupNames(Set<String> names);
	Set<String> getGroupNames();
	void addGroupName(String name);
	
	void setSubjects(Set<IObjectReference> subjects);
	Set<IObjectReference> getSubjects();
	
	IObjectReference getRoleRef();
	void setRoleRef(IObjectReference roleRef);
}
