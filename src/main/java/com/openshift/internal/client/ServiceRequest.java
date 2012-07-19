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

import com.openshift.client.OpenShiftException;
import com.openshift.internal.client.response.Link;
import com.openshift.internal.client.response.RestResponse;

/**
 * @author Andre Dietisheim
 */
public class ServiceRequest {

	private Link link;
	private String linkName;
	private AbstractOpenShiftResource resource;

	protected ServiceRequest(String linkName, AbstractOpenShiftResource resource) {
		this(resource);
		this.linkName = linkName;
	}

	protected ServiceRequest(Link link, AbstractOpenShiftResource resource) {
		this(resource);
		this.link = link;
	}
	
	protected ServiceRequest(AbstractOpenShiftResource resource) {
		this.resource = resource;
	}

	Link getLink() throws OpenShiftException {
		if (link != null) {
			return link;
		} else {
			if (resource == null) {
				return null;
			}
			return resource.getLink(linkName);
		}
	}
	
	public <DTO> DTO execute(final ServiceParameter... parameters) throws OpenShiftException {
		final Link link = getLink();
		if (link == null) {
			throw new OpenShiftException("Could not request resource, no link present");
		}
		// avoid concurrency issues, to prevent reading the links map while it
		// is still being retrieved
		final RestResponse response = resource.getService().request(link, parameters);
		if(response != null) {
			return response.getData();
		}
		return null;
	}

}
