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
package com.openshift.internal.client;

import com.openshift.client.IAuthorization;
import com.openshift.client.OpenShiftException;
import com.openshift.internal.client.httpclient.request.StringParameter;
import com.openshift.internal.client.response.AuthorizationResourceDTO;
import com.openshift.internal.client.utils.IOpenShiftJsonConstants;


public class AuthorizationResource extends AbstractOpenShiftResource implements IAuthorization {

    private static final String LINK_GET = "GET";
    private static final String LINK_UPDATE = "UPDATE";
    private static final String LINK_DELETE = "DELETE";

    private String id;
    private String note;
    private String scopes;
    private String token;


    protected AuthorizationResource(final APIResource api, AuthorizationResourceDTO authorizationDTO) {
        super(api.getService(), authorizationDTO.getLinks(), authorizationDTO.getMessages());
        this.id = authorizationDTO.getId();
        this.note = authorizationDTO.getNote();
        this.scopes = authorizationDTO.getScopes();
        this.token = authorizationDTO.getToken();
    }


    @Override
    public void refresh() throws OpenShiftException {

        final AuthorizationResourceDTO authorizationDTO = new GetAuthorizationRequest().execute();
        this.id = authorizationDTO.getId();

        this.note = authorizationDTO.getNote();

        this.scopes = authorizationDTO.getScopes();
        this.token = authorizationDTO.getToken();

    }


    @Override
    public String toString() {
        return "Authorization ["
                + "id=" + id + ", "
                + "note=" + note + ", "
                + "scopes=" + scopes + ", "
                + "token=" + token
                + "]";
    }


    public void destroy() throws OpenShiftException {
        new DeleteAuthorizationRequest().execute();
        this.id=null;
        this.note=null;
        this.scopes=null;
        this.token=null;
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


    private class GetAuthorizationRequest extends ServiceRequest {

        private GetAuthorizationRequest() throws OpenShiftException {
            super(LINK_GET);
        }

        protected AuthorizationResourceDTO execute() throws OpenShiftException {
            return (AuthorizationResourceDTO) super.execute();
        }
    }


    private class DeleteAuthorizationRequest extends ServiceRequest {

        private DeleteAuthorizationRequest() throws OpenShiftException {
            super(LINK_DELETE);
        }

        protected void execute(boolean force) throws OpenShiftException {
            super.execute(new StringParameter(IOpenShiftJsonConstants.PROPERTY_FORCE, String.valueOf(force)));
        }
    }


}
