/*******************************************************************************
 * Copyright (c) 2015 Red Hat, Inc. Distributed under license by Red Hat, Inc.
 * All rights reserved. This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Red Hat, Inc.
 ******************************************************************************/
package com.openshift.internal.util;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;

/**
 * Helper methods for manipulating URIs
 * 
 * @author Jeff Cantrill
 */
public class URIUtils {
	
	private URIUtils(){
	}
	
	public static  Map<String, String> splitFragment(String location){
		URI uri = null;
		try {
			uri = new URI(location);
		} catch (URISyntaxException e) {
			throw new RuntimeException(e);
		}
		return splitFragment(uri);
	}
	
	public static Map<String, String> splitFragment(URI uri){
		return splitQuery(uri.getFragment());
	}
	
	public static Map<String,String> splitQuery(String q) {
		HashMap<String, String> params = new HashMap<String, String>();
		if (q != null) {
			for (NameValuePair pair : URLEncodedUtils.parse(q, StandardCharsets.UTF_8)) {
				params.put(pair.getName(), pair.getValue());
			}
		}
		return params;
	}
}
