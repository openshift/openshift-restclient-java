/******************************************************************************* 
 * Copyright (c) 2011 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/ 
package com.openshift.express.client;

import java.io.IOException;

import com.openshift.express.internal.client.InternalUser;

/**
 * @author André Dietisheim
 */
public class User extends InternalUser {

	public User(String password, String id) throws OpenShiftException, IOException {
		super(password, id);
	}

	public User(String rhlogin, String password, String id) throws OpenShiftException, IOException {
		super(rhlogin, password, id);
	}
	
	protected User(String rhlogin, String password, String id, String url) {
		super(rhlogin, password, id, url);
	}
	
	protected User(String rhlogin, String password, String id, String url, IOpenShiftService service) {
		super(rhlogin, password, id, url, service);
	}
	
	public User(String rhlogin, String password, String id, IOpenShiftService service) {
		super(rhlogin, password, id, null, (ISSHPublicKey) null, service);
	}
}
