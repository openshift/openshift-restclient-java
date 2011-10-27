/******************************************************************************* 
 * Copyright (c) 2007 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/
package org.jboss.tools.openshift.express.internal.client.test;

import static org.junit.Assert.fail;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;

import org.jboss.tools.openshift.express.client.ApplicationLogReader;

public class LogReaderRunnable implements Runnable {

	private ApplicationLogReader logReader;
	private long timeout;
	private BlockingQueue<Character> logQueue = new LinkedBlockingDeque<Character>();
	private StringBuilder logBuilder = new StringBuilder();
	
	public LogReaderRunnable(ApplicationLogReader logReader, long timeout) {
		this.logReader = logReader;
		this.timeout = timeout;
	}

	@Override
	public void run() {
		try {
			for (int data = -1; (data = logReader.read()) != -1;) {
				logQueue.put((char) data);
			}
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}

	private boolean waitForNewLogentries(StringBuilder builder) throws InterruptedException {
		Character character = logQueue.poll(timeout, TimeUnit.MILLISECONDS);
		boolean isNewEntry = character != null;
		if (isNewEntry) {
			builder.append(character);
			logBuilder.append(character);
		}
		return isNewEntry;
	}

	public String waitUntilNoNewLogentries() throws InterruptedException {
		StringBuilder builder = new StringBuilder();
		while (waitForNewLogentries(builder)) {
			;
		}
		return builder.toString();
	}

	public String getLog() {
		return logBuilder.toString();
	}
}