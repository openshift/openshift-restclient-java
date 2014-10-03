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
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Writer;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Test;

import com.openshift.client.OpenShiftException;
import com.openshift.client.configuration.AbstractOpenshiftConfiguration.ConfigurationOptions;
import com.openshift.client.configuration.DefaultConfiguration;
import com.openshift.client.configuration.IOpenShiftConfiguration;
import com.openshift.client.configuration.SystemConfiguration;
import com.openshift.client.fakes.EmptySystemPropertiesFake;
import com.openshift.client.fakes.SystemConfigurationFake;
import com.openshift.client.fakes.UserConfigurationFake;
import com.openshift.internal.client.utils.StreamUtils;
import com.openshift.internal.client.utils.UrlUtils;

/**
 * @author André Dietisheim
 */
public class ConfigurationTest extends TestTimer {

	private static final String USERNAME_REGEX = "[^=]+=(.+)";
	private static final String USERNAME = "dummyUser";
	private static final String USERNAME2 = "André";
	private static final String USERNAME3 = "Dietisheim";
	private static final String ANOTHER_USERNAME = "anotherUser";
	protected static final String LIBRA_SERVER = "openshift.redhat.com";
	protected static final String SIGLEQUOTED_LIBRA_SERVER = "'openshift.redhat.com'";
	protected static final String DOUBLEQUOTED_LIBRA_SERVER = "\"openshift.redhat.com\"";

	@Test
	public void versionTest() throws OpenShiftException, IOException {
		// operation
		String version = new RestServiceProperties().getVersion();

		// verification
		assertNotNull(version);
		assertFalse(version.contains("pom"));
	}

	@Test
	public void canReadUsername() throws OpenShiftException, IOException {
		UserConfigurationFake userConfiguration = new UserConfigurationFake() {

			protected void initFile(Writer writer) throws IOException {
				writer.append(KEY_RHLOGIN).append('=').append(USERNAME).append('\n');
			}

		};
		assertEquals(USERNAME, userConfiguration.getRhlogin());
	}

	@Test
	public void canStoreUsername() throws OpenShiftException, IOException {
		// pre-condition
		UserConfigurationFake userConfiguration = new UserConfigurationFake() {

			protected void initFile(Writer writer) throws IOException {
				writer.append(KEY_RHLOGIN).append('=').append(USERNAME).append('\n');
			}

		};

		// operation
		userConfiguration.setRhlogin(ANOTHER_USERNAME);
		userConfiguration.save();

		// verification
		File userConfigurationFile = userConfiguration.getFile();
		assertNotNull(userConfigurationFile);
		String fileContent = StreamUtils.readToString(new FileReader(userConfigurationFile));
		Pattern pattern = Pattern.compile(USERNAME_REGEX);
		Matcher matcher = pattern.matcher(fileContent);
		assertTrue(matcher.find());
		assertEquals(1, matcher.groupCount());
		assertEquals(ANOTHER_USERNAME, matcher.group(1));
	}

	@Test
	public void canStoreAndReadUsername() throws OpenShiftException, IOException {
		// pre-condition
		UserConfigurationFake userConfiguration = new UserConfigurationFake() {

			protected void initFile(Writer writer) throws IOException {
				writer.append(KEY_RHLOGIN).append('=').append(USERNAME).append('\n');
			}
		};
		assertEquals(USERNAME, userConfiguration.getRhlogin());

		// operation
		userConfiguration.setRhlogin(ANOTHER_USERNAME);
		userConfiguration.save();
		final File userConfigurationFile = userConfiguration.getFile();
		assertNotNull(userConfigurationFile);
		UserConfigurationFake userConfiguration2 = new UserConfigurationFake() {

			protected File createFile() {
				return userConfigurationFile;
			}

			protected void initFile(File file) {
			}
		};

		// verification
		assertEquals(ANOTHER_USERNAME, userConfiguration2.getRhlogin());
	}

	@Test
	public void canReadUsernameIfItsInSystemConfigurationOnly() throws OpenShiftException, IOException {
		SystemConfiguration systemConfiguration = new SystemConfigurationFake(new DefaultConfiguration()) {

			protected void init(Properties properties) {
				properties.put(KEY_RHLOGIN, USERNAME);
			}

		};
		UserConfigurationFake userConfiguration = new UserConfigurationFake(systemConfiguration);
		assertEquals(USERNAME, userConfiguration.getRhlogin());
	}

	@Test
	public void usernameInUserconfigOverridesUsernameInSystemconfig() throws OpenShiftException, IOException {
		SystemConfiguration systemConfiguration = new SystemConfigurationFake(new DefaultConfiguration()) {

			protected void init(Properties properties) {
				properties.put(KEY_RHLOGIN, USERNAME);
			}

		};
		UserConfigurationFake userConfiguration = new UserConfigurationFake(systemConfiguration) {

			protected void initFile(Writer writer) throws IOException {
				writer.append(KEY_RHLOGIN).append('=').append(USERNAME2).append('\n');
			}

		};
		assertEquals(USERNAME2, userConfiguration.getRhlogin());
	}

	@Test
	public void quotedLibraServerIsReturnedWithoutQuotes() throws OpenShiftException, IOException {
		SystemConfiguration systemConfiguration = new SystemConfigurationFake(new DefaultConfiguration()) {

			protected void init(Properties properties) {
				properties.put(KEY_LIBRA_SERVER, SIGLEQUOTED_LIBRA_SERVER);
			}

		};
		UserConfigurationFake userConfiguration = new UserConfigurationFake(systemConfiguration);
		assertEquals(UrlUtils.SCHEME_HTTPS + LIBRA_SERVER, userConfiguration.getLibraServer());
	}

