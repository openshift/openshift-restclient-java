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

import org.jboss.dmr.ModelNode;
import org.junit.Test;

import com.openshift.internal.util.JBossDmrExtentions;

public class JBossDmrExtentionsTest {

	@Test
	public void testAsMapWhenPropertyKeysAreNull() {
		assertNotNull(JBossDmrExtentions.asMap(new ModelNode(), null, null));
	}

}
