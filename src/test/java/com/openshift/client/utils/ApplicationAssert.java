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
package com.openshift.client.utils;

import static org.fest.assertions.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.fest.assertions.AssertExtension;

import com.openshift.client.IApplication;
import com.openshift.client.IDomain;
import com.openshift.client.OpenShiftException;
import com.openshift.client.cartridge.IEmbeddableCartridge;
import com.openshift.client.cartridge.IEmbeddedCartridge;
import com.openshift.client.cartridge.IStandaloneCartridge;
import com.openshift.client.cartridge.selector.LatestEmbeddableCartridge;
import com.openshift.internal.client.utils.StreamUtils;

/**
 * @author André Dietisheim
 */
public class ApplicationAssert implements AssertExtension {

	public static final Pattern APPLICATION_URL_PATTERN = Pattern.compile("https*://(.+)-([^\\.]+)\\.(.+)/(.*)");
	public static final Pattern GIT_URL_PATTERN = Pattern.compile("ssh://(.+)@(.+)-([^\\.]+)\\.(.+)/~/git/(.+).git/");
	
	private static final long APPLICATION_WAIT_TIMEOUT = 2 * 60 * 1000;
	
	private IApplication application;

	public ApplicationAssert(IApplication application) {
		this.application = application;
	}

	public ApplicationAssert hasName(String name) {
		assertEquals(name, application.getName());
		return this;
	}

	public ApplicationAssert hasUUID(String uuid) {
		assertEquals(uuid, application.getUUID());
		return this;
	}

	public ApplicationAssert hasUUID() {
		assertNotNull(application.getUUID());
		return this;
	}

	public ApplicationAssert hasCartridge(IStandaloneCartridge cartridge) {
		assertEquals(cartridge, application.getCartridge());
		return this;
	}

	public ApplicationAssert hasCreationTime(String creationTime) {
		assertEquals(creationTime, application.getCreationTime());
		return this;
	}

	public ApplicationAssert hasCreationTime() {
		assertNotNull(application.getCreationTime());
		return this;
	}

	public ApplicationAssert hasGitUrl(String gitUrl) {
		assertEquals(gitUrl, application.getGitUrl());
		return this;
	}

	public ApplicationAssert hasValidGitUrl() {
		Matcher matcher = GIT_URL_PATTERN.matcher(application.getGitUrl());
		assertTrue(matcher.matches());
		assertEquals(5, matcher.groupCount());
		
		assertEquals(application.getUUID(), matcher.group(1));
		assertEquals(application.getName(), matcher.group(2));
		assertEquals(application.getDomain().getSuffix(), matcher.group(4));
		assertEquals(application.getName(), matcher.group(5));

		return this;
	}

	public ApplicationAssert hasInitialGitUrl() {
		assertThat(application.getInitialGitUrl()).isNotEmpty();
		return this;
	}

	public ApplicationAssert hasNoInitialGitUrl() {
		assertThat(application.getInitialGitUrl()).isNull();
		return this;
	}

	public ApplicationAssert hasInitialGitUrl(String initialGitUrl) {
		assertThat(application.getInitialGitUrl()).isEqualTo(initialGitUrl);
		return this;
	}

	public ApplicationAssert hasApplicationUrl(String applicationUrl) {
		assertEquals(applicationUrl, application.getApplicationUrl());
		return this;
	}

	public ApplicationAssert hasValidApplicationUrl() {
		assertApplicationUrl();
		return this;
	}

	private void assertApplicationUrl() {
		Matcher matcher = APPLICATION_URL_PATTERN.matcher(application.getApplicationUrl());
		assertTrue(matcher.matches());
		assertTrue(matcher.groupCount() >= 3);

		assertEquals(application.getName(), matcher.group(1));
		IDomain domain = application.getDomain();
		assertEquals(domain.getId(), matcher.group(2));
		assertEquals(domain.getSuffix(), matcher.group(3));
	}
	
