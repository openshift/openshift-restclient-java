/*******************************************************************************
 * Copyright (c) 2012 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Red Hat, Inc. - initial API and implementation
 ******************************************************************************/

package com.openshift.restclient.server;

import java.net.MalformedURLException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.security.KeyStore;
import java.text.MessageFormat;
import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLServerSocket;

/**
 * A Https server fake that holds a single self-signed certificate in its
 * keystore. The certificate is in src/test/resources/server-keystore.jks and
 * hold a single self-signed certificate that was created in the following way
 * (password: 123456):
 * 
 * <pre>
 * <code>
 * keytool -genkey -keystore server-keystore.jks -alias localhost -dname "CN=localhost,OU=JBoss Tools" -keyalg "RSA" -sigalg "SHA1withRSA" -keysize 2048 -validity 3650
 * </code>
 * </pre>
 *
 * @author André Dietisheim
 */
public class HttpsServerFake extends HttpServerFake {

    private static final String KEYSTORE_PASSWORD = "123456";
    private static final String KEYSTORE_TYPE = "JKS";
    private static final String KEYSTORE_FILE = "/server-keystore.jks";

    public HttpsServerFake(int port) {
        super(port);
    }

    /**
     *
     * @param port
     *            the port to listen to (address is always localhost)
     * @param response
     *            the reponse to return to the requesting socket. If
     *            <code>null</code> the request string is returned.
     * @param statusLine
     *            the status line that shall be returned
     *
     * @see ServerFakeSocket#getResponse(Socket)
     */
    public HttpsServerFake(int port, String response, String statusLine) {
        super(port, response, statusLine);
    }

    @Override
    public URL getUrl() throws MalformedURLException {
        return new URL(MessageFormat.format("https://localhost:{0}/", String.valueOf(getPort())));
    }

    @Override
    protected ServerFakeSocket createServerFakeSocket(String statusLine, String response, int port) throws Exception {
        return new HttpsServerFakeSocket(statusLine, response, port);
    }

    protected class HttpsServerFakeSocket extends ServerFakeSocket {

        public HttpsServerFakeSocket(String statusLine, String response, int port) throws Exception {
            super(statusLine, response, port);
        }

        @Override
        protected ServerSocket createServerSocket(int port) throws Exception {
            KeyStore keyStore = KeyStore.getInstance(KEYSTORE_TYPE);
            keyStore.load(getClass().getResourceAsStream(KEYSTORE_FILE), KEYSTORE_PASSWORD.toCharArray());

            KeyManagerFactory keyManagerFactory = KeyManagerFactory
                    .getInstance(KeyManagerFactory.getDefaultAlgorithm());
            keyManagerFactory.init(keyStore, KEYSTORE_PASSWORD.toCharArray());
            KeyManager [] keyManagers = keyManagerFactory.getKeyManagers();

            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(keyManagers, null, null);
            SSLServerSocket sslServerSocket = (SSLServerSocket) sslContext.getServerSocketFactory()
                    .createServerSocket(port);
            sslServerSocket.setEnabledCipherSuites(sslContext.getServerSocketFactory().getSupportedCipherSuites());
            return sslServerSocket;
        }
    }
}
