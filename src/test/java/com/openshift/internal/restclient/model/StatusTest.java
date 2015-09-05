/*******************************************************************************
 * Copyright (c) 2015 Red Hat, Inc. Distributed under license by Red Hat, Inc.
 * All rights reserved. This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package com.openshift.internal.restclient.model;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import org.jboss.dmr.ModelNode;
import org.junit.Before;
import org.junit.Test;

import com.openshift.internal.restclient.model.properties.ResourcePropertyKeys;
import com.openshift.internal.util.JBossDmrExtentions;
import com.openshift.restclient.ResourceKind;

/**
 * Tests that have logic and not specific to a particular api version
 * @author jeff.cantrill
 *
 */
public class StatusTest {

	private Status status;
	
	@Before
	public void setup() {
		ModelNode root = new ModelNode();
		JBossDmrExtentions.set(root, JBossDmrExtentions.getPath(ResourcePropertyKeys.KIND), ResourceKind.STATUS);
		status = spy(new Status(root,null,null));
	}
	
	@Test
	public void isFailureShouldReturnTrueWhenFailure() {
		doReturn("Failure").when(status).getStatus();
		
		assertTrue(status.isFailure());
	}

	@Test
	public void isFailureShouldReturnFalseWhenNotFailure() {
		doReturn("Other").when(status).getStatus();
		
		assertFalse(status.isFailure());
	}
}
