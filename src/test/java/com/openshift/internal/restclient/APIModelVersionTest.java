/*******************************************************************************
 * Copyright (c) 2015 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package com.openshift.internal.restclient;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import com.openshift.internal.restclient.APIModelVersion.VersionComparitor;

/**
 * 
 * @author Jeff Cantrill
 *
 */
public class APIModelVersionTest {

	@Test
	public void testVersionComparitor() {
		VersionComparitor comparitor = new APIModelVersion.VersionComparitor();
		int result = comparitor.compare(OpenShiftAPIVersion.v1, OpenShiftAPIVersion.v1beta3);
		assertEquals("Exp. v1 to be greater then v1beta3", 1,result);
	}

}
