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

import java.io.IOException;

import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.ws.WebSocket;
import okhttp3.ws.WebSocketListener;
import okio.Buffer;

/**
 * Adapter to WebSocketListener
 * @author jeff.cantrill
 *
 */
public class WebSocketAdapter implements WebSocketListener{

	@Override
	public void onOpen(WebSocket webSocket, Response response) {
	}

	@Override
	public void onFailure(IOException e, Response response) {
	}

	@Override
	public void onMessage(ResponseBody message) throws IOException {
	}

	@Override
	public void onPong(Buffer payload) {
	}

	@Override
	public void onClose(int code, String reason) {
	}
	
}