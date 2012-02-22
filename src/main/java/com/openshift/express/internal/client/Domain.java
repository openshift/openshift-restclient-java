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

import com.openshift.express.client.IApplication;
import com.openshift.express.client.IDomain;
import com.openshift.express.client.IOpenShiftService;
import com.openshift.express.client.OpenShiftException;


/**
 * @author André Dietisheim
 */
public class Domain extends UserInfoAware implements IDomain {

	private String namespace;
	private IOpenShiftService service;
	private String rhcDomain;

	public Domain(String namespace, InternalUser user, IOpenShiftService service) {
		this(namespace, null, user, service);
	}

	public Domain(String namespace, String rhcDomain, InternalUser user, IOpenShiftService service) {
		super(user);
		this.namespace = namespace;
		this.rhcDomain = rhcDomain;
		this.service = service;
	}

	public String getNamespace() {
		return namespace;
	}
	
	public String getRhcDomain() throws OpenShiftException {
		if (rhcDomain == null) {
			this.rhcDomain = getUserInfo().getRhcDomain();
		}
		return rhcDomain;
	}

	public void setNamespace(String namespace) throws OpenShiftException {
		InternalUser user = getInternalUser();
		IDomain domain = service.changeDomain(namespace, user.getSshKey(), user);
		update(domain);
	}

	private void update(IDomain domain) throws OpenShiftException {
		this.namespace = domain.getNamespace();
		this.rhcDomain = domain.getRhcDomain();
	}

	public boolean waitForAccessible(long timeout) throws OpenShiftException {
		boolean accessible = true;
		for (IApplication application : getInternalUser().getApplications()) {
			accessible |= service.waitForHostResolves(application.getApplicationUrl(), timeout);
		}
		return accessible;
	}
	
    public void destroy() throws OpenShiftException {
    	getInternalUser().destroyDomain();
    }
}
