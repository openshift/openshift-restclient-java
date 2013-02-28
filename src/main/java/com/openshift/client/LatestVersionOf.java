/******************************************************************************* 
 * Copyright (c) 2013 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/
package com.openshift.client;

import com.openshift.internal.client.LatestVersionOfName;

/**
 * @author Andre Dietisheim
 * 
 */
public class LatestVersionOf {

	public static ICartridgeConstraint mySQL() {
		return new LatestVersionOfName(IEmbeddableCartridge.NAME_MYSQL);
	}

	public static ICartridgeConstraint phpMyAdmin() {
		return new LatestVersionOfName(IEmbeddableCartridge.NAME_PHPMYADMIN);
	}

	public static  ICartridgeConstraint postgreSQL() {
		return new LatestVersionOfName(IEmbeddableCartridge.NAME_POSTGRESQL);
	}
	
	public static  ICartridgeConstraint mongoDB() {
		return new LatestVersionOfName(IEmbeddableCartridge.NAME_MONGODB);
	}

	public static  ICartridgeConstraint rockMongo() {
		return new LatestVersionOfName(IEmbeddableCartridge.NAME_ROCKMONGO);
	}

	public static  ICartridgeConstraint mmsAgent() {
		return new LatestVersionOfName(IEmbeddableCartridge.NAME_10GEN_MMS_AGENT);
	}

	public static  ICartridgeConstraint jenkinsClient() {
		return new LatestVersionOfName(IEmbeddableCartridge.NAME_JENKINS_CLIENT);
	}

	public static  ICartridgeConstraint metrics() {
		return new LatestVersionOfName(IEmbeddableCartridge.NAME_METRICS);
	}
}
