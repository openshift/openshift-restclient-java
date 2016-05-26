/*******************************************************************************
 * Copyright (c) 2016 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package com.openshift.internal.restclient.model.kubeclient;

import java.util.Map;

import com.openshift.restclient.model.kubeclient.IUser;

public class User implements IUser {

	private String name;
	private Map<String, String> user;

	public void setUser(Map<String, String> user) {
		this.user = user;
	}
	@Override
	public String getToken() {
		return user.get("token");
	}
	
	@Override
	public String getName() {
		return this.name;
	}
	
	public void setName(String name) {
		this.name = name;
	}

	
}
