 /*******************************************************************************
 * Copyright (c) 2015 Red Hat, Inc. Distributed under license by Red Hat, Inc.
 * All rights reserved. This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Red Hat, Inc.
 ******************************************************************************/
package com.openshift.internal.restclient.http;

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * @author Andre Dietisheim
 */
public class HttpMethodTest {

	@Test
	public void hasValueForKnownValueShouldReturnTrue() {
		assertTrue(HttpMethod.hasValue("POST"));
		assertTrue(HttpMethod.hasValue("pOst"));
	}
	@Test
	public void hasValueForUnKnownValueShouldReturnFalse() {
		assertFalse(HttpMethod.hasValue("afdadsfads"));
	}

}
