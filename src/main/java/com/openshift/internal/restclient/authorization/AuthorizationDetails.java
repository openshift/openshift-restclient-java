/*******************************************************************************
 * Copyright (c) 2015 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package com.openshift.internal.restclient.authorization;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.openshift.restclient.authorization.IAuthorizationDetails;

import okhttp3.Headers;

/**
 * @author Jeff Cantrill
 */
public class AuthorizationDetails implements IAuthorizationDetails {

	private static final String LINK = "Link";
	private static final String WARNING = "Warning";
	private static final String WWW_AUTHENTICATE = "WWW-Authenticate";

	private static final Pattern LINK_RE = Pattern.compile(".*?((?:http|https)(?::\\/{2}[\\w]+)(?:[\\/|\\.]?)(?:[^\\s\"<>]*))",Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
	private static final Pattern WARNING_RE = Pattern.compile(".*?(\".*?\")",Pattern.CASE_INSENSITIVE | Pattern.DOTALL);

	private String message = "";
	private String link = "";
	private String scheme = "";

	public AuthorizationDetails(String link) {
		this.link = link;
	}

	public AuthorizationDetails(String error, String errorDetails) {
		this.message = "Unknown authorization error";
		if (error != null) {
			this.message = error;
		}
		if (errorDetails != null) {
			this.message = this.message + ": " + errorDetails;
		}
	}

	public AuthorizationDetails(Headers headers) {
		for (String name : headers.names()) {
			if(LINK.equalsIgnoreCase(name)) {
				Matcher matcher = LINK_RE.matcher(headers.get(name));
				if(matcher.find()) {
					link = matcher.group(1);
				}
			}else if(WARNING.equalsIgnoreCase(name)) {
				Matcher matcher = WARNING_RE.matcher(headers.get(name));
				if(matcher.find()) {
					message = matcher.group(1);
				}
			}else if(WWW_AUTHENTICATE.equalsIgnoreCase(name)) {
				scheme = headers.get(name);
				if(scheme.contains("realm")) {
					scheme = scheme.split(" ")[0];
				}
			}
		}
	}


	public AuthorizationDetails(Headers headers, String link) {
		this(headers);
		if(link != null) {
			this.link = link;
		}
	}

	@Override
	public String getScheme() {
		return scheme ;
	}



	@Override
	public String getMessage() {
		return message;
	}

	@Override
	public String getRequestTokenLink() {
		return link;
	}


	@Override
	public String toString() {
		return message;
	}


}
