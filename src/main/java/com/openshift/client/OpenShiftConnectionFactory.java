/******************************************************************************* 
 * Copyright (c) 2012-2014 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/
package com.openshift.client;

import java.io.FileNotFoundException;
import java.io.IOException;

import com.openshift.client.IHttpClient.ISSLCertificateCallback;
import com.openshift.client.configuration.AbstractOpenshiftConfiguration.ConfigurationOptions;
import com.openshift.client.configuration.IOpenShiftConfiguration;
import com.openshift.client.configuration.OpenShiftConfiguration;
import com.openshift.client.utils.SSLUtils;
import com.openshift.internal.client.AbstractOpenShiftConnectionFactory;
import com.openshift.internal.client.IRestService;
import com.openshift.internal.client.RestService;
import com.openshift.internal.client.httpclient.UrlConnectionHttpClientBuilder;
import com.openshift.internal.client.httpclient.request.JsonMediaType;
import com.openshift.internal.client.response.OpenShiftJsonDTOFactory;
import com.openshift.internal.client.utils.Assert;

/**
 * Connection Factory, used to establish a connection and retrieve a user.
 * 
 * @author Xavier Coulon
 * @author Andre Dietisheim
 * @author Corey Daley
 * @author Sean Kavanagh
 * 
 */
public class OpenShiftConnectionFactory extends AbstractOpenShiftConnectionFactory {
	private IOpenShiftConfiguration configuration;
	/**
	 * Establish a connection with the clientId along with user's password.
	 * User's login and Server URL are retrieved from the local configuration
	 * file (in see $USER_HOME/.openshift/express.conf)
	 * 
	 * @param clientId
	 *            http client id
	 * @param password
	 *            user's password
	 * @return a valid connection
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @throws OpenShiftException
	 */
	public IOpenShiftConnection getConnection(final String clientId, final String password) throws OpenShiftException {
		IOpenShiftConfiguration configuration = getConfiguration();
		return getConnection(clientId, configuration.getRhlogin(), password, configuration.getLibraServer());
	}

	/**
	 * Establish a connection with the clientId along with user's login and
	 * password. Server URL is retrieved from the local configuration file (in
	 * see $USER_HOME/.openshift/express.conf)
	 * 
	 * @param clientId
	 *            http client id
	 * @param username
	 *            user's login
	 * @param password
	 *            user's password
	 * @return a valid connection
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @throws OpenShiftException
	 */
	public IOpenShiftConnection getConnection(final String clientId, final String username, final String password)
			throws OpenShiftException {
		return getConnection(clientId, username, password, getConfiguration().getLibraServer());
	}

	/**
	 * Establish a connection with the clientId along with user's login and
	 * password.
	 * 
	 * @param clientId
	 *            http client id
	 * @param username
	 *            user's login.
	 * @param password
	 *            user's password.
	 * @param serverUrl
	 *            the server url.
	 * @return a valid connection
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @throws OpenShiftException
	 */
	public IOpenShiftConnection getConnection(final String clientId, final String username, final String password,
			final String serverUrl) throws OpenShiftException {
		return getConnection(clientId, username, password, serverUrl, (ISSLCertificateCallback) null);
	}

	public IOpenShiftConnection getConnection(final String clientId, final String username, final String password,
			final String serverUrl, ISSLCertificateCallback sslCallback) throws OpenShiftException {
		return getConnection(clientId, username, password, null, null, null, serverUrl, sslCallback);
	}
    public IOpenShiftConnection getConnection(final String clientId, final String token,
                                              final String serverUrl, ISSLCertificateCallback sslCallback) throws OpenShiftException {
        return getConnection(clientId, null, null, null, null, token, serverUrl, sslCallback);
    }

	public IOpenShiftConnection getConnection(final String clientId, final String username, final String password,
			final String authKey, final String authIV, final String serverUrl) throws OpenShiftException {
		return getConnection(clientId, username, password, null, null, null, serverUrl, null);
	}
	
	public IOpenShiftConnection getConnection(final String clientId, final String username, final String password,
			final String authKey, final String authIV, final String token, final String serverUrl,
			final ISSLCertificateCallback sslCertificateCallback) throws OpenShiftException {
		return getConnection(clientId, username, password, authKey, authIV, token, serverUrl, sslCertificateCallback, createCipherExclusionRegex(getConfiguration()));
	}
	
