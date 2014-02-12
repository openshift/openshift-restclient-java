/******************************************************************************* 
 * Copyright (c) 2012 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/
package com.openshift.internal.client.response;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.jboss.dmr.ModelNode;
import org.jboss.dmr.ModelType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.openshift.client.IField;
import com.openshift.client.Message;
import com.openshift.client.Messages;
import com.openshift.client.OpenShiftException;
import com.openshift.internal.client.utils.IOpenShiftJsonConstants;
import com.openshift.internal.client.utils.StringUtils;

/**
 * A factory for creating ResourceDTO objects.
 * 
 * @author Xavier Coulon
 * @author Andre Dietisheim
 */
public abstract class AbstractJsonDTOFactory implements IRestResponseFactory {

	private final Logger LOGGER = LoggerFactory.getLogger(AbstractJsonDTOFactory.class);

	@Override
	public RestResponse get(final String json) throws OpenShiftException {
		// in case the server answers with 'no-content'
		if (StringUtils.isEmpty(json)) {
			return null;
		}
		LOGGER.trace("Unmarshalling response\n{}", json);
		final ModelNode rootNode = getModelNode(json);
		final String type = getAsString(rootNode, IOpenShiftJsonConstants.PROPERTY_TYPE);
		final String status = getAsString(rootNode, IOpenShiftJsonConstants.PROPERTY_STATUS);
		final Messages messages = createMessages(rootNode.get(IOpenShiftJsonConstants.PROPERTY_MESSAGES));
		final EnumDataType dataType = EnumDataType.safeValueOf(type);
		final ModelNode dataNode = rootNode.get(IOpenShiftJsonConstants.PROPERTY_DATA);
		Object data = null;
		if (dataNode.isDefined()) {
			data = createData(dataType, messages, rootNode.get(IOpenShiftJsonConstants.PROPERTY_DATA));
		}

		return new RestResponse(status, messages, data, dataType);
	}

	abstract protected Object createData(EnumDataType dataType, Messages messages, ModelNode dataNode);

	/**
	 * Creates a new ResourceDTO object.
	 * 
	 * @param messagesNode
	 *            the messages node
	 * @return the list< string>
	 */
	private Messages createMessages(ModelNode messagesNode) {
		Map<IField, List<Message>> messagesByField = new LinkedHashMap<IField, List<Message>>();
		if (messagesNode.getType() == ModelType.LIST) {
			for (ModelNode messageNode : messagesNode.asList()) {
				Message message = createMessage(messageNode);
				List<Message> messages = (List<Message>) messagesByField.get(message.getField());
				if (messages == null) {
					messages = new ArrayList<Message>();
				}
				messages.add(message);
				messagesByField.put(message.getField(), messages);
			}
		}
		return new Messages(messagesByField);
	}
	
	private Message createMessage(ModelNode messageNode) {
		String text = getAsString(messageNode, IOpenShiftJsonConstants.PROPERTY_TEXT);
		String field = getAsString(messageNode, IOpenShiftJsonConstants.PROPERTY_FIELD);
		int exitCode = getAsInteger(messageNode, IOpenShiftJsonConstants.PROPERTY_EXIT_CODE);
		String severity = getAsString(messageNode, IOpenShiftJsonConstants.PROPERTY_SEVERITY);
		return new Message(text, field, severity, exitCode);
	}
	
	/**
	 * Gets the model node.
	 * 
	 * @param content
	 *            the content
	 * @return the model node
	 * @throws OpenShiftException
	 *             the open shift exception
	 */
	protected ModelNode getModelNode(final String content) throws OpenShiftException {
		if (content == null) {
			throw new OpenShiftException("Could not unmarshall response: no content.");
		}
		final ModelNode node = ModelNode.fromJSONString(content);
		if (!node.isDefined()) {
			throw new OpenShiftException("Could not unmarshall response: erroneous content.");
		}

		return node;
	}

	/**
	 * Returns the property identified by the given name in the given model
	 * node, or null if the named property is undefined.
	 * 
	 * @param node
	 *            the model node
	 * @param propertyName
	 *            the name of the property
	 * @return the property as a String
	 */
	protected String getAsString(final ModelNode node, String propertyName) {
		final ModelNode propertyNode = node.get(propertyName);
		return propertyNode.isDefined() ? propertyNode.asString() : null;
	}
	
	/**
	 * Returns the property identified by the given name in the given model node, or null if the named property is
	 * undefined.
	 * 
	 * @param node
	 *            the model node
	 * @param propertyName
	 *            the name of the property
	 * @return the property as a String
	 */
	protected Boolean getAsBoolean(final ModelNode node, String propertyName) {
		final ModelNode propertyNode = node.get(propertyName);
		return propertyNode.isDefined() ? propertyNode.asBoolean() : Boolean.FALSE;
	}
	
	/**
	 * Returns the property identified by the given name in the given model node, or null if the named property is
	 * undefined.
	 * 
	 * @param node
	 *            the model node
	 * @param propertyName
	 *            the name of the property
	 * @return the property as an Integer
	 */
	protected int getAsInteger(final ModelNode node, String propertyName) {
		final ModelNode propertyNode = node.get(propertyName);
		return propertyNode.isDefined() ? propertyNode.asInt() : -1;
	}
	
	protected boolean isDefined(ModelNode node) {
		return node != null
				&& node.isDefined();
	}
}
