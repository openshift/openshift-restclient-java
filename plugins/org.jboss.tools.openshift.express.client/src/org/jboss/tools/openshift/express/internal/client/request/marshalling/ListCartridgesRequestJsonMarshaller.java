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
import org.jboss.tools.openshift.express.internal.client.IOpenShiftJsonConstants;
import org.jboss.tools.openshift.express.internal.client.request.ListCartridgesRequest;

/**
 * @author André Dietisheim
 */
public class ListCartridgesRequestJsonMarshaller extends AbstractJsonMarshaller<ListCartridgesRequest> {

	@Override
	protected void setJsonDataProperties(ModelNode node, ListCartridgesRequest request) {
		setStringProperty(IOpenShiftJsonConstants.PROPERTY_CART_TYPE, request.getCartType(), node);
	}
}
