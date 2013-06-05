/******************************************************************************* 
 * Copyright (c) 2012 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/
package com.openshift.internal.client;

import java.io.IOException;
import java.util.Map;

import com.openshift.client.HttpMethod;
import com.openshift.client.IOpenShiftConnection;
import com.openshift.client.OpenShiftException;
import com.openshift.internal.client.response.Link;
import com.openshift.internal.client.response.RestResponse;

/**
 * Connection Factory, used to establish a connection and retrieve a user.
 * 
 * @author Xavier Coulon
 * @author Andre Dietisheim
 * 
 */
public abstract class AbstractOpenShiftConnectionFactory {
	
	@SuppressWarnings("unchecked")
	protected IOpenShiftConnection getConnection(IRestService service, final String login, final String password) throws IOException, OpenShiftException {
		RestResponse response =
				(RestResponse) service.request(new Link("Get API", "/api", HttpMethod.GET));
		return new APIResource(login, password, service, (Map<String, Link>) response.getData());
	}
	
}
