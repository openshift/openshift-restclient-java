package com.openshift.client.fakes;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;

import com.openshift.client.IHttpClient;
import com.openshift.client.OpenShiftException;
import com.openshift.client.configuration.IOpenShiftConfiguration;
import com.openshift.client.utils.SSLUtils;
import com.openshift.client.utils.TestConnectionBuilder;
import com.openshift.internal.client.APIResource;
import com.openshift.internal.client.httpclient.UrlConnectionHttpClient;

public class SSLCiphersConnectionBuilderFake extends TestConnectionBuilder {

	public SSLCiphersConnectionBuilderFake() throws OpenShiftException, IOException {
	}

	public SSLCiphersConnectionBuilder sslCiphersConnection() throws IOException {
		return new SSLCiphersConnectionBuilder(null, getConfiguration());
	}
	
	public class SSLCiphersConnectionBuilder extends AbstractConnectionBuilder {

		SSLCiphersConnectionBuilder(String serverUrl, IOpenShiftConfiguration configuration) {
			super(serverUrl, configuration);
		}

		@Override
		public SSLCipherConnection create() {
			return new SSLCipherConnection(new FilteredSSLCiphersClientFake(sslCipherExclusionRegex));
		}
	}

	private class FilteredSSLCiphersClientFake extends UrlConnectionHttpClient {

		private FilteredSSLCiphersClientFake(String excludedSSLCipherRegex) {
			super(null, MEDIATYPE_APPLICATION_JSON, null, null, null, excludedSSLCipherRegex);
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
					new URL("https://localhost"), 
					userAgent,
					acceptedVersion, 
					acceptedMediaType, 
					null, 
					IHttpClient.NO_TIMEOUT);
			return connection.getSSLSocketFactory().getSupportedCipherSuites();
		}
	}
	
	public static class SSLCipherConnection extends APIResource {

		private FilteredSSLCiphersClientFake client;

		protected SSLCipherConnection(FilteredSSLCiphersClientFake client) {
			super(null, null, null);
			junit.framework.Assert.assertNotNull("http client was not created yet", client);
			this.client = client;
		}

		public String[] getSupportedCiphers() throws MalformedURLException, IOException, KeyManagementException, NoSuchAlgorithmException {
			return client.getSupportedCiphers(SSLUtils.getSSLContext(null));
		}
		
		public String[] getFilteredCiphers() throws MalformedURLException, IOException {
			return client.getFilteredCiphers();
		}
	}
}
