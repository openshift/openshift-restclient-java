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
package org.jboss.tools.openshift.express.internal.client.test.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.text.MessageFormat;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.datatype.DatatypeConfigurationException;

import org.jboss.tools.openshift.express.client.IApplication;
import org.jboss.tools.openshift.express.client.OpenShiftException;
import org.jboss.tools.openshift.express.client.utils.RFC822DateUtils;

/**
 * @author André Dietisheim
 */
public class ApplicationAsserts {

	/**
	 * $1 = application uuid
	 * $2 = application name
	 * $3 = InternalUser uuid
	 * $4 = rhc cloud domain (rhcloud.com)
	 * $5 = application name 
	 */
	public static final Pattern GIT_URI_REGEXP = Pattern.compile("ssh://(.+)@(.+)-([^\\.]+)\\.(.+)/~/git/(.+).git/");

	public static final Pattern APPLICATION_URL_REGEXP = Pattern.compile("http://(.+)-([^\\.]+)\\.(.+)/");

	public static void assertThatContainsApplication(String applicationName, String embedded, String applicationUUID,
			String cartridgeName, String creationTime, List<IApplication> applications) throws OpenShiftException {
		IApplication application = getApplication(applicationName, applications);
		if (application == null) {
			fail(MessageFormat.format("Could not find application with name \"{0}\"", applicationName));
		}
		assertApplication(applicationName, applicationUUID, cartridgeName, embedded, creationTime, application);
	}

	public static void assertThatContainsApplication(String applicationName, List<IApplication> applications) {
		assertNotNull(getApplication(applicationName, applications));
	}

	private static IApplication getApplication(String name, List<IApplication> applications) {
		IApplication matchingApplication = null;
		for (IApplication application : applications) {
			if (name.equals(application.getName())) {
				matchingApplication = application;
				break;
			}
		}
		return matchingApplication;
	}

	public static void assertApplication(String name, String uuid, String cartridgeName, String embedded,
			String creationTime, IApplication application) throws OpenShiftException {
		assertNotNull(application);
		assertEquals(embedded, application.getEmbedded());
		assertEquals(uuid, application.getUUID());
		try {
			assertEquals(RFC822DateUtils.getDate(creationTime), application.getCreationTime());
		} catch (DatatypeConfigurationException e) {
			fail(e.getMessage());
		}
	}

	public static void assertApplication(String name, String cartridgeName, IApplication application) throws OpenShiftException {
		assertNotNull(application);
		assertNotNull(application.getCartridge());
		assertEquals(cartridgeName, application.getCartridge().getName());
	}
	
	public static void assertGitUri(String applicationName, String gitUri) {
		Matcher matcher = GIT_URI_REGEXP.matcher(gitUri);
		assertTrue(matcher.matches());
		assertEquals(5, matcher.groupCount());
		assertEquals(applicationName, matcher.group(2));
		assertEquals(applicationName, matcher.group(5));
	}
	
	public static void assertGitUri(String uuid, String name, String namespace, String rhcDomain, String gitUri) {
		Matcher matcher = GIT_URI_REGEXP.matcher(gitUri);
		assertTrue(matcher.matches());
		assertEquals(5, matcher.groupCount());
		assertEquals(uuid, matcher.group(1));
		assertEquals(name, matcher.group(2));
		assertEquals(namespace, matcher.group(3));
		assertEquals(rhcDomain, matcher.group(4));
		assertEquals(name, matcher.group(5));
	}

	public static void assertAppliactionUrl(String name, String applicationUrl) {
		Matcher matcher = APPLICATION_URL_REGEXP.matcher(applicationUrl);
		assertTrue(matcher.matches());
		assertEquals(3, matcher.groupCount());
		assertEquals(name, matcher.group(1));
	}

	public static void assertAppliactionUrl(String name, String namespace, String rhcDomain, String applicationUrl) {
		Matcher matcher = APPLICATION_URL_REGEXP.matcher(applicationUrl);
		assertTrue(matcher.matches());
		assertEquals(3, matcher.groupCount());
		assertEquals(name, matcher.group(1));
		assertEquals(namespace, matcher.group(2));
		assertEquals(rhcDomain, matcher.group(3));
	}
}
