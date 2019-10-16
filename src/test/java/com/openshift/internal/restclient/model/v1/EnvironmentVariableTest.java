/*******************************************************************************
 * Copyright (c) 2017 Red Hat, Inc. Distributed under license by Red Hat, Inc.
 * All rights reserved. This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Red Hat, Inc.
 ******************************************************************************/

package com.openshift.internal.restclient.model.v1;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import com.openshift.internal.restclient.model.EnvironmentVariable;
import com.openshift.internal.restclient.model.ModelNodeBuilder;
import com.openshift.restclient.model.IEnvironmentVariable;
import com.openshift.restclient.utils.EnvironmentVariableUtils;

/**
 * @author Andre Dietisheim
 */
public class EnvironmentVariableTest {

    @Test
    public void shouldBeEqualGivenEqualNameAndValue() {
        // given
        IEnvironmentVariable var1 = createEnvironmentVariable("foo", "bar");
        IEnvironmentVariable var2 = createEnvironmentVariable("foo", "bar");
        // when
        // then
        assertThat(var1).isEqualTo(var2);
    }

    @Test
    public void shouldBeNonEqualGivenNonEqualNameAndValue() {
        // given
        IEnvironmentVariable var1 = createEnvironmentVariable("foo", "bar");
        IEnvironmentVariable var2 = createEnvironmentVariable("kung", "foo");
        // when
        // then
        assertThat(var1).isNotEqualTo(var2);
    }

    @Test
    public void shouldReturnEmptyMapGivenEmptyEnvVars() {
        // given
        Collection<IEnvironmentVariable> envVars = Collections.emptyList();
        // when
        Map<String, String> envVarsMap = EnvironmentVariableUtils.toMapOfStrings(envVars);
        // then
        assertThat(envVarsMap).isEmpty();
    }

    @SuppressWarnings("serial")
    @Test
    public void shouldReturnMapOfStringsForEnvVariables() {
        // given
        Collection<IEnvironmentVariable> envVars = createEnvironmentVariables("foo", "bar", "kung", "foo", "smurfHater",
                "gargamel");
        // when
        Map<String, String> envVarsMap = EnvironmentVariableUtils.toMapOfStrings(envVars);
        // then
        assertThat(envVarsMap).isEqualTo(new HashMap<String, String>() {
            {
                put("foo", "bar");
                put("kung", "foo");
                put("smurfHater", "gargamel");
            }
        });
    }

    private Collection<IEnvironmentVariable> createEnvironmentVariables(String... touples) {
        assertThat(touples).isNotNull();
        assertThat(touples.length % 2).isEqualTo(0);

        ArrayList<IEnvironmentVariable> envVars = new ArrayList<IEnvironmentVariable>();
        for (int i = 0; i < touples.length;) {
            envVars.add(createEnvironmentVariable(touples[i++], touples[i++]));
        }
        return envVars;
    }

    private IEnvironmentVariable createEnvironmentVariable(String name, String value) {
        ModelNodeBuilder builder = new ModelNodeBuilder();
        builder.set(EnvironmentVariable.NAME, name).set(EnvironmentVariable.VALUE, value);
        return new EnvironmentVariable(builder.build(), null);
    }
}
