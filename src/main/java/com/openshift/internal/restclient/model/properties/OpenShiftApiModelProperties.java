/*******************************************************************************
 * Copyright (c) 2015 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package com.openshift.internal.restclient.model.properties;

import java.util.HashMap;
import java.util.Map;

/**
 * @author jeff.cantrill
 */
public interface OpenShiftApiModelProperties extends ResourcePropertyKeys{
	
	@SuppressWarnings("serial")
	static final Map<String, String []> V1BETA3_OPENSHIFT_MAP = new HashMap<String, String []>(){{
		//common properties
		put(ANNOTATIONS, new String [] {"metadata", "annotations"});
		put(APIVERSION, new String [] {"apiVersion"});
		put(CREATION_TIMESTAMP, new String []  {"metadata", "creationTimestamp"});
		put(DESCRIPTION, new String []  { "description"});
		put(IMAGE, new String []  { "image"});
		put(PORTS, new String []  { "ports"});
		put(ENV, new String []  { "env"});
		put(CONTAINER_PORT, new String []  { "containerPort"});
		put(PROTOCOL, new String []  { "protocol"});
		put(VALUE, new String []  { "value"});
		put(DISPLAYNAME, new String []  { "displayName"});
		put(LABELS, new String []  { "metadata","labels"});
		put(LABELS, new String []  { "metadata","labels"});
		put(NAME , new String []  {"metadata", "name"});
		put(NAMESPACE, new String []  {"metadata", "namespace"});
		
		put(BUILD_MESSAGE, new String[]{"status","message"});
		put(BUILD_PODNAME, new String[]{"podName"});
		put(BUILD_STATUS, new String[]{"status","phase"});
		
		put(BUILDCONFIG_SOURCE_CONTEXTDIR, new String[]{"spec","source","contextDir"});
		put(BUILDCONFIG_SOURCE_TYPE, new String[]{"spec","source","type"});
		put(BUILDCONFIG_SOURCE_URI, new String[]{"spec","source","git","uri"});
		put(BUILDCONFIG_SOURCE_REF, new String[]{"spec","source","git","ref"});
		put(BUILDCONFIG_STRATEGY, new String[]{"spec","strategy"});
		put(BUILDCONFIG_TYPE, new String[]{"spec","strategy", "type"});
		put(BUILDCONFIG_CUSTOM_IMAGE, new String[]{"spec","strategy", "customStrategy", "image"});
		put(BUILDCONFIG_CUSTOM_EXPOSEDOCKERSOCKET, new String[]{"spec","strategy", "customStrategy", "exposeDockerSocket"});
		put(BUILDCONFIG_CUSTOM_ENV, new String[]{"spec","strategy", "customStrategy", "env"});
		put(BUILDCONFIG_DOCKER_CONTEXTDIR, new String[]{"spec","strategy", "dockerStrategy", "contextDir"});
		put(BUILDCONFIG_DOCKER_NOCACHE, new String[]{"spec","strategy", "dockerStrategy", "noCache"});
		put(BUILDCONFIG_DOCKER_BASEIMAGE, new String[]{"spec","strategy", "dockerStrategy","baseImage"});
		put(BUILDCONFIG_OUTPUT_REPO, new String[]{"spec","output", "to","name"});
		put(BUILDCONFIG_STI_IMAGE, new String[]{"spec","strategy", "sourceStrategy", "from","name"});
		put(BUILDCONFIG_STI_SCRIPTS, new String[]{"spec","strategy", "sourceStrategy", "scripts"});
		put(BUILDCONFIG_STI_INCREMENTAL, new String[]{"spec","strategy", "sourceStrategy", "incremental"});
		put(BUILDCONFIG_STI_ENV, new String[]{"spec","strategy", "sourceStrategy", "env"});
		put(BUILDCONFIG_TRIGGERS, new String[]{"spec","triggers"});
		put(BUILD_CONFIG_WEBHOOK_GITHUB_SECRET, new String[]{"github","secret"});
		put(BUILD_CONFIG_WEBHOOK_GENERIC_SECRET, new String[]{"generic","secret"});
		put(BUILD_CONFIG_IMAGECHANGE_IMAGE, new String[]{"imageChange","image"});
		put(BUILD_CONFIG_IMAGECHANGE_NAME, new String[]{"imageChange", "from","name"});
		put(BUILD_CONFIG_IMAGECHANGE_TAG, new String[]{"imageChange","tag"});
		
		put(DEPLOYMENTCONFIG_CONTAINERS, new String[]{"spec","template","spec","containers"});
		put(DEPLOYMENTCONFIG_VOLUMES, new String[]{"spec","template","spec","volumes"});
		put(DEPLOYMENTCONFIG_REPLICAS, new String[]{"spec","replicas"});
		put(DEPLOYMENTCONFIG_REPLICA_SELECTOR, new String[]{"spec","selector"});
		put(DEPLOYMENTCONFIG_TEMPLATE_LABELS, new String[]{"spec","template","metadata","labels"});
		put(DEPLOYMENTCONFIG_TRIGGERS, new String[]{"spec","triggers"});
		put(DEPLOYMENTCONFIG_STRATEGY, new String[]{"spec","strategy","type"});
		put(DEPLOYMENTCONFIG_TRIGGER_IMAGECHANGE_AUTO, new String[]{"imageChangeParams","automatic"});
		put(DEPLOYMENTCONFIG_TRIGGER_CONTAINERS, new String[]{"imageChangeParams","containerNames"});
		put(DEPLOYMENTCONFIG_TRIGGER_FROM, new String[]{"imageChangeParams","from","name"});
		put(DEPLOYMENTCONFIG_TRIGGER_FROM_KIND, new String[]{"imageChangeParams","from","kind"});

		put(KIND, new String[]{"kind"});
		
		put(IMAGESTREAM_DOCKER_IMAGE_REPO, new String[]{"spec","dockerImageRepository"});
		
		put(PROJECT_DISPLAY_NAME, new String[]{"metadata","annotations","displayName"});
		
		put(ROUTE_HOST, new String[] {"spec", "host" });
		put(ROUTE_PATH, new String[] { "spec", "path" });
		put(ROUTE_KIND, new String[] {"spec", "to", "kind" });
		put(ROUTE_SERVICE_NAME, new String[] {"spec", "to", "name" });
		put(ROUTE_TLS, new String[]{ "spec", "tls" });
		put(ROUTE_TLS_TERMINATION_TYPE, new String[]{"spec",  "tls", "termination" });
		put(ROUTE_TLS_CERTIFICATE, new String[]{"spec",  "tls", "certificate" });
		put(ROUTE_TLS_KEY, new String[]{ "spec", "tls", "key" });
		put(ROUTE_TLS_CACERT, new String[]{"spec", "tls","caCertificate"});
		put(ROUTE_TLS_DESTINATION_CACERT, new String[]{"spec", "tls","destinationCACertificate"});
		
		put(TEMPLATE_PARAMETERS, new String[]{"parameters"});
		put(TEMPLATE_ITEMS, new String[]{"objects"});
		put(TEMPLATE_OBJECT_LABELS, new String[]{"labels"});

		put(TYPE, new String[]{"type"});
		
		put(USER_FULLNAME, new String[]{"fullName"});
		put(SECRET_TYPE, new String[]{"type"});
		put(SECRET_DATA, new String[]{"data"});
	}};
	
