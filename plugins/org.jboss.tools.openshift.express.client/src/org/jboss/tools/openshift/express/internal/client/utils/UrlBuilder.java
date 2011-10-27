package org.jboss.tools.openshift.express.internal.client.utils;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Collection;

/**
 * A builder for an url. Currently no state checking is done, the InternalUser is
 * responsible to build something that makes sense.
 * 
 * @author André Dietisheim
 */
public class UrlBuilder {
	private static final String URL_ENCODING = "UTF-8";
	private static final String HOST_PROTOCOL_DELIMITER = ":";
	private static final String HTTP_PROTOCOL_PREFIX = "http://";
	private static final char PARAMETER_URL_DELIMITER = '?';
	private static final char PARAMETER_DELIMITER = '&';
	private static final char PARAMETER_NAME_VALUE_DELIMITER = '=';
	private static final char PATH_SEPARATOR = '/';

	private StringBuilder urlStringBuilder = new StringBuilder();

	private boolean parametersAdded = false;

	public UrlBuilder() {
	}

	public UrlBuilder(String baseUrl) {
		urlStringBuilder.append(baseUrl);
	}

	public UrlBuilder(URL baseUrl) {
		urlStringBuilder.append(baseUrl.toString());
	}

	/**
	 * adds a host to .
	 * 
	 * @param host
	 *            the host
	 * 
	 * @return the url builder
	 */
	public UrlBuilder host(String host) {
		urlStringBuilder.append(HTTP_PROTOCOL_PREFIX);
		urlStringBuilder.append(host);
		return this;
	}

	/**
	 * Adds a port.
	 * 
	 * @param port
	 *            the port
	 * 
	 * @return the url builder
	 */
	public UrlBuilder port(String port) {
		urlStringBuilder.append(HOST_PROTOCOL_DELIMITER);
		urlStringBuilder.append(port);
		return this;
	}

	/**
	 * adds a path to the url.
	 * 
	 * @param path
	 *            the path
	 * 
	 * @return the url builder
	 */
	public UrlBuilder path(String path) {
		urlStringBuilder.append(PATH_SEPARATOR);
		urlStringBuilder.append(path);
		return this;
	}

	public UrlBuilder path(Collection<String> paths) {
		for (String path : paths) {
			path(path);
		}
		return this;
	}

	public UrlBuilder parameter(String name, String value) {
		if (value != null) {
			appendParameterDelimiter();
			urlStringBuilder.append(name).append(PARAMETER_NAME_VALUE_DELIMITER).append(urlEncode(value));
		}
		return this;
	}

	private void appendParameterDelimiter() {
		if (!parametersAdded) {
			urlStringBuilder.append(PARAMETER_URL_DELIMITER);
			parametersAdded = true;
		} else {
			urlStringBuilder.append(PARAMETER_DELIMITER);
		}
	}

	public UrlBuilder parameters(String... parameters) {
		for (String parameter : parameters) {
			parameter(parameter);
		}
		return this;
	}

	public UrlBuilder parameter(String parameter) {
		appendParameterDelimiter();
		urlStringBuilder.append(urlEncode(parameter));
		return this;
	}

	private String urlEncode(String value) {
		try {
			return URLEncoder.encode(value, URL_ENCODING);
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
	}
	
	public URL toUrl() throws MalformedURLException {
		return new URL(urlStringBuilder.toString());
	}

	public String toString() {
		return urlStringBuilder.toString();
	}
}
