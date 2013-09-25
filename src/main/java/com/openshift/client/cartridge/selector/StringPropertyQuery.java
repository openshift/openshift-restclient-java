/******************************************************************************* 
 * Copyright (c) 2012 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/
package com.openshift.client.cartridge.selector;

import java.util.regex.Pattern;

import com.openshift.client.cartridge.ICartridge;
import com.openshift.client.cartridge.IEmbeddableCartridge;
import com.openshift.client.cartridge.IStandaloneCartridge;
import com.openshift.internal.client.cartridge.AbstractCartridgeQuery;
import com.openshift.internal.client.utils.Assert;

/**
 * A constraint that shall match available cartridges by url. 
 * 
 * @author Andre Dietisheim
 * 
 * @see IEmbeddableCartridge 
 * @see IStandaloneCartridge
 */
public abstract class StringPropertyQuery extends AbstractCartridgeQuery {

	private final Pattern namePattern;

	public StringPropertyQuery(final String propertyPattern) {
		Assert.isTrue(propertyPattern != null);
		this.namePattern = Pattern.compile(propertyPattern);
	}
	
	

	public StringPropertyQuery(Pattern pattern) {
		Assert.isTrue(pattern != null);
		this.namePattern = pattern;
	}

	@Override
	public <C extends ICartridge> boolean matches(C cartridge) {
		if (cartridge == null) {
			return false;
		}
		String propertyValue = getProperty(cartridge);
		if (propertyValue == null) {
			return namePattern == null;
		}
		return namePattern.matcher(propertyValue).matches();
	}
	
	protected abstract <C extends ICartridge> String getProperty(C cartridge);
}
