/*******************************************************************************
 * Copyright (c) 2011 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package com.openshift.client.utils;

import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;

/**
 * @author Andre Dietisheim
 */
public class HostUtils {

	public static boolean canResolv(String urlString) throws MalformedURLException {
		try {
			URL url = new URL(urlString);
			String host = url.getHost();
			return InetAddress.getByName(host) != null;
		} catch (UnknownHostException e) {
			return false;
		}
	}

}
