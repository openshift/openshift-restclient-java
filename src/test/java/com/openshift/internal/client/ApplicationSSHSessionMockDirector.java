/******************************************************************************* 
 * Copyright (c) 2013 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/
package com.openshift.internal.client;

import java.io.ByteArrayInputStream;
import java.net.SocketTimeoutException;

import org.mockito.Mockito;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.openshift.client.IApplication;
import com.openshift.internal.client.httpclient.HttpClientException;

/**
 * @author Andre Dietisheim
 * @author Syed Iqbal
 */
public class ApplicationSSHSessionMockDirector {

	private ApplicationSSHSession applicationSession;
	private ApplicationSSHSession spyedApplicationSession;

	public ApplicationSSHSessionMockDirector(IApplication application) throws SocketTimeoutException,
			HttpClientException, JSchException {
		ApplicationResource spyedApplication = Mockito.spy(((ApplicationResource) application));
		this.applicationSession =
				new ApplicationSSHSession(spyedApplication, new JSch().getSession("mockuser", "mockhost", 22));
		this.spyedApplicationSession = Mockito.spy(((ApplicationSSHSession) applicationSession));
		Mockito.doReturn(true)
				.when(spyedApplicationSession)
				.isConnected();
	}

	public ApplicationSSHSessionMockDirector mockGetForwardablePorts(String response) {
		Mockito.doReturn(new ByteArrayInputStream(response.getBytes()))
				.when(spyedApplicationSession)
				.execCommand(Mockito.anyString(), (ApplicationSSHSession.ChannelInputStreams) Mockito.any(),
						(Session) Mockito.any());
		return this;
	}

	public ApplicationSSHSessionMockDirector mockGetEnvironmentProperties(String response) {
		Mockito.doReturn(new ByteArrayInputStream(response.getBytes()))
				.when(spyedApplicationSession)
				.execCommand(Mockito.anyString(),
						(ApplicationSSHSession.ChannelInputStreams) Mockito.any(),
						(Session) Mockito.any());
		return this;
	}

	public ApplicationSSHSession getMock() {
		return spyedApplicationSession;
	}

}
