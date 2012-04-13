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
package com.openshift.express.internal.client;

import java.io.StringReader;
import java.util.StringTokenizer;

import com.openshift.express.client.ICartridge;
import com.openshift.express.client.IJenkinsApplication;
import com.openshift.express.client.IOpenShiftService;
import com.openshift.express.client.IPerlApplication;
import com.openshift.express.client.IPythonApplication;
import com.openshift.express.client.OpenShiftException;

/**
 * @author William DeCoste
 * @author Andre Dietisheim
 */
public class JenkinsApplication extends Application implements IJenkinsApplication {
	protected String username = null;
	protected String password = null;

	public JenkinsApplication(String name, String uuid, String creationLog, String healthCheckPath, ICartridge cartridge,
			InternalUser user, IOpenShiftService service) {
		super(name, uuid, creationLog, healthCheckPath, cartridge, user, service);
		
		parseUsernamePassword(creationLog);
	}

	public JenkinsApplication(String name, String uuid, ICartridge cartridge, ApplicationInfo applicationInfo, InternalUser user,
			IOpenShiftService service) {
		super(name, uuid, cartridge, applicationInfo, user, service);
	}
	
	public String getHealthCheckUrl() throws OpenShiftException {
		return getApplicationUrl() + "login?from=%2F";
	}
	
	public String getHealthCheckResponse() throws OpenShiftException {
		return "<html>";
	}
	
	public String getUsername(){
		return username;
	}
	
	public String getPassword(){
		return password;
	}
	
	protected void parseUsernamePassword(String creationLog) {
		StringTokenizer tokenizer = new StringTokenizer(creationLog, ": ");
		while (tokenizer.hasMoreTokens()){
			String token = tokenizer.nextToken().trim();
			if (token.equals("User"))
				username = tokenizer.nextToken().trim();
			else if (token.equals("Password"))
				password = tokenizer.nextToken().trim();
		}
	}
}
