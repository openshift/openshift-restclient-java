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
		return new LatestVersionOfName("mysql");
	}

	public static ICartridgeConstraint phpMyAdmin() {
		return new LatestVersionOfName("phpmyadmin");
	}

	public static  ICartridgeConstraint postgreSQL() {
		return new LatestVersionOfName("postgresql");
	}
	
	public static  ICartridgeConstraint mongoDB() {
		return new LatestVersionOfName("mongodb");
	}

	public static  ICartridgeConstraint rockMongo() {
		return new LatestVersionOfName("rockmongo");
	}

	public static  ICartridgeConstraint mmsAgent() {
		return new LatestVersionOfName("10gen-mms-agent");
	}

	public static  ICartridgeConstraint jenkinsClient() {
		return new LatestVersionOfName("jenkins-client");
	}

	public static  ICartridgeConstraint metrics() {
		return new LatestVersionOfName("metrics");
	}
}
