/******************************************************************************* 
 * Copyright (c) 2013-2014 Red Hat, Inc. 
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
import static org.junit.Assert.fail;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import com.openshift.client.ApplicationScale;
import com.openshift.client.DeploymentTypes;
import com.openshift.client.IApplication;
import com.openshift.client.IDomain;
import com.openshift.client.IEnvironmentVariable;
import com.openshift.client.IGearGroup;
import com.openshift.client.IOpenShiftConnection;
import com.openshift.client.IUser;
import com.openshift.client.OpenShiftEndpointException;
import com.openshift.client.OpenShiftException;
import com.openshift.client.cartridge.query.LatestVersionOf;
import com.openshift.client.utils.ApplicationTestUtils;
import com.openshift.client.utils.DomainTestUtils;
import com.openshift.client.utils.GearGroupsAssert;
import com.openshift.client.utils.TestConnectionBuilder;

/**
 * @author André Dietisheim
 * @author Syed Iqbal
 * @author Martes G Wigglesworth 
 * @author Jeff Cantrill
 */
public class ApplicationResourceIntegrationTest extends TestTimer {

	private static final long WAIT_TIMEOUT = 3 * 60 * 1000;

	private IUser user;
	private IDomain domain;

	@Before
	public void setUp() throws Exception {
		IOpenShiftConnection connection = new TestConnectionBuilder().defaultCredentials().disableSSLCertificateChecks().create();
		this.user = connection.getUser();
		this.domain = DomainTestUtils.ensureHasDomain(user);
	}

	@Test
	public void shouldReturnGearGroups() throws Exception {
		// pre-conditions
		IApplication application = ApplicationTestUtils.getOrCreateApplication(domain);

		// operation
		Collection<IGearGroup> gearGroups = application.getGearGroups();

		// verification
		assertThat(gearGroups.size()).isGreaterThanOrEqualTo(1);
		assertThat(new GearGroupsAssert(gearGroups))
				.assertGroup(0).hasUUID().hasGears()
				.assertGear(0).hasId().hasState();
	}
	
	@Test
	public void shouldDestroyApplication() throws Exception {
		// pre-condition
		IApplication application = ApplicationTestUtils.getOrCreateApplication(domain);
		assertThat(application.getName()).isNotEmpty();

		// operation
		application.destroy();

		// verification
		assertThat(domain.getApplications()).excludes(application);
	}
	
