/******************************************************************************* 
 * Copyright (c) 2014-2019 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/

package com.openshift.restclient.utils;

import java.io.InputStream;

import org.apache.commons.io.IOUtils;

/**
 * @author Andre Dietisheim
 * @author Jeff Cantrill
 */
public enum Samples {
    OPENSHIFT_VERSION("openshift3/api_openshift_version.json"),
    KUBERNETES_VERSION("openshift3/api_kubernetes_version.json"),

    GROUP_ENDPONT_API_V1("openshift3/api_v1_endpoint.json"),
    GROUP_ENDPONT_OAPI_V1("openshift3/oapi_v1_endpoint.json"),
    GROUP_ENDPONT_APIS("openshift3/apis_endpoint.json"),
    GROUP_ENDPONT_APIS_EXTENSIONS("openshift3/apis_endpoint_extensions.json"),
    
    // api/extensions
    V1BETA1_API_EXT_SCALE("openshift3/api/extensions/v1beta1_scale.json"),
    
    //v1
    V1_KUBE_CONFIG("openshift3/v1_kubeconfig.yaml"),

    V1_BUILD_CONFIG("openshift3/v1_build_config.json"),
    V1_BUILD_CONFIG_LIST("openshift3/v1_build_config_list.json"),
    V1_DEPLOYMENT_CONIFIG("openshift3/v1_deployment_config.json"),
    V1_ENDPOINTS("openshift3/api/v1_endpoints.json"),
    V1_EVENT("openshift3/v1_event.json"),
    V1_IMAGE_STREAM("openshift3/v1_image_stream.json"),
    V1_IMAGE_STREAM_IMPORT("openshift3/v1_image_stream_import.json"), 
    V1_BUILD("openshift3/v1_build.json"), 
    V1_OBJECT_REF("openshift3/v1_objectref.json"),
    V1_NAMESPACE("openshift3/v1_namespace.json"),
    V1_POD("openshift3/v1_pod.json"), 
    V1_POD_MULTICONTAINER_READY("openshift3/v1_pod_multiContainer_ready.json"), 
    V1_PROJECT("openshift3/v1_project.json"), 
    V1_PROJECT_REQUEST("openshift3/v1_project_request.json"), 
    V1_PVC("openshift3/v1_pvc.json"),
    V1_PERSISTENT_VOLUME("openshift3/v1_persistent_volume.json"),
    V1_REPLICATION_CONTROLLER("openshift3/v1_replication_controller.json"), 
    V1_ROLE_BINDING("openshift3/v1_role_binding.json"), 
    V1_ROUTE("openshift3/v1_route.json"),
    V1_ROUTE_WO_TLS("openshift3/v1_route_wo_tls.json"),
    V1_ROUTE_PORT_NUMERIC("openshift3/v1_route_port_numeric.json"),
    V1_ROUTE_PORT_NAME("openshift3/v1_route_port_name.json"),
    V1_SERVICE("openshift3/v1_service.json"),
    V1_SERVICE_ACCOUNT("openshift3/v1_service_account.json"),
    V1_Status("openshift3/v1_status.json"),
    V1_TEMPLATE("openshift3/v1_template.json"),
    GROUP_TEMPLATE("openshift3/group_template.json"),
    V1_USER("openshift3/v1_user.json"),
    V1_IDENTITY("openshift3/v1_identity.json"),
    V1_GROUP("openshift3/v1_group.json"),
    V1_SECRET("openshift3/v1_secret.json"),
    V1_UNRECOGNIZED("openshift3/v1_unrecognized.json"),
    V1_CONFIG_MAP("openshift3/v1_config_map.json"),
    V1_CONFIG_MAP_LIST_EMPTY("openshift3/v1_config_map_list_empty.json"),
    V1_EMPTYDIR_VOLUME_SOURCE("openshift3/v1_empty_dir_volume_source.json"),
    V1_SECRET_VOLUME_SOURCE("openshift3/v1_secret_volume_source.json"),
    V1_PVC_VOLUME_SOURCE("openshift3/v1_pvc_volume_source.json"),
    V1_LIFECYCLE("openshift3/v1_lifecycle.json"),
    V1_DOCKER_IMAGE_MANIFEST("dockerregistry/v1_image_manifest.json"),
    V1_BUILDCONFIG_PIPELINE("openshift3/v1_buildconfig_pipeline.json"),
    V1_CONFIGMAP_CONSOLE_PUBLIC("openshift3/v1_config_map_console_public.json");
    
    private static final String SAMPLES_FOLDER = "/samples/";

    private String filePath;

    Samples(String fileName) {
        this.filePath = SAMPLES_FOLDER + fileName;
    }

    Samples(String root, String filename) {
        this.filePath = root + filename;
    }

    public String getContentAsString() {
        String content = null;
        try {
            final InputStream contentStream = Samples.class.getResourceAsStream(filePath);
            content = IOUtils.toString(contentStream, "UTF-8");
        } catch (Throwable e) {
            e.printStackTrace();
            throw new RuntimeException("Could not read file " + filePath + ": " + e.getMessage());
        }
        return content;
    }
}
