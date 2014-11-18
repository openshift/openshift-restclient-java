package com.openshift.kube;

import java.util.List;

import com.openshift.internal.kube.Resource;
import com.openshift.kube.capability.Capability;

/**
 * Client is the the simplest interface for interacting with the OpenShift
 * master server.
 *
 */
public interface Client {
	
	/**
	 * List all possible resources of the given kind
	 * @param kind
	 * @return
	 */
	<T extends Resource> List<T> list(ResourceKind kind);

	
	/**
	 * list the given given resource kind scoping it to a specific namespace
	 * 
	 * @param kind
	 * @param namespace    The namespace to scope the possible results of this list
	 * @return
	 */
	<T extends Resource> List<T> list(ResourceKind kind, String namespace);

	/**
	 * 
	 * @param service
	 * @param name
	 * @return
	 */
	<T extends Resource> T get(ResourceKind kind, String name, String namespace);
	
	/**
	 * @param resource
	 * @return
	 */
	<T extends Resource> T create(T resource);

	/**
	 * @param resource
	 */
	<T extends Resource> void delete(T resource);

	/**
	 * Get the capability of the desired type
	 * 
	 * @param capability
	 * @return an implementation of the given capability
	 */
	<T extends Capability> T getCapability(Class<T> capability);
	
	/**
	 * Determine if the client supports the desired capability
	 *  
	 * @param capability
	 * @return true if the client is able to offer this capability
	 */
	boolean isCapableOf(Class<? extends Capability> capability);
	
	/**
	 * Connect to the OpenShift server and potentially
	 * returns a authorization context?
	 */
	AuthorizationContext authorize();
	
	static class AuthorizationContext {
	}
}
