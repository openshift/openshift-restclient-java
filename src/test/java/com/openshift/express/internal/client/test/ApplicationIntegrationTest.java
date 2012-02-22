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
package com.openshift.express.internal.client.test;

import static com.openshift.express.internal.client.test.utils.ApplicationAsserts.assertApplicationUrl;
import static com.openshift.express.internal.client.test.utils.ApplicationAsserts.assertGitUri;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;

import org.junit.Before;
import org.junit.Test;

import com.openshift.express.client.ApplicationLogReader;
import com.openshift.express.client.IApplication;
import com.openshift.express.client.ICartridge;
import com.openshift.express.client.IHttpClient;
import com.openshift.express.client.IJBossASApplication;
import com.openshift.express.client.IOpenShiftService;
import com.openshift.express.client.IRubyApplication;
import com.openshift.express.client.InvalidCredentialsOpenShiftException;
import com.openshift.express.client.OpenShiftException;
import com.openshift.express.client.OpenShiftService;
import com.openshift.express.client.User;
import com.openshift.express.client.configuration.OpenShiftConfiguration;
import com.openshift.express.internal.client.ApplicationInfo;
import com.openshift.express.internal.client.UserInfo;
import com.openshift.express.internal.client.test.fakes.TestUser;
import com.openshift.express.internal.client.test.utils.ApplicationUtils;
import com.openshift.express.internal.client.utils.StreamUtils;

/**
 * @author André Dietisheim
 */
public class ApplicationIntegrationTest {

	private IOpenShiftService service;

	private User user;
	private User invalidUser;
	
	@Before
	public void setUp() throws OpenShiftException, IOException {
//		UserConfiguration userConfiguration = new UserConfiguration(new SystemConfiguration(new DefaultConfiguration()));
		service = new OpenShiftService(TestUser.ID, new OpenShiftConfiguration().getLibraServer());
		service.setEnableSSLCertChecks(Boolean.parseBoolean(System.getProperty("enableSSLCertChecks")));
		
		user = new TestUser(service);
		invalidUser = new TestUser("bogusPassword", service);
	}

	@Test(expected = InvalidCredentialsOpenShiftException.class)
	public void createApplicationWithInvalidCredentialsThrowsException() throws Exception {
		service.createApplication(ApplicationUtils.createRandomApplicationName(), ICartridge.JBOSSAS_7, invalidUser);
	}

	@Test
	public void canCreateApplication() throws Exception {
		String applicationName = ApplicationUtils.createRandomApplicationName();
		try {
			ICartridge cartridge = ICartridge.JBOSSAS_7;
			IApplication application = service.createApplication(applicationName, cartridge, user);
			assertNotNull(application);
			assertEquals(applicationName, application.getName());
			assertEquals(cartridge, application.getCartridge());
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			ApplicationUtils.silentlyDestroyAS7Application(applicationName, user, service);
		}
	}
	
	@Test
	public void canCreatePHPApplication() throws Exception {
		String applicationName = ApplicationUtils.createRandomApplicationName();
		ICartridge cartridge = null;
		try {
			cartridge = ICartridge.PHP_53;
			IApplication application = service.createApplication(applicationName, cartridge, user);
			assertNotNull(application);
			assertEquals(applicationName, application.getName());
			assertEquals(cartridge, application.getCartridge());
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			//service.destroyApplication(applicationName, cartridge, invalidUser);
			//ApplicationUtils.silentlyDestroyAS7Application(applicationName, user, service);
		}
	}


	@Test
	public void canDestroyApplication() throws Exception {
		String applicationName = ApplicationUtils.createRandomApplicationName();
		service.createApplication(applicationName, ICartridge.JBOSSAS_7, user);
		service.destroyApplication(applicationName, ICartridge.JBOSSAS_7, user);
	}

	@Test(expected = OpenShiftException.class)
	public void createDuplicateApplicationThrowsException() throws Exception {
		String applicationName = ApplicationUtils.createRandomApplicationName();
		try {
			service.createApplication(applicationName, ICartridge.JBOSSAS_7, user);
			service.createApplication(applicationName, ICartridge.JBOSSAS_7, user);
		} finally {
			ApplicationUtils.silentlyDestroyAS7Application(applicationName, user, service);
		}
	}

	@Test
	public void canStopApplication() throws Exception {
		String applicationName = ApplicationUtils.createRandomApplicationName();
		try {
			service.createApplication(applicationName, ICartridge.JBOSSAS_7, user);
			service.stopApplication(applicationName, ICartridge.JBOSSAS_7, user);
		} finally {
			ApplicationUtils.silentlyDestroyAS7Application(applicationName, user, service);
		}
	}

