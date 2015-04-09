/******************************************************************************* 
 * Copyright (c) 2014 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package com.openshift.restclient.utils;

import java.security.AlgorithmParameterGenerator;
import java.security.InvalidParameterException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;

/**
 * @author Andre Dietisheim
 */
public class SSLUtils {

	private static final String SSL_CONTEXT_NAME = "SSL";
	public static final String CIPHER_DHE_REGEX = ".*_DHE_.*";
	private static final String CIPHER_DHE_NAME = "DiffieHellman";
	private static final int CIPHER_DHE_MINSIZE = 512;
	private static final int CIPHER_DHE_MAXSIZE = 16384;
	private static final int CIPHER_DHE_MODULO = 64;

	private SSLUtils() {
		// inhibit instantiation
	}

	/**
	 * Returns <code>true</code> if the jdk supports DEH cipher keys in the
	 * given length.
	 * inspired by http://stackoverflow.com/a/18254095/231357
	 * 
	 * @param length
	 * @return
	 * 
	 */
	public static boolean supportsDHECipherKeysOf(int length) {
		try {
			return isMaxKeysize(length, CIPHER_DHE_MINSIZE, CIPHER_DHE_MAXSIZE, CIPHER_DHE_MODULO,
					AlgorithmParameterGenerator.getInstance(CIPHER_DHE_NAME));
		} catch (NoSuchAlgorithmException e1) {
			return false;
		}
	}

	private static boolean isMaxKeysize(int length, int minSize, int maxSize, int modulo,
			AlgorithmParameterGenerator algorithmParamGen) {
		int maxLength = 0;
		for (int i = minSize; i <= maxSize; i += modulo) {
			try {
				algorithmParamGen.init(i);
			} catch (InvalidParameterException e) {
				break;
			}
			maxLength = i;
		}
		return maxLength >= length;
	}

	public static final String[] filterCiphers(String excludedCipherRegex, String[] ciphers) {
		if (excludedCipherRegex == null
				|| ciphers == null) {
			return ciphers;
		}

		List<String> filteredCiphers = new ArrayList<String>();
		for (String cipher : ciphers) {
			if (!cipher.matches(excludedCipherRegex)) {
				filteredCiphers.add(cipher);
			}
		}
		return filteredCiphers.toArray(new String[filteredCiphers.size()]);
	}

	public static SSLContext getSSLContext(TrustManager trustManager) throws NoSuchAlgorithmException, KeyManagementException {
		TrustManager[] trustManagers = null;
		if (trustManager != null) {
			trustManagers = new TrustManager[] { trustManager };
		}
		SSLContext sslContext = SSLContext.getInstance(SSL_CONTEXT_NAME);
		sslContext.init(null, trustManagers, null);
		return sslContext;
	}
}
