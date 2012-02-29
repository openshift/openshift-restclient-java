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

import com.openshift.express.client.IOpenShiftService;
import com.openshift.express.client.IUser;
import com.openshift.express.client.OpenShiftException;


/**
 * A cartridge that is available on the openshift server. This class is no enum
 * since we dont know all available types and they may change at any time.
 * 
 * @author André Dietisheim
 */
public class JenkinsClientEmbeddableCartridge extends EmbeddableCartridge {
	
	public JenkinsClientEmbeddableCartridge(String name) {
		super(name);
	}
	
	public JenkinsClientEmbeddableCartridge(IOpenShiftService service, IUser user) throws OpenShiftException {
		super(service, user);
		
		name = this.getCartridgeName(JENKINS_CLIENT);
		
	}
	
	public JenkinsClientEmbeddableCartridge(IOpenShiftService service, IUser user, Application application) throws OpenShiftException {
		super(service, user, application);
		
		name = this.getCartridgeName(JENKINS_CLIENT);
		
	}
	
	public JenkinsClientEmbeddableCartridge(IOpenShiftService service, IUser user, String url) throws OpenShiftException {
		super(service, user, url);
		
		name = this.getCartridgeName(JENKINS_CLIENT);
		
	}
	
	public JenkinsClientEmbeddableCartridge(IOpenShiftService service, IUser user, String url, Application application) throws OpenShiftException {
		super(service, user, url, application);
		
		name = this.getCartridgeName(JENKINS_CLIENT);
	
	}

}