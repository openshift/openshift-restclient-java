/*******************************************************************************
 * Copyright (c) 2015 Red Hat, Inc. Distributed under license by Red Hat, Inc.
 * All rights reserved. This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Red Hat, Inc.
 ******************************************************************************/
package com.openshift.internal.util;

import com.openshift.restclient.OpenShiftException;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.TestName;

import java.io.IOException;

/**
 * @author Corey Dailey
 */
public class TestTimer {
	protected long startTime;
	protected long endTime;

	@Rule
	public TestName name = new TestName();

	@Before
	public void startTimer() throws OpenShiftException, IOException {
		this.startTime = 0;
		this.startTime = System.currentTimeMillis();
	}

	@After
	public void endTimer() {
		this.endTime = 0;
		this.endTime = System.currentTimeMillis();
		calcExecTime();


	}

	public void calcExecTime() {
		if (System.getProperty("showTestTimes") != null) {
			System.out.println(this.getClass() +"#"+name.getMethodName() + " : " + (this.endTime - this.startTime));
		}
	}
}
