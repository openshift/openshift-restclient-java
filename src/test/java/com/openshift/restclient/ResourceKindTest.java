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

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class ResourceKindTest {

    @Test
    public void testPluralizeWhenNull() {
        assertEquals("", ResourceKind.pluralize(null));
    }

    @Test
    public void testPluralizeWhenEmpty() {
        assertEquals("", ResourceKind.pluralize(" "));
    }

    @Test
    public void testPluralizeWhenEndsWithAnS() {
        assertEquals("Status", ResourceKind.pluralize(ResourceKind.STATUS));
    }

    @Test
    public void testPluralizeWhenEndsWithY() {
        assertEquals("Families", ResourceKind.pluralize("Family"));
    }

    @Test
    public void testPluralizeWhenEndsWithAnythingElse() {
        assertEquals("Services", ResourceKind.pluralize(ResourceKind.SERVICE));
    }

    @Test
    public void testPluralizeCamelCaseKind() {
        assertEquals("ReplicationControllers", ResourceKind.pluralize(ResourceKind.REPLICATION_CONTROLLER));
    }

    @Test
    public void testPluralizeCamelCaseKindForceLower() {
        assertEquals("replicationcontrollers", ResourceKind.pluralize(ResourceKind.REPLICATION_CONTROLLER, true, true));
    }

    @Test
    public void testPluralizeCamelCaseKindUncapitalize() {
        assertEquals("replicationControllers",
                ResourceKind.pluralize(ResourceKind.REPLICATION_CONTROLLER, false, true));
    }

}
