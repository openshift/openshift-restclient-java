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
package com.openshift.client.utils;

import static org.fest.assertions.Assertions.assertThat;

import com.openshift.client.OpenShiftException;
import com.openshift.client.cartridge.IDeployedStandaloneCartridge;

/**
 * @author André Dietisheim
 */
public class DeployedStandaloneCartridgeAssert extends CartridgeAssert<IDeployedStandaloneCartridge> {

	public DeployedStandaloneCartridgeAssert(IDeployedStandaloneCartridge cartridge) {
		super(cartridge);
	}

	public CartridgeAssert<IDeployedStandaloneCartridge> isEqualTo(IDeployedStandaloneCartridge otherCartridge)
			throws OpenShiftException {
		super.isEqualTo(otherCartridge);

		assertThat(getCartridge().getAdditionalGearStorage()).equals(otherCartridge.getAdditionalGearStorage());
		return this;
	}


}
