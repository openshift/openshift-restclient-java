/*******************************************************************************
 * Copyright (c) 2015 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package com.openshift.internal.restclient.okhttp;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import java.io.IOException;
import java.net.ProtocolException;

import com.openshift.restclient.PredefinedResourceKind;
import org.junit.Test;

import com.openshift.internal.restclient.DefaultClient;
import com.openshift.internal.restclient.okhttp.WatchClient.WatchEndpoint;
import com.openshift.restclient.IOpenShiftWatchListener;
import com.openshift.restclient.IOpenShiftWatchListener.ChangeType;
import com.openshift.restclient.ResourceKind;
import com.openshift.restclient.http.IHttpConstants;

import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.Response;

/**
 * @author Andre Dietisheim
 */
public class WatchClientTest {
	
	@Test
	public void testOnFailureCallBackNotifiesListener() {
		DefaultClient client = null;
		
		IOpenShiftWatchListener listener = mock(IOpenShiftWatchListener.class);
		
		WatchEndpoint endpoint = new WatchEndpoint(client, listener, PredefinedResourceKind.BUILD.getIdentifier());
		endpoint.onFailure(new IOException(), null);
		verify(listener).error(any(Throwable.class));
	}
	
	@Test
	public void shouldIgnoreUnsupportedFeatureResponseOnFailure() {
		DefaultClient client = mock(DefaultClient.class);
		IOpenShiftWatchListener listener = mock(IOpenShiftWatchListener.class);
		
		WatchEndpoint endpoint = new WatchEndpoint(client, listener, PredefinedResourceKind.BUILD.getIdentifier());
		Response.Builder responseBuilder = new Response.Builder();
		responseBuilder.code(IHttpConstants.STATUS_OK)
						.protocol(Protocol.HTTP_2)
						.request(new Request.Builder().url("http://localhost").build());
		endpoint.onFailure(new ProtocolException(), responseBuilder.build());
		verify(listener, never()).error(any());
	}

	@Test
	public void changeTypeShouldEqualSameChangeType() {
		assertThat(ChangeType.ADDED, 
				equalTo(ChangeType.ADDED));
	}
	
	@Test
	public void changeTypeShouldNotEqualDifferentChangeType() {
		assertThat(ChangeType.ADDED, 
				not(equalTo(ChangeType.DELETED)));
	}

	@Test
	public void changeTypeShouldEqualSameChangeTypeInLowercase() {
		assertThat(ChangeType.ADDED, 
				equalTo(new ChangeType(ChangeType.ADDED.getValue().toLowerCase())));
	}
}
