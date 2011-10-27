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
package org.jboss.tools.openshift.express.internal.client;

import org.jboss.tools.openshift.express.client.IDomain;
import org.jboss.tools.openshift.express.client.IOpenShiftService;
import org.jboss.tools.openshift.express.client.OpenShiftException;


/**
 * @author André Dietisheim
 */
public class Domain extends UserInfoAware implements IDomain {

	private String namespace;
	private IOpenShiftService service;

	public Domain(String namespace, InternalUser user, IOpenShiftService service) {
		this(namespace, null, user, service);
	}

	public Domain(String namespace, String rhcDomain, InternalUser user, IOpenShiftService service) {
		super(user);
		this.namespace = namespace;
		this.service = service;
	}

	@Override
	public String getNamespace() {
		return namespace;
	}

	@Override
	public String getRhcDomain() throws OpenShiftException {
		return getUserInfo().getRhcDomain();
	}

	@Override
	public void setNamespace(String namespace) throws OpenShiftException {
		InternalUser user = getUser();
		IDomain domain = service.changeDomain(namespace, user.getSshKey(), user);
		update(domain);
	}

	private void update(IDomain domain) {
		this.namespace = domain.getNamespace();
	}
}
