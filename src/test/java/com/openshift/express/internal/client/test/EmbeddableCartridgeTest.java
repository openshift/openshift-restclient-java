/******************************************************************************* 
 * Copyright (c) 2007 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/
package com.openshift.express.internal.client.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.openshift.express.client.IEmbeddableCartridge;
import com.openshift.express.internal.client.EmbeddableCartridge;

/**
 * @author André Dietisheim
 */
public class EmbeddableCartridgeTest {

	@Test
	public void cartridgeEqualsOtherCartridgeWithSameName() {
		assertEquals(new EmbeddableCartridge("redhat"), new EmbeddableCartridge("redhat"));
		assertEquals(IEmbeddableCartridge.JENKINS_14, new EmbeddableCartridge(IEmbeddableCartridge.JENKINS_14.getName()));
		assertEquals(
				new EmbeddableCartridge("redhat", "http://www.redhat.com"),
				new EmbeddableCartridge("redhat", "http://www.jboss.com/"));
		assertTrue(!new EmbeddableCartridge("redhat").equals(new EmbeddableCartridge("jboss")));
	}
}
