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
import com.openshift.express.client.OpenShiftException;
import com.openshift.express.internal.client.request.AbstractDomainRequest;
import com.openshift.express.internal.client.utils.IOpenShiftJsonConstants;

/**
 * @author André Dietisheim
 */
public class DomainRequestJsonMarshaller extends AbstractJsonMarshaller<AbstractDomainRequest> {

	protected void setJsonDataProperties(ModelNode node, AbstractDomainRequest request) throws OpenShiftException {
		node.get(IOpenShiftJsonConstants.PROPERTY_NAMESPACE).set(request.getName());
		node.get(IOpenShiftJsonConstants.PROPERTY_ALTER).set(String.valueOf(request.isAlter()));
		node.get(IOpenShiftJsonConstants.PROPERTY_SSH).set(request.getSshKey().getPublicKey());
		node.get(IOpenShiftJsonConstants.PROPERTY_KEY_TYPE).set(request.getSshKey().getKeyType().getTypeId());
		node.get(IOpenShiftJsonConstants.PROPERTY_DELETE).set(String.valueOf(request.isDelete()));
	}
}
