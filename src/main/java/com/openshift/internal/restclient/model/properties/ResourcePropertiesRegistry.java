/*******************************************************************************
 * Copyright (c) 2015 Red Hat, Inc. Distributed under license by Red Hat, Inc.
 * All rights reserved. This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Red Hat, Inc.
 ******************************************************************************/
package com.openshift.internal.restclient.model.properties;

import static com.openshift.internal.restclient.model.properties.KubernetesApiModelProperties.V1BETA3_KUBERNETES_MAP;
import static com.openshift.internal.restclient.model.properties.KubernetesApiModelProperties.V1_KUBERNETES_MAP;
import static com.openshift.internal.restclient.model.properties.OpenShiftApiModelProperties.V1BETA3_OPENSHIFT_MAP;
import static com.openshift.internal.restclient.model.properties.OpenShiftApiModelProperties.V1_OPENSHIFT_MAP;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.openshift.internal.restclient.APIModelVersion;
import com.openshift.internal.restclient.KubernetesAPIVersion;
import com.openshift.internal.restclient.OpenShiftAPIVersion;
import com.openshift.restclient.IncompatibleApiVersionsException;
import com.openshift.restclient.ResourceKind;
import com.openshift.restclient.UnsupportedVersionException;

/**
 * Registry of keys to property paths by version for each API resource type 
 * 
 * @author Jeff Cantrill
 */
public class ResourcePropertiesRegistry implements ResourcePropertyKeys {

	private static ResourcePropertiesRegistry instance;
	
	private final Map<VersionKey, Map<String, String []>> versionPropertyMap = new HashMap<VersionKey, Map<String, String []>>();
	
	@SuppressWarnings("serial")
	static final Map<String, String []> UNREGISTERED_MAP = new HashMap<String, String []>(){{
		//common properties
		put(ANNOTATIONS, new String [] {"metadata", "annotations"});
		put(APIVERSION, new String [] {"apiVersion"});
		put(CREATION_TIMESTAMP, new String []  {"metadata", "creationTimestamp"});
		put(KIND, new String[]{"kind"});
		put(LABELS, new String []  { "metadata","labels"});
		put(NAME , new String []  {"metadata", "name"});
		put(NAMESPACE, new String []  {"metadata", "namespace"});
	}};

