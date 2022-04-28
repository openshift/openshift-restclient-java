/*******************************************************************************
 * Copyright (c) 2016 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/

package com.openshift.restclient;

import java.io.IOException;
import java.net.Proxy;
import java.net.ProxySelector;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Collection;
import java.util.concurrent.TimeUnit;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;

import com.openshift.internal.restclient.DefaultClient;
import com.openshift.internal.restclient.ResourceFactory;
import com.openshift.internal.restclient.authorization.AuthorizationContext;
import com.openshift.internal.restclient.okhttp.AuthenticatorInterceptor;
import com.openshift.internal.restclient.okhttp.ResponseCodeInterceptor;
import com.openshift.restclient.http.IHttpConstants;
import com.openshift.restclient.utils.SSLUtils;

import okhttp3.Authenticator;
import okhttp3.Dispatcher;
import okhttp3.Interceptor;
import okhttp3.OkHttp;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Builder to create IClient instances.
 *
 */
public class ClientBuilder {

    private String baseUrl;
    private ISSLCertificateCallback sslCertificateCallback = new NoopSSLCertificateCallback();
    private boolean sslCertCallbackWithDefaultHostnameVerifier = false;
    private X509Certificate certificate;
    private Collection<X509Certificate> certificateCollection;
    private String certificateAlias;
    private IResourceFactory resourceFactory;
    private String userName;
    private String token;
    private String password;
    private String userAgentPrefix;

    private Proxy proxy;
    private ProxySelector proxySelector;
    private Authenticator proxyAuthenticator;

    private int maxRequests = 64;
    private int maxRequestsPerHost = 10;

    private int readTimeout = IHttpConstants.DEFAULT_READ_TIMEOUT;
    private TimeUnit readTimeoutUnit = TimeUnit.MILLISECONDS;
    private int connectTimeout = IHttpConstants.DEFAULT_READ_TIMEOUT;
    private TimeUnit connectTimeoutUnit = TimeUnit.MILLISECONDS;
    private int writeTimeout = IHttpConstants.DEFAULT_READ_TIMEOUT;
    private TimeUnit writeTimeoutUnit = TimeUnit.MILLISECONDS;
    private int pingInterval = 0;
    private TimeUnit pingIntervalUnit = TimeUnit.MILLISECONDS;

    public ClientBuilder() {
        this(null);
    }

