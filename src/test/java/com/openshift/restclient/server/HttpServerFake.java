/*************************************************************************
 * 
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.  The
 * ASF licenses this file to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the
 * License.  You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations
 * under the License.
 * 
 *************************************************************************/
package com.openshift.restclient.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.text.MessageFormat;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.apache.commons.io.IOUtils;
/**
 * @author André Dietisheim
 * @author Nicolas Spano
 */
public class HttpServerFake {

	public static final int DEFAULT_PORT = 1234;
	private static final String DEFAULT_STATUSLINE = "HTTP/1.1 200 OK\n";
	
	private ExecutorService executor;
	private int port;
	private String response;
	private String statusLine;
	private ServerFakeSocket serverFakeSocket;

	public HttpServerFake(int port) {
		this(port, null, DEFAULT_STATUSLINE);
	}

	public HttpServerFake() {
		this(null);
	}

	public HttpServerFake(String response) {
		this(DEFAULT_PORT, response, DEFAULT_STATUSLINE);
	}

	public HttpServerFake(String response, String statusLine) {
		this(DEFAULT_PORT, response, statusLine);
	}

	/**
	 * 
	 * @param port
	 *            the port to listen to (address is always localhost)
	 * @param response
	 *            the reponse to return to the requesting socket. If
	 *            <code>null</code> the request string is returned.
	 * @param statusLine the staus line that shall be returned
	 * 	           
	 * @see ServerFakeSocket#getResponse(Socket)
	 */
	public HttpServerFake(int port, String response, String statusLine) {
		this.port = port;
		this.response = response;
		if (statusLine != null) {
			this.statusLine = statusLine;
		} else {
			this.statusLine = DEFAULT_STATUSLINE;
		}
	}

	public void start() throws Exception {
		executor = Executors.newFixedThreadPool(1);
		this.serverFakeSocket = createServerFakeSocket(statusLine, response, port);
		executor.submit(serverFakeSocket);
	}

	protected ServerFakeSocket createServerFakeSocket(String statusLine, String response, int port) throws Exception {
		return new  ServerFakeSocket(statusLine, response, port);
	}

	public URL getUrl() throws MalformedURLException {
		return new URL(MessageFormat.format("http://localhost:{0}/", String.valueOf(port)));
	}

	public URL getHttpsUrl() throws MalformedURLException {
		return new URL(MessageFormat.format("https://localhost:{0}/", String.valueOf(port)));
	}
	
	public void stop() {
		serverFakeSocket.close();
		executor.shutdownNow();
	}


	protected void write(byte[] bytes, OutputStream outputStream) throws IOException{
        outputStream.write(bytes);
    }
	
	protected int getPort() {
		return port;
	}
	
	protected class ServerFakeSocket implements Runnable {

		private String statusLine;
		private String response;
		private ServerSocket serverSocket;
		
		public ServerFakeSocket(String statusLine, String response, int port) throws Exception {
			this.statusLine = statusLine;
			this.response = response;
			this.serverSocket = createServerSocket(port);
		}
		
		protected ServerSocket createServerSocket(int port) throws Exception {
			return new ServerSocket(port);
		}

		public void run() {
			Socket socket = null;
			OutputStream outputStream = null;
			try {
				socket = serverSocket.accept();
				String response = getResponse(socket);
				outputStream = socket.getOutputStream();
				writeResponseHeader(outputStream);
				write(response.getBytes(), outputStream);
				outputStream.flush();
			} catch (IOException e) {
				// e.printStackTrace();
			} finally {
				// we should not close the connection, let the client close the
				// connection
				IOUtils.closeQuietly(outputStream);
			}
		}

		protected void writeResponseHeader(OutputStream outputStream) throws IOException {
			outputStream.write(statusLine.getBytes());
			outputStream.write("\n".getBytes());
		}
		
		/**
		 * Returns the response given to this server at creation time or the
		 * content that may be read from the socket is returned.
		 * 
		 * @param inputStream
		 * @return
		 * @throws IOException
		 */
		private String getResponse(Socket socket) throws IOException {
			if (response != null) {
				return response;
			}
			return readRequestToString(socket.getInputStream());
		}

		private String readRequestToString(InputStream inputStream) throws IOException {
			BufferedReader bufferedReader = null;
			bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
			StringWriter writer = new StringWriter();
			String line = null;
			while ((line = bufferedReader.readLine()) != null) {
				if (line.isEmpty()) {
					break;
				}
				writer.write(line);
				writer.write('\n');
			}
			return writer.toString();
		}

		public void close() {
			silentlyClose(serverSocket);
		}
		
		private void silentlyClose(ServerSocket serverSocket) {
			if (serverSocket != null) {
				try {
					serverSocket.close();
				} catch (IOException e) {
					//e.printStackTrace();
				}
			}
		}
		
	}
}
