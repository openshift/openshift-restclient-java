/*******************************************************************************
 * Copyright (c) 2015 Red Hat, Inc. Distributed under license by Red Hat, Inc.
 * All rights reserved. This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Red Hat, Inc.
 ******************************************************************************/

package com.openshift.internal.util;

import static com.openshift.internal.util.JBossDmrExtentions.asBoolean;
import static com.openshift.internal.util.JBossDmrExtentions.asInt;
import static com.openshift.internal.util.JBossDmrExtentions.asList;
import static com.openshift.internal.util.JBossDmrExtentions.asMap;
import static com.openshift.internal.util.JBossDmrExtentions.asSet;
import static com.openshift.internal.util.JBossDmrExtentions.asString;
import static com.openshift.internal.util.JBossDmrExtentions.toJsonString;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jboss.dmr.ModelNode;
import org.jboss.dmr.ModelType;
import org.junit.Before;
import org.junit.Test;

public class JBossDmrExtentionsTest {

    private ModelNode node = ModelNode
            .fromJSONString("{\"foo\":\"bar\", \"int\":\"3\", \"bool\":\"true\", \"list\": [\"1\", \"2\", \"3\"]}");
    private Map<String, String[]> paths = new HashMap<String, String[]>();
    private static final String KEY_FOO = "foo";
    private static final String KEY_XYZ = "xyz";
    private static final String KEY_BOOL = "bool";
    private static final String KEY_INT = "int";
    private static final String KEY_LIST = "list";

    @Before
    public void setup() {
        paths.put(KEY_FOO, new String[] { "foo" });
        paths.put(KEY_XYZ, new String[] { "xyz" });
        paths.put(KEY_BOOL, new String[] { "bool" });
        paths.put(KEY_INT, new String[] { "int" });
        paths.put(KEY_LIST, new String[] { "list" });
    }

    @Test
    public void testToJson() {
        ModelNode complex = new ModelNode();
        complex.get("sub1", "sub2");
        complex.get("sub1a").set("avalue");

        ModelNode node = new ModelNode();
        node.get("foo", "bar");
        node.get("xyz").add(1).add(2).add(3);
        node.get("xyz").get(1).clear();
        node.get("xyz").add(complex);
        node.get("def").add(new ModelNode());
        node.get("abc").set("xyx");
        assertEquals("{\"foo\" : {}, \"xyz\" : [1,3,{\"sub1\" : {}, \"sub1a\" : \"avalue\"}], \"abc\" : \"xyx\"}",
                toJsonString(node, true));
    }

    @Test
    public void testGettersDoNotAddNodeToJsonTree() {
        asMap(node, paths, "openshift.map");
        assertFalse(node.has("openshift", "map"));

        asSet(node, paths, "openshift.set", ModelType.STRING);
        assertFalse(node.has("openshift", "set"));

        asInt(node, paths, "openshift.int");
        assertFalse(node.has("openshift", "int"));

        asString(node, paths, "openshift.string");
        assertFalse(node.has("openshift", "string"));

        asBoolean(node, paths, "openshift.bool");
        assertFalse(node.has("openshift", "bool"));

        asList(node, paths, "openshift.list", ModelType.STRING);
        assertFalse(node.has("openshift", "set"));
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
        assertEquals(3, asInt(node, paths, KEY_INT));
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

    @Test
    public void asListForStringReturnsOrderedValues() {
        List<String> l = asList(node, paths, KEY_LIST, ModelType.STRING);
        assertEquals("1", l.get(0));
        assertEquals("2", l.get(1));
        assertEquals("3", l.get(2));
    }

}
