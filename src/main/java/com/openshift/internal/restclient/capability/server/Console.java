/*******************************************************************************
 * Copyright (c) 2019 Red Hat, Inc. Distributed under license by Red Hat, Inc.
 * All rights reserved. This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Red Hat, Inc.
 ******************************************************************************/

package com.openshift.internal.restclient.capability.server;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Stream;

import com.openshift.restclient.IClient;
import com.openshift.restclient.ResourceKind;
import com.openshift.restclient.capability.server.IConsole;
import com.openshift.restclient.model.IConfigMap;
import com.openshift.restclient.model.IProject;
import com.openshift.restclient.model.IResource;

public class Console implements IConsole {

    public static final String CONFIGMAP_DATA_CONSOLE_URL = "consoleURL";
    public static final String NAMESPACE_OPENSHIFT_CONFIG_MANAGED = "openshift-config-managed";
    public static final String CONFIGMAP_CONSOLE_PUBLIC = "console-public";

    private IClient client;

    public Console(IClient client) {
        this.client = client;
    }

    @Override
    public boolean isSupported() {
        return true;
    }

    @Override
    public String getName() {
        return this.getClass().getSimpleName();
    }

    @Override
    public String getConsoleUrl() {
        switch (client.getOpenShiftMajorVersion()) {
        case 3:
            return getOpenShift3ConsoleUrl();
        case 4:
            return getOpenShift4ConsoleUrl();
        default:
            return null;
        }
    }

    @Override
    public <R extends IResource> String getConsoleUrl(R resource) {
        switch (client.getOpenShiftMajorVersion()) {
        case 3:
            return getOpenShift3ConsoleUrl(resource);
        case 4:
            return getOpenShift4ConsoleUrl(resource);
        default:
            return null;
        }
    }

    private String getOpenShift3ConsoleUrl() {
        return client.getBaseURL() + "/console";
    }

    private <R extends IResource> String getOpenShift3ConsoleUrl(R resource) {
        StringBuilder builder = new StringBuilder(getOpenShift3ConsoleUrl());
        String projectName = resource == null ? null : resource.getNamespaceName();
        if (projectName != null) {
            builder.append("/project/").append(projectName);
        }
        if (resource != null 
                && !(resource instanceof IProject)) {
            String consoleResourceUrl = getOpenShiftResourceURL(resource, 
                    Arrays.stream(OpenShiftConsoleResourceUrls.values())
                            .filter(resourceUrl -> resourceUrl.getVersion() ==  3));
            if (consoleResourceUrl != null) {
                builder.append(consoleResourceUrl);
            }
        }
        return builder.toString();
    }

    private String getOpenShift4ConsoleUrl() {
        IConfigMap configMap = client.get(
                ResourceKind.CONFIG_MAP, CONFIGMAP_CONSOLE_PUBLIC, NAMESPACE_OPENSHIFT_CONFIG_MANAGED);
        if (configMap == null) {
            return null;
        }
        Map<String, String> data = configMap.getData();
        if (data == null
                || data.isEmpty()) {
            return null;
        }
        return data.get(CONFIGMAP_DATA_CONSOLE_URL);
    }

    private <R extends IResource> String getOpenShift4ConsoleUrl(R resource) {
        StringBuilder builder = new StringBuilder(getOpenShift4ConsoleUrl());
        if (resource == null) {
            return builder.toString();
        }
        String projectName = resource.getNamespaceName();
        if (resource.getKind().equals(ResourceKind.PROJECT)) {
            builder.append("/overview/ns/").append(projectName);
        } else {
            String consoleResourceUrl = getOpenShiftResourceURL(resource, 
                    Arrays.stream(OpenShiftConsoleResourceUrls.values())
                    .filter(resourceUrl -> resourceUrl.getVersion() ==  4));
            if (consoleResourceUrl != null) {
                builder.append("/k8s/ns/").append(projectName).append(consoleResourceUrl);
            } else {
                // show project overview
                builder.append("/overview/ns/").append(projectName);
            }
        }
        return builder.toString();
    }
    
