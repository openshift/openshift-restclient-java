/*******************************************************************************
 * Copyright (c) 2015 Red Hat, Inc. Distributed under license by Red Hat, Inc.
 * All rights reserved. This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: Red Hat, Inc.
 ******************************************************************************/

package com.openshift.restclient;

import java.util.Optional;

import com.openshift.internal.restclient.model.Build;
import com.openshift.internal.restclient.model.BuildConfig;
import com.openshift.internal.restclient.model.ConfigMap;
import com.openshift.internal.restclient.model.DeploymentConfig;
import com.openshift.internal.restclient.model.ImageStream;
import com.openshift.internal.restclient.model.KubernetesEvent;
import com.openshift.internal.restclient.model.LimitRange;
import com.openshift.internal.restclient.model.Namespace;
import com.openshift.internal.restclient.model.Pod;
import com.openshift.internal.restclient.model.Project;
import com.openshift.internal.restclient.model.ReplicationController;
import com.openshift.internal.restclient.model.ResourceQuota;
import com.openshift.internal.restclient.model.Route;
import com.openshift.internal.restclient.model.Secret;
import com.openshift.internal.restclient.model.Service;
import com.openshift.internal.restclient.model.ServiceAccount;
import com.openshift.internal.restclient.model.authorization.OpenshiftPolicy;
import com.openshift.internal.restclient.model.authorization.OpenshiftRole;
import com.openshift.internal.restclient.model.authorization.PolicyBinding;
import com.openshift.internal.restclient.model.authorization.RoleBinding;
import com.openshift.internal.restclient.model.build.BuildRequest;
import com.openshift.internal.restclient.model.image.ImageStreamImport;
import com.openshift.internal.restclient.model.oauth.OAuthAccessToken;
import com.openshift.internal.restclient.model.oauth.OAuthAuthorizeToken;
import com.openshift.internal.restclient.model.oauth.OAuthClient;
import com.openshift.internal.restclient.model.oauth.OAuthClientAuthorization;
import com.openshift.internal.restclient.model.project.OpenshiftProjectRequest;
import com.openshift.internal.restclient.model.template.Template;
import com.openshift.internal.restclient.model.user.OpenShiftUser;
import com.openshift.internal.restclient.model.volume.PersistentVolume;
import com.openshift.internal.restclient.model.volume.PersistentVolumeClaim;
import com.openshift.restclient.model.IResource;

/**
 * Predfined resource kinds which are used
 */
public enum PredefinedResourceKind implements ResourceKind {

    BUILD("Build", Build.class),
    BUILD_CONFIG("BuildConfig", BuildConfig.class),
    DEPLOYMENT_CONFIG("DeploymentConfig", DeploymentConfig.class),

    IMAGE_STREAM("ImageStream", ImageStream.class),

    IMAGE_STREAM_TAG("ImageStreamTag", null), // No Binding?

    IMAGE_STREAM_IMPORT("ImageStreamImport", ImageStreamImport.class),
    NAMESPACE("Namespace", Namespace.class),
    OAUTH_ACCESS_TOKEN("OAuthAccessToken", OAuthAccessToken.class),
    OAUTH_AUTHORIZE_TOKEN("OAuthAuthorizeToken", OAuthAuthorizeToken.class),
    OAUTH_CLIENT("OAuthClient", OAuthClient.class),
    OAUTH_CLIENT_AUTHORIZATION("OAuthClientAuthorization", OAuthClientAuthorization.class),
    POLICY("Policy", OpenshiftPolicy.class),
    POLICY_BINDING("PolicyBinding", PolicyBinding.class),
    PROJECT("Project", Project.class),
    PROJECT_REQUEST("ProjectRequest", OpenshiftProjectRequest.class),
    ROLE("Role", OpenshiftRole.class),
    ROLE_BINDING("RoleBinding", RoleBinding.class),
    ROUTE("Route", Route.class),
    TEMPLATE("Template", Template.class),
    USER("User", OpenShiftUser.class),

    //Kubernetes Kinds
    ENDPOINTS("Endpoints", null),
    EVENT("Event", KubernetesEvent.class),
    LIMIT_RANGE("LimitRange", LimitRange.class),
    POD("Pod", Pod.class),
    PVC("PersistentVolumeClaim", PersistentVolumeClaim.class),
    PERSISTENT_VOLUME("PersistentVolume", PersistentVolume.class),
    REPLICATION_CONTROLLER("ReplicationController", ReplicationController.class),
    RESOURCE_QUOTA("ResourceQuota", ResourceQuota.class),
    SERVICE("Service", Service.class),
    SECRET("Secret", Secret.class),
    SERVICE_ACCOUNT("ServiceAccount", ServiceAccount.class),
    CONFIG_MAP("ConfigMap", ConfigMap.class),
    /*
     * These are not true resources that can be used (mostly) in
     * RESTful operations
     */
    BUILD_REQUEST("BuildRequest", BuildRequest.class),

    @Deprecated
    CONFIG("Config", null), // not rest resource
    LIST("List", com.openshift.internal.restclient.model.List.class),
    STATUS("Status", null), // not rest resource
    PROCESSED_TEMPLATES("ProcessedTemplates", null), //mechanism for processing templates

    /**
     * The default if we haven't implemented the kind yet
     */
    UNRECOGNIZED("Unrecognized", null);

    private final String identifier;
    private final Optional<Class<? extends IResource>> implementationClass;

    PredefinedResourceKind(final String identifier,
                           final Class<? extends IResource> implementationClass) {
        this.identifier = identifier;
        this.implementationClass = Optional.ofNullable(implementationClass);
    }

    @Override
    public String getIdentifier() {
        return identifier;
    }

    @Override
    public Optional<Class<? extends IResource>> getImplementationClass() {
        return implementationClass;
    }


}