    public ClientBuilder(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public ClientBuilder sslCertificateCallback(ISSLCertificateCallback callback) {
        this.sslCertificateCallback = callback == null ? new NoopSSLCertificateCallback() : callback;
        return this;
    }

    public ClientBuilder sslCertCallbackWithDefaultHostnameVerifier(boolean b) {
        this.sslCertCallbackWithDefaultHostnameVerifier = b;
        return this;
    }

    public ClientBuilder sslCertificate(String alias, X509Certificate cert) {
        this.certificateAlias = alias;
        this.certificate = cert;
        return this;
    }

    public ClientBuilder sslCertificateCollection(String alias, Collection<X509Certificate> certs) {
        this.certificateAlias = alias;
        this.certificateCollection = certs;
        return this;
    }

    public ClientBuilder resourceFactory(IResourceFactory factory) {
        this.resourceFactory = factory;
        return this;
    }

    public ClientBuilder toCluster(String baseUrl) {
        this.baseUrl = baseUrl;
        return this;
    }

    public ClientBuilder withUserName(String userName) {
        this.userName = userName;
        return this;
    }

    public ClientBuilder withPassword(String password) {
        this.password = password;
        return this;
    }

    public ClientBuilder usingToken(String token) {
        this.token = token;
        return this;
    }

    public ClientBuilder usingUserAgentPrefix(String prefix) {
        this.userAgentPrefix = prefix;
        return this;
    }

    public ClientBuilder withConnectTimeout(int timeout, TimeUnit unit) {
        this.connectTimeout = timeout;
        this.connectTimeoutUnit = unit;
        return this;
    }

    /**
     * The connect timeout parameter used for establishing the connection to a
     * remote server
     *
     * @param connectInMillis
     *            A value in milliseconds
     */
    public ClientBuilder withConnectTimeout(int connectInMillis) {
        this.connectTimeout = connectInMillis;
        return this;
    }

    public ClientBuilder withReadTimeout(int timeout, TimeUnit unit) {
        this.readTimeout = timeout;
        this.readTimeoutUnit = unit;
        return this;
    }

    public ClientBuilder withWriteTimeout(int timeout, TimeUnit unit) {
        this.writeTimeout = timeout;
        this.writeTimeoutUnit = unit;
        return this;
    }

    public ClientBuilder withPingInterval(int pingInterval, TimeUnit unit) {
        this.pingInterval = pingInterval;
        this.pingIntervalUnit = unit;
        return this;
    }

    public ClientBuilder proxy(Proxy proxy) {
        this.proxy = proxy;
        return this;
    }

    public ClientBuilder proxySelector(ProxySelector proxySelector) {
        this.proxySelector = proxySelector;
        return this;
    }


    public ClientBuilder proxyAuthenticator(Authenticator proxyAuthenticator) {
        this.proxyAuthenticator = proxyAuthenticator;
        return this;
    }


    /**
     * The maximum concurrent requests for this client.
     *
     * @param maxRequests
     *            the maximum number of concurrent requests
     * @return the client builder
     */
    public ClientBuilder withMaxRequests(int maxRequests) {
        this.maxRequests = maxRequests;
        return this;
    }

    /**
     * The maximum concurrent request for this client for a single host.
     *
     * @param maxRequestsPerHost
     *            the maximum number of concurrent requests for a single host
     * @return the client builder
     */
    public ClientBuilder withMaxRequestsPerHost(int maxRequestsPerHost) {
        this.maxRequestsPerHost = maxRequestsPerHost;
        return this;
    }

    /**
     * Build a client
     *
     * @throws KeyManagementException an exception
     */
    public IClient build() {
        try {
            TrustManagerFactory trustManagerFactory = initTrustManagerFactory(certificateAlias, certificate,
                    certificateCollection);
            X509TrustManager trustManager = getCurrentTrustManager(trustManagerFactory);
            SSLContext sslContext = SSLUtils.getSSLContext(trustManager);

            AuthenticatorInterceptor authenticatorInterceptor = new AuthenticatorInterceptor();
            ResponseCodeInterceptor responseCodeInterceptor = new ResponseCodeInterceptor();
            Dispatcher dispatcher = createDispatcher();

            OkHttpClient okClient = 
                    createOkHttpClient(trustManager, sslContext, authenticatorInterceptor, responseCodeInterceptor, dispatcher);

            IResourceFactory factory = (IResourceFactory) ObjectUtils.defaultIfNull(resourceFactory, new ResourceFactory(null));
            AuthorizationContext authContext = new AuthorizationContext(token, userName, password);
            DefaultClient client = new DefaultClient(new URL(this.baseUrl), okClient, factory, null, authContext);

            authContext.setClient(client);
            authenticatorInterceptor.setClient(client);
            responseCodeInterceptor.setClient(client);
            factory.setClient(client);
            return client;
        } catch (KeyManagementException | NoSuchAlgorithmException | KeyStoreException | CertificateException
                | IOException e) {
            throw new OpenShiftException(e, "Unable to initialize client");
        }
    }

    private Dispatcher createDispatcher() {
        Dispatcher dispatcher = new Dispatcher();

        // hiding these for now to since not certain
        // if we need to really expose them.
        dispatcher.setMaxRequests(maxRequests);
        dispatcher.setMaxRequestsPerHost(maxRequestsPerHost);
        return dispatcher;
    }

    private OkHttpClient createOkHttpClient(X509TrustManager trustManager, SSLContext sslContext,
            AuthenticatorInterceptor authenticatorInterceptor, ResponseCodeInterceptor responseCodeInterceptor, Dispatcher dispatcher) {
        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .addNetworkInterceptor(new UserAgentInterceptor(userAgentPrefix))
                .addInterceptor(responseCodeInterceptor)
                .addInterceptor(authenticatorInterceptor)
                .dispatcher(dispatcher)
                .readTimeout(readTimeout, readTimeoutUnit)
                .writeTimeout(writeTimeout, writeTimeoutUnit)
                .connectTimeout(connectTimeout, connectTimeoutUnit)
                .pingInterval(pingInterval, pingIntervalUnit)
                .sslSocketFactory(sslContext.getSocketFactory(), trustManager);

        if (!this.sslCertCallbackWithDefaultHostnameVerifier) {
            builder.hostnameVerifier(sslCertificateCallback);
        }

        if (proxy != null) {
            builder.proxy(proxy);
        }

        if (proxySelector != null) {
            builder.proxySelector(proxySelector);
        }

        if (proxyAuthenticator != null) {
            builder.proxyAuthenticator(proxyAuthenticator);
        }

        return builder.build();
    }

    private X509TrustManager getCurrentTrustManager(TrustManagerFactory trustManagerFactory)
            throws NoSuchAlgorithmException, KeyStoreException {
        for (TrustManager trustManager : trustManagerFactory.getTrustManagers()) {
            if (trustManager instanceof X509TrustManager) {
                X509TrustManager x509TrustManager = (X509TrustManager) trustManager;
                return new CallbackTrustManager(x509TrustManager, this.sslCertificateCallback);
            }
        }
        return null;

    }

    private TrustManagerFactory initTrustManagerFactory(String alias, X509Certificate cert,
            Collection<X509Certificate> certs)
            throws NoSuchAlgorithmException, KeyStoreException, CertificateException, IOException {
        TrustManagerFactory trustManagerFactory = TrustManagerFactory
                .getInstance(TrustManagerFactory.getDefaultAlgorithm());
        if (alias != null && (cert != null || certs != null)) {
            KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());
            // need this load to initialize the key store, and allow for the subsequent set
            // certificate entry
            ks.load(null, null);
            if (cert != null) {
                cert.checkValidity();
                ks.setCertificateEntry(alias, cert);
            }
            if (certs != null) {
                int i = 0;
                for (X509Certificate x509 : certs) {
                    x509.checkValidity();
                    ks.setCertificateEntry(alias + i, x509);
                    i++;
                }
            }

            // testing has proven that you can only call init() once for a
            // TrustManagerFactory wrt loading certs
            // from the KeyStore ... subsequent KeyStore.setCertificateEntry /
            // TrustManagerFactory.init calls are
            // ignored.
            // So if a specific cert is required to validate this connection's communication
            // with the server, add it up front
            // in the ctor.
            trustManagerFactory.init(ks);
        } else {
            trustManagerFactory.init((KeyStore) null);
        }
        return trustManagerFactory;
    }

