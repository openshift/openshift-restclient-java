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
package com.openshift.client;

import java.text.MessageFormat;

import com.openshift.internal.client.utils.StringUtils;


/**
 * @author Andre Dietisheim
 */
public class Message {

	public static final String FIELD_DEFAULT = null;
	public static final String FIELD_RESULT = "result";
	public static final String FIELD_APPINFO = "appinfo";
	
	public enum Severity {

		INFO, ERROR, UNKNOWN;

		private static Severity safeValueOf(String severityString) {
			try {
				if (severityString == null) {
					return UNKNOWN;
				}
				return valueOf(severityString.toUpperCase());
			} catch (IllegalArgumentException e) {
				return UNKNOWN;
			}
		}
	}

	private String text;
	private Severity severity;
	private String field;
	private int exitCode;

	public Message(String text, String field, String severity, int exitCode) {
		this.text = text;
		this.severity = Severity.safeValueOf(severity);
		this.field = field;
		this.exitCode = exitCode;
	}

	public String getField() {
		return field;
	}

	public String getText() {
		return text;
	}

	public Severity getSeverity() {
		return severity;
	}

	public int getExitCode() {
		return exitCode;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder(getOperationState());

		if (!StringUtils.isEmpty(field)) {
			builder.append(" on field \"{0}\"");
			if (severity != null) {
				builder.append(", sevirty \"{1}\"");
			}
		}
		if (exitCode != -1) {
			builder.append(" with exit code \"{2}\"");
		}
		builder.append('.');
		if (!StringUtils.isEmpty(text)) {
			builder.append("Reason given: \"{3}\"");
		}

		return MessageFormat.format(builder.toString(), field, severity, exitCode, text);
	}

	private String getOperationState() {
		switch (severity) {
		case ERROR:
			return "Operation failed";
		case INFO:
			return "Operation succeeded";
		case UNKNOWN:
		default:
			return "Operation state is unknown";
		}
	}
}