	@SuppressWarnings("deprecation")
	private ResourcePropertiesRegistry(){
		//v1beta3
		versionPropertyMap.put(new VersionKey(KubernetesAPIVersion.v1beta3, ResourceKind.EVENT), V1BETA3_KUBERNETES_MAP);
		versionPropertyMap.put(new VersionKey(KubernetesAPIVersion.v1beta3, ResourceKind.LIMIT_RANGE), V1BETA3_KUBERNETES_MAP);
		versionPropertyMap.put(new VersionKey(KubernetesAPIVersion.v1beta3, ResourceKind.POD), V1BETA3_KUBERNETES_MAP);
		versionPropertyMap.put(new VersionKey(KubernetesAPIVersion.v1beta3, ResourceKind.PVC), V1BETA3_KUBERNETES_MAP);
		versionPropertyMap.put(new VersionKey(KubernetesAPIVersion.v1beta3, ResourceKind.REPLICATION_CONTROLLER), V1BETA3_KUBERNETES_MAP);
		versionPropertyMap.put(new VersionKey(KubernetesAPIVersion.v1beta3, ResourceKind.RESOURCE_QUOTA), V1BETA3_KUBERNETES_MAP);
		versionPropertyMap.put(new VersionKey(KubernetesAPIVersion.v1beta3, ResourceKind.SERVICE), V1BETA3_KUBERNETES_MAP);
		versionPropertyMap.put(new VersionKey(KubernetesAPIVersion.v1beta3, ResourceKind.STATUS), V1BETA3_KUBERNETES_MAP);

		versionPropertyMap.put(new VersionKey(OpenShiftAPIVersion.v1beta3, ResourceKind.BUILD), V1BETA3_OPENSHIFT_MAP);
		versionPropertyMap.put(new VersionKey(OpenShiftAPIVersion.v1beta3, ResourceKind.BUILD_CONFIG), V1BETA3_OPENSHIFT_MAP);
		versionPropertyMap.put(new VersionKey(OpenShiftAPIVersion.v1beta3, ResourceKind.BUILD_REQUEST), V1BETA3_OPENSHIFT_MAP);
		versionPropertyMap.put(new VersionKey(OpenShiftAPIVersion.v1beta3, ResourceKind.LIST), V1BETA3_OPENSHIFT_MAP);
		versionPropertyMap.put(new VersionKey(OpenShiftAPIVersion.v1beta3, ResourceKind.DEPLOYMENT_CONFIG), V1BETA3_OPENSHIFT_MAP);
		versionPropertyMap.put(new VersionKey(OpenShiftAPIVersion.v1beta3, ResourceKind.IMAGE_STREAM), V1BETA3_OPENSHIFT_MAP);
		versionPropertyMap.put(new VersionKey(OpenShiftAPIVersion.v1beta3, ResourceKind.OAUTH_ACCESS_TOKEN), V1BETA3_OPENSHIFT_MAP);
		versionPropertyMap.put(new VersionKey(OpenShiftAPIVersion.v1beta3, ResourceKind.OAUTH_AUTHORIZE_TOKEN), V1BETA3_OPENSHIFT_MAP);
		versionPropertyMap.put(new VersionKey(OpenShiftAPIVersion.v1beta3, ResourceKind.OAUTH_CLIENT), V1BETA3_OPENSHIFT_MAP);
		versionPropertyMap.put(new VersionKey(OpenShiftAPIVersion.v1beta3, ResourceKind.OAUTH_CLIENT_AUTHORIZATION), V1BETA3_OPENSHIFT_MAP);
		versionPropertyMap.put(new VersionKey(OpenShiftAPIVersion.v1beta3, ResourceKind.POLICY), V1BETA3_OPENSHIFT_MAP);
		versionPropertyMap.put(new VersionKey(OpenShiftAPIVersion.v1beta3, ResourceKind.POLICY_BINDING), V1BETA3_OPENSHIFT_MAP);
		versionPropertyMap.put(new VersionKey(OpenShiftAPIVersion.v1beta3, ResourceKind.PROJECT), V1BETA3_OPENSHIFT_MAP);
		versionPropertyMap.put(new VersionKey(OpenShiftAPIVersion.v1beta3, ResourceKind.PROJECT_REQUEST), V1BETA3_OPENSHIFT_MAP);
		versionPropertyMap.put(new VersionKey(OpenShiftAPIVersion.v1beta3, ResourceKind.ROLE), V1BETA3_OPENSHIFT_MAP);
		versionPropertyMap.put(new VersionKey(OpenShiftAPIVersion.v1beta3, ResourceKind.ROLE_BINDING), V1BETA3_OPENSHIFT_MAP);
		versionPropertyMap.put(new VersionKey(OpenShiftAPIVersion.v1beta3, ResourceKind.ROUTE), V1BETA3_OPENSHIFT_MAP);
		versionPropertyMap.put(new VersionKey(OpenShiftAPIVersion.v1beta3, ResourceKind.TEMPLATE), V1BETA3_OPENSHIFT_MAP);
		versionPropertyMap.put(new VersionKey(OpenShiftAPIVersion.v1beta3, ResourceKind.USER), V1BETA3_OPENSHIFT_MAP);
		versionPropertyMap.put(new VersionKey(OpenShiftAPIVersion.v1beta3, ResourceKind.SECRET), V1BETA3_OPENSHIFT_MAP);

		//v1
		versionPropertyMap.put(new VersionKey(KubernetesAPIVersion.v1, ResourceKind.EVENT), V1_KUBERNETES_MAP);
		versionPropertyMap.put(new VersionKey(KubernetesAPIVersion.v1, ResourceKind.LIMIT_RANGE), V1_KUBERNETES_MAP);
		versionPropertyMap.put(new VersionKey(KubernetesAPIVersion.v1, ResourceKind.POD), V1_KUBERNETES_MAP);
		versionPropertyMap.put(new VersionKey(KubernetesAPIVersion.v1, ResourceKind.PVC), V1_KUBERNETES_MAP);
		versionPropertyMap.put(new VersionKey(KubernetesAPIVersion.v1, ResourceKind.REPLICATION_CONTROLLER), V1_KUBERNETES_MAP);
		versionPropertyMap.put(new VersionKey(KubernetesAPIVersion.v1, ResourceKind.RESOURCE_QUOTA), V1_KUBERNETES_MAP);
		versionPropertyMap.put(new VersionKey(KubernetesAPIVersion.v1, ResourceKind.SERVICE), V1_KUBERNETES_MAP);
		versionPropertyMap.put(new VersionKey(KubernetesAPIVersion.v1, ResourceKind.STATUS), V1_KUBERNETES_MAP);
		
		versionPropertyMap.put(new VersionKey(OpenShiftAPIVersion.v1, ResourceKind.BUILD), V1_OPENSHIFT_MAP);
		versionPropertyMap.put(new VersionKey(OpenShiftAPIVersion.v1, ResourceKind.BUILD_CONFIG), V1_OPENSHIFT_MAP);
		versionPropertyMap.put(new VersionKey(OpenShiftAPIVersion.v1, ResourceKind.BUILD_REQUEST), V1_OPENSHIFT_MAP);
		versionPropertyMap.put(new VersionKey(OpenShiftAPIVersion.v1, ResourceKind.LIST), V1_OPENSHIFT_MAP);
		versionPropertyMap.put(new VersionKey(OpenShiftAPIVersion.v1, ResourceKind.DEPLOYMENT_CONFIG), V1_OPENSHIFT_MAP);
		versionPropertyMap.put(new VersionKey(OpenShiftAPIVersion.v1, ResourceKind.IMAGE_STREAM), V1_OPENSHIFT_MAP);
		versionPropertyMap.put(new VersionKey(OpenShiftAPIVersion.v1, ResourceKind.OAUTH_ACCESS_TOKEN), V1_OPENSHIFT_MAP);
		versionPropertyMap.put(new VersionKey(OpenShiftAPIVersion.v1, ResourceKind.OAUTH_AUTHORIZE_TOKEN), V1_OPENSHIFT_MAP);
		versionPropertyMap.put(new VersionKey(OpenShiftAPIVersion.v1, ResourceKind.OAUTH_CLIENT), V1_OPENSHIFT_MAP);
		versionPropertyMap.put(new VersionKey(OpenShiftAPIVersion.v1, ResourceKind.OAUTH_CLIENT_AUTHORIZATION), V1_OPENSHIFT_MAP);
		versionPropertyMap.put(new VersionKey(OpenShiftAPIVersion.v1, ResourceKind.POLICY), V1_OPENSHIFT_MAP);
		versionPropertyMap.put(new VersionKey(OpenShiftAPIVersion.v1, ResourceKind.POLICY_BINDING), V1_OPENSHIFT_MAP);
		versionPropertyMap.put(new VersionKey(OpenShiftAPIVersion.v1, ResourceKind.PROJECT), V1_OPENSHIFT_MAP);
		versionPropertyMap.put(new VersionKey(OpenShiftAPIVersion.v1, ResourceKind.PROJECT_REQUEST), V1_OPENSHIFT_MAP);
		versionPropertyMap.put(new VersionKey(OpenShiftAPIVersion.v1, ResourceKind.ROLE), V1_OPENSHIFT_MAP);
		versionPropertyMap.put(new VersionKey(OpenShiftAPIVersion.v1, ResourceKind.ROLE_BINDING), V1_OPENSHIFT_MAP);
		versionPropertyMap.put(new VersionKey(OpenShiftAPIVersion.v1, ResourceKind.ROUTE), V1_OPENSHIFT_MAP);
		versionPropertyMap.put(new VersionKey(OpenShiftAPIVersion.v1, ResourceKind.TEMPLATE), V1_OPENSHIFT_MAP);
		versionPropertyMap.put(new VersionKey(OpenShiftAPIVersion.v1, ResourceKind.USER), V1_OPENSHIFT_MAP);
		versionPropertyMap.put(new VersionKey(OpenShiftAPIVersion.v1, ResourceKind.SECRET), V1_OPENSHIFT_MAP);
	}
	
