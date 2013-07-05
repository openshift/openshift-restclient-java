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

import java.text.MessageFormat;

import com.openshift.internal.client.Field;
import com.openshift.internal.client.Severity;
import com.openshift.internal.client.utils.StringUtils;

/**
 * @author Andre Dietisheim
 */
public class Message {

	private String text;
	private ISeverity severity;
	private IField field;
	private int exitCode;

	public Message(String text, String field, String severity, int exitCode) {
		this.text = text;
		this.severity = new Severity(severity);
		this.field = new Field(field);
		this.exitCode = exitCode;
	}

	public IField getField() {
		return field;
	}

	public String getText() {
		return text;
	}

	public ISeverity getSeverity() {
		return severity;
	}

	public int getExitCode() {
		return exitCode;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder(getOperationState());

		if (!StringUtils.isEmpty(field.getValue())) {
			builder.append(" on field \"{0}\",");
		}
		if (!StringUtils.isEmpty(severity.getValue())) {
			builder.append(" severity \"{1}\"");
		}
		if (exitCode != -1) {
			builder.append(" with exit code \"{2}\"");
		}
		builder.append('.');
		if (!StringUtils.isEmpty(text)) {
			builder.append(" Reason given: \"{3}\"");
		}

		return MessageFormat.format(builder.toString(), field, severity.getValue(), exitCode, text);
	}

	private String getOperationState() {
		if (ISeverity.INFO.equals(severity.getValue()) 
				|| ISeverity.DEBUG.equals(severity.getValue())
				|| ISeverity.RESULT.equals(severity.getValue())) {
			return "Operation succeeded";
		} else if (ISeverity.ERROR.equals(severity.getValue())) {
			return "Operation failed";
		} else {
			return "Operation state is " + severity.getValue();
		}
	}
}
