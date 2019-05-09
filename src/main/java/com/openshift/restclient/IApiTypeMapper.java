/******************************************************************************* 
 * Copyright (c) 2016-2019 Red Hat, Inc. 
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
 */
public interface IApiTypeMapper {

    static final String KUBE_API = "api";
    static final String OS_API = "oapi";
    static final String API_GROUPS_API = "apis";
    static final String FWD_SLASH = "/";
    static final char   DOT = '.';

    String getPreferedVersionFor(String endpoint);

    /**
     * return the versioned endpoint for the given kind
     * 
     * @param version
     *            the apiVersion, null or empty is best guess
     * @throws UnsupportedEndpointException
     *             if unable to determine the endpoint for the given kind
     */
    IVersionedApiResource getEndpointFor(String apiVersion, String kind);

    /**
     * Using the kind and apiVersion, determine if the cluster is able to consume
     * the given resource
     * 
     * @return true if supported; false otherwise
     */
    boolean isSupported(IResource resource);

    /**
     * Using the kind determine if the cluster is able to consume the given resource
     * 
     * @param kind
     *            the resource kind
     * @return true if supported; false otherwise
     */
    boolean isSupported(String kind);

    /**
     * Using the kind and apiVersion, determine if the cluster is able to consume
     * the given resource
     * 
     * @param version
     *            the apiVersion, null or empty is best guess
     * @param kind
     *            the resource kind
     * @return true if supported; false otherwise
     */
    boolean isSupported(String version, String kind);
    
    /**
     * Get the type information best matching the version and kind arguments
     * 
     * @param apiVersion the optional version (with or without group)
     * @param kind the type kind
     * @return the registered type or null
     */
    IVersionedType getType(String apiVersion, String kind);

    /**
     * The api group for a given set of resources and the versions it supports.
     * 
     * @author jeff.cantrill
     *
     */
    interface IApiGroup {

        /**
         * The prefix for this api group (e.g. api, oapi, apis)
         * 
         */
        String getPrefix();

        /**
         * The name of the api group
         * 
         */
        String getName();

        /**
         * The list of supported versions for the group.
         * 
         */
        Collection<String> getVersions();

        /**
         * The prefered version for the group
         * 
         */
        String getPreferedVersion();

        /**
         * Get the endpoint path for the given version
         * 
         */
        String pathFor(String version);
    }

    /**
     * A description of an endpoint for a given resource and the capabilities it
     * supports
     * 
     * @author jeff.cantrill
     */
    interface IVersionedApiResource {

        /**
         * The prefix for this api group (e.g. api, oapi, apis)
         * 
         */
        String getPrefix();

        /**
         * The groupname of the resource (e.g. extensions of extensions/v1beta1)
         * 
         */
        String getApiGroupName();

        /**
         * The version of the resource (e.g. v1)
         * 
         */
        String getVersion();

        /**
         * get resource name this group supports
         * 
         */
        String getName();

        /**
         * the kind used with this resource
         * 
         */
        String getKind();

        /**
         * @return true if the associated resource is namespaced
         */
        boolean isNamespaced();

        /**
         * Determine if the capability is supported (e.g. builds/webhooks)
         * 
         * @return true if the capability is supported.
         */
        boolean isSupported(String capability);

    }
    
    interface IVersionedType {
        /**
         * The prefix of the resource type (e.g. extensions of extensions/v1beta1)
         * 
         */
        String getPrefix();
        
        /**
         * The groupname of the resource type (e.g. extensions of extensions/v1beta1)
         * 
         */
        String getApiGroupName();

        /**
         * The version of the resource type (e.g. v1)
         * 
         */
        String getVersion();
        
        /**
         * The optional groupname and version of the resource type (e.g. extensions of extensions/v1beta1)
         * 
         */
        default String getApiGroupNameAndVersion() {
            if (getApiGroupName() != null) {
                return getApiGroupName() + IApiTypeMapper.FWD_SLASH + getVersion();
            }
            return getVersion();
        }
        
        /**
         * the kind used with this resource type
         * 
         */
        String getKind();
    }
}
