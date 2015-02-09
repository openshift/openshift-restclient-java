/*******************************************************************************
 * Copyright (c) 2015 Red Hat, Inc. Distributed under license by Red Hat, Inc.
 * All rights reserved. This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Red Hat, Inc.
 ******************************************************************************/
package com.openshift3.internal.client.model;

import java.util.HashMap;
import java.util.Map;

import com.openshift3.client.ResourceKind;
import com.openshift3.internal.client.APIModelVersion;
import com.openshift3.internal.client.KubernetesAPIVersion;
import com.openshift3.internal.client.OpenShiftAPIVersion;

/**
 * Registry of keys to property paths by version for each API resource type 
 */
@SuppressWarnings("serial")
public final class ResourcePropertiesRegistry implements ResourcePropertyKeys {

	private static ResourcePropertiesRegistry instance;

	private final Map<VersionKey, Map<String, String []>> versionPropertyMap = new HashMap<VersionKey, Map<String, String []>>();
	private static final Map<String, String []> V1BETA1_KUBERNETES_MAP = new HashMap<String, String []>(){{
		put(ANNOTATIONS, new String [] {"annotations"});
		put(APIVERSION, new String [] {"apiVersion"});
		put(CREATION_TIMESTAMP, new String []  {"creationTimestamp"});
		put(LABELS, new String []  {"labels"});
		put(NAME , new String []  {"id"});
		put(NAMESPACE, new String []  {"namespace"});
		
		put(REPLICATION_CONTROLLER_REPLICA_COUNT, new String [] {"desiredState", "replicas"});
		put(REPLICATION_CONTROLLER_REPLICA_SELECTOR, new String [] {"desiredState", "replicaSelector"});
		
		put(POD_IP, new String[]{"currentState","podIP"});
		put(SERVICE_CONTAINER_PORT, new String [] {"containerPort"});
		put(SERVICE_PORT, new String [] {"port"});
		put(SERVICE_SELECTOR, new String [] {"selector"});
		put(SERVICE_PORTALIP, new String [] {"portalIP"});
		put(STATUS_MESSAGE, new String [] {"message"});
	}};

	private static final Map<String, String []> V1BETA1_OPENSHIFT_MAP = new HashMap<String, String []>(){{
		//common properties
		put(ANNOTATIONS, new String [] {"metadata", "annotations"});
		put(CREATION_TIMESTAMP, new String []  {"metadata", "creationTimestamp"});
		put(LABELS, new String []  {"metadata", "labels"});
		put(NAME , new String []  {"metadata", "name"});
		put(NAMESPACE, new String []  {"metadata", "namespace"});
		
		put(BUILD_MESSAGE, new String[]{"message"});
		put(BUILD_STATUS, new String[]{"status"});
		
		put(BUILDCONFIG_SOURCE_URI, new String[]{"parameters","source","git","uri"});
		
		put(DEPLOYMENTCONFIG_CONTAINERS, new String[]{"template","controllerTemplate","podTemplate","desiredState","manifest","containers"});
		put(DEPLOYMENTCONFIG_REPLICAS, new String[]{"template","controllerTemplate","replicas"});
		put(DEPLOYMENTCONFIG_REPLICA_SELECTOR, new String[]{"template","controllerTemplate","replicaSelector"});
		put(DEPLOYMENTCONFIG_TRIGGERS, new String[]{"triggers"});
		
		put(IMAGEREPO_DOCKER_IMAGE_REPO, new String[]{"status","dockerImageRepository"});
		
		put(PROJECT_DISPLAY_NAME, new String[]{"displayName"});
	}};

	private ResourcePropertiesRegistry(){
		versionPropertyMap.put(new VersionKey(KubernetesAPIVersion.v1beta1, ResourceKind.Pod), V1BETA1_KUBERNETES_MAP);
		versionPropertyMap.put(new VersionKey(KubernetesAPIVersion.v1beta1, ResourceKind.ReplicationController), V1BETA1_KUBERNETES_MAP);
		versionPropertyMap.put(new VersionKey(KubernetesAPIVersion.v1beta1, ResourceKind.Service), V1BETA1_KUBERNETES_MAP);
		versionPropertyMap.put(new VersionKey(KubernetesAPIVersion.v1beta1, ResourceKind.Status), V1BETA1_KUBERNETES_MAP);
		
		versionPropertyMap.put(new VersionKey(OpenShiftAPIVersion.v1beta1, ResourceKind.Build), V1BETA1_OPENSHIFT_MAP);
		versionPropertyMap.put(new VersionKey(OpenShiftAPIVersion.v1beta1, ResourceKind.BuildConfig), V1BETA1_OPENSHIFT_MAP);
		versionPropertyMap.put(new VersionKey(OpenShiftAPIVersion.v1beta1, ResourceKind.DeploymentConfig), V1BETA1_OPENSHIFT_MAP);
		versionPropertyMap.put(new VersionKey(OpenShiftAPIVersion.v1beta1, ResourceKind.ImageRepository), V1BETA1_OPENSHIFT_MAP);
		versionPropertyMap.put(new VersionKey(OpenShiftAPIVersion.v1beta1, ResourceKind.Project), V1BETA1_OPENSHIFT_MAP);
	}
	
	public static final ResourcePropertiesRegistry getInstance(){
		if(instance == null){
			instance = new ResourcePropertiesRegistry();
		}
		return instance;
	}
	
	/**
	 * Retrieve a given resource property map for a given version
	 */
	public Map<String, String []> get(final String apiVersion, final ResourceKind kind) {
		final VersionKey key = new VersionKey(apiVersion, kind);
		if(!versionPropertyMap.containsKey(key)){
			throw new RuntimeException(String.format("Version '%s' not supported for kind '%s'", apiVersion, kind));
		}
		return versionPropertyMap.get(key);
	}
	
	/**
	 * The maximum Kubernetes API supported by this client
	 * @return
	 */
	public KubernetesAPIVersion getMaxSupportedKubernetesVersion(){
		return KubernetesAPIVersion.v1beta1;
	}
	
	/**
	 * The maximum OpenShift API supported by this client
	 * @return
	 */
	public OpenShiftAPIVersion getMaxSupportedOpenShiftVersion(){
		return OpenShiftAPIVersion.v1beta1;
	}
	
	private static class VersionKey {
		private String version;
		private ResourceKind kind;

		VersionKey(APIModelVersion version, ResourceKind kind){
			this(version.toString(), kind);
		}

		VersionKey(String version, ResourceKind kind){
			this.version = version.toString();
			this.kind = kind;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result
					+ ((kind == null) ? 0 : kind.hashCode());
			result = prime * result
					+ ((version == null) ? 0 : version.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			VersionKey other = (VersionKey) obj;
			if (kind == null) {
				if (other.kind != null)
					return false;
			} else if (!kind.equals(other.kind))
				return false;
			if (version == null) {
				if (other.version != null)
					return false;
			} else if (!version.equals(other.version))
				return false;
			return true;
		}
		
	}

}
