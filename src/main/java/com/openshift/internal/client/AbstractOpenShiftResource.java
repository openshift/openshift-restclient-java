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
import com.openshift.client.IOpenShiftResource;
import com.openshift.client.Message;
import com.openshift.client.Messages;
import com.openshift.client.OpenShiftException;
import com.openshift.client.OpenShiftRequestException;
import com.openshift.client.cartridge.ICartridge;
import com.openshift.client.cartridge.IEmbeddableCartridge;
import com.openshift.client.cartridge.IStandaloneCartridge;
import com.openshift.internal.client.httpclient.request.IMediaType;
import com.openshift.internal.client.httpclient.request.Parameter;
import com.openshift.internal.client.httpclient.request.ParameterValueArray;
import com.openshift.internal.client.httpclient.request.ParameterValueMap;
import com.openshift.internal.client.httpclient.request.StringParameter;
import com.openshift.internal.client.response.Link;
import com.openshift.internal.client.response.RestResponse;
import com.openshift.internal.client.utils.IOpenShiftJsonConstants;
import com.openshift.internal.client.utils.IOpenShiftParameterConstants;
import com.openshift.internal.client.utils.StringUtils;
import com.openshift.internal.client.utils.UrlUtils;

/**
 * The Class AbstractOpenShiftResource.
 * 
 * @author Xavier Coulon
 * @author Andre Dietisheim
 * @author Syed Iqbal
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
	
	protected void setLinks(Map<String, Link> links) {
		this.links = links;
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

		protected final String linkName;
		
		protected ServiceRequest(final String linkName) {
			this.linkName = linkName;
		}

		protected <DTO> DTO execute(final Parameter... parameters) throws OpenShiftException {
			return getData(getService().request(getLink(linkName), parameters));
		}
		
		protected <DTO> DTO execute(final int timeout, final Parameter... parameters) throws OpenShiftException {
			return getData(getService().request(getLink(linkName), timeout, parameters));
		}
		
		protected <DTO> DTO execute(final int timeout, final IMediaType mediaType, final Parameter... parameters)
				throws OpenShiftException {
			return getData(getService().request(getLink(linkName), timeout, mediaType, parameters));
		}

		protected <DTO> DTO execute(final List<Parameter> urlParameter, final Parameter... parameters) throws OpenShiftException {
			return getData(getService().request(getLink(linkName), urlParameter, parameters));
		}

		protected <DTO> DTO execute(final int timeout, List<Parameter> urlParameter, final Parameter... parameters) throws OpenShiftException {
			return getData(getService().request(getLink(linkName), timeout, urlParameter, parameters));
		}

		protected <DTO> DTO execute(final int timeout, List<Parameter> urlParameter, final IMediaType mediaType, final Parameter... parameters) throws OpenShiftException {
			return getData(getService().request(getLink(linkName), timeout, urlParameter, mediaType,  parameters));
		}

		protected <DTO> DTO getData(RestResponse response) {
			// in some cases, there is not response body, just a return code to
			// indicate that the operation was successful (e.g.: delete domain)
			if (response == null) {
				return null;
			}
						
			return response.getData();			
		}

	}

	protected static class Parameters {
				
		private List<Parameter> parameters = new ArrayList<Parameter>();
		
		protected Parameters addCartridge(IEmbeddableCartridge embeddable) {
			ParameterValueMap parameter = createCartridgeParameter(embeddable);
			return add(new Parameter(IOpenShiftJsonConstants.PROPERTY_CARTRIDGE, parameter));
		}

		protected Parameters addCartridges(IStandaloneCartridge standalone, IEmbeddableCartridge[] embeddables) {
			ParameterValueArray parameters = new ParameterValueArray();
			if (standalone != null) {
				parameters.add(createCartridgeParameter(standalone));
			}
			if (embeddables != null
					&& embeddables.length > 0) {
				parameters.addAll(createCartridgeParameters(embeddables));
			}

			return add(new Parameter(IOpenShiftJsonConstants.PROPERTY_CARTRIDGES, parameters));
		}
		
		protected Parameters addEnvironmentVariables(Map<String,String> environmentVariables){
			if (environmentVariables == null 
					|| environmentVariables.isEmpty()) {
				return this;
			}
			
			ParameterValueArray parameters = new ParameterValueArray()
					.addAll(createEnvironmentVariableParameters(environmentVariables));
			return add(new Parameter(IOpenShiftJsonConstants.PROPERTY_ENVIRONMENT_VARIABLES, parameters));
		}
		
		
		private List<ParameterValueMap> createEnvironmentVariableParameters(Map<String,String> environmentVariables){
			List<ParameterValueMap> parameters = new ArrayList<ParameterValueMap>();
			if(environmentVariables==null ||environmentVariables.isEmpty()){
				return parameters;
			}
			for(Map.Entry<String, String> environmentVariable : environmentVariables.entrySet()){
				parameters.add(createEnvironmentVariableParameter(environmentVariable.getKey(),environmentVariable.getValue()));
			}
			return parameters;
		}
		private ParameterValueMap createEnvironmentVariableParameter(String name,String value){
            ParameterValueMap parameters = new ParameterValueMap();
			parameters.add(IOpenShiftJsonConstants.PROPERTY_NAME,name)
			.add(IOpenShiftJsonConstants.PROPERTY_VALUE, value);
			return parameters;
		}

		/**
		 * Returns a map parameter for a given cartridge. 
		 * @param cartridge the cartridge that a request parameter shall get created for
		 * @return the parameter 
		 */
		private ParameterValueMap createCartridgeParameter(ICartridge cartridge) {
			if (cartridge.isDownloadable()) {
				return new ParameterValueMap().add(IOpenShiftJsonConstants.PROPERTY_URL, UrlUtils.toString(cartridge.getUrl()));
			} else {
				return new ParameterValueMap().add(IOpenShiftJsonConstants.PROPERTY_NAME, cartridge.getName());
			}
		}

		private List<ParameterValueMap> createCartridgeParameters(ICartridge[] cartridges) {
			List<ParameterValueMap> parameters = new ArrayList<ParameterValueMap>();
			if (cartridges == null
					|| cartridges.length == 0) {
				return parameters;
			}
			
			for (ICartridge cartridge : cartridges) {
				ParameterValueMap parameter = createCartridgeParameter(cartridge);
				if (parameter != null) {
					parameters.add(parameter);
				}
			}
			
			return parameters;
		}

		protected Parameters scale(ApplicationScale scale) {
			if (scale == null) {
				return this;
			}
			return add(new StringParameter(IOpenShiftJsonConstants.PROPERTY_SCALE, scale.getValue()));
		}

		protected Parameters gearProfile(IGearProfile gearProfile) {
			if (gearProfile == null) {
				return this;
			}
			return add(new StringParameter(IOpenShiftJsonConstants.PROPERTY_GEAR_PROFILE, gearProfile.getName()));
		}

		protected Parameters include(String includedResource) {
			add(IOpenShiftParameterConstants.PARAMETER_INCLUDE, includedResource);
			return this;
		}
		
		protected Parameters add(String name, String value) {
			if (StringUtils.isEmpty(value)) {
				return this;
			}
			return add(new StringParameter(name, value));
		}
		
		protected Parameters add(Parameter parameter) {
			if (parameter == null
					|| StringUtils.isEmpty(parameter.getName()) 
					|| parameter.getValue() == null
					|| parameter.getValue().getValue() == null) {
				return this;
			}
			
			parameters.add(parameter);
			return this;
		}

		protected Parameter[] toArray() {
			return parameters.toArray(new Parameter[parameters.size()]);
		}

		protected List<Parameter> toList() {
			return parameters;
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
