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
package com.openshift.internal.client;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.openshift.client.ApplicationBuilder;
import com.openshift.client.ApplicationScale;
import com.openshift.client.IDomain;
import com.openshift.client.IGearProfile;
import com.openshift.client.cartridge.ICartridge;
import com.openshift.client.cartridge.IEmbeddableCartridge;
import com.openshift.client.cartridge.IStandaloneCartridge;
import com.openshift.client.utils.CartridgeTestUtils;
import com.openshift.internal.client.utils.Assert.AssertionFailedException;

/**
 * @author Andre Dietisheim
 */
public class ApplicationBuilderTest extends TestTimer {

	private IDomain domain;

	@Before
	public void setup() throws Throwable {
		this.domain = Mockito.mock(IDomain.class);
	}

	@Test
	public void shouldCreateApplicationWithStandaloneAndEmbeddableCartridges() {
		// pre-conditions
		
		String name = "redberret";
		IStandaloneCartridge as7 = CartridgeTestUtils.as7();
		IEmbeddableCartridge[] embedded = 
				new IEmbeddableCartridge[] { CartridgeTestUtils.mysql51(), CartridgeTestUtils.mongodb22() };
		IGearProfile gear = new GearProfile("screamforicecream");
		ApplicationScale scale = ApplicationScale.NO_SCALE;
		int timeout = 42;
		String gitUrl = "git@github.com:openshift/openshift-java-client.git";
		Map<String, String> environmentVariables = new HashMap<String, String>();
		environmentVariables.put("adietish", "Andre Dietisheim");
		
		// operation
		new ApplicationBuilder(domain)
			.setName(name)
			.setStandaloneCartridge(as7)
			.setEmbeddableCartridges(embedded)
			.setApplicationScale(scale)
			.setTimeout(timeout)
			.setInitialGitUrl(gitUrl)
			.setGearProfile(gear)
			.setEnvironmentVariables(environmentVariables)
			.build();
		
		// verification
		LinkedList<ICartridge> cartridges = new LinkedList<ICartridge>(Arrays.asList(embedded));
		cartridges.add(0, as7);
		Mockito.verify(domain).createApplication(
				name, scale, gear, gitUrl, timeout, environmentVariables, cartridges.toArray(new ICartridge[cartridges.size()]));
	}

	@Test
	public void shouldCreateApplicationWithCartridges() {
		// pre-conditions
		
		String name = "redberries";
		ICartridge[] cartridges = 
				new ICartridge[] { CartridgeTestUtils.as7(), CartridgeTestUtils.mysql51(), CartridgeTestUtils.mongodb22() };
		IGearProfile gear = new GearProfile("igloo");
		ApplicationScale scale = ApplicationScale.NO_SCALE;
		int timeout = 42;
		String gitUrl = "git@github.com:openshift/openshift-java-client.git";
		Map<String, String> environmentVariables = new HashMap<String, String>();
		environmentVariables.put("adietish", "Andre Dietisheim");
		
		// operation
		new ApplicationBuilder(domain)
			.setName(name)
			.setCartridges(Arrays.asList(cartridges))
			.setApplicationScale(scale)
			.setTimeout(timeout)
			.setInitialGitUrl(gitUrl)
			.setGearProfile(gear)
			.setEnvironmentVariables(environmentVariables)
			.build();
		
		// verification
		Mockito.verify(domain).createApplication(
				name, scale, gear, gitUrl, timeout, environmentVariables, cartridges);
	}
	
	@Test(expected=AssertionFailedException.class)
	public void shouldNotAcceptEmptyCartridges() {
		// pre-conditions
		
		// operation
		new ApplicationBuilder(domain)
			.setName("rabbitinthehole")
			.setCartridges(Collections.<ICartridge>emptyList());
		
		// verification
	}
}
