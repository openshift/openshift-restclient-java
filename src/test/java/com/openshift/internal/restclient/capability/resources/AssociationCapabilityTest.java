/*******************************************************************************
 * Copyright (c) 2015 Red Hat, Inc. Distributed under license by Red Hat, Inc.
 * All rights reserved. This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Red Hat, Inc.
 ******************************************************************************/
package com.openshift.internal.restclient.capability.resources;

import static org.junit.Assert.*;

import org.junit.Test;

import com.openshift.internal.restclient.capability.resources.AssociationCapability;

public class AssociationCapabilityTest {

	@Test
	public void unsupportedWhenTheClientIsNull(){
		AssociationCapability capability = new AssociationCapability("MyCapability", null, null) {
				@Override
				protected String getAnnotationKey() {
					return "foobar";
				}
			};
		assertFalse("Exp. the capability to be unsupported because the IClient is null", capability.isSupported());
	}
}
