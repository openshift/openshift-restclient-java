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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Writer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jboss.tools.openshift.express.client.OpenShiftException;
import org.jboss.tools.openshift.express.internal.client.test.fakes.UserConfigurationFake;
import org.jboss.tools.openshift.express.internal.client.utils.StreamUtils;
import org.junit.Test;

/**
 * @author André Dietisheim
 */
public class UserConfigurationTest {

	private static final String USERNAME_REGEX = "[^=]+=(.+)";
	private static final String USERNAME = "dummyUser";
	private static final String ANOTHER_USERNAME = "anotherUser";

	@Test
	public void canReadUsername() throws OpenShiftException, IOException {
		UserConfigurationFake userConfiguration = new UserConfigurationFake() {

			@Override
			protected void initFile(Writer writer) throws IOException {
				writer.append(KEY_RHLOGIN).append('=').append(USERNAME).append('\n');
			}

		};
		assertEquals(USERNAME, userConfiguration.getRhlogin());
	}

	@Test
	public void canStoreUsername() throws OpenShiftException, IOException {
		UserConfigurationFake userConfiguration = new UserConfigurationFake() {

			@Override
			protected void initFile(Writer writer) throws IOException {
				writer.append(KEY_RHLOGIN).append('=').append(USERNAME).append('\n');
			}

		};
		userConfiguration.setRhlogin(ANOTHER_USERNAME);
		userConfiguration.store();
		final File userConfigurationFile = userConfiguration.getFile();
		assertNotNull(userConfigurationFile);
		String fileContent = StreamUtils.readToString(new FileReader(userConfigurationFile));
		Pattern pattern = Pattern.compile(USERNAME_REGEX);
		Matcher matcher = pattern.matcher(fileContent);
		assertTrue(matcher.matches());
		assertEquals(1, matcher.groupCount());
		assertEquals(ANOTHER_USERNAME, matcher.group(1));
	}

	@Test
	public void canStoreAndReadUsername() throws OpenShiftException, IOException {
		UserConfigurationFake userConfiguration = new UserConfigurationFake() {

			@Override
			protected void initFile(Writer writer) throws IOException {
				writer.append(KEY_RHLOGIN).append('=').append(USERNAME).append('\n');
			}

		};
		userConfiguration.setRhlogin(ANOTHER_USERNAME);
		userConfiguration.store();
		final File userConfigurationFile = userConfiguration.getFile();
		assertNotNull(userConfigurationFile);
		UserConfigurationFake userConfiguration2 = new UserConfigurationFake() {

			@Override
			protected File getUserConfigurationFile() throws OpenShiftException, IOException {
				return userConfigurationFile;
			}
		};
		assertEquals(ANOTHER_USERNAME, userConfiguration2.getRhlogin());
	}
}
