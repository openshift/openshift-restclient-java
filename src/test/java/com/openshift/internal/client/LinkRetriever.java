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

import static org.junit.Assert.fail;

import java.net.SocketTimeoutException;
import java.util.Map;

import com.openshift.client.OpenShiftException;
import com.openshift.internal.client.response.Link;

/**
 * Utility class to validate that a link in a resources contains a given fragment.
 * 
 * @author Xavier Coulon
 * 
 */

public class LinkRetriever {

	/**
	 * Retrieves the link identified by the given name from the given resource.
	 * @throws OpenShiftException 
	 * @throws SocketTimeoutException 
	 * 
	 */
	public static Link retrieveLink(final Object resource, final String linkName) {
		try {
			return ((AbstractOpenShiftResource)resource).getLink(linkName);
		} catch (Exception e) {
			fail("Failed to retrieve link for given resource: " + resource);
		}
		return null;
	}

	/**
	 * Retrieves the links from the given resource.
	 * @throws OpenShiftException 
	 * @throws SocketTimeoutException 
	 * 
	 */
	public static Map<String, Link> retrieveLinks(final Object resource) {
		try {
			return ((AbstractOpenShiftResource)resource).getLinks();
		} catch (Exception e) {
			fail("Failed to retrieve links for given resource: " + resource);
		}
		return null;
	}
}
