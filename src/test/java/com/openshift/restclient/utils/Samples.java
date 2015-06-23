/******************************************************************************* 
 * Copyright (c) 2014-2015 Red Hat, Inc. 
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

	//v1beta3
	V1BETA3_BUILD_CONFIG("openshift3/v1beta3_build_config.json"),
	V1BETA3_DEPLOYMENT_CONIFIG("openshift3/v1beta3_deployment_config.json"), 
	V1BETA3_IMAGE_STREAM("openshift3/v1beta3_image_stream.json"), 
	V1BETA3_BUILD("openshift3/v1beta3_build.json"), 
	V1BETA3_POD("openshift3/v1beta3_pod.json"), 
	V1BETA3_PROJECT("openshift3/v1beta3_project.json"), 
	V1BETA3_PROJECT_REQUEST("openshift3/v1beta3_project_request.json"), 
	V1BETA3_PVC("openshift3/v1beta3_pvc.json"), 
	V1BETA3_REPLICATION_CONTROLLER("openshift3/v1beta3_replication_controller.json"), 
	V1BETA3_ROUTE("openshift3/v1beta3_route.json"), 
	V1BETA3_ROUTE_WO_TLS("openshift3/v1beta3_route_wo_tls.json"), 
	V1BETA3_SERVICE("openshift3/v1beta3_service.json"),
	V1BETA3_SERVICE_ACCOUNT("openshift3/v1beta3_service_account.json"),
	V1BETA3_TEMPLATE("openshift3/v1beta3_template.json"),
	V1BETA3_Status("openshift3/v1beta3_status.json"),
	V1BETA3_USER("openshift3/v1beta3_user.json"),
	V1BETA3_SECRET("openshift3/v1beta3_secret.json"),
	V1BETA3_UNRECOGNIZED("openshift3/v1_unrecognized.json"),

	//v1
	V1_BUILD_CONFIG("openshift3/v1_build_config.json"),
	V1_DEPLOYMENT_CONIFIG("openshift3/v1_deployment_config.json"), 
	V1_IMAGE_STREAM("openshift3/v1_image_stream.json"), 
	V1_BUILD("openshift3/v1_build.json"), 
	V1_POD("openshift3/v1_pod.json"), 
	V1_PROJECT("openshift3/v1_project.json"), 
	V1_PROJECT_REQUEST("openshift3/v1_project_request.json"), 
	V1_PVC("openshift3/v1_pvc.json"), 
	V1_REPLICATION_CONTROLLER("openshift3/v1_replication_controller.json"), 
	V1_ROUTE("openshift3/v1_route.json"), 
	V1_ROUTE_WO_TLS("openshift3/v1_route_wo_tls.json"), 
	V1_SERVICE("openshift3/v1_service.json"),
	V1_SERVICE_ACCOUNT("openshift3/v1_service_account.json"),
	V1_TEMPLATE("openshift3/v1_template.json"), 
	V1_Status("openshift3/v1_status.json"),
	V1_USER("openshift3/v1_user.json"),
	V1_SECRET("openshift3/v1_secret.json"),
	V1_UNRECOGNIZED("openshift3/v1_unrecognized.json");

	private static final String SAMPLES_FOLDER = "/samples/";

	private String filePath;

	Samples(String fileName) {
		this.filePath = SAMPLES_FOLDER + fileName;
	}
	
	Samples(String root, String filename){
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
