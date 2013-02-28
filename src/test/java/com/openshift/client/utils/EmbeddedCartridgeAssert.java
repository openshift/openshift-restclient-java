/******************************************************************************* 
 * Copyright (c) 2013 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/
package com.openshift.client.utils;

import static org.fest.assertions.Assertions.assertThat;

import java.net.URI;
import java.net.URISyntaxException;

import org.fest.assertions.AssertExtension;

import com.openshift.client.IEmbeddedCartridge;
import com.openshift.client.OpenShiftException;

/**
 * @author André Dietisheim
 */
public class EmbeddedCartridgeAssert implements AssertExtension {

	private IEmbeddedCartridge embeddedCartridge;

	public EmbeddedCartridgeAssert(IEmbeddedCartridge embeddedCartridge) {
		assertThat(embeddedCartridge).isNotNull();
		this.embeddedCartridge = embeddedCartridge;
	}

	public EmbeddedCartridgeAssert hasUrl() throws OpenShiftException, URISyntaxException {
		assertThat(embeddedCartridge.getUrl()).isNotEmpty();
		new URI(embeddedCartridge.getUrl());
		return this;
	}
}
