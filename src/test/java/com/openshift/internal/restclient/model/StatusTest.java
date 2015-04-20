/*******************************************************************************
 * Copyright (c) 2015 Red Hat, Inc. Distributed under license by Red Hat, Inc.
 * All rights reserved. This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package com.openshift.internal.restclient.model;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import org.junit.Before;
import org.junit.Test;

/**
 * Tests that have logic and not specific to a particular api version
 * @author jeff.cantrill
 *
 */
public class StatusTest {

	private Status status;
	
	@Before
	public void setup() {
		status = spy(new Status(null,null,null));
	}
	
	@Test
	public void isFailureShouldReturnTrueWhenFailure() {
		doReturn("Failure").when(status).getStatus();
		
		assertTrue(status.isFailure());
	}

	@Test
	public void isFailureShouldReturnFalseWhenNotFailure() {
		doReturn("Other").when(status).getStatus();
		
		assertFalse(status.isFailure());
	}
}
