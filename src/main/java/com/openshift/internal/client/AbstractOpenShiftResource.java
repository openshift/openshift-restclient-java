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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.openshift.client.ApplicationScale;
import com.openshift.client.IGearProfile;
import com.openshift.client.IHttpClient;
import com.openshift.client.IOpenShiftResource;
import com.openshift.client.Message;
import com.openshift.client.Messages;
import com.openshift.client.OpenShiftException;
import com.openshift.client.OpenShiftRequestException;
import com.openshift.client.cartridge.IEmbeddableCartridge;
import com.openshift.client.cartridge.IStandaloneCartridge;
import com.openshift.client.utils.OpenShiftResourceUtils;
import com.openshift.internal.client.response.Link;
import com.openshift.internal.client.response.RestResponse;
import com.openshift.internal.client.utils.IOpenShiftJsonConstants;

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

	private Messages messages;

	/**
	 * Instantiates a new abstract open shift resource.
	 * 
	 * @param service
	 *            the service
	 */
	protected AbstractOpenShiftResource(final IRestService service) {
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
	protected AbstractOpenShiftResource(final IRestService service, final Map<String, Link> links, final Messages messages) {
		this.service = service;
		this.links = links;
		this.messages = messages;
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

	/**
	 * Gets the service.
	 * 
	 * @return the service
	 */
	IRestService getService() {
		return service;
	}

	/**
	 * Gets the link for the given name. Throws OpenShiftRequestException if no
	 * link with this name exists within this resource.
	 * <p>
	 * This method is protected for testing purposes only.
	 * 
	 * @param linkName
	 *            the name of the link that shall get retrieved
	 * @return the link with the given name
	 * @throws OpenShiftException
	 *             thrown if no link with the given name exists
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

	boolean areLinksLoaded() {
		return links != null;
	}

	protected class ServiceRequest {

		private String linkName;

		protected ServiceRequest(final String linkName) {
			this.linkName = linkName;
		}
		
		protected <DTO> DTO execute(final RequestParameter... parameters) throws OpenShiftException {
			return execute(IHttpClient.NO_TIMEOUT, parameters);
		}
		
		protected <DTO> DTO execute(final int timeout, final List<RequestParameter> parameters) throws OpenShiftException {
			return execute(timeout, parameters.toArray(new RequestParameter[parameters.size()]));
		}

		protected <DTO> DTO execute(final int timeout, final RequestParameter... parameters) throws OpenShiftException {
			Link link = getLink(linkName);
			RestResponse response = getService().request(link, timeout, parameters);
			
			// in some cases, there is not response body, just a return code to
			// indicate that the operation was successful (e.g.: delete domain)
			if (response == null) {
				return null;
			}
						
			return response.getData();
		}
	}

	protected class RequestParameters {
		
		private ArrayList<RequestParameter> parameters = new ArrayList<RequestParameter>();

		protected RequestParameters addCartridge(IStandaloneCartridge cartridge) {
			if (cartridge == null) {
				return this;
			}
			return add(IOpenShiftJsonConstants.PROPERTY_CARTRIDGE, cartridge.getName());
		}
		
		protected RequestParameters addScale(ApplicationScale scale) {
			if (scale == null) {
				return this;
			}
			return add(IOpenShiftJsonConstants.PROPERTY_SCALE, scale.getValue());
		}

		protected RequestParameters addGearProfile(IGearProfile gearProfile) {
			if (gearProfile == null) {
				return this;
			}
			return add(IOpenShiftJsonConstants.PROPERTY_GEAR_PROFILE, gearProfile.getName());
		}

		protected RequestParameters addEmbeddableCartridges(IEmbeddableCartridge[] cartridges) {
			if (cartridges == null
					|| cartridges.length == 0) {
				return this;
			}
			parameters.add(new ArrayRequestParameter(
							IOpenShiftJsonConstants.PROPERTY_CARTRIDGES, 
							OpenShiftResourceUtils.toNames(cartridges)));
			return this;
		}

		protected RequestParameters add(String name, Object value) {
			
			if (value == null) {
				return this;
			}
			
			parameters.add(new RequestParameter(name, value));
			return this;
		}
		
		protected RequestParameter[] toArray() {
			return parameters.toArray(new RequestParameter[parameters.size()]);
		}
	}

	public boolean hasCreationLog() {
		return messages.hasMessages();
	}

	public String getCreationLog() {
		if (!hasCreationLog()) {
			return null;
		}
		StringBuilder builder = new StringBuilder();
		for (Message message : messages.getAll()) {
			builder.append(message.getText());
		}
		return builder.toString();
	}

	public Messages getMessages() {
		return messages;
	}
}