	@Test
	public void canStartStoppedApplication() throws Exception {
		String applicationName = ApplicationUtils.createRandomApplicationName();
		try {
			service.createApplication(applicationName, ICartridge.JBOSSAS_7, user);
			service.stopApplication(applicationName, ICartridge.JBOSSAS_7, user);
			service.startApplication(applicationName, ICartridge.JBOSSAS_7, user);
		} finally {
			ApplicationUtils.silentlyDestroyAS7Application(applicationName, user, service);
		}
	}

	@Test
	public void canStartStartedApplication() throws Exception {
		String applicationName = ApplicationUtils.createRandomApplicationName();
		try {
			/**
			 * freshly created apps are started
			 * 
			 * @link 
			 *       https://github.com/openshift/os-client-tools/blob/master/express
			 *       /doc/API
			 */
			service.createApplication(applicationName, ICartridge.JBOSSAS_7, user);
			service.startApplication(applicationName, ICartridge.JBOSSAS_7, user);
		} finally {
			ApplicationUtils.silentlyDestroyAS7Application(applicationName, user, service);
		}
	}

	@Test
	public void canStopStoppedApplication() throws Exception {
		String applicationName = ApplicationUtils.createRandomApplicationName();
		try {
			/**
			 * freshly created apps are started
			 * 
			 * @link 
			 *       https://github.com/openshift/os-client-tools/blob/master/express
			 *       /doc/API
			 */
			service.createApplication(applicationName, ICartridge.JBOSSAS_7, user);
			service.stopApplication(applicationName, ICartridge.JBOSSAS_7, user);
			service.stopApplication(applicationName, ICartridge.JBOSSAS_7, user);
		} finally {
			ApplicationUtils.silentlyDestroyAS7Application(applicationName, user, service);
		}
	}

	@Test
	public void canRestartApplication() throws Exception {
		String applicationName = ApplicationUtils.createRandomApplicationName();
		try {
			/**
			 * freshly created apps are started
			 * 
			 * @link 
			 *       https://github.com/openshift/os-client-tools/blob/master/express
			 *       /doc/API
			 */
			service.createApplication(applicationName, ICartridge.JBOSSAS_7, user);
			service.restartApplication(applicationName, ICartridge.JBOSSAS_7, user);
		} finally {
			ApplicationUtils.silentlyDestroyAS7Application(applicationName, user, service);
		}
	}

	@Test
	public void canGetStatus() throws Exception {
		String applicationName = ApplicationUtils.createRandomApplicationName();
		try {
			IApplication application = service.createApplication(applicationName, ICartridge.JBOSSAS_7, user);
			String applicationStatus = service.getStatus(application.getName(), application.getCartridge(), user);
			assertNotNull(applicationStatus);
		} finally {
			ApplicationUtils.silentlyDestroyAS7Application(applicationName, user, service);
		}
	}

	@Test
	public void returnsValidGitUri() throws Exception {
		String applicationName = ApplicationUtils.createRandomApplicationName();
		try {
			IApplication application = service.createApplication(applicationName, ICartridge.JBOSSAS_7, user);
			String gitUri = application.getGitUri();
			assertNotNull(gitUri);
			assertGitUri(applicationName, gitUri);
		} finally {
			ApplicationUtils.silentlyDestroyAS7Application(applicationName, user, service);
		}
	}

	@Test
	public void returnsValidApplicationUrl() throws Exception {
		String applicationName = ApplicationUtils.createRandomApplicationName();
		try {
			IApplication application = service.createApplication(applicationName, ICartridge.JBOSSAS_7, user);
			String applicationUrl = application.getApplicationUrl();
			assertNotNull(applicationUrl);
			assertApplicationUrl(applicationName, applicationUrl);
		} finally {
			ApplicationUtils.silentlyDestroyAS7Application(applicationName, user, service);
		}
	}

	@Test
	public void returnsCreationTime() throws Exception {
		String applicationName = ApplicationUtils.createRandomApplicationName();
		try {
			IApplication application = service.createApplication(applicationName, ICartridge.JBOSSAS_7, user);
			Date creationTime = application.getCreationTime();
			assertNotNull(creationTime);
			assertTrue(creationTime.compareTo(new Date()) == -1);
		} finally {
			ApplicationUtils.silentlyDestroyAS7Application(applicationName, user, service);
		}
	}

