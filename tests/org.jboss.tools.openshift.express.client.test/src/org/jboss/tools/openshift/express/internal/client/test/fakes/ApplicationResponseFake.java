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
package org.jboss.tools.openshift.express.internal.client.test.fakes;

import java.util.Date;

import org.jboss.tools.openshift.express.client.ICartridge;

/**
 * @author André Dietisheim
 */
public class ApplicationResponseFake {

	public static final String RHLOGIN = "jbosstools@redhat.com";
	public static final String PASSWORD = "$!445password%&";
	public static final String UUID = "0123456789abcdefghijklmnopqrstuvwz";
	public static final String SSHPUBLICKEY = "sshpublicKey&!%$123456";
	
	public static final String RHC_DOMAIN = "rhcloud.com";
	public static final String NAMESPACE = "1315839296868";

	public static final String APPLICATION_NAME = "1316010645406";
	public static final ICartridge APPLICATION_CARTRIDGE = ICartridge.JBOSSAS_7;

	public static final String APPLICATION_UUID = "0123456789abcdefg";
	public static final String APPLICATION_EMBEDDED = null;
	public static final Date APPLICATION_CREATIONTIME = new Date();
	
	public static final String appResponse =
			"{"
					+ "	\"messages\":\"\","
					+ "	\"debug\":\"Validating application limit toolsjboss@gmail.com: num of apps(0) must be < app limit(5)\n\","
					+ "	\"data\":{"
					+ "		\"health_check_path\":\"health\""
					+ "	},"
					+ "	\"api\":\"1.1.1\","
					+ "	\"api_c\":[\"placeholder\"],"
					+ "	\"result\":\"Successfully created application: "

					+ APPLICATION_NAME

					+ "\","
					+ "	\"broker\":\"1.1.1\","
					+ "	\"broker_c\":[\"namespace\","
					+ "	\"rhlogin\","
					+ "	\"ssh\","
					+ "	\"app_uuid\","
					+ "	\"debug\","
					+ "	\"alter\","
					+ "	\"cartridge\","
					+ "	\"cart_type\","
					+ "	\"action\","
					+ "	\"app_name\","
					+ "	\"api\"],"
					+ "	\"exit_code\":0"
					+ "}";




	public static final String log =
			"10:30:38,700 INFO  [org.apache.catalina.core.AprLifecycleListener] (MSC service thread 1-1) "
					+ "The Apache Tomcat Native library which allows optimal performance in production environments was not found on the java.library.path:"
					+ "/usr/lib/jvm/java-1.6.0-openjdk-1.6.0.0.x86_64/jre/lib/amd64/server:/usr/lib/jvm/java-1.6.0-openjdk-1.6.0.0.x86_64/jre/lib/amd64:"
					+ "/usr/lib/jvm/java-1.6.0-openjdk-1.6.0.0.x86_64/jre/../lib/amd64:/usr/java/packages/lib/amd64:/usr/lib64:/lib64:/lib:/usr/lib\n"
					+ "10:30:38,792 INFO  [org.apache.coyote.http11.Http11Protocol] (MSC service thread 1-3) Starting Coyote HTTP/1.1 on http--127.1.7.1-8080\n"
					+ "10:30:38,836 INFO  [org.jboss.as.connector] (MSC service thread 1-4) Starting JCA Subsystem (JBoss IronJacamar 1.0.3.Final)\n"
					+ "10:30:38,892 INFO  [org.jboss.as.connector.subsystems.datasources] (MSC service thread 1-1) Bound data source [java:jboss/datasources/ExampleDS]\n"
					+ "10:30:39,293 INFO  [org.jboss.as.deployment] (MSC service thread 1-2) Started FileSystemDeploymentService for directory /var/lib/libra/664e4d4dbce74c69ac321053149546df/1316010645406/jbossas-7.0/standalone/deployments\n"
					+ "10:30:39,314 INFO  [org.jboss.as] (Controller Boot Thread) JBoss AS 7.0.1.Final \\\"Zap\\\" started in 2732ms - Started 82 of 107 services (22 services are passive or on-demand)\n"
					+ "10:30:39,339 INFO  [org.jboss.as.server.deployment] (MSC service thread 1-3) Starting deployment of \\\"ROOT.war\\\"\n"
					+ "10:30:39,424 INFO  [org.jboss.as.jpa] (MSC service thread 1-1) added javax.persistence.api dependency to ROOT.war\n"
					+ "10:30:39,700 INFO  [org.jboss.web] (MSC service thread 1-2) registering web context: \n"
					+ "10:30:39,742 INFO  [org.jboss.as.server.controller] (DeploymentScanner-threads - 2) Deployed \\\"ROOT.war\\\"\n";

	public static final String tail =
			"tailing /var/lib/libra/664e4d4dbce74c69ac321053149546df/"

					+ APPLICATION_NAME

					+ "//"

					+ APPLICATION_CARTRIDGE

					+ "/standalone/log/server.log\n"
					+ "------ Tail of 1316010645406 application server.log ------\n"

					+ log;

	public static final String statusResponse =
			"{\"messages\":\"\","
					+ "\"debug\":\"\","
					+ "\"data\":null,"
					+ "\"api\":\"1.1.1\","
					+ "\"api_c\":[\"placeholder\"],"
					+ "\"result\":\""

					+ tail

					+ "\","
					+ "\"broker\":\"1.1.1\","
					+ "\"broker_c\":[\"namespace\",\"rhlogin\",\"ssh\",\"app_uuid\",\"debug\",\"alter\",\"cartridge\",\"cart_type\",\"action\",\"app_name\",\"api\"],"
					+ "\"exit_code\":0}";

}
