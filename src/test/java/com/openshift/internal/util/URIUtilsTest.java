/*******************************************************************************
 * Copyright (c) 2015 Red Hat, Inc. Distributed under license by Red Hat, Inc.
 * All rights reserved. This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Red Hat, Inc.
 ******************************************************************************/
package com.openshift.internal.util;

import static org.junit.Assert.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import com.openshift.internal.util.URIUtils;

/**
 * @author Jeff Cantrill
 */
public class URIUtilsTest {
	
	private String location = "https://10.0.2.15:8443/oauth/token/display#access_token=MmJiMTQzMGMtZjA0Mi00ODJmLTkzMDUtYzEyMTE5ODU1OGJh&expires_in=3600&token_type=bearer";
	private Map<String, String> exp = new HashMap<String, String>();
	
	@Before
	public void setup(){
		exp.put("access_token", "MmJiMTQzMGMtZjA0Mi00ODJmLTkzMDUtYzEyMTE5ODU1OGJh");
		exp.put("expires_in", "3600");
		exp.put("token_type", "bearer");
	}
	
	@Test
	public void testSplitFragmentFromURIString(){
		assertMaps(exp, URIUtils.splitFragment(location));
	}
	
	@Test
	public void testSplitFragmentFromURIWithNoFragment() throws Exception{
		URI uri = new URI("http://localhost");
		Map<String, String> pairs = URIUtils.splitFragment(uri);
		assertMaps(new HashMap<String, String>(), pairs);
	}
	
	@Test
	public void testSplitFragmentFromURI() throws URISyntaxException {
		URI uri = new URI(location);
		
		Map<String, String> pairs = URIUtils.splitFragment(uri);
		assertMaps(exp, pairs);
	}
	
	private void assertMaps(Map<String, String> exp, Map<String, String> act){
		assertArrayEquals(exp.entrySet().toArray(), act.entrySet().toArray());
	}
}