	@SuppressWarnings("serial")
	static final Map<String, String []> V1_OPENSHIFT_MAP = new HashMap<String, String []>(){{
		//common properties
		put(ANNOTATIONS, new String [] {"metadata", "annotations"});
		put(APIVERSION, new String [] {"apiVersion"});
		put(CREATION_TIMESTAMP, new String []  {"metadata", "creationTimestamp"});
		put(DESCRIPTION, new String []  { "description"});
		put(IMAGE, new String []  { "image"});
		put(PORTS, new String []  { "ports"});
		put(ENV, new String []  { "env"});
		put(CONTAINER_PORT, new String []  { "containerPort"});
		put(PROTOCOL, new String []  { "protocol"});
		put(VALUE, new String []  { "value"});
		put(DISPLAYNAME, new String []  { "displayName"});
		put(LABELS, new String []  { "metadata","labels"});
		put(NAME , new String []  {"metadata", "name"});
		put(NAMESPACE, new String []  {"metadata", "namespace"});
		
		put(BUILD_MESSAGE, new String[]{"status","message"});
		put(BUILD_PODNAME, new String[]{"podName"});
		put(BUILD_STATUS, new String[]{"status","phase"});

		put(BUILDCONFIG_SOURCE_CONTEXTDIR, new String[]{"spec","source","contextDir"});
		put(BUILDCONFIG_SOURCE_TYPE, new String[]{"spec","source","type"});
		put(BUILDCONFIG_SOURCE_URI, new String[]{"spec","source","git","uri"});
		put(BUILDCONFIG_SOURCE_REF, new String[]{"spec","source","git","ref"});
		put(BUILDCONFIG_STRATEGY, new String[]{"spec","strategy"});
		put(BUILDCONFIG_TYPE, new String[]{"spec","strategy", "type"});
		put(BUILDCONFIG_CUSTOM_IMAGE, new String[]{"spec","strategy", "customStrategy", "image"});
		put(BUILDCONFIG_CUSTOM_EXPOSEDOCKERSOCKET, new String[]{"spec","strategy", "customStrategy", "exposeDockerSocket"});
		put(BUILDCONFIG_CUSTOM_ENV, new String[]{"spec","strategy", "customStrategy", "env"});
		put(BUILDCONFIG_DOCKER_CONTEXTDIR, new String[]{"spec","strategy", "dockerStrategy", "contextDir"});
		put(BUILDCONFIG_DOCKER_NOCACHE, new String[]{"spec","strategy", "dockerStrategy", "noCache"});
		put(BUILDCONFIG_DOCKER_BASEIMAGE, new String[]{"spec","strategy", "dockerStrategy","baseImage"});
		put(BUILDCONFIG_OUTPUT_REPO, new String[]{"spec","output", "to","name"});
		put(BUILDCONFIG_STI_IMAGE, new String[]{"spec","strategy", "sourceStrategy", "from","name"});
		put(BUILDCONFIG_STI_SCRIPTS, new String[]{"spec","strategy", "sourceStrategy", "scripts"});
		put(BUILDCONFIG_STI_INCREMENTAL, new String[]{"spec","strategy", "sourceStrategy", "incremental"});
		put(BUILDCONFIG_STI_ENV, new String[]{"spec","strategy", "sourceStrategy", "env"});
		put(BUILDCONFIG_TRIGGERS, new String[]{"spec","triggers"});
		put(BUILD_CONFIG_WEBHOOK_GITHUB_SECRET, new String[]{"github","secret"});
		put(BUILD_CONFIG_WEBHOOK_GENERIC_SECRET, new String[]{"generic","secret"});
		put(BUILD_CONFIG_IMAGECHANGE_IMAGE, new String[]{"imageChange","image"});
		put(BUILD_CONFIG_IMAGECHANGE_NAME, new String[]{"imageChange", "from","name"});
		put(BUILD_CONFIG_IMAGECHANGE_TAG, new String[]{"imageChange","tag"});
		
		put(DEPLOYMENTCONFIG_CONTAINERS, new String[]{"spec","template","spec","containers"});
		put(DEPLOYMENTCONFIG_VOLUMES, new String[]{"spec","template","spec","volumes"});
		put(DEPLOYMENTCONFIG_REPLICAS, new String[]{"spec","replicas"});
		put(DEPLOYMENTCONFIG_REPLICA_SELECTOR, new String[]{"spec","selector"});
		put(DEPLOYMENTCONFIG_TEMPLATE_LABELS, new String[]{"spec","template","metadata","labels"});
		put(DEPLOYMENTCONFIG_TRIGGERS, new String[]{"spec","triggers"});
		put(DEPLOYMENTCONFIG_STRATEGY, new String[]{"spec","strategy","type"});
		put(DEPLOYMENTCONFIG_TRIGGER_IMAGECHANGE_AUTO, new String[]{"imageChangeParams","automatic"});
		put(DEPLOYMENTCONFIG_TRIGGER_CONTAINERS, new String[]{"imageChangeParams","containerNames"});
		put(DEPLOYMENTCONFIG_TRIGGER_FROM, new String[]{"imageChangeParams","from","name"});
		put(DEPLOYMENTCONFIG_TRIGGER_FROM_KIND, new String[]{"imageChangeParams","from","kind"});
		
		put(KIND, new String[]{"kind"});
		
		put(IMAGESTREAM_DOCKER_IMAGE_REPO, new String[]{"spec","dockerImageRepository"});
		
		put(PROJECT_DISPLAY_NAME, new String[]{"displayName"});
		
		put(ROUTE_HOST, new String[] {"spec", "host" });
		put(ROUTE_PATH, new String[] { "spec", "path" });
		put(ROUTE_KIND, new String[] {"spec", "to", "kind" });
		put(ROUTE_SERVICE_NAME, new String[] {"spec", "to", "name" });
		put(ROUTE_TLS, new String[]{ "spec", "tls" });
		put(ROUTE_TLS_TERMINATION_TYPE, new String[]{"spec",  "tls", "termination" });
		put(ROUTE_TLS_CERTIFICATE, new String[]{"spec",  "tls", "certificate" });
		put(ROUTE_TLS_KEY, new String[]{ "spec", "tls", "key" });
		put(ROUTE_TLS_CACERT, new String[]{"spec", "tls","caCertificate"});
		put(ROUTE_TLS_DESTINATION_CACERT, new String[]{"spec", "tls","destinationCACertificate"});
		
		put(TEMPLATE_PARAMETERS, new String[]{"parameters"});
		put(TEMPLATE_ITEMS, new String[]{"objects"});
		put(TEMPLATE_OBJECT_LABELS, new String[]{"labels"});
		
		put(TYPE, new String[]{"type"});
		
		put(USER_FULLNAME, new String[]{"fullName"});
		put(SECRET_TYPE, new String[]{"type"});
		put(SECRET_DATA, new String[]{"data"});
	}};
}
