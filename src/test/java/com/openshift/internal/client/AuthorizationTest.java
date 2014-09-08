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
import com.openshift.client.IOpenShiftConnection;
import com.openshift.client.IUser;
import com.openshift.client.utils.TestConnectionFactory;
import com.openshift.internal.client.httpclient.HttpClientException;
import org.junit.Before;
import org.junit.Test;

import java.net.SocketTimeoutException;

import static org.junit.Assert.*;

public class AuthorizationTest extends TestTimer {

    private IUser user;
    private HttpClientMockDirector mockDirector;

    @Before
    public void setUp() throws SocketTimeoutException, HttpClientException, Throwable {
        this.mockDirector = new HttpClientMockDirector();
        final IOpenShiftConnection connection =
                new TestConnectionFactory().getConnection();
        this.user = connection.getUser();
    }

    @Test
    public void shouldCreateGenericAuthorization() throws Exception {

        IAuthorization authorization = user.getAuthorization();
        assertNotNull(authorization.getToken());
        assertEquals(authorization.getScopes(), "session");

        IOpenShiftConnection connection =
                new TestConnectionFactory().getAuthTokenConnection(authorization.getToken());

        authorization = connection.getUser().getAuthorization();
        assertEquals(authorization.getScopes(), "session");
        
        authorization.destroy();

        assertNull(authorization.getToken());
    }

    @Test
    public void shouldCreateAuthorization() throws Exception {

        IAuthorization authorization = user.createAuthorization("my note", "session read");
        assertNotNull(authorization.getToken());
        assertEquals(authorization.getScopes(), "session read");

        IOpenShiftConnection connection =
                new TestConnectionFactory().getAuthTokenConnection(authorization.getToken());

        authorization = connection.getUser().getAuthorization();

        assertEquals(authorization.getScopes(), "session read");
        assertEquals(authorization.getNote(), "my note");

        authorization.destroy();

        assertNull(authorization.getToken());
    }

    @Test
    public void shouldCreateAuthorizationWithExpiration() throws Exception {

        IAuthorization authorization = user.createAuthorization("my note", "session read", 600);
        assertNotNull(authorization.getToken());
        assertEquals(authorization.getScopes(), "session read");

        IOpenShiftConnection connection =
                new TestConnectionFactory().getAuthTokenConnection(authorization.getToken());

        authorization = connection.getUser().getAuthorization();

        assertEquals(authorization.getScopes(), "session read");
        assertEquals(authorization.getNote(), "my note");

        authorization.destroy();

        assertNull(authorization.getToken());
    }


}
