/*******************************************************************************
 * Copyright (c) 2016 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package com.openshift.restclient;

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * 
 * @author jeff.cantrill
 *
 */
public class ResourceKindTest {

	@Test
	public void testPluralizeWhenNull() {
		assertEquals("",ResourceKind.pluralize(null));
	}

	@Test
	public void testPluralizeWhenEmpty() {
		assertEquals("",ResourceKind.pluralize(" "));
	}

	@Test
	public void testPluralizeWhenEndsWithAnS() {
		assertEquals("status",ResourceKind.pluralize(ResourceKind.STATUS));
	}

	@Test
	public void testPluralizeWhenEndsWithY() {
		assertEquals("families",ResourceKind.pluralize("Family"));
	}

	@Test
	public void testPluralizeWhenEndsWithAnythingElse() {
		assertEquals("services",ResourceKind.pluralize(ResourceKind.SERVICE));
	}

}
