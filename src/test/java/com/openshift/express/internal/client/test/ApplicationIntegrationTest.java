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
import java.util.Iterator;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.openshift.express.client.ApplicationLogReader;
import com.openshift.express.client.Cartridge;
import com.openshift.express.client.HAProxyCartridge;
import com.openshift.express.client.IApplication;
import com.openshift.express.client.ICartridge;
import com.openshift.express.client.IEmbeddableCartridge;
import com.openshift.express.client.IHAProxyApplication;
import com.openshift.express.client.IHttpClient;
import com.openshift.express.client.IJBossASApplication;
import com.openshift.express.client.IJenkinsApplication;
import com.openshift.express.client.INodeJSApplication;
import com.openshift.express.client.IOpenShiftService;
import com.openshift.express.client.IPHPApplication;
import com.openshift.express.client.IPerlApplication;
import com.openshift.express.client.IPythonApplication;
import com.openshift.express.client.IRawApplication;
import com.openshift.express.client.IRubyApplication;
import com.openshift.express.client.InvalidCredentialsOpenShiftException;
import com.openshift.express.client.JBossCartridge;
import com.openshift.express.client.JenkinsCartridge;
import com.openshift.express.client.NodeJSCartridge;
import com.openshift.express.client.OpenShiftException;
import com.openshift.express.client.OpenShiftService;
import com.openshift.express.client.PHPCartridge;
import com.openshift.express.client.PerlCartridge;
import com.openshift.express.client.PythonCartridge;
import com.openshift.express.client.RawCartridge;
import com.openshift.express.client.RubyCartridge;
import com.openshift.express.client.User;
import com.openshift.express.client.configuration.OpenShiftConfiguration;
import com.openshift.express.internal.client.ApplicationInfo;
import com.openshift.express.internal.client.EmbeddableCartridge;
import com.openshift.express.internal.client.UserInfo;
import com.openshift.express.internal.client.test.fakes.TestUser;
import com.openshift.express.internal.client.test.utils.ApplicationUtils;
import com.openshift.express.internal.client.utils.StreamUtils;

/**
 * @author André Dietisheim
 */
public class ApplicationIntegrationTest {
	
	private static final int WAIT_FOR_APPLICATION = 10 * 1024;

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
	
	@Test(expected=OpenShiftException.class)
	public void canCreateBogusApplication() throws Exception {
		List<ICartridge> cartridges = service.getCartridges(user);
		
		Iterator<ICartridge> i = cartridges.iterator();
		while (i.hasNext()){
			ICartridge cartridge = i.next();
		}
		
		String applicationName = ApplicationUtils.createRandomApplicationName();
		
		ICartridge bogus = new Cartridge("bogus-1.0");
		IApplication application = service.createApplication(applicationName, bogus, user);
	}

	//@Test(expected = InvalidCredentialsOpenShiftException.class)
	public void createApplicationWithInvalidCredentialsThrowsException() throws Exception {
		service.createApplication(ApplicationUtils.createRandomApplicationName(), ICartridge.JBOSSAS_7, invalidUser);
	}

	@Test
	public void canCreateJBossApplication() throws Exception {
		String applicationName = ApplicationUtils.createRandomApplicationName();
		IJBossASApplication application = null;
		try {
			application = service.createJBossASApplication(applicationName, user);
			assertNotNull(application);
			assertEquals(applicationName, application.getName());
			assertTrue(application.getCartridge() instanceof JBossCartridge);
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			ApplicationUtils.silentlyDestroyApplication(applicationName, application.getCartridge(), user, service);
		}
	}
	
	@Test
	public void canCreateRubyApplication() throws Exception {
		String applicationName = ApplicationUtils.createRandomApplicationName();
		IRubyApplication application = null;
		try {
			application = service.createRubyApplication(applicationName, user);
			assertNotNull(application);
			assertEquals(applicationName, application.getName());
			assertTrue(application.getCartridge() instanceof RubyCartridge);
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			ApplicationUtils.silentlyDestroyApplication(applicationName, application.getCartridge(), user, service);
		}
	}
	
	@Test
	public void canCreateHAProxyApplication() throws Exception {
		String applicationName = ApplicationUtils.createRandomApplicationName();
		IHAProxyApplication application = null;
		try {
			application = service.createHAProxyApplication(applicationName, user);
			assertNotNull(application);
			assertEquals(applicationName, application.getName());
			assertTrue(application.getCartridge() instanceof HAProxyCartridge);
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			ApplicationUtils.silentlyDestroyApplication(applicationName, application.getCartridge(), user, service);
		}
	}
	
	@Test
	public void canRawProxyApplication() throws Exception {
		String applicationName = ApplicationUtils.createRandomApplicationName();
		IRawApplication application = null;
		try {
			application = service.createRawApplication(applicationName, user);
			assertNotNull(application);
			assertEquals(applicationName, application.getName());
			assertTrue(application.getCartridge() instanceof RawCartridge);
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			ApplicationUtils.silentlyDestroyApplication(applicationName, application.getCartridge(), user, service);
		}
	}
	
