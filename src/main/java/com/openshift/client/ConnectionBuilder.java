/******************************************************************************* 
 * Copyright (c) 2014 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/
package com.openshift.client;

import java.io.IOException;
import java.util.Collections;
import java.util.Map;

import com.openshift.client.IHttpClient.ISSLCertificateCallback;
import com.openshift.client.configuration.AbstractOpenshiftConfiguration.ConfigurationOptions;
import com.openshift.client.configuration.IOpenShiftConfiguration;
import com.openshift.client.configuration.OpenShiftConfiguration;
import com.openshift.client.utils.SSLUtils;
import com.openshift.internal.client.APIResource;
import com.openshift.internal.client.IRestService;
import com.openshift.internal.client.RestService;
import com.openshift.internal.client.httpclient.UrlConnectionHttpClientBuilder;
import com.openshift.internal.client.httpclient.request.JsonMediaType;
import com.openshift.internal.client.httpclient.request.Parameter;
import com.openshift.internal.client.response.Link;
import com.openshift.internal.client.response.OpenShiftJsonDTOFactory;
import com.openshift.internal.client.response.RestResponse;
import com.openshift3.client.authorization.BasicAuthorizationStrategy;
import com.openshift3.client.authorization.BearerTokenAuthorizationStrategy;
import com.openshift3.client.authorization.IAuthorizationStrategy;
import com.openshift3.client.authorization.KerbrosBrokerAuthorizationStrategy;

/**
 * A builder for a connection to OpenShift.
 * 
 * @author Andre Dietisheim
 */
public class ConnectionBuilder {

	private String serverUrl;
	private IOpenShiftConfiguration configuration;

	public ConnectionBuilder() throws OpenShiftException, IOException {
		this(null);
	}

	public ConnectionBuilder(String serverUrl) throws OpenShiftException, IOException {
		this.configuration = createConfiguration();
		if (serverUrl == null) {
			serverUrl = configuration.getLibraServer();
		}
		this.serverUrl = serverUrl;
	}

	protected IOpenShiftConfiguration getConfiguration() {
		return configuration;
	}
	
	public CredentialsConnectionBuilder credentials(String username, String password) {
		return new CredentialsConnectionBuilder(username, password, serverUrl, configuration);
	}

	public CredentialsConnectionBuilder credentials(String password) {
		return credentials(configuration.getRhlogin(), password);
	}

	public TokenConnectionBuilder token(String token) {
		return new TokenConnectionBuilder(token, serverUrl, configuration);
	}

	public KeyConnectionBuilder key(String authIV, String authKey) {
		return new KeyConnectionBuilder(authIV, authKey, serverUrl, configuration);
	}

	protected IOpenShiftConfiguration createConfiguration() throws IOException {
		if (this.configuration == null) {
			this.configuration = new OpenShiftConfiguration();
		}
		return configuration;
	}

	public class KeyConnectionBuilder extends AbstractConnectionBuilder {

		private IAuthorizationStrategy authStrategy;

		protected KeyConnectionBuilder(String authIV, String authKey, String serverUrl,
				IOpenShiftConfiguration configuration) {
			super(serverUrl, configuration);
			this.authStrategy = new KerbrosBrokerAuthorizationStrategy(authKey, authIV);
		}

		@Override
		public IOpenShiftConnection create() {
			
			IHttpClient client = createHttpClient(
					clientId,
					authStrategy,
					serverUrl,
					timeout,
					callback,
					sslCipherExclusionRegex);
			return getAPIResource(null, null, null, createRestService(client));
		}
	}

	public class TokenConnectionBuilder extends AbstractConnectionBuilder {

		private final BearerTokenAuthorizationStrategy authStrategy;

		protected TokenConnectionBuilder(String token, String serverUrl, IOpenShiftConfiguration configuration) {
			super(serverUrl, configuration);
			this.authStrategy = new BearerTokenAuthorizationStrategy(token);
		}

		@Override
		public IOpenShiftConnection create() {
			// TODO: separate auth strategies in UrlConnectionHttpClient
			IHttpClient client = createHttpClient(
					clientId,
					authStrategy,
					serverUrl,
					timeout,
					callback,
					sslCipherExclusionRegex);
			return getAPIResource(null, null, authStrategy.getToken(), createRestService(client));
		}
	}
	