	public static final ResourcePropertiesRegistry getInstance(){
		if(instance == null){
			instance = new ResourcePropertiesRegistry();
		}
		return instance;
	}
	
	public Map<String, String []> get(final String apiVersion, final String kind) {
		return get(apiVersion, kind, false);
	}
	/**
	 * Retrieve a given resource property map for a given version
	 * @param apiVersion
	 * @param kind
	 * @param strict    true if it should throw an error when properties are not found; false otherwise
	 * 
	 * @return The set of paths for the properties of the resource
	 */
	public Map<String, String []> get(final String apiVersion, final String kind, boolean strict) {
		final VersionKey key = new VersionKey(apiVersion, kind);
		if(!versionPropertyMap.containsKey(key)){
			if(!strict) return UNREGISTERED_MAP;
			Collection<String> versions = new ArrayList<String>();
			for (VersionKey version : versionPropertyMap.keySet()) {
				if(version.kind.equals(kind)) {
					versions.add(version.version);
				}
			}
			String kindVersions = StringUtils.join(versions, ",");
			throw new UnsupportedVersionException("Version '%s' not supported for kind '%s'. Supported Versions are: %s", apiVersion, kind, kindVersions);
		}
		return versionPropertyMap.get(key);
	}
	
	public KubernetesAPIVersion [] getSupportedKubernetesVersions(){
		return KubernetesAPIVersion.values();
	}

