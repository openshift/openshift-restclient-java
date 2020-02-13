/*******************************************************************************
 * Copyright (c) 2015 Red Hat, Inc. Distributed under license by Red Hat, Inc.
 * All rights reserved. This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Red Hat, Inc.
 ******************************************************************************/

package com.openshift.internal.util;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Helper methods for manipulating URIs
 * 
 */
public class URIUtils {
    private static final Logger LOG = LoggerFactory.getLogger(URIUtils.class);

    private URIUtils() {
    }

    public static Map<String, String> splitFragment(String location) {
        if (StringUtils.isEmpty(location)) {
            return Collections.emptyMap();
        }
        URI uri = null;
        try {
            uri = new URI(location);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
        return splitFragment(uri);
    }

    public static Map<String, String> splitFragment(URI uri) {
        return splitQuery(uri.getFragment());
    }

    public static Map<String, String> splitQuery(String q) {
        HashMap<String, String> params = new HashMap<String, String>();
        if (q != null) {
            try {
                String decoded = URLDecoder.decode(q, StandardCharsets.UTF_8.toString());
                String[] split = decoded.split("&");
                for (String pair : split) {
                    String[] keyValue = pair.split("=");
                    if (keyValue.length >= 2) {
                        params.put(keyValue[0], keyValue[1]);
                    }
                }
            } catch (UnsupportedEncodingException e) {
                LOG.error("Unable to decode " + q, e);
            }
        }
        return params;
    }
}
