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

import java.util.List;
import java.util.Map;

import com.openshift.client.IHttpClient;
import com.openshift.client.IOpenShiftResource;
import com.openshift.client.OpenShiftException;
import com.openshift.client.OpenShiftRequestException;
import com.openshift.internal.client.response.Link;
import com.openshift.internal.client.response.Message;
import com.openshift.internal.client.response.RestResponse;

/**
 * The Class AbstractOpenShiftResource.
 * 
 * @author Xavier Coulon
 * @author Andre Dietisheim
 */
public abstract class AbstractOpenShiftResource implements IOpenShiftResource {

	/** The links. Null means collection is not loaded yet. */
	private Map<String, Link> links;

	/** The service. */
	private final IRestService service;

	private List<Message> creationLog;

	/**
	 * Instantiates a new abstract open shift resource.
	 * 
	 * @param service
	 *            the service
	 */
	public AbstractOpenShiftResource(final IRestService service) {
		this(service, null, null);
	}

	/**
	 * Instantiates a new abstract open shift resource.
	 * 
	 * @param service
	 *            the service
	 * @param links
	 *            the links
	 */
	public AbstractOpenShiftResource(final IRestService service, final Map<String, Link> links, final List<Message> creationLog) {
		this.service = service;
		this.links = links;
		this.creationLog = creationLog;
	}

	/**
	 * Gets the links.
	 * 
	 * @return the links
	 * @throws OpenShiftException
	 */
	Map<String, Link> getLinks() throws OpenShiftException {
		return links;
	}

	void setLinks(final Map<String, Link> links) {
		this.links = links;
	}

	/**
	 * Gets the service.
	 * 
	 * @return the service
	 */
	protected final IRestService getService() {
		return service;
	}

	// made protected for testing purpose, but not part of the public interface,
	// though
	/**
	 * Gets the link.
	 * 
	 * @param linkName
	 *            the link name
	 * @return the link
	 * @throws OpenShiftException
	 *             the open shift exception
	 */
	protected Link getLink(String linkName) throws OpenShiftException {
		Link link = null;
		if (getLinks() != null) {
			link = getLinks().get(linkName);
		}
		if (link == null) {
			throw new OpenShiftRequestException(
					"Could not find link \"{0}\" in resource \"{1}\"", linkName, getClass().getSimpleName());
		}
		return link;
	}

	protected boolean areLinksLoaded() {
		return links != null;
	}

	protected class ServiceRequest {

		private String linkName;

		protected ServiceRequest(String linkName) {
			this.linkName = linkName;
		}

		protected <DTO> DTO execute(ServiceParameter... parameters) throws OpenShiftException {
			Link link = getLink(linkName);
			RestResponse response = getService().request(link, parameters);
			// in some cases, there is not response body, just a return code to
			// indicate that the operation was successful (e.g.: delete domain)
			if (response == null) {
				return null;
			}
			return response.getData();
		}
		
		protected String execute(String addOn, ServiceParameter... parameters) throws OpenShiftException {
			try {
				Link link = getLink(linkName);
				link = new Link(link.getRel(), link.getHref() + "/" + addOn, link.getHttpMethod(), link.getRequiredParams(), link.getOptionalParams());
				
				String response = getService().request(link, IHttpClient.MEDIATYPE_APPLICATION_XML, parameters);
				
				return response;
			} catch (Exception e) {
				throw new OpenShiftException(e, "Unable to execute request");
			}
		}

	}

	public String getCreationLog() {
		if (!hasCreationLog()) {
			return null;
		}
		StringBuilder builder = new StringBuilder();
		for (Message message : creationLog) {
			builder.append(message.getText());
		}
		return builder.toString();
	}

	public boolean hasCreationLog() {
		return creationLog != null
				&& creationLog.size() > 0;
	}
	
}