	@Test
	public void canCreatePythonApplication() throws Exception {
		String applicationName = ApplicationUtils.createRandomApplicationName();
		IPythonApplication application = null;
		try {
			application = service.createPythonApplication(applicationName, user);
			assertNotNull(application);
			assertEquals(applicationName, application.getName());
			assertTrue(application.getCartridge() instanceof PythonCartridge);
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			ApplicationUtils.silentlyDestroyApplication(applicationName, application.getCartridge(), user, service);
		}
	}
	
	@Test
	public void canCreatePHPApplication() throws Exception {
		String applicationName = ApplicationUtils.createRandomApplicationName();
		IPHPApplication application = null;
		try {
			application = service.createPHPApplication(applicationName, user);
			assertNotNull(application);
			assertEquals(applicationName, application.getName());
			assertTrue(application.getCartridge() instanceof PHPCartridge);
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			ApplicationUtils.silentlyDestroyApplication(applicationName, application.getCartridge(), user, service);
		}
	}
	
	@Test
	public void canCreatePerlApplication() throws Exception {
		String applicationName = ApplicationUtils.createRandomApplicationName();
		IPerlApplication application = null;
		try {
			application = service.createPerlApplication(applicationName, user);
			assertNotNull(application);
			assertEquals(applicationName, application.getName());
			assertTrue(application.getCartridge() instanceof PerlCartridge);
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			ApplicationUtils.silentlyDestroyApplication(applicationName, application.getCartridge(), user, service);
		}
	}
	
	@Test
	public void canCreateNodeJSApplication() throws Exception {
		String applicationName = ApplicationUtils.createRandomApplicationName();
		INodeJSApplication application = null;
		try {
			application = service.createNodeJSApplication(applicationName, user);
			assertNotNull(application);
			assertEquals(applicationName, application.getName());
			assertTrue(application.getCartridge() instanceof NodeJSCartridge);
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			ApplicationUtils.silentlyDestroyApplication(applicationName, application.getCartridge(), user, service);
		}
	}
	
	@Test
	public void canCreateJenkinsApplication() throws Exception {
		String applicationName = ApplicationUtils.createRandomApplicationName();
		IJenkinsApplication application = null;
		try {
			application = service.createJenkinsApplication(applicationName, user);
			assertNotNull(application);
			assertEquals(applicationName, application.getName());
			assertTrue(application.getCartridge() instanceof JenkinsCartridge);
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			ApplicationUtils.silentlyDestroyApplication(applicationName, application.getCartridge(), user, service);
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
		IJBossASApplication application = null;
		try {
			application = service.createJBossASApplication(applicationName, user);
			assertNotNull(application);
			assertEquals(applicationName, application.getName());
			assertTrue(application.getCartridge() instanceof JBossCartridge);
			
			String logFile = application.threadDump();
			
			String log = service.getStatus(applicationName, application.getCartridge(), user, logFile, 100);
				
			assertTrue("Failed to retrieve logged thread dump", log.contains("new generation"));
			
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			ApplicationUtils.silentlyDestroyApplication(applicationName, application.getCartridge(), user, service);
			
			if (reader != null)
				reader.close();
		}
	}
	
	@Test
	public void canThreadDumpRackApplication() throws Exception {
		String applicationName = ApplicationUtils.createRandomApplicationName();
		ApplicationLogReader reader = null;
		InputStream urlStream = null;
		IRubyApplication application = null;
		try {
			application = service.createRubyApplication(applicationName, user);
			assertNotNull(application);
			assertEquals(applicationName, application.getName());
			assertTrue(application.getCartridge() instanceof RubyCartridge);
			
			URL url = new URL("http://" + applicationName + "-" + user.getDomain().getNamespace() + ".dev.rhcloud.com/lobster");
			
			Thread.sleep(20 * 1000);
			
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			
			//Need to hit the app to start the Rack/Ruby process
			String result = StreamUtils.readToString(connection.getInputStream());
			
			String logFile = application.threadDump();
			
			logFile = "logs/error_log-20120229-000000-EST";
				
			String log = service.getStatus(applicationName, application.getCartridge(), user, logFile, 100);
			
			assertTrue("Failed to retrieve logged thread dump", log.contains("passenger-3.0.4"));
			
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			ApplicationUtils.silentlyDestroyApplication(applicationName, application.getCartridge(), user, service);
			
			if (reader != null)
				reader.close();
			
			if (urlStream != null)
				urlStream.close();
		}
	}
	
	@Test
	public void canWaitForApplication() throws OpenShiftException, MalformedURLException, IOException {
		String applicationName = null;
		IApplication application = null;
		try {
			applicationName = ApplicationUtils.createRandomApplicationName();
			application = service.createJBossASApplication(applicationName, user);
			assertNotNull(application);
			
			assertTrue(application.waitForAccessible(WAIT_FOR_APPLICATION));
			
		} finally {
			ApplicationUtils.silentlyDestroyApplication(applicationName, application.getCartridge(), user, service);
		}
	}
}
