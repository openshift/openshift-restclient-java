/*******************************************************************************
 * Copyright (c) 2018 Red Hat, Inc. Distributed under license by Red Hat, Inc.
 * All rights reserved. This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Red Hat, Inc.
 ******************************************************************************/

package com.openshift.internal.util;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

public class StringSplitterTest {
    @Test
    public void testSimpleUniqueParameter() {
        List<String> result = StringSplitter.split("parm1");
        List<String> expected = Arrays.asList("parm1");
        assertThat(result, is(expected));
    }

    @Test
    public void testSimpleManyParameters() {
        List<String> result = StringSplitter.split("parm1 parm2 parm3");
        List<String> expected = Arrays.asList("parm1", "parm2", "parm3");
        assertThat(result, is(expected));
    }

    @Test
    public void testQuotedUniqueParameter() {
        List<String> result = StringSplitter.split("\"parm1\"");
        List<String> expected = Arrays.asList("parm1");
        assertThat(result, is(expected));
    }

    @Test
    public void testQuotedManyParameters() {
        List<String> result = StringSplitter.split("\"parm1\" \"parm2\" \"parm3\"");
        List<String> expected = Arrays.asList("parm1", "parm2", "parm3");
        assertThat(result, is(expected));
    }

    @Test
    public void testQuotedManyParametersWithAdjacentValue() {
        List<String> result = StringSplitter.split("\"parm1\" \"parm2\"a \"parm3\"");
        List<String> expected = Arrays.asList("parm1", "parm2a", "parm3");
        assertThat(result, is(expected));
    }

    @Test
    public void testQuotedManyParametersUnfinished() {
        List<String> result = StringSplitter.split("\"parm1\" \"parm2\" \"parm3");
        List<String> expected = Arrays.asList("parm1", "parm2", "parm3");
        assertThat(result, is(expected));
    }

    @Test
    public void testSimpleDoubleSpaceManyParameters() {
        List<String> result = StringSplitter.split("parm1  parm2 parm3");
        List<String> expected = Arrays.asList("parm1", "parm2", "parm3");
        assertThat(result, is(expected));
    }

    @Test
    public void testSimpleEndSpaceManyParameters() {
        List<String> result = StringSplitter.split("parm1 parm2 parm3 ");
        List<String> expected = Arrays.asList("parm1", "parm2", "parm3");
        assertThat(result, is(expected));
    }
}