    protected String getOpenShiftResourceURL(IResource resource, Stream<OpenShiftConsoleResourceUrls> resourceUrls) {
        return resourceUrls
            .filter(resUrl -> resUrl.getResType().equals(resource.getKind()))
            .findAny()
            .map(resUrl -> resUrl.getUrlPart() + resUrl.getEndUrlFunc().apply(resource))
            .orElse(null);
    }

    protected enum OpenShiftConsoleResourceUrls {
        // OS3
        V3_BUILD(3, ResourceKind.BUILD, "/browse/builds/", r -> String.join("/", r.getLabels().get("buildconfig"), r.getName())), 
        V3_BUILDCONFIG(3, ResourceKind.BUILD_CONFIG, "/browse/builds/", IResource::getName),
        V3_DEPLOYMENT(3, ResourceKind.REPLICATION_CONTROLLER, "/browse/rc/", IResource::getName),
        V3_DEPLOYMENTCONFIG(3, ResourceKind.DEPLOYMENT_CONFIG, "/browse/deployments/", IResource::getName),
        V3_EVENT(3, ResourceKind.EVENT, "/browse/events/", r -> org.apache.commons.lang.StringUtils.EMPTY),
        V3_IMAGESTREAM(3, ResourceKind.IMAGE_STREAM, "/browse/images/", IResource::getName),
        V3_PERSISTENTVOLUMECLAIM(3, ResourceKind.PVC, "/browse/persistentvolumeclaims/", IResource::getName),
        V3_POD(3, ResourceKind.POD, "/browse/pods/", IResource::getName),
        V3_ROUTES(3, ResourceKind.ROUTE, "/browse/routes/", IResource::getName),
        V3_SERVICE(3, ResourceKind.SERVICE, "/browse/services/", IResource::getName),
        // OS4
        V4_BUILDCONFIG(4, ResourceKind.BUILD_CONFIG, "/buildconfigs/", IResource::getName),
        V4_BUILD(4, ResourceKind.BUILD, "/builds/", IResource::getName), 
        V4_DEPLOYMENT(4, ResourceKind.REPLICATION_CONTROLLER, "/replicationcontrollers/", IResource::getName),
        V4_DEPLOYMENTCONFIG(4, ResourceKind.DEPLOYMENT_CONFIG, "/deploymentconfigs/", IResource::getName),
        V4_EVENT(4, ResourceKind.EVENT, "/events/", r -> org.apache.commons.lang.StringUtils.EMPTY),
        V4_IMAGESTREAM(4, ResourceKind.IMAGE_STREAM, "/imagestreams/", IResource::getName),
        V4_PERSISTENTVOLUMECLAIM(4, ResourceKind.PVC, "/persistentvolumeclaims/", IResource::getName),
        V4_POD(4, ResourceKind.POD, "/pods/", IResource::getName),
        V4_ROUTES(4, ResourceKind.ROUTE, "/routes/", IResource::getName),
        V4_SERVICE(4, ResourceKind.SERVICE, "/services/", IResource::getName);

        private int openShiftVersion;
        private final String resourceKind;
        private final String urlPart;
        private final Function<IResource, String> endUrlFunc;

        private OpenShiftConsoleResourceUrls(int version, String resourceKind, String urlPart,
                Function<IResource, String> endUrlFunc) {
            this.openShiftVersion = version;
            this.resourceKind = resourceKind;
            this.urlPart = urlPart;
            this.endUrlFunc = endUrlFunc;
        }

        public int getVersion() {
            return openShiftVersion;
        }

        public String getResType() {
            return resourceKind;
        }

        public String getUrlPart() {
            return urlPart;
        }

        public Function<IResource, String> getEndUrlFunc() {
            return endUrlFunc;
        }
    }
}
