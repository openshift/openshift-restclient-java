/******************************************************************************* 
 * Copyright (c) 2014 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 *
 * Contributors: 
 * Sean Kavanagh - initial API and implementation 
 ******************************************************************************/
package com.openshift.internal.client.response;


import com.openshift.client.Messages;

import java.util.Map;

public class AuthorizationResourceDTO extends BaseResourceDTO {


    /* authorization id */
   	private final String id;
    /* authorization note */
    private final String note;
    /* authorization scopes */
    private final String scopes;
    /* authorization token */
    private final String token;



    AuthorizationResourceDTO(final String id, String note, String scopes, String token, final Map<String, Link> links, final Messages messages) {
   		super(links, messages);
   		this.id = id;
        this.note=note;
        this.scopes=scopes;
        this.token=token;
   	}


    public String getId() {
        return id;
    }

    public String getNote() {
        return note;
    }

    public String getScopes() {
        return scopes;
    }

    public String getToken() {
        return token;
    }
}
