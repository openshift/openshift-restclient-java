/*******************************************************************************
 * Copyright (c) 2015 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package com.openshift.internal.restclient;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import com.openshift.restclient.IOpenShiftWatchListener.ChangeType;

/**
 * @author Andre Dietisheim
 */
public class WatchClientTest {

	@Test
	public void changeTypeShouldEqualSameChangeType() {
		assertThat(ChangeType.ADDED, 
				equalTo(ChangeType.ADDED));
	}
	
	@Test
	public void changeTypeShouldNotEqualDifferentChangeType() {
		assertThat(ChangeType.ADDED, 
				not(equalTo(ChangeType.DELETED)));
	}

	@Test
	public void changeTypeShouldEqualSameChangeTypeInLowercase() {
		assertThat(ChangeType.ADDED, 
				equalTo(new ChangeType(ChangeType.ADDED.getValue().toLowerCase())));
	}
}
