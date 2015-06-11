/*******************************************************************************
 * Copyright (c) 2015 Red Hat, Inc. Distributed under license by Red Hat, Inc.
 * All rights reserved. This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Red Hat, Inc.
 ******************************************************************************/
package com.openshift.internal.util;

import static org.junit.Assert.*;
import static com.openshift.internal.util.JBossDmrExtentions.*;

import java.util.HashMap;
import java.util.Map;

import org.jboss.dmr.ModelNode;
import org.junit.Before;
import org.junit.Test;


/**
 * @author Jeff Cantrill
 */
public class JBossDmrExtentionsTest {

	private ModelNode node = ModelNode.fromJSONString("{\"foo\":\"bar\", \"int\":\"3\", \"bool\":\"true\"}");
	private Map<String, String []> paths = new HashMap<String, String[]>();
	private static final String KEY_FOO = "foo";
	private static final String KEY_XYZ = "xyz";
	private static final String KEY_BOOL = "bool";
	private static final String KEY_INT = "int";

	@Before
	public void setup() {
		paths.put(KEY_FOO, new String[] {"foo"});
		paths.put(KEY_XYZ, new String[] {"xyz"});
		paths.put(KEY_BOOL, new String[] {"bool"});
		paths.put(KEY_INT, new String[] {"int"});
	}

	@Test
	public void testAsMapWhenPropertyKeysAreNull() {
		assertNotNull(asMap(new ModelNode(), null, null));
	}
	
	@Test
	public void asIntForUndefinedShouldReturnZero() {
		assertEquals(0, asInt(node, paths, KEY_XYZ));
	}

	@Test
	public void asIntForAValueShouldReturnValue() {
		assertEquals(3,asInt(node, paths, KEY_INT));
	}
	@Test
	public void asBooleanForUndefinedShouldReturnFalse() {
		assertFalse(asBoolean(node, paths, KEY_XYZ));
	}
	
	@Test
	public void asBooleanForAValueShouldReturnValue() {
		assertTrue(asBoolean(node, paths, KEY_BOOL));
	}

	@Test
	public void asStringForUndefinedShouldReturnEmptySpace() {
		assertEquals("", asString(node, paths, KEY_XYZ));
	}

	@Test
	public void asStringForAValueShouldReturnTheValue() {
		assertEquals("bar", asString(node, paths, KEY_FOO));
	}

}
