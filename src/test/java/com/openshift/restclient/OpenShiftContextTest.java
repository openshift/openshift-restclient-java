/*******************************************************************************
 * Copyright (c) 2015 Red Hat, Inc. Distributed under license by Red Hat, Inc.
 * All rights reserved. This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Red Hat, Inc.
 ******************************************************************************/

package com.openshift.restclient;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;

import org.junit.After;
import org.junit.Test;

/**
 * 
 * @author Fred Bricon
 *
 */
public class OpenShiftContextTest {

    @After
    public void tearDown() {
        OpenShiftContext.get().clear();
    }

    @Test
    public void testGetValue() {
        String value = "bar";
        OpenShiftContext.get().put("foo", value);
        assertEquals(value, OpenShiftContext.get().get("foo"));
    }

    @Test
    public void testRemove() {
        OpenShiftContext.get().put("foo", "bar");
        OpenShiftContext.get().remove("foo");
        assertNull(OpenShiftContext.get().get("foo"));
    }

    @Test
    public void testClear() {
        OpenShiftContext.get().put("foo", "bar");
        OpenShiftContext.get().clear();
        assertNull(OpenShiftContext.get().get("foo"));
    }

    @Test
    public void testConcurrency() throws Exception {
        OpenShiftContext[] contexts = new OpenShiftContext[2];
        Thread t0 = new SomeThread(0, contexts);
        Thread t1 = new SomeThread(1, contexts);

        t0.start();
        t1.start();
        t0.join();
        t1.join();

        assertNotSame(contexts[0], contexts[1]);
        assertEquals(t0.getName(), contexts[0].get("foo"));
        assertEquals(t1.getName(), contexts[1].get("foo"));
    }

    static class SomeThread extends Thread {

        private int index;
        private OpenShiftContext[] contexts;

        SomeThread(int index, OpenShiftContext[] contexts) {
            super("Thread-" + index);
            this.index = index;
            this.contexts = contexts;
        }

        public void run() {
            assertNull(OpenShiftContext.get().get("foo"));
            OpenShiftContext.get().put("foo", getName());
            contexts[index] = OpenShiftContext.get();
        }
    }
}