	/**
	 * This tests checks if the creation time is returned in the 2nd
	 * application. The creation time is only available in the
	 * {@link ApplicationInfo} which is held by the {@link UserInfo}. The
	 * UserInfo is fetched when the 1st application is created and then stored.
	 * The 2nd application has therefore to force the user to refresh the user
	 * info.
	 * 
	 * @throws Exception
	 * 
	 * @see UserInfo
	 * @see ApplicationInfo
	 */
	@Test
	public void returnsCreationTimeOn2ndApplication() throws Exception {
		String applicationName = null;
		String applicationName2 = null;
		try {
			applicationName = ApplicationUtils.createRandomApplicationName();
			IApplication application = user.createApplication(applicationName, ICartridge.JBOSSAS_7);
			Date creationTime = application.getCreationTime();
			assertNotNull(creationTime);
			applicationName2 = ApplicationUtils.createRandomApplicationName();
			IApplication application2 = user.createApplication(applicationName2, ICartridge.JBOSSAS_7);
			Date creationTime2 = application2.getCreationTime();
			assertNotNull(creationTime2);
			assertTrue(creationTime.compareTo(creationTime2) == -1);
		} finally {
			ApplicationUtils.silentlyDestroyAS7Application(applicationName, user, service);
			ApplicationUtils.silentlyDestroyAS7Application(applicationName2, user, service);
		}
	}
	
	@Test
	public void canThreadDumpJBossApplication() throws Exception {
		String applicationName = ApplicationUtils.createRandomApplicationName();
		ApplicationLogReader reader = null;
		try {
			ICartridge cartridge = ICartridge.JBOSSAS_7;
			IJBossASApplication application = (IJBossASApplication)service.createApplication(applicationName, cartridge, user);
			assertNotNull(application);
			assertEquals(applicationName, application.getName());
			assertEquals(cartridge, application.getCartridge());
			
			String logFile = application.threadDump();
			
			String log = service.getStatus(applicationName, cartridge, user, logFile, 100);
			
			assertTrue("Failed to retrieve logged thread dump", log.contains("object space"));
			
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			ApplicationUtils.silentlyDestroyAS7Application(applicationName, user, service);
			
			if (reader != null)
				reader.close();
		}
	}
	
	@Test
	public void canThreadDumpRackApplication() throws Exception {
		String applicationName = ApplicationUtils.createRandomApplicationName();
		ApplicationLogReader reader = null;
		InputStream urlStream = null;
		try {
			ICartridge cartridge = ICartridge.RUBY_18;
			IRubyApplication application = (IRubyApplication)service.createApplication(applicationName, cartridge, user);
			assertNotNull(application);
			assertEquals(applicationName, application.getName());
			assertEquals(cartridge, application.getCartridge());
			
			URL url = new URL("http://" + applicationName + "-" + user.getDomain().getNamespace() + ".dev.rhcloud.com/lobster");
			
			Thread.sleep(20 * 1000);
			
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			
			//Need to hit the app to start the Rack/Ruby process
			String result = StreamUtils.readToString(connection.getInputStream());
			
			String logFile = application.threadDump();
			
			String log = service.getStatus(applicationName, cartridge, user, logFile, 100);
			
			assertTrue("Failed to retrieve logged thread dump", log.contains("passenger-3.0.4"));
			
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			ApplicationUtils.silentlyDestroyRubyApplication(applicationName, user, service);
			
			if (reader != null)
				reader.close();
			
			if (urlStream != null)
				urlStream.close();
		}
	}
	
	@Test
	public void canWaitForApplication() throws OpenShiftException, MalformedURLException, IOException {
		String applicationName = null;
		try {
			applicationName = ApplicationUtils.createRandomApplicationName();
			IApplication application = user.createApplication(applicationName, ICartridge.JBOSSAS_7);
			assertNotNull(application);
			long waitStartTime = System.currentTimeMillis();
			assertTrue(application.waitForAccessible(10 * 1024));

			assertTrue(System.currentTimeMillis() > waitStartTime);
			String applicationUrl = application.getApplicationUrl();
			assertNotNull(applicationUrl);
			HttpURLConnection connection = (HttpURLConnection) new URL(applicationUrl).openConnection();
			assertEquals(IHttpClient.RESPONSE_CODE_OK, connection.getResponseCode());
			String applicationResponse = StreamUtils.readToString(connection.getInputStream());
			assertNotNull(applicationResponse);
			
		} finally {
			ApplicationUtils.silentlyDestroyAS7Application(applicationName, user, service);
		}
	}
}
