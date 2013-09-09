/******************************************************************************* 
 * Copyright (c) 2012 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/
package com.openshift.client.cartridge;



/**
 * @author Xavier Coulon
 * @author Andre Dietisheim
 */
public interface IEmbeddableCartridge extends ICartridge {
	
	public static final String NAME_10GEN_MMS_AGENT = "10gen-mms-agent";
	public static final String NAME_CRON = "cron";
	public static final String NAME_HAPROXY = "haproxy";
	public static final String NAME_JENKINS_CLIENT = "jenkins-client";
	public static final String NAME_METRICS = "metrics";
	public static final String NAME_MONGODB = "mongodb";
	public static final String NAME_PHPMYADMIN = "phpmyadmin";
	public static final String NAME_POSTGRESQL = "postgresql";
	public static final String NAME_MYSQL = "mysql";
	public static final String NAME_ROCKMONGO = "rockmongo";
	public static final String NAME_SWITCHYARD = "switchyard";
}
