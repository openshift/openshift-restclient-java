/*******************************************************************************
 * Copyright (c) 2015 Red Hat, Inc. Distributed under license by Red Hat, Inc.
 * All rights reserved. This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Red Hat, Inc.
 ******************************************************************************/
package com.openshift3.internal.client.model;

import java.util.Map;

import org.jboss.dmr.ModelNode;

import com.openshift3.client.IClient;
import com.openshift3.client.model.route.IRoute;
import com.openshift3.client.model.route.ITLSConfig;

public class Route extends KubernetesResource implements IRoute {
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
	}

	@Override
	public ITLSConfig getTLSConfig() {
		return new TLSConfig();
	}

	private class TLSConfig implements ITLSConfig {

		@Override
		public TLSTerminationType getTerminationType() {
			return TLSTerminationType
					.valueOf(asString(ROUTE_TLS_TERMINATION_TYPE));
		}

		@Override
		public void setTerminationType(TLSTerminationType type) {
			get(ROUTE_TLS_TERMINATION_TYPE).set(type.toString());
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
