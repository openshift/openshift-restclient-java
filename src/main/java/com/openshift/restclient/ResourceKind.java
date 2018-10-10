/*******************************************************************************
 * Copyright (c) 2015 Red Hat, Inc. Distributed under license by Red Hat, Inc.
 * All rights reserved. This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Red Hat, Inc.
 ******************************************************************************/

package com.openshift.restclient;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

/**
 * ResourceKind are the various types of Kubernetes resources that are of
 * interest
 *
 */
public final class ResourceKind {

    // OpenShift Kinds
    public static final String BUILD = "Build";
    public static final String BUILD_CONFIG = "BuildConfig";
    public static final String DEPLOYMENT_CONFIG = "DeploymentConfig";
    public static final String IMAGE_STREAM = "ImageStream";
    public static final String IMAGE_STREAM_TAG = "ImageStreamTag";
    public static final String IMAGE_STREAM_IMPORT = "ImageStreamImport";
    public static final String NAMESPACE = "Namespace";
    public static final String OAUTH_ACCESS_TOKEN = "OAuthAccessToken";
    public static final String OAUTH_AUTHORIZE_TOKEN = "OAuthAuthorizeToken";
    public static final String OAUTH_CLIENT = "OAuthClient";
    public static final String OAUTH_CLIENT_AUTHORIZATION = "OAuthClientAuthorization";
    public static final String POLICY = "Policy";
    public static final String POLICY_BINDING = "PolicyBinding";
    public static final String PROJECT = "Project";
    public static final String PROJECT_REQUEST = "ProjectRequest";
    public static final String ROLE = "Role";
    public static final String ROLE_BINDING = "RoleBinding";
    public static final String ROUTE = "Route";
    public static final String TEMPLATE = "Template";
    public static final String USER = "User";
    public static final String GROUP = "Group";
    public static final String IDENTITY = "Identity";

    // Kubernetes Kinds
    public static final String ENDPOINTS = "Endpoints";
    public static final String EVENT = "Event";
    public static final String LIMIT_RANGE = "LimitRange";
    public static final String POD = "Pod";
    public static final String PVC = "PersistentVolumeClaim";
    public static final String PERSISTENT_VOLUME = "PersistentVolume";
    public static final String REPLICATION_CONTROLLER = "ReplicationController";
    public static final String RESOURCE_QUOTA = "ResourceQuota";
    public static final String SERVICE = "Service";
    public static final String SECRET = "Secret";
    public static final String SERVICE_ACCOUNT = "ServiceAccount";
    public static final String CONFIG_MAP = "ConfigMap";
    /*
     * These are not true resources that can be used (mostly) in RESTful operations
     */
    public static final String BUILD_REQUEST = "BuildRequest";

    @Deprecated
    public static final String CONFIG = "Config";// not rest resource;
    public static final String LIST = "List";
    public static final String STATUS = "Status";// not rest resource
    public static final String PROCESSED_TEMPLATES = "ProcessedTemplates";// mechanism for processing templates

    /**
     * The default if we haven't implemented the kind yet
     */
    public static final String UNRECOGNIZED = "Unrecognized";

    private static final Collection<String> values;

    public static Collection<String> values() {
        return values;
    }

    public static String pluralize(String kind) {
        return pluralize(kind, false, false);
    }

    public static String pluralize(String kind, boolean lowercase, boolean uncapitalize) {
        if (StringUtils.isBlank(kind)) {
            return "";
        }
        if (kind.endsWith("y")) {
            kind = kind.substring(0, kind.length() - 1).concat("ies");
        } else if (!kind.endsWith("s")) {
            kind = kind.concat("s");
        }
        if (lowercase) {
            kind = kind.toLowerCase();
        }
        if (uncapitalize) {
            kind = StringUtils.uncapitalize(kind);
        }
        return kind;
    }

    static {
        Set<String> set = new HashSet<String>();
        // OpenShift Kinds
        set.add(BUILD);
        set.add(BUILD_CONFIG);
        set.add(DEPLOYMENT_CONFIG);
        set.add(IMAGE_STREAM);
        set.add(IMAGE_STREAM_TAG);
        set.add(IMAGE_STREAM_IMPORT);
        set.add(OAUTH_ACCESS_TOKEN);
        set.add(OAUTH_AUTHORIZE_TOKEN);
        set.add(OAUTH_CLIENT);
        set.add(OAUTH_CLIENT_AUTHORIZATION);
        set.add(POLICY);
        set.add(POLICY_BINDING);
        set.add(PROJECT);
        set.add(PROJECT_REQUEST);
        set.add(ROLE);
        set.add(ROLE_BINDING);
        set.add(ROUTE);
        set.add(TEMPLATE);
        set.add(USER);
        set.add(GROUP);
        set.add(IDENTITY);

        // Kubernetes Kinds
        set.add(EVENT);
        set.add(LIMIT_RANGE);
        set.add(POD);
        set.add(PVC);
        set.add(PERSISTENT_VOLUME);
        set.add(REPLICATION_CONTROLLER);
        set.add(RESOURCE_QUOTA);
        set.add(SERVICE);
        set.add(SECRET);
        set.add(SERVICE_ACCOUNT);
        set.add(CONFIG_MAP);

        /*
         * These are not true resources that can be used (mostly) in RESTful operations
         */
        set.add(BUILD_REQUEST);
        set.add(CONFIG);
        set.add(LIST);
        set.add(STATUS);
        set.add("ProcessedTemplates");
        values = Collections.unmodifiableCollection(set);
    }

    private ResourceKind() {
    }
}