	public ApplicationAssert hasEmbeddedCartridges(LatestEmbeddableCartridge... selectors)
			throws OpenShiftException {
		for (LatestEmbeddableCartridge selector : selectors) {
			hasEmbeddedCartridge(selector);
		}
		return this;
	}

	public ApplicationAssert hasEmbeddedCartridge(LatestEmbeddableCartridge selector)
			throws OpenShiftException {
		IEmbeddableCartridge embeddableCartridge = selector.get(application);
		assertTrue(application.hasEmbeddedCartridge(embeddableCartridge));

		return this;
	}

	public ApplicationAssert hasEmbeddableCartridges(String... embeddableCartridgeNames) throws OpenShiftException {
		if (embeddableCartridgeNames.length == 0) {
			assertEquals(0, application.getEmbeddedCartridges().size());
		}

		for (String cartridgeName : embeddableCartridgeNames) {
			assertTrue(application.hasEmbeddedCartridge(cartridgeName));
		}

		return this;
	}

	public ApplicationAssert hasEmbeddableCartridges(int numberOf) {
		assertNotNull(application.getEmbeddedCartridges());
		assertEquals(numberOf, application.getEmbeddedCartridges().size());
		return this;
	}
	
	public ApplicationAssert hasNotEmbeddableCartridges(String... embeddableCartridgeNames) throws OpenShiftException {		
		for (String cartridgeName : embeddableCartridgeNames) {
			assertFalse(application.hasEmbeddedCartridge(cartridgeName));
		}

		return this;
	}

	public ApplicationAssert hasNotEmbeddableCartridges(LatestEmbeddableCartridge... selectors) throws OpenShiftException {
		for (LatestEmbeddableCartridge selector : selectors) {
			assertThat(application.hasEmbeddedCartridge(selector.get(application))).isFalse();
		}

		return this;
	}

	public ApplicationAssert hasAlias(String... aliasNames) {
		if (aliasNames.length == 0) {
			assertEquals(0, application.getAliases().size());
		}

		for (String cartridgeName : aliasNames) {
			assertTrue(application.hasAlias(cartridgeName));
		}

		return this;
	}

	public void hasNotEmbeddableCartridge(LatestEmbeddableCartridge constraint) {
		hasNotEmbeddableCartridge(constraint.get(application));
	}

	public void hasNotEmbeddableCartridge(IEmbeddableCartridge cartridge) {
		hasNotEmbeddableCartridge(cartridge.getName());
	}

	public void hasNotEmbeddableCartridge(String name) {
		assertNull(getEmbeddableCartridge(name));
	}

	public void assertThatDoesntContainCartridges(Collection<IEmbeddableCartridge> shouldNotBeContained, List<IEmbeddedCartridge> cartridges) {
		for(IEmbeddableCartridge shouldNot : shouldNotBeContained) {
			assertFalse(cartridges.contains(shouldNot));
		}
	}

	private IEmbeddedCartridge getEmbeddableCartridge(String name) {
		IEmbeddedCartridge matchingCartridge = null;
		for (IEmbeddedCartridge cartridge : application.getEmbeddedCartridges()) {
			if (name.equals(cartridge.getName())) {
				matchingCartridge = cartridge;
				break;
			}
		}
		return matchingCartridge;
	}

	public void assertThatContainsCartridges(Collection<IEmbeddableCartridge> shouldBeContained, List<IEmbeddedCartridge> cartridgesToCheck) {
		for (IEmbeddableCartridge cartridge : shouldBeContained) {
			assertTrue(cartridgesToCheck.contains(cartridge));
		}
	}
	
	public ApplicationAssert hasContent(String page, String contains) throws IOException {
		URL appUrl = new URL(application.getApplicationUrl() + page);
		assertThat(application.waitForAccessible(APPLICATION_WAIT_TIMEOUT)).isTrue();
		String content = StreamUtils.readToString(appUrl.openConnection().getInputStream());
		assertThat(content).contains(contains);
		return this;
	}
}
