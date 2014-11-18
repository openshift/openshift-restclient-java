package com.openshift.kube.capability;

public interface Capability {
	
	/**
	 * Allow the implementation of the capability to determine
	 * if it is supported on the OpenShift server.  Implementations
	 * should return false if they can not
	 * 
	 * @return true if the capability exists
	 */
	boolean exists();
	
	/**
	 * Well known name of the capability
	 * @return
	 */
	String getName();
}
