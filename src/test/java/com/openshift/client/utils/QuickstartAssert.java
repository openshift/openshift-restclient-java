/******************************************************************************* 
 * Copyright (c) 2014 Red Hat, Inc. 
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
import static org.junit.Assert.fail;

import java.util.Collection;
import java.util.List;
import java.util.regex.Pattern;

import org.fest.assertions.AssertExtension;

import com.openshift.client.IQuickstart;
import com.openshift.client.cartridge.ICartridge;
import com.openshift.internal.client.AlternativeCartridges;
import com.openshift.internal.client.utils.StringUtils;
/**
 * @author Andre Dietisheim
 */
public class QuickstartAssert implements AssertExtension {

	public static final Pattern APPLICATION_URL_PATTERN = Pattern.compile("https*://(.+)-([^\\.]+)\\.(.+)/(.*)");
	public static final Pattern GIT_URL_PATTERN = Pattern.compile("ssh://(.+)@(.+)-([^\\.]+)\\.(.+)/~/git/(.+).git/");

	private IQuickstart quickstart;

	public QuickstartAssert(IQuickstart quickstart) {
		assertThat(quickstart).isNotNull();
		this.quickstart = quickstart;
	}

	public QuickstartAssert hasName(String name) {
		assertThat(quickstart.getName()).isEqualTo(name);
		return this;
	}

	public QuickstartAssert hasHref(String href) {
		assertThat(quickstart.getHref()).isEqualTo(href);
		return this;
	}

	public QuickstartAssert hasId(String id) {
		assertThat(quickstart.getId()).isEqualTo(id);
		return this;
	}

	public QuickstartAssert hasSummary(String summary) {
		assertThat(quickstart.getSummary()).isEqualTo(summary);
		return this;
	}

	public void hasCartridgeNames(Collection<String>... assertedAlternatives) {
		assertThat(assertedAlternatives).isNotNull();
		assertThat(quickstart.getSuitableCartridges()).isNotNull();
		assertThat(assertedAlternatives.length).isEqualTo(quickstart.getSuitableCartridges().size());

		for (int i = 0; i < assertedAlternatives.length; i++) {
			assertThat(quickstart.getSuitableCartridges().get(i).get())
					.onProperty("name").containsOnly(assertedAlternatives[i].toArray());
		}
	}

	public void hasCartridges(Collection<ICartridge> shouldBeContained) {
		assertThat(quickstart.getSuitableCartridges()).isNotNull();
		for (ICartridge assertedCartridge : shouldBeContained) {
			assertCartridge(assertedCartridge, quickstart.getSuitableCartridges());
		}
	}

	protected void assertCartridge(ICartridge assertedCartridge, List<AlternativeCartridges> allSuitableAlternatives) {
		for (AlternativeCartridges suitableAlternatives : allSuitableAlternatives) {
			if (suitableAlternatives.get().contains(assertedCartridge)) {
				return;
			}
		}
		fail(getCartridgeLabel(assertedCartridge) +
				" is not listed in the cartridges required by quickstart " + quickstart.getName());
	}

	protected String getCartridgeLabel(ICartridge assertedCartridge) {
		String cartridgeId = assertedCartridge.getName();
		if (StringUtils.isEmpty(cartridgeId)) {
			cartridgeId = String.valueOf(assertedCartridge.getUrl());
		}
		return cartridgeId;
	}

	public QuickstartAssert hasWebsite(String website) {
		assertThat(quickstart.getWebsite()).isEqualTo(website);
		return this;
	}

	public QuickstartAssert hasTags(String... shouldBeContained) {
		assertThat(quickstart.getTags())
				.isNotNull()
				.contains(shouldBeContained);
		return this;
	}

	public QuickstartAssert hasLanguage(String language) {
		assertThat(quickstart.getLanguage()).isEqualTo(language);
		return this;
	}

	public QuickstartAssert hasInitialGitUrl(String initialGitUrl) {
		assertThat(quickstart.getInitialGitUrl()).isEqualTo(initialGitUrl);
		return this;
	}

	public QuickstartAssert hasProvider(String provider) {
		assertThat(quickstart.getProvider()).isEqualTo(provider);
		return this;
	}
}
