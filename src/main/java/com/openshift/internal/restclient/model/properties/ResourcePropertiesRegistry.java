/*******************************************************************************
 * Copyright (c) 2015 Red Hat, Inc. Distributed under license by Red Hat, Inc.
 * All rights reserved. This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Red Hat, Inc.
 ******************************************************************************/
package com.openshift.internal.restclient.model.properties;

import static com.openshift.internal.restclient.model.properties.KubernetesApiModelProperties.V1BETA1_KUBERNETES_MAP;
import static com.openshift.internal.restclient.model.properties.KubernetesApiModelProperties.V1BETA3_KUBERNETES_MAP;
import static com.openshift.internal.restclient.model.properties.OpenShiftApiModelProperties.V1BETA1_OPENSHIFT_MAP;
import static com.openshift.internal.restclient.model.properties.OpenShiftApiModelProperties.V1BETA3_OPENSHIFT_MAP;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.openshift.internal.restclient.APIModelVersion;
import com.openshift.internal.restclient.KubernetesAPIVersion;
import com.openshift.internal.restclient.OpenShiftAPIVersion;
import com.openshift.restclient.IncompatibleApiVersionsException;
import com.openshift.restclient.ResourceKind;

/**
 * Registry of keys to property paths by version for each API resource type 
 * 
 * @author Jeff Cantrill
 */
public class ResourcePropertiesRegistry implements ResourcePropertyKeys {

	private static ResourcePropertiesRegistry instance;
	
	private final Map<VersionKey, Map<String, String []>> versionPropertyMap = new HashMap<VersionKey, Map<String, String []>>();

	@SuppressWarnings("deprecation")
	private ResourcePropertiesRegistry(){
		//v1beta1
		versionPropertyMap.put(new VersionKey(KubernetesAPIVersion.v1beta1, ResourceKind.Pod), V1BETA1_KUBERNETES_MAP);
		versionPropertyMap.put(new VersionKey(KubernetesAPIVersion.v1beta1, ResourceKind.ReplicationController), V1BETA1_KUBERNETES_MAP);
		versionPropertyMap.put(new VersionKey(KubernetesAPIVersion.v1beta1, ResourceKind.Service), V1BETA1_KUBERNETES_MAP);
		versionPropertyMap.put(new VersionKey(KubernetesAPIVersion.v1beta1, ResourceKind.Status), V1BETA1_KUBERNETES_MAP);

		
		versionPropertyMap.put(new VersionKey(OpenShiftAPIVersion.v1beta1, ResourceKind.Build), V1BETA1_OPENSHIFT_MAP);
		versionPropertyMap.put(new VersionKey(OpenShiftAPIVersion.v1beta1, ResourceKind.BuildConfig), V1BETA1_OPENSHIFT_MAP);
		versionPropertyMap.put(new VersionKey(OpenShiftAPIVersion.v1beta1, ResourceKind.Config), V1BETA1_OPENSHIFT_MAP);
		versionPropertyMap.put(new VersionKey(OpenShiftAPIVersion.v1beta1, ResourceKind.DeploymentConfig), V1BETA1_OPENSHIFT_MAP);
		versionPropertyMap.put(new VersionKey(OpenShiftAPIVersion.v1beta1, ResourceKind.ImageStream), V1BETA1_OPENSHIFT_MAP);
		versionPropertyMap.put(new VersionKey(OpenShiftAPIVersion.v1beta1, ResourceKind.Project), V1BETA1_OPENSHIFT_MAP);
		versionPropertyMap.put(new VersionKey(OpenShiftAPIVersion.v1beta1, ResourceKind.Route), V1BETA1_OPENSHIFT_MAP);
		versionPropertyMap.put(new VersionKey(OpenShiftAPIVersion.v1beta1, ResourceKind.Template), V1BETA1_OPENSHIFT_MAP);
		
		//v1beta3
		versionPropertyMap.put(new VersionKey(KubernetesAPIVersion.v1beta3, ResourceKind.Pod), V1BETA3_KUBERNETES_MAP);
		versionPropertyMap.put(new VersionKey(KubernetesAPIVersion.v1beta3, ResourceKind.ReplicationController), V1BETA3_KUBERNETES_MAP);
		versionPropertyMap.put(new VersionKey(KubernetesAPIVersion.v1beta3, ResourceKind.Service), V1BETA3_KUBERNETES_MAP);
		versionPropertyMap.put(new VersionKey(KubernetesAPIVersion.v1beta3, ResourceKind.Status), V1BETA3_KUBERNETES_MAP);

		versionPropertyMap.put(new VersionKey(OpenShiftAPIVersion.v1beta3, ResourceKind.Build), V1BETA3_OPENSHIFT_MAP);
		versionPropertyMap.put(new VersionKey(OpenShiftAPIVersion.v1beta3, ResourceKind.BuildConfig), V1BETA3_OPENSHIFT_MAP);
		versionPropertyMap.put(new VersionKey(OpenShiftAPIVersion.v1beta3, ResourceKind.List), V1BETA3_OPENSHIFT_MAP);
		versionPropertyMap.put(new VersionKey(OpenShiftAPIVersion.v1beta3, ResourceKind.DeploymentConfig), V1BETA3_OPENSHIFT_MAP);
		versionPropertyMap.put(new VersionKey(OpenShiftAPIVersion.v1beta3, ResourceKind.ImageStream), V1BETA3_OPENSHIFT_MAP);
		versionPropertyMap.put(new VersionKey(OpenShiftAPIVersion.v1beta3, ResourceKind.Project), V1BETA3_OPENSHIFT_MAP);
		versionPropertyMap.put(new VersionKey(OpenShiftAPIVersion.v1beta3, ResourceKind.Route), V1BETA3_OPENSHIFT_MAP);
		versionPropertyMap.put(new VersionKey(OpenShiftAPIVersion.v1beta3, ResourceKind.Template), V1BETA3_OPENSHIFT_MAP);
		
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
