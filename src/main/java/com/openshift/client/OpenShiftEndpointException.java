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

import java.util.List;

import com.openshift.internal.client.response.RestResponse;

/**
 * @author André Dietisheim
 */
public class OpenShiftEndpointException extends OpenShiftException {

	private static final long serialVersionUID = 8251533839480636815L;

	private final String url;
	private final RestResponse response;
	
	public OpenShiftEndpointException(final String url, final Throwable cause, RestResponse response, final String message, final Object... arguments) {
		super(cause, message, arguments);
		this.url = url;
		this.response = response;
	}
		
	/**
	 * @return the server response after converting it into a {@link RestResponse}.
	 * @throws OpenShiftException if the unmarshalling fails
	 */
	public RestResponse getRestResponse() throws OpenShiftException {
		return response;
	}

	/**
	 * @return the server response messages after converting the response into a {@link RestResponse}. If the unmarshalling fails, the returned list is <code>null</code>.
	 */
	public Messages getRestResponseMessages() {
		if (response == null) {
			return null;
		}
		return response.getMessages();
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
	public List<Message> getRestResponseMessage(IField field) {
		Messages messages = getRestResponseMessages();
		if (messages == null) {
			return null;
		}
		return messages.getBy(field);
	}

	protected String getUrl() {
		return url;
	}
}
