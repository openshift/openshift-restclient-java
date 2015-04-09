/*******************************************************************************
 * Copyright (c) 2015 Red Hat, Inc. Distributed under license by Red Hat, Inc.
 * All rights reserved. This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Red Hat, Inc.
 ******************************************************************************/
package com.openshift.internal.util;

import java.util.HashMap;
import java.util.Map;
import java.net.URI;
import java.net.URISyntaxException;

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
		HashMap<String, String> fragments = new HashMap<String, String>();
		String fragment = uri.getFragment();
		if(fragment != null){
			String [] entries = fragment.split("&");
			for (String entry : entries) {
				String[] pair = entry.split("=");
				fragments.put(pair[0], pair[1]);
			}
		}
		return fragments;
	}
}
