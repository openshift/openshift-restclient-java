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
package com.openshift.internal.client.httpclient;

import com.openshift.client.IHttpClient;
import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;
import java.util.Map;

/**
 * @author Ioannis Canellos
 */
public class JsonMediaType implements IMediaType {

    private static final JsonFactory JSON_FACTORY = new JsonFactory();
    private static final ObjectMapper MAPPER = new ObjectMapper(JSON_FACTORY);

    public String getType() {
        return IHttpClient.MEDIATYPE_APPLICATION_JSON;
    }

    public String encodeParameters(Map<String, Object> parameters) throws EncodingException {
        try {
            return MAPPER.writeValueAsString(parameters);
        } catch (IOException e) {
            throw new EncodingException(e);
        }
    }
}
