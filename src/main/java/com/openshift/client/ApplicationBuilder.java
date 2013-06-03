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

import com.openshift.client.cartridge.IStandaloneCartridge;

public class ApplicationBuilder {

	private IDomain domain;
	private IStandaloneCartridge cartridge;
	private String name;
	private IGearProfile gearProfile;
	private ApplicationScale applicationScale;
	private String initialGitUrl;
	
	public ApplicationBuilder(IDomain domain) {
		this.domain = domain;
	}
	
	public CartridgeHolder setCartridge(IStandaloneCartridge cartridge) {
		ApplicationBuilder.this.cartridge = cartridge;
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

		public IApplication build() {
			return domain.createApplication(name, cartridge, applicationScale, gearProfile, initialGitUrl);
		}
	}
}
