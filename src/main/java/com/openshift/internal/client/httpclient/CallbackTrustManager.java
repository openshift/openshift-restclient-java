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
package com.openshift.internal.client.httpclient;

import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.X509TrustManager;

import com.openshift.internal.client.utils.Assert;

/**
 * A trust manager that calls a callback if the wrapped trustmanager fails to
 * validate a given certificate.
 * 
 * @author Andre Dietisheim
 * 
 * @see TrustManagerCallback
 */
public class CallbackTrustManager implements X509TrustManager {

	private X509TrustManager trustManager;
	private TrustManagerCallback callback;

	private CallbackTrustManager(X509TrustManager trustManager, TrustManagerCallback callback) {
		Assert.isTrue(trustManager != null);
		this.trustManager = trustManager;
		this.callback = callback;
	}

	public X509Certificate[] getAcceptedIssuers() {
		return trustManager.getAcceptedIssuers();
	}

	public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
		try {
			trustManager.checkServerTrusted(chain, authType);
		} catch (CertificateException e) {
			if (callback.allow(chain)) {
				throw e;
			}
		}
	}

	public void checkClientTrusted(X509Certificate[] chain,
			String authType) throws CertificateException {
		trustManager.checkServerTrusted(chain, authType);
	}

	public interface TrustManagerCallback {

		public boolean allow(X509Certificate[] chain);
	}
}