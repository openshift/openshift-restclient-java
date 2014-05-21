/******************************************************************************* 
 * Copyright (c) 2014 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/
package com.openshift.client;

import java.util.Arrays;
import java.util.List;

/**
 * @author Andre Dietisheim
 */
public class DeploymentTypes {

	private static final String GIT = "git";
	private static final String BINARY = "binary";
	
	private DeploymentTypes() {
	}
	
	public static String git() {
		return GIT;
	}

	public static String binary() {
		return BINARY;
	}
	
	public static boolean isBinary(String deploymentType) {
		return BINARY.equals(deploymentType);
	}

	public static boolean isGit(String deploymentType) {
		return GIT.equals(deploymentType);
	}
	
	public static List<String> getAll() {
		return Arrays.asList(GIT, BINARY);
	}

	public static String switchType(String deploymentType) {
		if (isBinary(deploymentType)) {
			return GIT;
		} else if (isGit(deploymentType)) {
			return BINARY;
		} else {
			throw new OpenShiftException("Unknown deployment type {0}", deploymentType);
		}
	}
	
}
