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
package com.openshift.client.utils;

import static org.fest.assertions.Assertions.assertThat;
import static org.junit.Assert.assertEquals;

import org.fest.assertions.AssertExtension;

import com.openshift.client.Message;
import com.openshift.client.Message.Severity;

/**
 * @author Andre Dietisheim
 */
public class MessageAssert implements AssertExtension {

	private Message message;

	public MessageAssert(Message message) {
		this.message = message;
	}

	public MessageAssert hasText() {
		assertThat(message.getText()).isNotEmpty();
		return this;
	}

	public MessageAssert hasText(String text) {
		assertEquals(text, message.getText());
		return this;
	}

	public MessageAssert hasSeverity(Severity severity) {
		assertEquals(severity, message.getSeverity());
		return this;
	}

	public MessageAssert hasExitCode(int exitCode) {
		assertEquals(exitCode, message.getExitCode());
		return this;
	}

	public MessageAssert hasField(String parameter) {
		assertEquals(parameter, message.getField());
		return this;
	}
}
