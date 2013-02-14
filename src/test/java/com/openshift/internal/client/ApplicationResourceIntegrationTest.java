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
package com.openshift.internal.client;

import static org.fest.assertions.Assertions.assertThat;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.net.MalformedURLException;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.openshift.client.ApplicationScale;
import com.openshift.client.IApplication;
import com.openshift.client.ICartridge;
import com.openshift.client.IDomain;
import com.openshift.client.IGearProfile;
import com.openshift.client.IOpenShiftConnection;
import com.openshift.client.IUser;
import com.openshift.client.OpenShiftEndpointException;
import com.openshift.client.OpenShiftException;
import com.openshift.client.utils.ApplicationAssert;
import com.openshift.client.utils.ApplicationTestUtils;
import com.openshift.client.utils.DomainTestUtils;
import com.openshift.client.utils.StringUtils;
import com.openshift.client.utils.TestConnectionFactory;

/**
 * @author André Dietisheim
 */
public class ApplicationResourceIntegrationTest {

	private static IDomain domain;

	@Before
	public void setUp() throws Exception {
		try {
			IOpenShiftConnection connection = new TestConnectionFactory().getConnection();
			IUser user = connection.getUser();
			domain = DomainTestUtils.getFirstDomainOrCreate(user);
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	@AfterClass
	public static void cleanup() {
		ApplicationTestUtils.silentlyDestroyAllApplications(domain);
	}
	
	@Test
	public void shouldCreateNonScalableApplication() throws Exception {
	
		ApplicationTestUtils.silentlyDestroy1Application(domain);
		String applicationName =
				ApplicationTestUtils.createRandomApplicationName();
		IApplication application = 
				domain.createApplication(applicationName, ICartridge.JBOSSAS_7);
		assertThat(new ApplicationAssert(application))
				.hasName(applicationName)
				.hasUUID()
				.hasCreationTime()
				.hasCartridge(ICartridge.JBOSSAS_7)
				.hasValidApplicationUrl()
				.hasValidGitUrl()
				.hasValidHealthCheckUrl()
				.hasEmbeddableCartridges()
				.hasAlias();
	}

	@Test
	public void shouldCreateNonScalableApplicationWithSmallGear() throws Exception {
		ApplicationTestUtils.silentlyDestroy1Application(domain);
		String applicationName =
				ApplicationTestUtils.createRandomApplicationName();
		IApplication application = domain.createApplication(
				applicationName, ICartridge.JBOSSAS_7, IGearProfile.SMALL);
		assertThat(new ApplicationAssert(application))
				.hasName(applicationName)
				.hasUUID()
				.hasCreationTime()
				.hasCartridge(ICartridge.JBOSSAS_7)
				.hasValidApplicationUrl()
				.hasValidGitUrl()
				.hasValidHealthCheckUrl()
				.hasEmbeddableCartridges()
				.hasAlias();
	}

	@Ignore("need larger application quota")
	@Test
	public void shouldCreateScalableApplication() throws Exception {
		ApplicationTestUtils.silentlyDestroyAllApplications(domain);
		String applicationName =
				ApplicationTestUtils.createRandomApplicationName();
		IApplication application = domain.createApplication(
				applicationName, ICartridge.JBOSSAS_7, ApplicationScale.SCALE, GearProfile.SMALL);
		assertThat(new ApplicationAssert(application))
				.hasName(applicationName)
				.hasUUID()
				.hasCreationTime()
				.hasCartridge(ICartridge.JBOSSAS_7)
				.hasValidApplicationUrl()
				.hasValidGitUrl()
				.hasValidHealthCheckUrl()
				.hasEmbeddableCartridges()
				.hasAlias();
	}

	@Ignore
	@Test
	public void shouldCreateRubyApplication() throws Exception {
		// String applicationName =
		// ApplicationTestUtils.createRandomApplicationName();
		// IRubyApplication application = null;
		// try {
		// application = service.createRubyApplication(applicationName, user);
		// assertNotNull(application);
		// assertEquals(applicationName, application.getName());
		// assertTrue(application.getCartridge() instanceof RubyCartridge);
		// } catch (Exception e) {
		// e.printStackTrace();
		// throw e;
		// } finally {
		// ApplicationTestUtils.silentlyDestroyApplication(applicationName,
		// application.getCartridge(), user, service);
		// }
	}

	@Ignore
	@Test
	public void shouldCreateHAProxyApplication() throws Exception {
		// String applicationName =
		// ApplicationTestUtils.createRandomApplicationName();
		// IHAProxyApplication application = null;
		// try {
		// application = service.createHAProxyApplication(applicationName,
		// user);
		// assertNotNull(application);
		// assertEquals(applicationName, application.getName());
		// assertTrue(application.getCartridge() instanceof HAProxyCartridge);
		// } catch (Exception e) {
		// e.printStackTrace();
		// throw e;
		// } finally {
		// ApplicationTestUtils.silentlyDestroyApplication(applicationName,
		// application.getCartridge(), user, service);
		// }
	}

	@Ignore
	@Test
	public void shouldRawProxyApplication() throws Exception {
		// String applicationName =
		// ApplicationTestUtils.createRandomApplicationName();
		// IRawApplication application = null;
		// try {
		// application = service.createRawApplication(applicationName, user);
		// assertNotNull(application);
		// assertEquals(applicationName, application.getName());
		// assertTrue(application.getCartridge() instanceof RawCartridge);
		// } catch (Exception e) {
		// e.printStackTrace();
		// throw e;
		// } finally {
		// ApplicationTestUtils.silentlyDestroyApplication(applicationName,
		// application.getCartridge(), user, service);
		// }
	}

	@Ignore
	@Test
	public void shouldCreatePythonApplication() throws Exception {
		// String applicationName =
		// ApplicationTestUtils.createRandomApplicationName();
		// IPythonApplication application = null;
		// try {
		// application = service.createPythonApplication(applicationName, user);
		// assertNotNull(application);
		// assertEquals(applicationName, application.getName());
		// assertTrue(application.getCartridge() instanceof PythonCartridge);
		// } catch (Exception e) {
		// e.printStackTrace();
		// throw e;
		// } finally {
		// ApplicationTestUtils.silentlyDestroyApplication(applicationName,
		// application.getCartridge(), user, service);
		// }
	}

	@Ignore
	@Test
	public void shouldCreatePHPApplication() throws Exception {
		// String applicationName =
		// ApplicationTestUtils.createRandomApplicationName();
		// IPHPApplication application = null;
		// try {
		// application = service.createPHPApplication(applicationName, user);
		// assertNotNull(application);
		// assertEquals(applicationName, application.getName());
		// assertTrue(application.getCartridge() instanceof PHPCartridge);
		// } catch (Exception e) {
		// e.printStackTrace();
		// throw e;
		// } finally {
		// ApplicationTestUtils.silentlyDestroyApplication(applicationName,
		// application.getCartridge(), user, service);
		// }
	}

	@Ignore
	@Test
	public void shouldCreatePerlApplication() throws Exception {
		// String applicationName =
		// ApplicationTestUtils.createRandomApplicationName();
		// IPerlApplication application = null;
		// try {
		// application = service.createPerlApplication(applicationName, user);
		// assertNotNull(application);
		// assertEquals(applicationName, application.getName());
		// assertTrue(application.getCartridge() instanceof PerlCartridge);
		// } catch (Exception e) {
		// e.printStackTrace();
		// throw e;
		// } finally {
		// ApplicationTestUtils.silentlyDestroyApplication(applicationName,
		// application.getCartridge(), user, service);
		// }
	}

	@Ignore
	@Test
	public void shouldCreateNodeJSApplication() throws Exception {
		// String applicationName =
		// ApplicationTestUtils.createRandomApplicationName();
		// INodeJSApplication application = null;
		// try {
		// application = service.createNodeJSApplication(applicationName, user);
		// assertNotNull(application);
		// assertEquals(applicationName, application.getName());
		// assertTrue(application.getCartridge() instanceof NodeJSCartridge);
		// } catch (Exception e) {
		// e.printStackTrace();
		// throw e;
		// } finally {
		// ApplicationTestUtils.silentlyDestroyApplication(applicationName,
		// application.getCartridge(), user, service);
		// }
	}

	@Ignore
	@Test
	public void shouldCreateJenkinsApplication() throws Exception {
		// String applicationName =
		// ApplicationTestUtils.createRandomApplicationName();
		// IJenkinsApplication application = null;
		// try {
		// application = service.createJenkinsApplication(applicationName,
		// user);
		// assertNotNull(application);
		// assertEquals(applicationName, application.getName());
		// assertTrue(application.getCartridge() instanceof JenkinsCartridge);
		// } catch (Exception e) {
		// e.printStackTrace();
		// throw e;
		// } finally {
		// ApplicationTestUtils.silentlyDestroyApplication(applicationName,
		// application.getCartridge(), user, service);
		// }
	}

	@Test
	public void shouldDestroyApplication() throws Exception {
		// pre-condition
		IApplication application = ApplicationTestUtils.getOrCreateApplication(domain);
		assertThat(application.getName()).isNotEmpty();

		// operation
		application.destroy();

		// verification
		assertThat(domain.hasApplicationByName(application.getName())).isFalse();

	}

	@Test(expected = OpenShiftException.class)
	public void createDuplicateApplicationThrowsException() throws Exception {
		IApplication application2 = null;
		try {
			// pre-condition
			IApplication application = ApplicationTestUtils.getOrCreateApplication(domain);

			// operation
			application2 = domain.createApplication(application.getName(), ICartridge.JBOSSAS_7, null, null);
		} finally {
			ApplicationTestUtils.silentlyDestroy(application2);
		}
	}

	@Test
	public void shouldStopApplication() throws Exception {
		// pre-condition
		IApplication application = ApplicationTestUtils.getOrCreateApplication(domain);

		// operation
		application.stop();
	}

	@Test
	public void shouldStartStoppedApplication() throws Exception {
		// pre-condition
		IApplication application = ApplicationTestUtils.getOrCreateApplication(domain);
		application.stop();

		// operation
		application.start();
	}

	@Test
	public void shouldStartStartedApplication() throws Exception {
		// pre-condition
		IApplication application = ApplicationTestUtils.getOrCreateApplication(domain);
		application.start();

		// operation
		application.start();

		// verification
		// there's currently no API to verify the application state
	}

	@Test
	public void shouldStopStoppedApplication() throws Exception {
		// pre-condition
		IApplication application = ApplicationTestUtils.getOrCreateApplication(domain);
		application.stop();

		// operation
		application.stop();

		// verification
		// there's currently no API to verify the application state
	}

	@Test
	public void shouldRestartStartedApplication() throws Exception {
		// pre-condition
		IApplication application = ApplicationTestUtils.getOrCreateApplication(domain);
		application.start();

		// operation
		application.restart();

		// verification
		// there's currently no API to verify the application state
	}

	@Test
	public void shouldRestartStoppedApplication() throws Exception {
		// pre-condition
		IApplication application = ApplicationTestUtils.getOrCreateApplication(domain);
		application.stop();

		// operation
		application.restart();

		// verification
		// there's currently no API to verify the application state
	}

	@Ignore("Unused feature")
	@Test
	public void shouldGetApplicationDescriptor() throws Throwable {

	}

	@Ignore("Need higher quotas on stg")
	@Test
	public void shouldScaleDownApplication() throws Throwable {
		// pre-condition
		IApplication application = ApplicationTestUtils.getOrCreateApplication(domain);

		// operation
		application.scaleDown();

		// verification
		// there's currently no API to verify the application state
	}

	@Test(expected = OpenShiftEndpointException.class)
	public void shouldNotScaleDownApplication() throws Throwable {
		IApplication application = null;
		try {
			// pre-condition
			application = domain.createApplication(
					DomainTestUtils.createRandomName(), ICartridge.JBOSSAS_7, ApplicationScale.NO_SCALE, null);

			// operation
			application.scaleDown();

			// verification
			// there's currently no API to verify the application state
		} finally {
			ApplicationTestUtils.silentlyDestroy(application);
		}
	}

	@Test(expected = OpenShiftEndpointException.class)
	public void shouldNotScaleUpApplication() throws Throwable {
		IApplication application = null;
		try {
			// pre-condition
			application = domain.createApplication(
					DomainTestUtils.createRandomName(), ICartridge.JBOSSAS_7, ApplicationScale.NO_SCALE, null);

			// operation
			application.scaleUp();

			// verification
			// there's currently no API to verify the application state
		} finally {
			ApplicationTestUtils.silentlyDestroy(application);
		}
	}

	@Test
	public void shouldAddAliasToApplication() throws Throwable {
		// pre-condition
		IApplication application = ApplicationTestUtils.getOrCreateApplication(domain);
		String alias = String.valueOf(System.currentTimeMillis());
		// operation

		application.addAlias(alias);

		// verification
		assertThat(application.getAliases()).contains(alias);
	}

	@Test
	public void shouldRemoveAliasOfApplication() throws Throwable {
		// pre-condition
		IApplication application = ApplicationTestUtils.getOrCreateApplication(domain);
		String alias = String.valueOf(System.currentTimeMillis());
		application.addAlias(alias);
		assertThat(application.getAliases()).contains(alias);

		// operation
		application.removeAlias(alias);

		// verification
		assertThat(alias).isNotIn(application.getAliases());
	}

	@Test(expected = OpenShiftEndpointException.class)
	public void shouldNotAddExistingAliasToApplication() throws Throwable {
		// pre-condition
		IApplication application = ApplicationTestUtils.getOrCreateApplication(domain);
		String alias = String.valueOf(System.currentTimeMillis());
		application.addAlias(alias);
		assertThat(application.getAliases()).contains(alias);

		// operation
		application.addAlias(alias);
	}

	@Ignore
	@Test
	public void shouldThreadDumpJBossApplication() throws Exception {
		// String applicationName =
		// ApplicationTestUtils.createRandomApplicationName();
		// ApplicationLogReader reader = null;
		// IJBossASApplication application = null;
		// try {
		// application = service.createJBossASApplication(applicationName,
		// user);
		// assertNotNull(application);
		// assertEquals(applicationName, application.getName());
		// assertTrue(application.getCartridge() instanceof JBossCartridge);
		//
		// String logFile = application.threadDump();
		//
		// String log = service.getStatus(applicationName,
		// application.getCartridge(), user, logFile, 100);
		//
		// assertTrue("Failed to retrieve logged thread dump",
		// log.contains("new generation"));
		//
		// } catch (Exception e) {
		// e.printStackTrace();
		// throw e;
		// } finally {
		// ApplicationTestUtils.silentlyDestroyApplication(applicationName,
		// application.getCartridge(), user, service);
		//
		// if (reader != null)
		// reader.close();
		// }
	}

	@Ignore
	@Test
	public void shouldThreadDumpRackApplication() throws Exception {
		// String applicationName =
		// ApplicationTestUtils.createRandomApplicationName();
		// ApplicationLogReader reader = null;
		// InputStream urlStream = null;
		// IRubyApplication application = null;
		// try {
		// application = service.createRubyApplication(applicationName, user);
		// assertNotNull(application);
		// assertEquals(applicationName, application.getName());
		// assertTrue(application.getCartridge() instanceof RubyCartridge);
		//
		// URL url = new URL("http://" + applicationName + "-" +
		// user.getDomain().getNamespace() + ".dev.rhcloud.com/lobster");
		//
		// Thread.sleep(20 * 1000);
		//
		// HttpURLConnection connection = (HttpURLConnection)
		// url.openConnection();
		//
		// //Need to hit the app to start the Rack/Ruby process
		// String result =
		// StreamUtils.readToString(connection.getInputStream());
		//
		// String logFile = application.threadDump();
		//
		// logFile = "logs/error_log-20120229-000000-EST";
		//
		// String log = service.getStatus(applicationName,
		// application.getCartridge(), user, logFile, 100);
		//
		// assertTrue("Failed to retrieve logged thread dump",
		// log.contains("passenger-3.0.4"));
		//
		// } catch (Exception e) {
		// e.printStackTrace();
		// throw e;
		// } finally {
		// ApplicationTestUtils.silentlyDestroyApplication(applicationName,
		// application.getCartridge(), user, service);
		//
		// if (reader != null)
		// reader.close();
		//
		// if (urlStream != null)
		// urlStream.close();
		// }
	}

	@Test
	public void shouldWaitForApplication() throws OpenShiftException, MalformedURLException, IOException {
		IApplication application = null;
		try {
			// pre-condition
			long startTime = System.currentTimeMillis();
			long timeout = 180 * 1024;
			application = domain.createApplication(StringUtils.createRandomString(), ICartridge.JBOSSAS_7, null, null);

			// operation
			boolean successfull = application.waitForAccessible(timeout);

			if (successfull) {
				assertTrue(System.currentTimeMillis() <= startTime + timeout);
			} else {
				assertTrue(System.currentTimeMillis() >= startTime + timeout);
			}
		} finally {
			ApplicationTestUtils.silentlyDestroy(application);
		}
	}
}
