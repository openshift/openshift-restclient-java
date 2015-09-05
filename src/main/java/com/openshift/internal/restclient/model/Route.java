/*******************************************************************************
 * Copyright (c) 2015 Red Hat, Inc. Distributed under license by Red Hat, Inc.
 * All rights reserved. This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Red Hat, Inc.
 ******************************************************************************/
package com.openshift.internal.restclient.model;

import java.util.Map;

import org.jboss.dmr.ModelNode;

import com.openshift.restclient.IClient;
import com.openshift.restclient.ResourceKind;
import com.openshift.restclient.model.route.IRoute;
import com.openshift.restclient.model.route.ITLSConfig;

/**
 * @author Jeff Cantrill
 */
public class Route extends KubernetesResource implements IRoute {
	
	private static final String ROUTE_HOST = "spec.host";
	private static final String ROUTE_PATH = "spec.path";
	private static final String ROUTE_KIND = "spec.to.kind";
	private static final String ROUTE_SERVICE_NAME = "spec.to.name";
	private static final String ROUTE_TLS = "spec.tls";
	private static final String ROUTE_TLS_TERMINATION_TYPE = "spec.tls.termination";
	private static final String ROUTE_TLS_CERTIFICATE = "spec.tls.certificate";
	private static final String ROUTE_TLS_KEY = "spec.tls.key";
	private static final String ROUTE_TLS_CACERT = "spec.tls.caCertificate";
	private static final String ROUTE_TLS_DESTINATION_CACERT = "spec.tls.destinationCACertificate";

	public Route(ModelNode node, IClient client,
			Map<String, String[]> propertyKeys) {
		super(node, client, propertyKeys);
	}

	@Override
	public String getHost() {
		return asString(ROUTE_HOST);
	}

	@Override
	public void setHost(String host) {
		get(ROUTE_HOST).set(host);
	}

	@Override
	public String getPath() {
		return asString(ROUTE_PATH);
	}

	@Override
	public void setPath(String path) {
		get(ROUTE_PATH).set(path);
	}

	@Override
	public String getServiceName() {
		return asString(ROUTE_SERVICE_NAME);
	}

	@Override
	public void setServiceName(String serviceName) {
		get(ROUTE_SERVICE_NAME).set(serviceName);
		get(ROUTE_KIND).set(ResourceKind.SERVICE);
	}

	@Override
	public ITLSConfig getTLSConfig() {
		if(get(ROUTE_TLS).isDefined()){
			return new TLSConfig();
		}
		return null;
	}
	
	@Override
	public String getURL() {
		String scheme = getTLSConfig() == null ? "http" : "https";
		String path = getPath();
		if(!path.startsWith("/")) {
			path = "/" + path;
		}
		return String.format("%s://%s%s", scheme, getHost(), path);
	}



	private class TLSConfig implements ITLSConfig {

		@Override
		public String getTerminationType() {
			return asString(ROUTE_TLS_TERMINATION_TYPE);
		}

		@Override
		public void setTerminationType(String type) {
			get(ROUTE_TLS_TERMINATION_TYPE).set(type);
		}

		@Override
		public String getCertificate() {
			return asString(ROUTE_TLS_CERTIFICATE);
		}

		@Override
		public void setCertificate(String certificate) {
			get(ROUTE_TLS_CERTIFICATE).set(certificate);
		}

		@Override
		public String getKey() {
			return asString(ROUTE_TLS_KEY);
		}

		@Override
		public void setKey(String key) {
			get(ROUTE_TLS_KEY).set(key);
		}

		@Override
		public String getCACertificate() {
			return asString(ROUTE_TLS_CACERT);
		}

		@Override
		public void setCACertificate(String caCertificate) {
			get(ROUTE_TLS_CACERT).set(caCertificate);
		}

		@Override
		public String getDestinationCertificate() {
			return asString(ROUTE_TLS_DESTINATION_CACERT);
		}

		@Override
		public void setDestinationCertificate(String destinationCertificate) {
			get(ROUTE_TLS_DESTINATION_CACERT).set(destinationCertificate);
		}

	}
}
