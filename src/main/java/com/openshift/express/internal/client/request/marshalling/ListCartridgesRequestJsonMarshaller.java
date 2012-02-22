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
import com.openshift.express.internal.client.request.ListCartridgesRequest;
import com.openshift.express.internal.client.utils.IOpenShiftJsonConstants;

/**
 * @author André Dietisheim
 */
public class ListCartridgesRequestJsonMarshaller extends AbstractJsonMarshaller<ListCartridgesRequest> {

	protected void setJsonDataProperties(ModelNode node, ListCartridgesRequest request) {
		setStringProperty(IOpenShiftJsonConstants.PROPERTY_CART_TYPE, request.getCartType().toString(), node);
	}
}
