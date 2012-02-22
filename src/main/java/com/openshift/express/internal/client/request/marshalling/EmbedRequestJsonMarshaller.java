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
package com.openshift.express.internal.client.request.marshalling;

import org.jboss.dmr.ModelNode;
import com.openshift.express.client.IEmbeddableCartridge;
import com.openshift.express.internal.client.request.EmbedRequest;
import com.openshift.express.internal.client.utils.IOpenShiftJsonConstants;

/**
 * @author André Dietisheim
 */
public class EmbedRequestJsonMarshaller extends AbstractJsonMarshaller<EmbedRequest> {

	protected void setJsonDataProperties(ModelNode node, EmbedRequest request) {
		setStringProperty(IOpenShiftJsonConstants.PROPERTY_CARTRIDGE, getCartridgeName(request.getEmbeddableCartridge()), node);
		setStringProperty(IOpenShiftJsonConstants.PROPERTY_ACTION, request.getAction().getCommand(), node);
		setStringProperty(IOpenShiftJsonConstants.PROPERTY_APP_NAME, request.getName(), node);
	}

	private String getCartridgeName(IEmbeddableCartridge cartridge) {
		if (cartridge == null) {
			return null;
		}
		return cartridge.getName();
	}
}
