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
package org.jboss.tools.openshift.express.internal.client.request.marshalling;

import org.jboss.dmr.ModelNode;
import org.jboss.tools.openshift.express.client.ICartridge;
import org.jboss.tools.openshift.express.internal.client.IOpenShiftJsonConstants;
import org.jboss.tools.openshift.express.internal.client.request.ApplicationAction;
import org.jboss.tools.openshift.express.internal.client.request.ApplicationRequest;

/**
 * @author André Dietisheim
 */
public class ApplicationRequestJsonMarshaller extends AbstractJsonMarshaller<ApplicationRequest> {

	@Override
	protected void setJsonDataProperties(ModelNode node, ApplicationRequest request) {
		setStringProperty(IOpenShiftJsonConstants.PROPERTY_CARTRIDGE, getCartridgeName(request.getCartridge()), node);
		setStringProperty(IOpenShiftJsonConstants.PROPERTY_ACTION, getActionName(request.getAction()), node);
		setStringProperty(IOpenShiftJsonConstants.PROPERTY_APP_NAME, request.getName(), node);
	}

	private String getCartridgeName(ICartridge cartridge) {
		if (cartridge == null) {
			return null;
		}
		return cartridge.getName();
	}

	private String getActionName(ApplicationAction action) {
		if (action == null) {
			return null;
		}
		return action.name().toLowerCase();
	}
}