    private static class CallbackTrustManager implements X509TrustManager {

        private X509TrustManager trustManager;
        private ISSLCertificateCallback callback;

        private CallbackTrustManager(X509TrustManager currentTrustManager, ISSLCertificateCallback callback)
                throws NoSuchAlgorithmException, KeyStoreException {
            this.trustManager = currentTrustManager;
            this.callback = callback;
        }

        public X509Certificate[] getAcceptedIssuers() {
            return trustManager.getAcceptedIssuers();
        }

        public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
            try {
                trustManager.checkServerTrusted(chain, authType);
            } catch (CertificateException e) {
                if (!callback.allowCertificate(chain)) {
                    throw e;
                }
            }
        }

        public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
            trustManager.checkServerTrusted(chain, authType);
        }
    }

    private static class UserAgentInterceptor implements Interceptor {
        
        private final String userAgent;
        
        public UserAgentInterceptor(String userAgentPrefix) {
            this.userAgent = StringUtils.join(
                    new String[]{ userAgentPrefix, "openshift-restclient-java", "okhttp/" + OkHttp.VERSION }, "/");
        }


        @Override
        public Response intercept(Chain chain) throws IOException {
            Request agent = chain.request().newBuilder().header("User-Agent", userAgent).build();
            return chain.proceed(agent);
        }
    }

}