	public OpenShiftAPIVersion[] getSupportedOpenShiftVersions(){
		return OpenShiftAPIVersion.values();
	}
	
	/**
	 * The maximum Kubernetes API supported by this client
	 * @return
	 * @throws IncompatibleApiVersionsException if the client can not support the server
	 */
	public KubernetesAPIVersion getMaxSupportedKubernetesVersion(List<KubernetesAPIVersion> serverVersions) {
		return getMaxSupportedVersion(Arrays.asList(getSupportedKubernetesVersions()), serverVersions);
	}
	
	/**
	 * The maximum OpenShift API supported by this client
	 * @return
	 * @throws IncompatibleApiVersionsException if the client can not support the server
	 */
	public OpenShiftAPIVersion getMaxSupportedOpenShiftVersion(List<OpenShiftAPIVersion> serverVersions){
		return getMaxSupportedVersion(Arrays.asList(getSupportedOpenShiftVersions()), serverVersions);
	}
	
	private <T extends APIModelVersion> T getMaxSupportedVersion(List<T> clientVersions, List<T> serverVersions) {
		Collections.sort(clientVersions, new APIModelVersion.VersionComparitor());
		Collections.sort(serverVersions, new APIModelVersion.VersionComparitor());
		T maxClientVersion = clientVersions.get(clientVersions.size() - 1);
		T maxServerVersion = serverVersions.get(serverVersions.size() - 1);
		if(serverVersions.contains(maxClientVersion)) {
			return maxClientVersion;
		}
		if(clientVersions.contains(maxServerVersion)) {
			return maxServerVersion;
		}
		throw new IncompatibleApiVersionsException(clientVersions.toString(), serverVersions.toString());
	}
	
	private static class VersionKey {
		private String version;
		private String kind;

		VersionKey(APIModelVersion version, String kind){
			this(version.toString(), kind);
		}

		VersionKey(String version, String kind){
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
