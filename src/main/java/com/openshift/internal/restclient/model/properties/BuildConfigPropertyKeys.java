/*******************************************************************************
 * Copyright (c) 2015 Red Hat, Inc. Distributed under license by Red Hat, Inc.
 * All rights reserved. This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Red Hat, Inc.
 ******************************************************************************/
package com.openshift.internal.restclient.model.properties;

/**
 * @author Jeff Cantrill
 */
public interface BuildConfigPropertyKeys {
	static final String BUILDCONFIG_SOURCE_TYPE = "buildconfig.source.type";
	static final String BUILDCONFIG_SOURCE_REF = "buildconfig.source.ref";
	static final String BUILDCONFIG_SOURCE_URI = "buildconfig.sourceuri";
	static final String BUILDCONFIG_STRATEGY = "buildconfig.strategy";
	static final String BUILDCONFIG_TYPE = "buildconfig.strategy.type";
	
	static final String BUILDCONFIG_CUSTOM_IMAGE = "buildconfig.custom.image";
	static final String BUILDCONFIG_CUSTOM_EXPOSEDOCKERSOCKET = "buildconfig.custom.exposesocket";
	static final String BUILDCONFIG_CUSTOM_ENV = "buildconfig.custom.env";
	
	static final String BUILDCONFIG_DOCKER_CONTEXTDIR = "buildconfig.docker.context";
	static final String BUILDCONFIG_DOCKER_NOCACHE = "buildconfig.docker.nocache";
	static final String BUILDCONFIG_DOCKER_BASEIMAGE = "buildconfig.docker.baseimage";

	static final String BUILDCONFIG_STI_IMAGE= "buildconfig.sti.image";
	static final String BUILDCONFIG_STI_SCRIPTS = "buildconfig.sti.scripts";
	static final String BUILDCONFIG_STI_CLEAN = "buildconfig.sti.clean";
	static final String BUILDCONFIG_STI_ENV = "buildconfig.sti.env";

	static final String BUILDCONFIG_OUTPUT_REPO = "buildconfig.outputrepo";
	
	static final String BUILDCONFIG_TRIGGERS = "buildconfig.triggers";
}