	public class CredentialsConnectionBuilder extends AbstractConnectionBuilder {

		private final BasicAuthorizationStrategy authStrategy;
		
		CredentialsConnectionBuilder(String username, String password, String serverUrl,
				IOpenShiftConfiguration configuration) {
			super(serverUrl, configuration);
			this.authStrategy = new BasicAuthorizationStrategy(username, password);
		}

		@Override
		public IOpenShiftConnection create() {
			// TODO: separate auth strategies in UrlConnectionHttpClient
			IHttpClient client = createHttpClient(
					clientId,
					authStrategy,
					serverUrl,
					timeout,
					callback,
					sslCipherExclusionRegex);
			return getAPIResource(authStrategy.getUsername(), authStrategy.getPassword(), null, createRestService(client));
		}
		
		public IOpenShiftConnection create(IHttpClient client) {
			return getAPIResource(authStrategy.getUsername(), authStrategy.getPassword(), null, createRestService(client));
		}
	
	}

	public abstract class AbstractConnectionBuilder {

		protected String serverUrl;
		protected String clientId;
		protected ISSLCertificateCallback callback;
		protected String sslCipherExclusionRegex;
		protected IOpenShiftConfiguration configuration;
		protected int timeout;

		protected AbstractConnectionBuilder(String serverUrl, IOpenShiftConfiguration configuration) {
			this.serverUrl = serverUrl;
			this.configuration = configuration;
			disableBadSSLCiphers(configuration.getDisableBadSSLCiphers());
		}

		public AbstractConnectionBuilder clientId(String clientId) {
			this.clientId = clientId;
			return this;
		}

		public AbstractConnectionBuilder sslCertificateCallback(ISSLCertificateCallback callback) {
			this.callback = callback;
			return this;
		}

		public AbstractConnectionBuilder disableSSLCertificateChecks() {
			return sslCertificateCallback(new NoopSSLCertificateCallback());
		}

		public AbstractConnectionBuilder sslCipherExclusion(String sslCipherExclusionRegex) {
			this.sslCipherExclusionRegex = sslCipherExclusionRegex;
			return this;
		}

		public AbstractConnectionBuilder disableBadSSLCiphers(ConfigurationOptions option) {
			this.sslCipherExclusionRegex = createCipherExclusionRegex(option);
			return this;
		}

		public AbstractConnectionBuilder timeout(int timeout) {
			this.timeout = timeout;
			return this;
		}

		public abstract IOpenShiftConnection create();

		protected String createCipherExclusionRegex(ConfigurationOptions option) {
			if (option == ConfigurationOptions.YES
					|| (option == ConfigurationOptions.AUTO) && !SSLUtils.supportsDHECipherKeysOf(1024 + 64)) {
				// jdk < 1.8 only support DHE cipher keys <= 1024 bit
				// https://issues.jboss.org/browse/JBIDE-18454
				return SSLUtils.CIPHER_DHE_REGEX;
			} else {
				return null;
			}
		}

		protected IRestService createRestService(IHttpClient httpClient) {
			return new RestService(serverUrl, clientId, new JsonMediaType(),
					IHttpClient.MEDIATYPE_APPLICATION_JSON, new OpenShiftJsonDTOFactory(), httpClient);
		}

		public IHttpClient createHttpClient(final String clientId, final IAuthorizationStrategy authStrategy, final String serverUrl,
				final int timeout, final ISSLCertificateCallback sslCertificateCallback, String exludeSSLCipherRegex) {
			return new UrlConnectionHttpClientBuilder()
					.setAuthorizationStrategy(authStrategy)
					.setConfigTimeout(timeout)
					.setSSLCertificateCallback(sslCertificateCallback)
					.excludeSSLCipher(exludeSSLCipherRegex)
					.client();
		}

		@SuppressWarnings("unchecked")
		protected APIResource getAPIResource(String username, String password, String token, IRestService service) {
			RestResponse response =
					(RestResponse) service.request(
							new Link("Get API", "/api", HttpMethod.GET),
							IHttpClient.NO_TIMEOUT,
							Collections.<Parameter> emptyList(),
							Collections.<Parameter> emptyList());
			return new APIResource(username, password, token, service, (Map<String, Link>) response.getData()) {};
		}
	}
}
