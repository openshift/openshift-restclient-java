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
package com.openshift.express.internal.client.test.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.text.MessageFormat;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.datatype.DatatypeConfigurationException;

import com.openshift.express.client.IApplication;
import com.openshift.express.client.ICartridge;
import com.openshift.express.client.IEmbeddableCartridge;
import com.openshift.express.client.OpenShiftException;
import com.openshift.express.client.utils.RFC822DateUtils;

/**
 * @author André Dietisheim
 */
public class ApplicationAsserts {

	/**
	 * $1 = application uuid $2 = application name $3 = InternalUser uuid $4 =
	 * rhc cloud domain (rhcloud.com) $5 = application name
	 */
	public static final Pattern GIT_URI_REGEXP = Pattern.compile("ssh://(.+)@(.+)-([^\\.]+)\\.(.+)/~/git/(.+).git/");

	public static final Pattern APPLICATION_URL_REGEXP = Pattern.compile("https*://(.+)-([^\\.]+)\\.(.+)/");

	public static void assertThatContainsApplication(String applicationName, List<IEmbeddableCartridge> embedded,
			String uuid, String cartridgeName, String creationTime, List<IApplication> applications)
			throws OpenShiftException {
		IApplication application = getApplication(applicationName, applications);
		if (application == null) {
			fail(MessageFormat.format("Could not find application with name \"{0}\"", applicationName));
		}
		assertApplication(applicationName, uuid, cartridgeName, embedded, creationTime, application);
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

	public static void assertApplication(String name, String uuid, String creationLog, ICartridge cartridge,
			IApplication application) throws OpenShiftException {
		assertNotNull(cartridge);
		assertApplication(name, uuid, creationLog, cartridge.getName(), application);
	}

	public static void assertApplication(String name, String uuid, String creationLog, String cartridgeName,
			IApplication application) throws OpenShiftException {
		assertNotNull(application);
		assertApplication(name, uuid, cartridgeName, application);
		assertEquals(creationLog, application.getCreationLog());
	}

	public static void assertApplication(String name, String uuid, String creationLog, String healthPath,
			String cartridgeName, IApplication application) throws OpenShiftException {
		assertApplication(name, uuid, creationLog, cartridgeName, application);
		assertEquals(healthPath, getHealthPath(application));
	}

	public static void assertApplication(String name, String uuid, String cartridgeName,
			List<IEmbeddableCartridge> embedded, String creationTime, IApplication application)
			throws OpenShiftException {
		assertApplication(name, uuid, cartridgeName, application);
		assertEquals(embedded, application.getEmbeddedCartridges());
		try {
			assertEquals(RFC822DateUtils.getDate(creationTime), application.getCreationTime());
		} catch (DatatypeConfigurationException e) {
			fail(e.getMessage());
		}
	}

	public static void assertApplication(String name, String creationLog, String uuid, String cartridgeName,
			List<IEmbeddableCartridge> embedded, String creationTime, IApplication application)
			throws OpenShiftException {
		assertApplication(name, uuid, cartridgeName, embedded, creationTime, application);
		assertEquals(creationLog, application.getCreationLog());
	}

	public static void assertApplication(String name, ICartridge cartridge, IApplication application)
			throws OpenShiftException {
		assertNotNull(cartridge);
		assertNotNull(application);
		assertEquals(name, application.getName());
		assertEquals(cartridge, application.getCartridge());
	}

	public static void assertApplication(String name, String uuid, ICartridge cartridge, IApplication application)
			throws OpenShiftException {
		assertNotNull(cartridge);
		assertApplication(name, uuid, cartridge.getName(), application);
	}

	public static void assertApplication(String name, String uuid, String cartridgeName, IApplication application)
			throws OpenShiftException {
		assertNotNull(application);
		assertEquals(name, application.getName());
		assertEquals(uuid, application.getUUID());
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

	public static void assertApplicationUrl(String name, String applicationUrl) {
		Matcher matcher = APPLICATION_URL_REGEXP.matcher(applicationUrl);
		assertTrue(matcher.matches());
		assertEquals(3, matcher.groupCount());
		assertEquals(name, matcher.group(1));
	}

	public static void assertApplicationUrl(String name, String namespace, String rhcDomain, String applicationUrl) {
		Matcher matcher = APPLICATION_URL_REGEXP.matcher(applicationUrl);
		assertTrue(matcher.matches());
		assertEquals(3, matcher.groupCount());
		assertEquals(name, matcher.group(1));
		assertEquals(namespace, matcher.group(2));
		assertEquals(rhcDomain, matcher.group(3));
	}

	public static void assertThatContainsEmbeddableCartridge(IEmbeddableCartridge embeddableCartridge,
			IApplication application) throws OpenShiftException {
		List<IEmbeddableCartridge> cartridges = application.getEmbeddedCartridges();
		assertNotNull(cartridges);
		assertTrue(cartridges.size() >= 1);
		for (IEmbeddableCartridge embeddedCartridge : cartridges) {
			if (embeddableCartridge.equals(embeddedCartridge)) {
				return;
			}
		}
		fail(MessageFormat.format(
				"embedded cartridge \"{0}\" is not present in application \"{1}\"", embeddableCartridge.getName(),
				application.getName()));
	}

	private static String getHealthPath(IApplication application) throws OpenShiftException {
		if (application == null) {
			return null;
		}

		String url = application.getHealthCheckUrl();
		if (url == null) {
			return null;
		}

		int pathStart = url.lastIndexOf('/');
		if (pathStart < 0
				|| pathStart >= url.length()) {
			return null;
		}
		return url.substring(pathStart + 1);
	}
}
