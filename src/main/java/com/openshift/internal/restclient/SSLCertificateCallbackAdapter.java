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
package com.openshift.internal.restclient;

import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLException;

import org.apache.http.conn.ssl.AbstractVerifier;
import org.apache.http.conn.ssl.TrustStrategy;

import com.openshift.restclient.ISSLCertificateCallback;

public class SSLCertificateCallbackAdapter extends AbstractVerifier implements TrustStrategy {

	private ISSLCertificateCallback callback;

	public SSLCertificateCallbackAdapter(ISSLCertificateCallback certCallback) {
		this.callback = certCallback;
	}

	@Override
	public boolean isTrusted(X509Certificate[] chain, String authType) throws CertificateException {
		return callback.allowCertificate(chain);
	}

	@Override
	public void verify(String host, String[] cns, String[] subjectAlts) throws SSLException {
		if(!callback.allowHostname(host, null)){
			throw new SSLException("Host " + host + " not allowed");
		}
	}

}
