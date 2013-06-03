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
package com.openshift.client;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.openshift.internal.client.response.ResourceDTOFactory;
import com.openshift.internal.client.response.RestResponse;

/**
 * @author André Dietisheim
 */
public class OpenShiftEndpointException extends OpenShiftException {

	private static final long serialVersionUID = 8251533839480636815L;

	private static final Logger LOGGER = LoggerFactory.getLogger(OpenShiftEndpointException.class);
	
	private final String url;
	
	private final String response;
	
	public OpenShiftEndpointException(final String url, final Throwable cause, final String response, final String message, final Object... arguments) {
		super(cause, message, arguments);
		this.response = response;
		this.url = url;
	}
		
	/**
	 * @return the server response after converting it into a {@link RestResponse}.
	 * @throws OpenShiftException if the unmarshalling fails
	 */
	public RestResponse getRestResponse() throws OpenShiftException {
		if (response == null) {
			return null;
		}
		return ResourceDTOFactory.get(response);
	}

	/**
	 * @return the server response messages after converting the response into a {@link RestResponse}. If the unmarshalling fails, the returned list is <code>null</code>.
	 */
	public Map<String, Message> getRestResponseMessages() {
		if (response == null) {
			return null;
		}
		try {
			return ResourceDTOFactory.get(response).getMessages();
		} catch (OpenShiftException e) {
			LOGGER.error("Unable to parse the response", e);
		} catch (IllegalArgumentException e) {
			LOGGER.error("Unable to parse the response", e);
		}
		
		return null;
	}
	
	/**
	 * Returns the message for the given field. Returns <code>null</code> otherwise.
	 * 
	 * @param field
	 * @return the message for the given field
	 * 
	 * @see Message#FIELD_DEFAULT
	 * @see Message#FIELD_APPINFO
	 * @see Message#FIELD_RESULT
	 */
	public Message getRestResponseMessage(String field) {
		Map<String, Message> messages = getRestResponseMessages();
		if (messages == null) {
			return null;
		}
		return messages.get(field);
	}

	protected String getUrl() {
		return url;
	}
}
