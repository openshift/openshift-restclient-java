/*******************************************************************************
 * Copyright (c) 2015 Red Hat, Inc. Distributed under license by Red Hat, Inc.
 * All rights reserved. This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Red Hat, Inc.
 ******************************************************************************/
package com.openshift.restclient;

import java.security.cert.X509Certificate;

import javax.net.ssl.SSLSession;

/**
 * @author Andre Dietisheim
 */
public interface ISSLCertificateCallback {
	
	boolean allowCertificate(X509Certificate[] chain);

	boolean allowHostname(String hostname, SSLSession session); 
}
