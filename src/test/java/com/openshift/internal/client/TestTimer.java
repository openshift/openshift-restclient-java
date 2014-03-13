package com.openshift.internal.client;

import com.openshift.client.OpenShiftException;
import com.openshift.client.utils.TestConnectionFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.TestName;

import java.io.IOException;

/**
 * Created by cdaley on 3/13/14.
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
