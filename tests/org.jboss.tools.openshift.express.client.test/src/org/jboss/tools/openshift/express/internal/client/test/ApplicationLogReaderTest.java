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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.jboss.tools.openshift.express.client.ApplicationLogReader;
import org.jboss.tools.openshift.express.client.ICartridge;
import org.jboss.tools.openshift.express.client.IOpenShiftService;
import org.jboss.tools.openshift.express.client.OpenShiftException;
import org.jboss.tools.openshift.express.internal.client.Application;
import org.jboss.tools.openshift.express.internal.client.InternalUser;
import org.jboss.tools.openshift.express.internal.client.test.fakes.NoopOpenShiftServiceFake;
import org.junit.Before;
import org.junit.Test;

/**
 * @author André Dietisheim
 */
public class ApplicationLogReaderTest {

	private static final int LOGREADER_TIMEOUT = 2 * 1024;

	private static final String LOG_HEADER = 
			"tailing /var/lib/libra/f36a4acf8a73450cb98637ed4483ea02/1317146294966//jbossas-7.0/standalone/log/server.log"
					+ "------ Tail of 1317146294966 application server.log ------\n";

	private static final String INITIAL_LOG =
					"11:31:36,289 INFO  [org.jboss.as.ee] (Controller Boot Thread) Activating EE subsystem\n"
					+ "11:31:36,340 INFO  [org.apache.coyote.http11.Http11Protocol] (MSC service thread 1-1) Starting Coyote HTTP/1.1 on http--127.1.9.1-8080\n"
					+ "11:31:36,440 INFO  [org.jboss.as.connector] (MSC service thread 1-4) Starting JCA Subsystem (JBoss IronJacamar 1.0.3.Final)\n"
					+ "11:31:36,471 INFO  [org.jboss.as.connector.subsystems.datasources] (MSC service thread 1-1) Bound data source [java:jboss/datasources/ExampleDS]\n"
					+ "11:31:36,966 INFO  [org.jboss.as.deployment] (MSC service thread 1-3) Started FileSystemDeploymentService for directory /var/lib/libra/b8ea642ed6aa4dd0af2a4fe94c0ff07c/1317137507620/jbossas-7.0/standalone/deployments\n"
					+ "11:31:36,982 INFO  [org.jboss.as] (Controller Boot Thread) JBoss AS 7.0.1.Final \"Zap\" started in 2796ms - Started 82 of 107 services (22 services are passive or on-demand)\n"
					+ "11:31:37,004 INFO  [org.jboss.as.server.deployment] (MSC service thread 1-2) Starting deployment of \"ROOT.war\"\n"
					+ "11:31:37,084 INFO  [org.jboss.as.jpa] (MSC service thread 1-4) added javax.persistence.api dependency to ROOT.war\n"
					+ "11:31:37,403 INFO  [org.jboss.web] (MSC service thread 1-3) registering web context: \n"
					+ "11:31:37,445 INFO  [org.jboss.as.server.controller] (DeploymentScanner-threads - 2) Deployed \"ROOT.war\"\n";

	private static final String LOG_CONTINUATION =
			"11:32:13,187 INFO  [org.apache.catalina.core.AprLifecycleListener] (MSC service thread 1-3) The Apache Tomcat Native library which allows optimal performance in production environments was not found on the java.library.path: /usr/lib/jvm/java-1.6.0-openjdk-1.6.0.0.x86_64/jre/lib/amd64/server:/usr/lib/jvm/java-1.6.0-openjdk-1.6.0.0.x86_64/jre/lib/amd64:/usr/lib/jvm/java-1.6.0-openjdk-1.6.0.0.x86_64/jre/../lib/amd64:/usr/java/packages/lib/amd64:/usr/lib64:/lib64:/lib:/usr/lib\n"
					+ "11:32:13,295 INFO  [org.apache.coyote.http11.Http11Protocol] (MSC service thread 1-1) Starting Coyote HTTP/1.1 on http--127.1.9.1-8080\n"
					+ "11:32:13,344 INFO  [org.jboss.as.connector] (MSC service thread 1-1) Starting JCA Subsystem (JBoss IronJacamar 1.0.3.Final)\n"
					+ "11:32:13,383 INFO  [org.jboss.as.connector.subsystems.datasources] (MSC service thread 1-2) Bound data source [java:jboss/datasources/ExampleDS]\n"
					+ "11:32:13,746 INFO  [org.jboss.as.deployment] (MSC service thread 1-2) Started FileSystemDeploymentService for directory /var/lib/libra/b8ea642ed6aa4dd0af2a4fe94c0ff07c/1317137507620/jbossas-7.0/standalone/deployments\n"
					+ "11:32:13,764 INFO  [org.jboss.as] (Controller Boot Thread) JBoss AS 7.0.1.Final \"Zap\" started in 2736ms - Started 82 of 107 services (22 services are passive or on-demand)\n"
					+ "11:32:13,772 INFO  [org.jboss.as.server.deployment] (MSC service thread 1-4) Starting deployment of \"ROOT.war\"\n"
					+ "11:32:13,868 INFO  [org.jboss.as.jpa] (MSC service thread 1-1) added javax.persistence.api dependency to ROOT.war\n"
					+ "11:32:14,176 INFO  [org.jboss.web] (MSC service thread 1-2) registering web context: \n"
					+ "11:32:14,207 INFO  [org.jboss.as.server.controller] (DeploymentScanner-threads - 2) Deployed \"ROOT.war\"\n";

	private String status = LOG_HEADER + INITIAL_LOG;
	private IOpenShiftService statusService;
	private Application application;

	@Before
	public void setUp() {
		this.statusService = new NoopOpenShiftServiceFake() {

			@Override
			public String getStatus(String applicationName, ICartridge cartridge, InternalUser user)
					throws OpenShiftException {
				return status;
			}
		};

		this.application = new ApplicationLogFake(statusService);
	}

	@Test
	public void logReaderReturnsNewEntriesAfterApplicationRestart() throws Exception {
		ExecutorService executor = null;
		try {
			ApplicationLogReader logReader = application.getLogReader();
			LogReaderRunnable logReaderRunnable = new LogReaderRunnable(logReader, LOGREADER_TIMEOUT);
			executor = Executors.newSingleThreadExecutor();
			executor.submit(logReaderRunnable);

			String log = logReaderRunnable.waitUntilNoNewLogentries();
			assertNotNull(log);
			assertTrue(log.length() > 0);

			application.restart();

			String newLog = logReaderRunnable.waitUntilNoNewLogentries();
			assertNotNull(newLog);
			assertTrue(newLog.length() > 0);
			assertFalse(log.equals(newLog));
			assertEquals(INITIAL_LOG + LOG_CONTINUATION, logReaderRunnable.getLog());
		} finally {
			if (executor != null) {
				executor.shutdownNow();
			}
		}
	}

	private class ApplicationLogFake extends Application {

		private ApplicationLogFake(IOpenShiftService service) {
			super("fakeApplication", ICartridge.JBOSSAS_7, null, service);
		}

		@Override
		public synchronized void restart() throws OpenShiftException {
			status = LOG_CONTINUATION;
			this.notifyAll();
		}
	};
}
