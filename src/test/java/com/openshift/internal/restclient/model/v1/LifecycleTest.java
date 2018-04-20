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

package com.openshift.internal.restclient.model.v1;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.jboss.dmr.ModelNode;
import org.junit.Before;
import org.junit.Test;

import com.openshift.internal.restclient.model.ExecAction;
import com.openshift.internal.restclient.model.Lifecycle;
import com.openshift.restclient.model.IExecAction;
import com.openshift.restclient.model.IHandler;
import com.openshift.restclient.model.ILifecycle;
import com.openshift.restclient.utils.Samples;

/**
 * @author Ulf Lilleengen
 */
public class LifecycleTest {
    private ILifecycle lifecycle;

    @Before
    public void setup() {
        ModelNode node = ModelNode.fromJSONString(Samples.V1_LIFECYCLE.getContentAsString());
        lifecycle = Lifecycle.fromJson(node);
    }

    @Test
    public void testPostStart() {
        assertTrue(lifecycle.getPostStart().isPresent());
        assertEquals(IHandler.EXEC, lifecycle.getPostStart().get().getType());
        IExecAction exec = (IExecAction)lifecycle.getPostStart().get();
        assertEquals(1, exec.getCommand().size());
        assertEquals("postcmd1", exec.getCommand().get(0));
    }

    @Test
    public void testPreStop() {
        assertTrue(lifecycle.getPreStop().isPresent());
        assertEquals(IHandler.EXEC, lifecycle.getPreStop().get().getType());
        IExecAction exec = (IExecAction)lifecycle.getPreStop().get();
        assertEquals(2, exec.getCommand().size());
        assertEquals("precmd1", exec.getCommand().get(0));
        assertEquals("precmd2", exec.getCommand().get(1));
    }

    @Test
    public void testBuilder() {
        lifecycle = new Lifecycle.Builder()
                .preStop(new ExecAction.Builder()
                        .command("cmd1")
                        .build())
                .postStart(new ExecAction.Builder()
                        .command("cmd2")
                        .build())
                .build();

        assertTrue(lifecycle.getPreStop().isPresent());
        assertTrue(lifecycle.getPostStart().isPresent());

        assertEqualJson("{\"preStop\":{\"exec\":{\"command\":[\"cmd1\"]}},\"postStart\":{\"exec\":{\"command\":[\"cmd2\"]}}}", lifecycle.toJson());
    }

    private static void assertEqualJson(String expected, String actual) {
        ModelNode expectedNode = ModelNode.fromJSONString(expected);
        ModelNode actualNode = ModelNode.fromJSONString(actual);
        assertEquals(expectedNode.toJSONString(true), actualNode.toJSONString(true));
    }
}

