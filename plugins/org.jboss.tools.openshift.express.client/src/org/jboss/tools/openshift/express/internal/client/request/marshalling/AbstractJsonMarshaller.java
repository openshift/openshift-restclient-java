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
import org.jboss.tools.openshift.express.client.OpenShiftException;
import org.jboss.tools.openshift.express.internal.client.IOpenShiftJsonConstants;
import org.jboss.tools.openshift.express.internal.client.request.AbstractOpenShiftRequest;

/**
 * @author André Dietisheim
 */
public abstract class AbstractJsonMarshaller<REQUEST extends AbstractOpenShiftRequest> implements
		IOpenShiftMarshaller<REQUEST> {

	public String marshall(REQUEST request) throws OpenShiftException {
		ModelNode node = new ModelNode();
		setStringProperty(IOpenShiftJsonConstants.PROPERTY_RHLOGIN, request.getRhLogin(), node);
		setStringProperty(IOpenShiftJsonConstants.PROPERTY_DEBUG, String.valueOf(request.isDebug()), node);
		setJsonDataProperties(node, request);
		return node.toJSONString(true);
	}

	protected void setJsonDataProperties(ModelNode node, REQUEST request) throws OpenShiftException {
		// empty default implementation
	}

	protected void setStringProperty(String propertyName, Object value, ModelNode node) {
		if (!(value instanceof String)
				&& !isSet((String) value)) {
			return;
		}

		setStringProperty((String) value, propertyName, node);
	}

	protected void setStringProperty(String propertyName, String value, ModelNode node) {
		if (!isSet(value)) {
			return;
		}

		node.get(propertyName).set(value);
	}

	protected boolean isSet(String value) {
		return value != null
				&& value.length() > 0;
	}

	protected boolean isSet(Object value) {
		return value != null;
	}

}
