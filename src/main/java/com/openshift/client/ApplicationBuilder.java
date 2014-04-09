/******************************************************************************* 
 * Copyright (c) 2013 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/
package com.openshift.client;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.openshift.client.cartridge.ICartridge;
import com.openshift.client.cartridge.IStandaloneCartridge;

public class ApplicationBuilder {

	private IDomain domain;
	private ICartridge standaloneCartridge;
	private String name;
	private IGearProfile gearProfile;
	private ApplicationScale applicationScale;
	private String initialGitUrl;
	private int timeout = IHttpClient.NO_TIMEOUT;
	public Collection<? extends ICartridge> embeddableCartridges;
	public Map<String, String> environmentVariables;
	
	public ApplicationBuilder(IDomain domain) {
		this.domain = domain;
	}
	
	public CartridgeHolder setStandaloneCartridge(IStandaloneCartridge standaloneCartridge) {
		return setStandaloneCartridge((ICartridge) standaloneCartridge);
	}

	public CartridgeHolder setStandaloneCartridge(ICartridge standaloneCartridge) {
		ApplicationBuilder.this.standaloneCartridge = standaloneCartridge;
		return new CartridgeHolder();
	}
	
	public class CartridgeHolder {

		public NamedCartridgeHolder setName(String name) {
			ApplicationBuilder.this.name = name;
			return new NamedCartridgeHolder();
		};
	}
	
	public class NamedCartridgeHolder {
		
		public NamedCartridgeHolder setGearProfile(IGearProfile gearProfile) {
			ApplicationBuilder.this.gearProfile = gearProfile;
			return this;
		}
		
		public NamedCartridgeHolder setApplicationScale(ApplicationScale applicationScale) {
			ApplicationBuilder.this.applicationScale = applicationScale;
			return this;
		}

		public NamedCartridgeHolder setInitialGitUrl(String initialGitUrl) {
			ApplicationBuilder.this.initialGitUrl = initialGitUrl;
			return this;
		}

		public NamedCartridgeHolder setTimeout(int timeout) {
			ApplicationBuilder.this.timeout = timeout;
			return this;
		}
		
		public NamedCartridgeHolder setEnvironmentVariables(Map<String, String> environmentVariables) {
			ApplicationBuilder.this.environmentVariables = environmentVariables;
			return this;
		}

		public NamedCartridgeHolder setEmbeddableCartridges(ICartridge... embeddableCartridges) {
			if (embeddableCartridges == null) {
				return this;
			}
			ApplicationBuilder.this.embeddableCartridges = Arrays.asList(embeddableCartridges);
			return this;
		}

		public NamedCartridgeHolder setEmbeddableCartridges(Collection<? extends ICartridge> embeddableCartridges) {
			ApplicationBuilder.this.embeddableCartridges = embeddableCartridges;
			return this;
		}
		
		public IApplication build() {
			return domain.createApplication(name, applicationScale, gearProfile, initialGitUrl, timeout, environmentVariables, 
					createCartridges(standaloneCartridge, embeddableCartridges));
		}

		protected ICartridge[] createCartridges(ICartridge standaloneCartridge, Collection<? extends ICartridge> embeddableCartridges) {
			List<ICartridge> cartridges = new ArrayList<ICartridge>();
			cartridges.add(standaloneCartridge);
			if (embeddableCartridges != null
					&& !embeddableCartridges.isEmpty()) {
				cartridges.addAll(embeddableCartridges);
			}
			return (ICartridge[]) cartridges.toArray(new ICartridge[cartridges.size()]);
		}
	}
}