	@Test
	public void doubleQuotedLibraServerIsReturnedWithoutQuotes() throws OpenShiftException, IOException {
		SystemConfiguration systemConfiguration = new SystemConfigurationFake(new DefaultConfiguration()) {

			protected void init(Properties properties) {
				properties.put(KEY_LIBRA_SERVER, DOUBLEQUOTED_LIBRA_SERVER);
			}

		};
		UserConfigurationFake userConfiguration = new UserConfigurationFake(systemConfiguration);
		assertEquals(UrlUtils.SCHEME_HTTPS + LIBRA_SERVER, userConfiguration.getLibraServer());
	}

	@Test
	public void nonQuotedLibraServerIsReturnedAsIs() throws OpenShiftException, IOException {
		SystemConfiguration systemConfiguration = new SystemConfigurationFake(new DefaultConfiguration()) {

			protected void init(Properties properties) {
				properties.put(KEY_LIBRA_SERVER, LIBRA_SERVER);
			}

		};
		UserConfigurationFake userConfiguration = new UserConfigurationFake(systemConfiguration);
		assertEquals(UrlUtils.SCHEME_HTTPS + LIBRA_SERVER, userConfiguration.getLibraServer());
	}

	@Test
	public void nullLibraServerIsReturnedAsNull() throws OpenShiftException, IOException {
		SystemConfiguration systemConfiguration = new SystemConfigurationFake();
		assertNull(systemConfiguration.getLibraServer());
	}

	@Test
	public void systemPropsOverrideSystemconfig() throws OpenShiftException, IOException {
		SystemConfiguration systemConfiguration = new SystemConfigurationFake(new DefaultConfiguration()) {

			@Override
			protected void init(Properties properties) {
				properties.put(KEY_RHLOGIN, USERNAME);
			}

		};
		UserConfigurationFake userConfiguration = new UserConfigurationFake(systemConfiguration) {

			@Override
			protected void initFile(Writer writer) throws IOException {
				writer.append(KEY_RHLOGIN).append('=').append(USERNAME2).append('\n');
			}

		};
		IOpenShiftConfiguration configuration = new EmptySystemPropertiesFake(userConfiguration);
		configuration.setRhlogin(USERNAME3);
		assertEquals(USERNAME3, configuration.getRhlogin());
	}

	@Test
	public void emptySystemPropertiesDefaultToUserConfig() throws OpenShiftException, IOException {
		SystemConfiguration systemConfiguration = new SystemConfigurationFake(new DefaultConfiguration()) {

			@Override
			protected void init(Properties properties) {
				properties.put(KEY_RHLOGIN, USERNAME);
			}

		};
		UserConfigurationFake userConfiguration = new UserConfigurationFake(systemConfiguration) {

			@Override
			protected void initFile(Writer writer) throws IOException {
				writer.append(KEY_RHLOGIN).append('=').append(USERNAME2).append('\n');
			}

		};
		IOpenShiftConfiguration configuration = new EmptySystemPropertiesFake(userConfiguration);
		assertEquals(USERNAME2, configuration.getRhlogin());
	}

	@Test
	public void fallsBackToDefaultUrl() throws OpenShiftException, IOException {
		IOpenShiftConfiguration configuration = new EmptySystemPropertiesFake(
				new UserConfigurationFake(new SystemConfigurationFake(new DefaultConfiguration())));
		assertNotNull(configuration.getLibraServer());
		assertTrue(configuration.getLibraServer().contains(DefaultConfiguration.LIBRA_SERVER));
		assertNull(configuration.getRhlogin());
	}

	@Test
	public void canReadPassword() throws OpenShiftException, IOException {
		UserConfigurationFake userConfiguration = new UserConfigurationFake() {

			protected void initFile(Writer writer) throws IOException {
				writer.append(KEY_PASSWORD).append('=').append("somePassword").append('\n');
			}

		};
		assertEquals("somePassword", userConfiguration.getPassword());
	}

	@Test
	public void canStripQuotesOfPassword() throws OpenShiftException, IOException {
		UserConfigurationFake userConfiguration = new UserConfigurationFake() {

			protected void initFile(Writer writer) throws IOException {
				writer.append(KEY_PASSWORD).append('=').append("'somePassword'").append('\n');
			}

		};
		assertEquals("somePassword", userConfiguration.getPassword());
	}
	
	@Test
	public void disableBadSSLCiphersShouldDefaultToNo() throws OpenShiftException, IOException {
		// pre-condition
		SystemConfiguration systemConfiguration = new SystemConfigurationFake(new DefaultConfiguration()) {

			@Override
			protected void init(Properties properties) {
				properties.put(KEY_DISABLE_BAD_SSL_CIPHERS, "bingobongo");
			}

		};
		
		// operation
		ConfigurationOptions option = systemConfiguration.getDisableBadSSLCiphers();
		
		// verification
		assertThat(option).isEqualTo(ConfigurationOptions.NO);
	}

	@Test
	public void disableBadSSLCiphersShouldbeYes() throws OpenShiftException, IOException {
		// pre-condition
		SystemConfiguration systemConfiguration = new SystemConfigurationFake(new DefaultConfiguration()) {

			@Override
			protected void init(Properties properties) {
				properties.put(KEY_DISABLE_BAD_SSL_CIPHERS, "\"yes\"");
			}

		};
		
		// operation
		ConfigurationOptions option = systemConfiguration.getDisableBadSSLCiphers();
		
		// verification
		assertThat(option).isEqualTo(ConfigurationOptions.YES);
	}
}
