/******************************************************************************* 
 * Copyright (c) 2011 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/
package com.openshift.express.client;

import com.openshift.express.internal.client.EmbeddableCartridge;


/**
 * @author André Dietisheim
 */
public interface IEmbeddableCartridge {

	@Deprecated
	public static final IEmbeddableCartridge PHPMYADMIN_34 = new EmbeddableCartridge("phpmyadmin-3.4");
	@Deprecated
	public static final IEmbeddableCartridge MYSQL_51 = new EmbeddableCartridge("mysql-5.1");
	@Deprecated
	public static final IEmbeddableCartridge JENKINS_14 = new EmbeddableCartridge("jenkins-client-1.4");
	@Deprecated
	public static final IEmbeddableCartridge METRICS_01 = new EmbeddableCartridge("metrics-0.1");

	public String getName();

	public String getUrl() throws OpenShiftException;
	
	public String getCreationLog();
	
	public void setCreationLog(String creationLog);
}