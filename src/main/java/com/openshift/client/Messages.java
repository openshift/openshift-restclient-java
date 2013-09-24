/******************************************************************************* 
 * Copyright (c) 2013 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/
package com.openshift.client;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Andre Dietisheim
 */
public class Messages {

	private Map<IField, List<Message>> messagesByField;

	/**
	 * Instantiates a new abstract open shift resource.
	 * 
	 * @param service
	 *            the service
	 * @param links
	 *            the links
	 */
	public Messages(final Map<IField, List<Message>> messages) {
		this.messagesByField = messages;
	}

	public List<Message> getAll() {
		List<Message> allMessages = new ArrayList<Message>();
		for (List<Message> messages : messagesByField.values()) {
			for (Message message : messages) {
				allMessages.add(message);
			}
		}
		return allMessages;
	}
	
	/**
	 * Returns the first message of the given field type and severity. The messages only exist
	 * at creation time. See {@link #getCreationLog()} for further details.
	 * 
	 * @param field the field type 
	 * @param severity the severity
	 * @return
	 * 
	 * @see Message#FIELD_APPINFO
	 * @see Message#FIELD_RESULT
	 * @see ISeverity
	 */
	public Message getFirstBy(IField field, ISeverity severity) {
		List<Message> messages = getBy(field, severity);
		if (messages == null
				|| messages.size() == 0) {
			return null;
		}
		
		return messages.get(0);
	}

	/**
	 * Returns the messages of the given field type and severity. These messages only exist
	 * at creation time. See {@link #getCreationLog()} for further details.
	 * 
	 * @param field the field type 
	 * @param severity the severity
	 * @return
	 * 
	 * @see Message#FIELD_APPINFO
	 * @see Message#FIELD_RESULT
	 * @see ISeverity
	 */
	public List<Message> getBy(IField field, ISeverity severity) {
		List<Message> messages = getBy(field);
		if (messages == null
				|| messages.size() == 0) {
			return null;
		}
		List<Message> matchingMessages = new ArrayList<Message>();
		for (Message message : messages) {
			if (severity == null
					|| severity.equals(message.getSeverity())) {
				matchingMessages.add(message);
			}
		}
		return matchingMessages;
	}

	/**
	 * Returns the first message of the given field type. The messages only exist
	 * at creation time. See {@link #getCreationLog()} for further details.
	 * 
	 * @param field the field type 
	 * @return
	 * 
	 * @see Message#FIELD_APPINFO
	 * @see Message#FIELD_RESULT
	 */
	public Message getFirstBy(IField field) {
		List<Message> messages = getBy(field);
		if (messages == null 
				|| messages.size() == 0) {
			return null;
		}
		return messages.get(0);
	}

	/**
	 * Returns all the message of the given field type. The messages only exist
	 * at creation time. See {@link #getCreationLog()} for further details.
	 * 
	 * @param field the field type 
	 * @return
	 * 
	 * @see Message#FIELD_APPINFO
	 * @see Message#FIELD_RESULT
	 */
	public List<Message> getBy(IField field) {
		if (messagesByField == null) {
			return null;
		}
		return messagesByField.get(field);
	}
	
	public boolean hasMessages() {
		for (List<Message> messages : messagesByField.values()) {
			if (messages.size() > 0) {
				return true;
			}
		}
		return false;
	}

	public int size() {
		return getAll().size();
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		for (Message message : getAll()) {
			builder.append(message.getText()).append('\n');					
		}
		return builder.toString();
	}

}
