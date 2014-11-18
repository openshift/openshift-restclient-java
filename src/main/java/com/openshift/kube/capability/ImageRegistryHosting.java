package com.openshift.kube.capability;

/**
 * Identifies an OpenShift server as capable of
 * hosting images via its own registry
 */
public interface ImageRegistryHosting extends Capability{
	
	/**
	 * Get the Image Registry URI
	 * @return the registry URI (e.g. 172.121.17.212:5001)
	 */
	String getRegistryUri();
}
