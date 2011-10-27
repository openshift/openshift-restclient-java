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

import java.util.Date;

import javax.xml.datatype.DatatypeConfigurationException;

import org.jboss.dmr.ModelNode;
import org.jboss.dmr.ModelType;
import org.jboss.tools.openshift.express.client.OpenShiftException;
import org.jboss.tools.openshift.express.client.utils.RFC822DateUtils;
import org.jboss.tools.openshift.express.internal.client.IOpenShiftJsonConstants;
import org.jboss.tools.openshift.express.internal.client.response.OpenShiftResponse;

/**
 * @author André Dietisheim
 */
public abstract class AbstractOpenShiftJsonResponseUnmarshaller<OPENSHIFTOBJECT> {

	private String response;

	public OpenShiftResponse<OPENSHIFTOBJECT> unmarshall(String response) throws OpenShiftException {
		try {
			ModelNode node = ModelNode.fromJSONString(response);
			boolean debug = node.get(IOpenShiftJsonConstants.PROPERTY_DEBUG).asBoolean();
			String messages = getString(IOpenShiftJsonConstants.PROPERTY_MESSAGES, node);
			String result = getString(IOpenShiftJsonConstants.PROPERTY_RESULT, node);
			int exitCode = node.get(IOpenShiftJsonConstants.PROPERTY_EXIT_CODE).asInt();
			OPENSHIFTOBJECT openshiftObject = createOpenShiftObject(node);
			return new OpenShiftResponse<OPENSHIFTOBJECT>(debug, messages, result, openshiftObject, exitCode);
		} catch (IllegalArgumentException e) {
			throw new OpenShiftException(e, "Could not parse response \"{0}\"", response);
		} catch (Exception e) {
			throw new OpenShiftException(e, "Could not unmarshall response \"{0}\"", response);
		}
	}

	protected abstract OPENSHIFTOBJECT createOpenShiftObject(ModelNode responseNode) throws Exception;

	protected String getResponse() {
		return response;
	}

	protected String getString(String property, ModelNode node) {
		ModelNode propertyNode = node.get(property);
		if (!isSet(propertyNode)) {
			// replace "undefined" by null
			return null;
		}
		return propertyNode.asString();
	}

	protected boolean isSet(ModelNode node) {
		return node != null
				&& node.getType() != ModelType.UNDEFINED;
	}

	protected Date getDate(String property, ModelNode node) throws DatatypeConfigurationException {
		ModelNode propertyNode = node.get(property);
		return RFC822DateUtils.getDate(propertyNode.asString());
	}

	protected long getLong(String property, ModelNode node) {
		ModelNode propertyNode = node.get(property);
		return propertyNode.asLong(-1);
	}
}