	@Test
	public void shouldNotFindApplicationByNameIfApplicationIsDestroyed() throws Throwable {
		// pre-conditions
		IApplication application = ApplicationTestUtils.getOrCreateApplication(domain);
		assertThat(application).isNotNull();
		String name = application.getName();
		
		// operation
		application.destroy();

		// verifications
		assertThat(domain.getApplicationByName(name)).isNull();
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

	@Test(expected = OpenShiftEndpointException.class)
	public void shouldNotScaleDownIfNotScaledUpApplication() throws Throwable {
		// pre-condition
		ApplicationTestUtils.silentlyDestroyAllApplications(domain);
		IApplication application = domain.createApplication(
				ApplicationTestUtils.createRandomApplicationName(), LatestVersionOf.php().get(user), ApplicationScale.NO_SCALE);

		// operation
		application.scaleDown();

		// verification
		// there's currently no API to verify the application state
	}

	@Test(expected = OpenShiftEndpointException.class)
	public void shouldNotScaleUpApplication() throws Throwable {
		// pre-condition
		ApplicationTestUtils.silentlyDestroyAllApplications(domain);
		IApplication application = domain.createApplication(
		        ApplicationTestUtils.createRandomApplicationName(), LatestVersionOf.php().get(user), ApplicationScale.NO_SCALE);

		// operation
		application.scaleUp();

		// verification
		// there's currently no API to verify the application state
	}

	public void shouldScaleUpApplication() throws Throwable {
		// pre-condition
		ApplicationTestUtils.silentlyDestroyAllApplications(domain);
		IApplication application = domain.createApplication(
		        ApplicationTestUtils.createRandomApplicationName(), LatestVersionOf.php().get(user), ApplicationScale.SCALE);

		// operation
		application.scaleUp();

		// verification
		// there's currently no API to verify the application state
	}

	@Test
	public void shouldScaleDownApplication() throws Throwable {
		// pre-condition
		ApplicationTestUtils.silentlyDestroyAllApplications(domain);
		IApplication application = domain.createApplication(
		        ApplicationTestUtils.createRandomApplicationName(), LatestVersionOf.php().get(user), ApplicationScale.SCALE);
		application.scaleUp();

		// operation
		application.scaleDown();

		// verification
		// there's currently no API to verify the application state
	}

	@Test
	public void shouldAddAliasToApplication() throws Throwable {
		// pre-condition
		IApplication application = ApplicationTestUtils.getOrCreateApplication(domain);
		String alias = createAlias();
		// operation

		application.addAlias(alias);

		// verification
		assertThat(application.getAliases()).contains(alias);
	}

	@Test
	public void shouldRemoveAliasOfApplication() throws Throwable {
		// pre-condition
		IApplication application = ApplicationTestUtils.getOrCreateApplication(domain);
		String alias = createAlias();
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

	/**
	 * https://issues.jboss.org/browse/JBIDE-13569
	 */
	@Test
	public void shouldWaitForApplication() throws OpenShiftException, MalformedURLException, IOException {
		// pre-condition
		ApplicationTestUtils.destroyIfMoreThan(2, domain);
		long startTime = System.currentTimeMillis();
		IApplication application = domain.createApplication(ApplicationTestUtils.createRandomApplicationName(), LatestVersionOf.php().get(user));

		// operation
		boolean successfull = application.waitForAccessible(WAIT_TIMEOUT);

		if (successfull) {
			assertTrue(System.currentTimeMillis() <= startTime + WAIT_TIMEOUT);
		} else {
			assertTrue(System.currentTimeMillis() >= startTime + WAIT_TIMEOUT);
		}
	}

	/**
	 * https://issues.jboss.org/browse/JBIDE-14721
	 */
	private String createAlias() {
		return new StringBuilder(String.valueOf(System.currentTimeMillis()))
		// valid alias is "/\A[a-z0-9]+(-[a-z0-9]+)*(\.[a-z0-9]+(-[a-z0-9]+)*)+\z/"
		.append('.')
		.append('1')
		.toString();
	}
	
    @Test
	public void shouldAddOneEnvironmentVariable() throws Throwable{
    	//pre-conditions
    	IApplication application = ApplicationTestUtils.getOrCreateApplication(domain);
    	
    	//operation
    	IEnvironmentVariable environmentVariable = application.addEnvironmentVariable("FOO","123");
    	
    	//vaerification
    	assertThat(environmentVariable).isNotNull();
    	assertThat(environmentVariable.getName()).isEqualTo("FOO");
		assertThat(environmentVariable.getValue()).isEqualTo("123");
    }
    
	@Test
	public void shouldAddEnvironmentVariables() throws Throwable {
		// pre-conditions
		IApplication application = ApplicationTestUtils.getOrCreateApplication(domain);
		ApplicationTestUtils.destroyAllEnvironmentVariables(application);
		
		// operation
		Map<String, String> environmentVariables = new HashMap<String, String>();
		environmentVariables.put("X_NAME", "X_VALUE");
		environmentVariables.put("Y_NAME", "Y_VALUE");
		Map<String, IEnvironmentVariable> variables = application.addEnvironmentVariables(environmentVariables);

		// verification
		assertThat(variables.size()).isEqualTo(2);
	}

	@Test
	public void shouldUpdateOneEnvironmentVariable() throws Throwable{
		//pre-conditions
		IApplication application = ApplicationTestUtils.getOrCreateApplication(domain);

		//operation
		IEnvironmentVariable environmentVariable = application.addEnvironmentVariable("FOO","123");
		assertThat(environmentVariable.getValue()).isEqualTo("123");
		application.updateEnvironmentVariable("FOO","321");

		//vaerification
		assertThat(environmentVariable).isNotNull();
		assertThat(environmentVariable.getName()).isEqualTo("FOO");
		assertThat(environmentVariable.getValue()).isEqualTo("321");
	}

	@Test
	public void shouldGetEnvironmentVariableByName() throws Throwable {
		// pre-conditions
		IApplication application = ApplicationTestUtils.getOrCreateApplication(domain);
		ApplicationTestUtils.destroyAllEnvironmentVariables(application);
		application.addEnvironmentVariable("Z_NAME", "Z_VALUE");

		// operation
		IEnvironmentVariable environmentVariable = application.getEnvironmentVariable("Z_NAME");

		// verification
		assertThat(environmentVariable).isNotNull();
		assertThat(environmentVariable.getName()).isEqualTo("Z_NAME");
		assertThat(environmentVariable.getValue()).isEqualTo("Z_VALUE");
	}

	@Test
	public void shouldGetEnvironmentVariableValueByName() throws Throwable {
		// pre-conditions
		IApplication application = ApplicationTestUtils.getOrCreateApplication(domain);
		ApplicationTestUtils.destroyAllEnvironmentVariables(application);
		application.addEnvironmentVariable("Z_NAME", "Z_VALUE");

		// verification
		assertTrue(application.hasEnvironmentVariable("Z_NAME"));
		assertThat(application.getEnvironmentVariableValue("Z_NAME")).isEqualTo("Z_VALUE");
	}

    @Test
	public void shouldRemoveEnvironmentVariable() throws Throwable{
    	//pre-conditions
    	IApplication application = ApplicationTestUtils.getOrCreateApplication(domain);
		ApplicationTestUtils.destroyAllEnvironmentVariables(application);
    	IEnvironmentVariable environmentVariable = application.addEnvironmentVariable("FOOBAR","123");
    	assertThat(application.getEnvironmentVariables().size()).isEqualTo(1);
    	
    	//operation
    	application.removeEnvironmentVariable(environmentVariable.getName());
    	
    	//verification
    	assertThat(application.getEnvironmentVariables().size()).isEqualTo(0);
    	assertThat(application.hasEnvironmentVariable("FOOBAR")).isFalse();
    }
    
    @Test
	public void shouldRemoveEnvironmentVariableByInstance() throws Throwable{
    	//pre-conditions
    	IApplication application = ApplicationTestUtils.getOrCreateApplication(domain);
		ApplicationTestUtils.destroyAllEnvironmentVariables(application);
    	IEnvironmentVariable environmentVariable = application.addEnvironmentVariable("FOOBAR","123");
    	assertThat(application.getEnvironmentVariables().size()).isEqualTo(1);
    	
    	//operation
    	application.removeEnvironmentVariable(environmentVariable);
    	
    	//verification
    	assertThat(application.getEnvironmentVariables().size()).isEqualTo(0);
    	assertThat(application.hasEnvironmentVariable("FOOBAR")).isFalse();
    }

    @Test
	public void shouldNotAddExistingEnvironmentVariableToApplication() throws Throwable {
		// precondition
		IApplication application = ApplicationTestUtils.getOrCreateApplication(domain);
		ApplicationTestUtils.destroyAllEnvironmentVariables(application);
		application.addEnvironmentVariable("A_NAME", "A_VALUE");

		// operation
		try {
			application.addEnvironmentVariable("A_NAME", "A_NEW_VALUE");
			fail("Should not be able to add same variable a 2nd time");
		} catch(OpenShiftException e) {
			// success
		}
	}

	
	@Test
    public void shouldGetMapOfAllEnvironmentVariables() throws Throwable {
        // preconditions
        ApplicationTestUtils.silentlyDestroyAllApplications(domain);
        IApplication application = ApplicationTestUtils.getOrCreateApplication(domain);
        Map<String, String> environmentVariableMap = new HashMap<String, String>();
        environmentVariableMap.put("X_NAME", "X_VALUE");
        environmentVariableMap.put("Y_NAME", "Y_VALUE");
        environmentVariableMap.put("Z_NAME", "Z_VALUE");
        application.addEnvironmentVariables(environmentVariableMap);

        // operation
        Map<String, IEnvironmentVariable> environmentVariables = application.getEnvironmentVariables();

        // verifications
        assertThat(environmentVariables).hasSize(3);
    }
    
	
    @Test
    public void shouldLoadEmptyMapOfEnvironmentVariables() throws Throwable{
        //precondition
        ApplicationTestUtils.silentlyDestroyAllApplications(domain);
        IApplication application = ApplicationTestUtils.getOrCreateApplication(domain);

        //operation
        Map<String, IEnvironmentVariable> environmentVariables = application.getEnvironmentVariables();

        //verifications
        assertThat(environmentVariables).isEmpty();
    }

	@Test
	public void shouldCanGetCanUpdateEnvironmentVariables() throws Throwable {
		// pre-conditions
		IApplication application = ApplicationTestUtils.getOrCreateApplication(domain);

		// operation
        // verifications
		assertThat(application.canUpdateEnvironmentVariables()).isTrue();
		//verify list environment variables
		assertThat(application.canGetEnvironmentVariables()).isTrue();
	}
	
	@Test
	public void shouldGetDeploymentType() throws Throwable {
		// pre-conditions
		IApplication application = ApplicationTestUtils.getOrCreateApplication(domain);

		// operation
        // verifications
		assertThat(application.getDeploymentType()).isIn(DeploymentTypes.getAll());
	}

	@Test
	public void shouldUpdateDeploymentType() throws Throwable {
		// pre-conditions
		IApplication application = ApplicationTestUtils.getOrCreateApplication(domain);
		String deploymentType = application.getDeploymentType();
		String newDeploymentType = DeploymentTypes.switchType(deploymentType);
		
		// operation
		application.setDeploymentType(newDeploymentType);
		// verifications
		assertThat(application.getDeploymentType()).isEqualTo(newDeploymentType);
	}
}
