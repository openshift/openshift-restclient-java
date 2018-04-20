/******************************************************************************* 
 * Copyright (c) 2016 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/

package com.openshift.internal.restclient.okhttp;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import org.apache.commons.lang.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.openshift.restclient.authorization.IAuthorizationContext;

import okhttp3.Headers;
import okhttp3.Request;
import okhttp3.Request.Builder;

@RunWith(MockitoJUnitRunner.class)
public class BasicChallangeHandlerTest {

    @Mock
    private IAuthorizationContext context;
    private BasicChallangeHandler handler;

    @Before
    public void setUp() throws Exception {
        this.handler = new BasicChallangeHandler(context);

        when(context.getUserName()).thenReturn("username");
        when(context.getPassword()).thenReturn("password");
    }

    @Test
    public void testCanHandle() {
        assertTrue(handler.canHandle(givenHeader(OpenShiftAuthenticator.PROPERTY_WWW_AUTHENTICATE, "basic")));
        assertTrue(handler.canHandle(givenHeader(OpenShiftAuthenticator.PROPERTY_WWW_AUTHENTICATE, "bAsIC")));
        assertFalse(handler.canHandle(givenHeader(OpenShiftAuthenticator.PROPERTY_WWW_AUTHENTICATE, "foobar")));
        assertFalse(handler.canHandle(givenHeader(OpenShiftAuthenticator.PROPERTY_WWW_AUTHENTICATE, "")));
        assertFalse(handler.canHandle(givenHeader("key", "value")));
    }

    @Test
    public void testHandleChallange() {
        Builder builder = new Request.Builder().url("http://foo");
        Request request = handler.handleChallange(builder).build();
        String authorization = request.header(OpenShiftAuthenticator.PROPERTY_AUTHORIZATION);
        assertTrue("Exp. auth to not be blank", StringUtils.isNotBlank(authorization));
        assertTrue("Exp. auth to be basic", authorization.startsWith(OpenShiftAuthenticator.AUTHORIZATION_BASIC));
    }

    @Test
    public void testHandleChallangeWhenUsernameIsNull() {
        when(context.getUserName()).thenReturn(null);
        Builder builder = new Request.Builder().url("http://foo");
        Request request = handler.handleChallange(builder).build();
        String authorization = request.header(OpenShiftAuthenticator.PROPERTY_AUTHORIZATION);
        assertTrue("Exp. auth to not be blank", StringUtils.isNotBlank(authorization));
        assertTrue("Exp. auth to be basic", authorization.startsWith(OpenShiftAuthenticator.AUTHORIZATION_BASIC));
    }

    private Headers givenHeader(String name, String value) {
        return new Headers.Builder().add(name, value).build();
    }
}
