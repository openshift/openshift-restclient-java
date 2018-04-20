/*******************************************************************************
 * Copyright (c) 2015 Red Hat, Inc. Distributed under license by Red Hat, Inc.
 * All rights reserved. This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: Red Hat, Inc. - initial API and implementation
 ******************************************************************************/

package com.openshift.restclient.http;

/**
 * HTTP constants
 * 
 * @author Jeff Cantrill
 * @author Andre Dietisheim
 *
 */

public interface IHttpConstants {
    public static final int STATUS_UPGRADE_PROTOCOL = 101;
    public static final int STATUS_OK = 200;
    public static final int STATUS_MOVED_PERMANENTLY = 301;
    public static final int STATUS_MOVED_TEMPORARILY = 302;
    public static final int STATUS_INTERNAL_SERVER_ERROR = 500;
    public static final int STATUS_BAD_REQUEST = 400;
    public static final int STATUS_UNAUTHORIZED = 401;
    public static final int STATUS_FORBIDDEN = 403;
    public static final int STATUS_NOT_FOUND = 404;

    public static final int STATUS_NORMAL_STOP = 1000;

    public static final String PROPERTY_CONTENT_TYPE = "Content-Type";
    public static final String PROPERTY_AUTHORIZATION = "Authorization";
    public static final String PROPERTY_ACCEPT = "Accept";
    public static final String PROPERTY_ORIGIN = "Origin";
    public static final String PROPERTY_LOCATION = "Location";
    public static final String PROPERTY_USER_AGENT = "User-Agent";
    public static final String PROPERTY_WWW_AUTHENTICATE = "Www-Authenticate";

    public static final String PROPERTY_AUTHKEY = "broker_auth_key";
    public static final String PROPERTY_AUTHIV = "broker_auth_iv";

    public static final String MEDIATYPE_ANY = "*/*";
    public static final String MEDIATYPE_APPLICATION_JSON = "application/json";
    public static final String MEDIATYPE_APPLICATION_XML = "application/xml";
    public static final String MEDIATYPE_APPLICATION_FORMURLENCODED = "application/x-www-form-urlencoded";

    public static final String AUTHORIZATION_BASIC = "Basic";
    public static final String AUTHORIZATION_BEARER = "Bearer";

    public static final String VERSION = "version";

    public static final char SPACE = ' ';
    public static final char COLON = ':';
    public static final char COMMA = ',';
    public static final char SEMICOLON = ';';
    public static final char AMPERSAND = '&';
    public static final char EQUALS = '=';
    public static final char SLASH = '/';
    public static final char QUESTION_MARK = '?';

    public static final int DEFAULT_READ_TIMEOUT = 2 * 60 * 1000;
    public static final int NO_TIMEOUT = -1;

    static final String PUT = "PUT";
    static final String POST = "POST";
}
