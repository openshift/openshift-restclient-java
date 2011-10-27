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
public abstract class AbstractDomainRequest extends AbstractOpenShiftRequest {

	private String name;
	private ISSHPublicKey sshKey;

	public AbstractDomainRequest(String name, ISSHPublicKey sshKey, String username) {
		this(name, sshKey, username, false);
	}

	public AbstractDomainRequest(String name, ISSHPublicKey sshKey, String username, boolean debug) {
		super(username, debug);
		this.name = name;
		this.sshKey = sshKey;
	}

	public String getName() {
		return name;
	}

	public ISSHPublicKey getSshKey() {
		return sshKey;
	}

	@Override
	public String getResourcePath() {
		return "domain";
	}

	public abstract boolean isAlter();
	
	public abstract String toHumanReadable();
}
