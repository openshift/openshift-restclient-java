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
package com.openshift.internal.client.utils;

/**
 * @author Andre Dietisheim
 */
public class UrlUtils {

	private static final String HTTPS = "https";
	private static final String SCHEMA_SEPARATOR = "://";

	public static final String SCHEME_HTTPS = HTTPS + SCHEMA_SEPARATOR;
	public static final char USERNAME_SEPARATOR = '@';
	
	private UrlUtils() {
		// inhibit instantiation
	}

	public static String ensureStartsWithHttps(String url) {
		if (url == null
				|| url.isEmpty()) {
			return url;
		}
		
		if (url.indexOf(SCHEMA_SEPARATOR) > 0) {
			return url;
		}

		return new StringBuilder(HTTPS)
				.append(SCHEMA_SEPARATOR)
				.append(url)
				.toString();
	}
}
