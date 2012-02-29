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


/**
 * A cartridge that is available on the openshift server. This class is no enum
 * since we dont know all available types and they may change at any time.
 * 
 * @author André Dietisheim
 */
public class JenkinsCartridge extends Cartridge {
	
	public JenkinsCartridge(String name) {
		super(name);
	}
	
	public JenkinsCartridge(IOpenShiftService service, IUser user) throws OpenShiftException {
		super(service, user);
		
		name = this.getCartridgeName(JENKINS);
	}

}