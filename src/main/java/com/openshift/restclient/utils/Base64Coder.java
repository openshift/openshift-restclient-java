/******************************************************************************* 
4 * Copyright (c) 2013-2018 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/

package com.openshift.restclient.utils;

import java.nio.charset.Charset;
import java.util.Base64;

import org.apache.commons.lang.ArrayUtils;

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
     * Encodes the given byte array to a base64 encoded String. returns {@code null}
     * if the given byte array is null, empty string if the given byte array is
     * empty.
     * 
     * @param unencoded the array of unencoded bytes that shall get encoded
     * @return the encoded string created using the platform standard charset
     * 
     * @see  Charset#defaultCharset
     */
    public static String encode(byte[] unencoded) {
        return encode(unencoded, Charset.defaultCharset());
    }

    /**
     * Encodes the given byte array to a base64 encoded String. returns {@code null}
     * if the given byte array is null, empty string if the given byte array is
     * empty.
     * 
     * @param unencoded the array of unencoded bytes that shall get encoded
     * @return the encoded string created using the platform standard charset
     * 
     * @see  Charset#defaultCharset
     */
    public static String encode(byte[] unencoded, Charset charset) {
        if (unencoded == null) {
            return null;
        } else if (unencoded.length == 0) {
            return "";
        }
        return new String(Base64.getEncoder().encode(unencoded), charset);
    }

    /**
     * Encodes the given string to a base64 encoded string. Returns
     * <code>null</code> if the given string is <code>null</code>.
     * 
     */
    public static String encode(String unencoded) {
        if (unencoded == null) {
            return null;
        }
        return encode(unencoded.getBytes(), Charset.defaultCharset());
    }

    public static String encode(String unencoded, Charset charset) {
        if (unencoded == null) {
            return null;
        }
        return encode(unencoded.getBytes(), charset);
    }

    public static String decode(byte[] encoded, Charset charset) {
        if (ArrayUtils.isEmpty(encoded)) {
            return "";
        }
        return new String(Base64.getDecoder().decode(encoded), charset);
    }

    /**
     * Decodes the given base64 encoded string assuming the default charset. Returns <code>null</code> if the
     * given string is <code>null</code>.
     * 
     * @param encoded
     *            the base64 encoded string
     * @return the decoded string
     */
    public static String decode(String encoded) {
        if (encoded == null) {
            return null;
        }
        return decode(encoded.getBytes(Charset.defaultCharset()), Charset.defaultCharset());
    }

    /**
     * Decodes the given base64 encoded string using the default charset. Returns
     * <code>null</code> if the given string is <code>null</code>.
     * 
     * @param encoded the base64 encoded string
     * @return the decoded binary data
     */
    public static byte[] decodeBinary(String encoded) {
        Charset charset = Charset.defaultCharset();
        return decode(encoded.getBytes(charset), charset).getBytes(charset);
    }
}
