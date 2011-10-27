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
package org.jboss.tools.openshift.express.internal.client.request;

import java.net.MalformedURLException;
import java.net.URL;

import org.jboss.tools.openshift.express.internal.client.utils.UrlBuilder;

/**
 * @author André Dietisheim
 */
public abstract class AbstractOpenShiftRequest implements IOpenShiftRequest {

	private String rhlogin;
	private boolean debug;

	public AbstractOpenShiftRequest(String username) {
		this(username, false);
	}

	public AbstractOpenShiftRequest(String username, boolean debug) {
		this.rhlogin = username;
		this.debug = debug;
	}

	public String getRhLogin() {
		return rhlogin;
	}

	public boolean isDebug() {
		return debug;
	}
	
	public URL getUrl(String baseUrl) throws MalformedURLException {
		return new UrlBuilder(baseUrl).path(getResourcePath()).toUrl();
	}
	
	public String getUrlString(String baseUrl) {
		return new UrlBuilder(baseUrl).path(getResourcePath()).toString();
	}

	protected abstract String getResourcePath();
	
}
