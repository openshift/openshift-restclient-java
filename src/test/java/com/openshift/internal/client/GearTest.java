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
package com.openshift.internal.client;

import static org.fest.assertions.Assertions.assertThat;

import org.junit.Test;

import com.openshift.client.GearState;

/**
 * @author Xavier Coulon
 * @author Andre Dietisheim
 */
public class GearTest {

	@Test
	public void nullShouldCreateUnknownGearState() {
		// operation
		GearState state = GearState.safeValueOf(null);
		// verification
		assertThat(state).isEqualTo(GearState.UNKNOWN);
	}

	@Test
	public void emptyShouldCreateUnknownGearState() {
		// operation
		GearState state = GearState.safeValueOf("");
		// verification
		assertThat(state).isEqualTo(GearState.UNKNOWN);
	}

	@Test
	public void stateCaseShouldNotMatter() {
		// operation
		GearState state = GearState.safeValueOf("sTaRtEd");
		// verification
		assertThat(state).isEqualTo(GearState.STARTED);
	}

}
