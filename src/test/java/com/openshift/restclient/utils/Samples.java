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
	
	//v1beta1
	V1BETA1_BUILD_CONFIG("openshift3/v1beta1_build_config.json"),
	V1BETA1_DEPLOYMENT_CONIFIG("openshift3/v1beta1_deployment_config.json"), 
	V1BETA1_IMAGE_STREAM("openshift3/v1beta1_image_stream.json"), 
	V1BETA1_BUILD("openshift3/v1beta1_build.json"), 
	V1BETA1_POD("openshift3/v1beta1_pod.json"), 
	V1BETA1_REPLICATION_CONTROLLER("openshift3/v1beta1_replication_controller.json"), 
	V1BETA1_ROUTE("openshift3/v1beta1_route.json"), 
	V1BETA1_SERVICE("openshift3/v1beta1_service.json"), 
	V1BETA1_TEMPLATE("openshift3/v1beta1_template.json"), 

	//v1beta3
	V1BETA3_BUILD_CONFIG("openshift3/v1beta3_build_config.json"),
	V1BETA3_DEPLOYMENT_CONIFIG("openshift3/v1beta3_deployment_config.json"), 
	V1BETA3_IMAGE_STREAM("openshift3/v1beta3_image_stream.json"), 
	V1BETA3_BUILD("openshift3/v1beta3_build.json"), 
	V1BETA3_POD("openshift3/v1beta3_pod.json"), 
	V1BETA3_REPLICATION_CONTROLLER("openshift3/v1beta3_replication_controller.json"), 
	V1BETA3_ROUTE("openshift3/v1beta3_route.json"), 
	V1BETA3_SERVICE("openshift3/v1beta3_service.json"), 
	V1BETA3_TEMPLATE("openshift3/v1beta3_template.json"), 
	V1BETA3_Status("openshift3/v1beta3_status.json"),
	V1BETA3_USER("openshift3/v1beta3_user.json"); 
	
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
