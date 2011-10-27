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
package org.jboss.tools.openshift.express.internal.client.request;

import org.jboss.tools.openshift.express.client.ISSHPublicKey;


/**
 * @author André Dietisheim
 */
public class ChangeDomainRequest extends AbstractDomainRequest {

	public ChangeDomainRequest(String name, ISSHPublicKey sshKey, String username) {
		this(name, sshKey, username, false);
	}

	public ChangeDomainRequest(String name, ISSHPublicKey sshKey, String username, boolean debug) {
		super(name, sshKey, username, debug);
	}

	public boolean isAlter() {
		return true;
	}

	@Override
	public String toHumanReadable() {
		return "change domain to " + getName();
	}
}
