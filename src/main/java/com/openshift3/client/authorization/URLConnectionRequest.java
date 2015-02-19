/*******************************************************************************
 * Copyright (c) 2015 Red Hat, Inc. Distributed under license by Red Hat, Inc.
 * All rights reserved. This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Red Hat, Inc.
 ******************************************************************************/
package com.openshift3.client.authorization;

import java.net.URLConnection;

public class URLConnectionRequest implements IRequest {

	private final URLConnection connection;

	public URLConnectionRequest(URLConnection connection) {
		this.connection = connection;
	}

	@Override
	public void setProperty(String name, String value) {
		connection.setRequestProperty(name, value);
	}

}
