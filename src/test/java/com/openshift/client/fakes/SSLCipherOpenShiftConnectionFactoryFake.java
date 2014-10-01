package com.openshift.client.fakes;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;

import com.openshift.client.IHttpClient;
import com.openshift.client.IHttpClient.ISSLCertificateCallback;
import com.openshift.client.IOpenShiftConnection;
import com.openshift.client.OpenShiftException;
import com.openshift.client.configuration.AbstractOpenshiftConfiguration.ConfigurationOptions;
import com.openshift.client.configuration.IOpenShiftConfiguration;
import com.openshift.client.utils.OpenShiftTestConfiguration;
import com.openshift.client.utils.SSLUtils;
import com.openshift.client.utils.TestConnectionFactory;
import com.openshift.internal.client.APIResource;
import com.openshift.internal.client.IRestService;
import com.openshift.internal.client.httpclient.UrlConnectionHttpClient;
import com.openshift.internal.client.response.Link;

public class SSLCipherOpenShiftConnectionFactoryFake extends TestConnectionFactory {

	private FilteredCiphersClientFake client;
	private ConfigurationOptions disableBadCiphers;

	public SSLCipherOpenShiftConnectionFactoryFake(ConfigurationOptions disableBadCiphers) {
		this.disableBadCiphers = disableBadCiphers;
	}

	public String[] getSupportedCiphers() throws MalformedURLException, IOException, KeyManagementException, NoSuchAlgorithmException {
		getConnection(); // create client
		junit.framework.Assert.assertNotNull("http client was not created yet", client);
		return client.getSupportedCiphers(SSLUtils.getSSLContext(null));
	}
	
	public String[] getFilteredCiphers() throws MalformedURLException, IOException {
		getConnection(); // create client
		junit.framework.Assert.assertNotNull("http client was not created yet", client);
		return client.getFilteredCiphers();
	}
	
	@Override
	protected IOpenShiftConfiguration createConfiguration() throws OpenShiftException {
		try {
			return new OpenShiftTestConfiguration() {

				@Override
				public ConfigurationOptions getDisableBadSSLCiphers() {
					return disableBadCiphers;
				}

			};
		} catch (IOException e) {
			throw new OpenShiftException(e, "Could not create OpenShift configuration");
		}
	}

	@Override
	protected IHttpClient createClient(String clientId, String username, String password, String authKey,
			String authIV, String token, String serverUrl, ISSLCertificateCallback sslCertificateCallback,
			String exludeSSLCipherRegex) {
		return this.client = new FilteredCiphersClientFake(clientId, username, password, null, null, null, authKey, authIV, token,
				sslCertificateCallback, IHttpClient.NO_TIMEOUT, exludeSSLCipherRegex);
	}
	
	@Override
	protected IOpenShiftConnection getConnection(IRestService service, final String login, final String password, final String token) 
			throws IOException, OpenShiftException {
		return new APIResource(login, password, token, service, new HashMap<String, Link>()) {};
	}
	
	public class FilteredCiphersClientFake extends UrlConnectionHttpClient {

		private FilteredCiphersClientFake(String clientId, String username, String password, String userAgent, String mediaType,
				String acceptVersion, String authKey, String authIv, String token, ISSLCertificateCallback callback, int timeout,
				String excludedSSLCipherRegex) {
			super(username, password, userAgent, mediaType, acceptVersion, authKey, authIv, token, callback, timeout,
					excludedSSLCipherRegex);
			
		}

		@Override
		protected String[] getSupportedCiphers(SSLContext sslContext) {
			try {
				return SSLUtils.getSSLContext(null).getServerSocketFactory().getSupportedCipherSuites();
			} catch (GeneralSecurityException e) {
				throw new RuntimeException(e);
			}
		}

		public String[] getFilteredCiphers() throws MalformedURLException, IOException {
			HttpsURLConnection connection = (HttpsURLConnection) createConnection(
					new URL("https://localhost"), username, password, authKey, authIV, token, userAgent,
					acceptedVersion, acceptedMediaType, null, IHttpClient.NO_TIMEOUT);
			return connection.getSSLSocketFactory().getSupportedCipherSuites();
		}
	}
}
