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
import com.openshift.client.cartridge.IEmbeddableCartridge;
import com.openshift.client.cartridge.IStandaloneCartridge;
import com.openshift.internal.client.utils.Assert;

public class ApplicationBuilder {

	private IDomain domain;
	
	public ApplicationBuilder(IDomain domain) {
		this.domain = domain;
	}
	
	public NamedBuilder setName(String name) {
		return new NamedBuilder(name);
	}
	
	public class NamedBuilder {
		
		private String name;
		
		protected NamedBuilder(String name) {
			this.name = name;
		}

		public NamedTypedCartridgeBuilder setStandaloneCartridge(IStandaloneCartridge standaloneCartridge) {
			return new NamedTypedCartridgeBuilder(name, standaloneCartridge);
		}

		public NamedUnTypedCartridgeBuilder setCartridges(Collection<ICartridge> cartridges) {
			Assert.isTrue(cartridges != null
					&& !cartridges.isEmpty());
			return new NamedUnTypedCartridgeBuilder(name, cartridges);
		}
	}
	
	protected abstract class AbstractNamedCartridgeBuilder<B> {
		
		protected String name;
		protected IGearProfile gearProfile;
		protected ApplicationScale applicationScale;
		protected String initialGitUrl;
		protected int timeout = IHttpClient.NO_TIMEOUT;
		protected Map<String, String> environmentVariables;
		
		AbstractNamedCartridgeBuilder(String name) {
			this.name = name;
		}

		public B setGearProfile(IGearProfile gearProfile) {
			this.gearProfile = gearProfile;
			return (B) this;
		}
		
		public B setApplicationScale(ApplicationScale applicationScale) {
			this.applicationScale = applicationScale;
			return (B) this;
		}

		public B setInitialGitUrl(String initialGitUrl) {
			this.initialGitUrl = initialGitUrl;
			return (B) this;
		}

		public B setTimeout(int timeout) {
			this.timeout = timeout;
			return (B) this;
		}
		
		public B setEnvironmentVariables(Map<String, String> environmentVariables) {
			this.environmentVariables = environmentVariables;
			return (B) this;
		}
	}
	
	public class NamedTypedCartridgeBuilder extends AbstractNamedCartridgeBuilder<NamedTypedCartridgeBuilder> {
		
		private IStandaloneCartridge standaloneCartridge;
		private Collection<IEmbeddableCartridge> embeddableCartridges;
		
		NamedTypedCartridgeBuilder(String name, IStandaloneCartridge standaloneCartridge) {
			super(name);
			this.standaloneCartridge = standaloneCartridge;
		}

		public NamedTypedCartridgeBuilder setEmbeddableCartridges(IEmbeddableCartridge... embeddableCartridges) {
			if (embeddableCartridges == null) {
				return this;
			}
			this.embeddableCartridges = Arrays.asList(embeddableCartridges);
			return this;
		}

		public IApplication build() {
			return domain.createApplication(name, applicationScale, gearProfile, initialGitUrl, timeout, environmentVariables, 
					createCartridges(standaloneCartridge, embeddableCartridges));
		}

		protected ICartridge[] createCartridges(IStandaloneCartridge standaloneCartridge, Collection<? extends ICartridge> embeddableCartridges) {
			List<ICartridge> cartridges = new ArrayList<ICartridge>();
			cartridges.add(standaloneCartridge);
			if (embeddableCartridges != null
					&& !embeddableCartridges.isEmpty()) {
				cartridges.addAll(embeddableCartridges);
			}
			return (ICartridge[]) cartridges.toArray(new ICartridge[cartridges.size()]);
		}
	}
	
	public class NamedUnTypedCartridgeBuilder extends AbstractNamedCartridgeBuilder<NamedUnTypedCartridgeBuilder> {
		
		private Collection<ICartridge> cartridges;
		
		NamedUnTypedCartridgeBuilder(String name, Collection<ICartridge> cartridges) {
			super(name);
			this.cartridges = cartridges;
		}

		public IApplication build() {
			return domain.createApplication(name, applicationScale, gearProfile, initialGitUrl, timeout, environmentVariables, 
					cartridges.toArray(new ICartridge[cartridges.size()]));
		}
	}
}
