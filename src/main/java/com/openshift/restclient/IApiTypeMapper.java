/******************************************************************************* 
 * Copyright (c) 2016 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/
package com.openshift.restclient;

import java.util.Collection;

import com.openshift.restclient.model.IResource;

/**
 * Determine the supported api endpoints by the cluster
 * 
 * @author jeff.cantrill
 *
 */
public interface IApiTypeMapper {
	
	String getPreferedVersionFor(String endpoint);
	
	/**
	 * return the versioned endpoint for the given kind
	 * @param version   the apiVersion, null or empty is best guess
	 * @param kind
	 * @return
	 * @throws OpenShiftException if unable to determine the endpoint for the given kind
	 */
	IVersionedApiResource getEndpointFor(String apiVersion, String kind);

	/**
	 * Using the kind and apiVersion, determine
	 * if the cluster is able to consume the
	 * given resource
	 * 
	 * @param resource
	 * @return true if supported; false otherwise
	 */
	boolean isSupported(IResource resource);
	
	/**
	 * Using the kind determine
	 * if the cluster is able to consume the
	 * given resource
	 * 
	 * @param kind      the resource kind
	 * @return true if supported; false otherwise
	 */
	boolean isSupported(String kind);

	/**
	 * Using the kind and apiVersion, determine
	 * if the cluster is able to consume the
	 * given resource
	 * 
	 * @param version   the apiVersion, null or empty is best guess
	 * @param kind      the resource kind
	 * @return true if supported; false otherwise
	 */
	boolean isSupported(String version, String kind);
	
	/**
	 * The api group for a given set of resources
	 * and the versions it supports.
	 * @author jeff.cantrill
	 *
	 */
	interface IApiGroup{
		
		/**
		 * The prefix for this api group (e.g. api, oapi, apis)
		 * @return
		 */
		String getPrefix();
		
		/**
		 * The name of the api group
		 * @return
		 */
		String getName();
		
		/**
		 * The list of supported versions for the group.
		 * @return
		 */
		Collection<String> getVersions();
		
		/**
		 * The prefered version for the group
		 * @return
		 */
		String getPreferedVersion();
		
		/**
		 * Get the endpoint path for the given version
		 * @param version
		 * @return
		 */
		String pathFor(String version);
	}
	
	/**
	 * A description of an endpoint for 
	 * a given resource and the capabilities
	 * it supports
	 * 
	 * @author jeff.cantrill
	 */
	interface IVersionedApiResource{

		/**
		 * The prefix for this api group (e.g. api, oapi, apis)
		 * @return
		 */
		String getPrefix();

		/**
		 * The groupname of the resource (e.g. extensions of extensions/v1beta1)
		 * @return
		 */
		String getApiGroupName();

		/**
		 * The version of the resource (e.g. v1)
		 * @return
		 */
		String getVersion();
		
		/**
		 * get resource name this group supports
		 * @return
		 */
		String getName();
		
		/**
		 * the kind used with this resource
		 * @return
		 */
		String getKind();
		
		/**
		 * @return true if the associated resource is namespaced
		 */
		boolean isNamespaced();
		
		/**
		 * Determine if the capability is supported
		 * (e.g. builds/webhooks) 
		 * @return true if the capability is supported.
		 */
		boolean isSupported(String capability);
		
	}
}