	protected String createCipherExclusionRegex(IOpenShiftConfiguration configuration) {
		if(configuration.getDisableBadSSLCiphers() == ConfigurationOptions.YES
				|| (configuration.getDisableBadSSLCiphers() == ConfigurationOptions.AUTO) && !SSLUtils.supportsDHECipherKeysOf(1024 + 64)) {
			// jdk < 1.8 only support DHE cipher keys <= 1024 bit
			// https://issues.jboss.org/browse/JBIDE-18454
			return SSLUtils.CIPHER_DHE_REGEX;
		} else {
			return null;
		}
	}

	/**
	 * Establish a connection with the clientId along with user's login and
	 * password.
	 * 
	 * @param clientId
	 *            http client id
	 * @param username
	 *            user's login.
	 * @param password
	 *            user's password.
	 * @param token
	 *            authorization token.
	 * @param serverUrl
	 *            the server url.
	 * @return a valid connection
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @throws OpenShiftException
	 */
	public IOpenShiftConnection getConnection(final String clientId, final String username, final String password,
			final String authKey, final String authIV, final String token, final String serverUrl,
			final ISSLCertificateCallback sslCertificateCallback, String exludeSSLCipherRegex)
			throws OpenShiftException {

		Assert.notNull(clientId);
		if (token == null || token.trim().length() == 0) {
			Assert.notNull(username);
			Assert.notNull(password);
		}
		Assert.notNull(serverUrl);

		IHttpClient httpClient = createClient(
				clientId, username, password, authKey, authIV, token, serverUrl, sslCertificateCallback, exludeSSLCipherRegex);
		try {
			return getConnection(clientId, username, password, token, serverUrl, httpClient);
		} catch (IOException e) {
			throw new OpenShiftException(e, "Failed to establish connection for user ''{0}}''", username);
		}
	}

	protected IHttpClient createClient(final String clientId, final String username, final String password,
			final String authKey, final String authIV, final String token, final String serverUrl,
			final ISSLCertificateCallback sslCertificateCallback, String exludeSSLCipherRegex) {
			return new UrlConnectionHttpClientBuilder()
						.setCredentials(username, password, authKey, authIV, token)
						.setSSLCertificateCallback(sslCertificateCallback)
						.setConfigTimeout(getConfiguration().getTimeout())
						.excludeSSLCipher(exludeSSLCipherRegex)
						.client();
	}

	protected IOpenShiftConfiguration getConfiguration() throws OpenShiftException {
		if (this.configuration == null) {
			this.configuration = createConfiguration();
		}
		return this.configuration;
	}
	
	protected IOpenShiftConfiguration createConfiguration() throws OpenShiftException {
		try {
			return new OpenShiftConfiguration();
		} catch (IOException e) {
			throw new OpenShiftException(e, "Failed to load OpenShift configuration file.");
		}
	}

	protected IOpenShiftConnection getConnection(final String clientId, final String username, final String password,
			final String token, final String serverUrl, IHttpClient httpClient) throws OpenShiftException, IOException {
		Assert.notNull(clientId);
		Assert.notNull(serverUrl);
		Assert.notNull(httpClient);

		IRestService service = new RestService(serverUrl, clientId, new JsonMediaType(),
				IHttpClient.MEDIATYPE_APPLICATION_JSON, new OpenShiftJsonDTOFactory(), httpClient);
		return getConnection(service, username, password, token);
	}

      /**
       * Establish a connection with the clientId along with a user's authorization token
       *
       * @param clientId
       *            http client id
       * @param token
       *            authorization token.
       * @param serverUrl
       *            the server url.
       * @return a valid connection
       * @throws FileNotFoundException
       * @throws IOException
       * @throws OpenShiftException
       */
      public IOpenShiftConnection getAuthTokenConnection(final String clientId,final String token, final String serverUrl) throws OpenShiftException {
          return getConnection(clientId, null, null,  null, null, token, serverUrl, null);
       }
      /**
       * Establish a connection with the clientId along with a user's authorization
       * token. Server URL is retrieved from the local configuration file (in
       * see $USER_HOME/.openshift/express.conf)
       *
       * @param clientId
       *            http client id
       * @param token
       *            authorization token.
       * @return a valid connection
       * @throws FileNotFoundException
       * @throws IOException
       * @throws OpenShiftException
       */
      public IOpenShiftConnection getAuthTokenConnection(final String clientId, final String token)
              throws OpenShiftException {
          try {
              configuration = new OpenShiftConfiguration();
          } catch (IOException e) {
              throw new OpenShiftException(e, "Failed to load OpenShift configuration file.");
          }
          return getConnection(clientId, null, null,  null, null, token, configuration.getLibraServer(), null);
      }

}
