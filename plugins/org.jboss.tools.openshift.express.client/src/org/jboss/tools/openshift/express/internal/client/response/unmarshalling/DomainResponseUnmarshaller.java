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
package org.jboss.tools.openshift.express.internal.client.response.unmarshalling;

import org.jboss.dmr.ModelNode;
import org.jboss.tools.openshift.express.client.IDomain;
import org.jboss.tools.openshift.express.client.IOpenShiftService;
import org.jboss.tools.openshift.express.internal.client.Domain;
import org.jboss.tools.openshift.express.internal.client.InternalUser;

/**
 * @author André Dietisheim
 */
public class DomainResponseUnmarshaller extends AbstractOpenShiftJsonResponseUnmarshaller<IDomain> {

	private String domainName;
	private InternalUser user;
	private IOpenShiftService service;
	
	public DomainResponseUnmarshaller(String domainName, InternalUser user, IOpenShiftService service) {
		this.domainName = domainName;
		this.user = user;
		this.service = service;
	}

	@Override
	protected IDomain createOpenShiftObject(ModelNode node) {
		return new Domain(domainName, user, service);
	}
}
