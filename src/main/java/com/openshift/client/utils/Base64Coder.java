/******************************************************************************* 
 * Copyright (c) 2013 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/
package com.openshift.client.utils;

import javax.xml.bind.DatatypeConverter;

import com.openshift.internal.client.utils.StringUtils;

/**
 * A utility class that offers methods to encode and decode strings from and to
 * base64
 * 
 * @author Andre Dietisheim
 */
public class Base64Coder {

	private Base64Coder() {
		// inhibit instantiation
	}

	/**
	 * Encodes the given byte array to a base64 encoded String
	 * 
	 * @param unencoded
	 *            the array of unencoded bytes that shall get encoded
	 * @return the encoded string
	 */
	public static String encode(byte[] unencoded) {
		if (unencoded == null) {
			return null;
		} else if (unencoded.length == 0) {
			return new String();
		}
		return DatatypeConverter.printBase64Binary(unencoded);
	}

	/**
	 * Encodes the given string to a base64 encoded string. Returns
	 * <code>null</code> if the given string is <code>null</code>.
	 * 
	 * @param unencoded
	 * @return
	 */
	public static String encode(String unencoded) {
		if (StringUtils.isEmpty(unencoded)) {
			return unencoded;
		}
		return encode(unencoded.getBytes());
	}

	public static String decode(byte[] encoded) {
		if (encoded == null
				|| encoded.length == 0) {
			return new String();
		}
		return decode(new String(encoded));
	}

	/**
	 * Decodes the given base64 encoded string. Returns <code>null</code> if the
	 * given string is <code>null</code>.
	 * 
	 * @param encoded
	 *            the base64 encoded string
	 * @return the decoded string
	 */
	public static String decode(String encoded) {
		if (StringUtils.isEmpty(encoded)) {
			return encoded;
		}
		byte[] encodedBytes = DatatypeConverter.parseBase64Binary(encoded);
		return new String(encodedBytes);
	}

}
