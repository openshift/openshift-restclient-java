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
package com.openshift.internal.client;

import java.util.ArrayList;
import java.util.List;

import com.openshift.client.IEmbeddableCartridge;
import com.openshift.client.IEmbeddableCartridgeConstraint;
import com.openshift.client.IOpenShiftConnection;
import com.openshift.client.OpenShiftException;
import com.openshift.internal.client.utils.Assert;

/**
 * A base class for a constraint that shall match available embeddable
 * cartridges (on the platform). Among several matching ones, the one with the
 * highest version is chosen. application.
 * 
 * @author Andre Dietisheim
 * 
 * @see IEmbeddableCartridge for cartridges that have already been added and
 *      configured to an application.
 */
public abstract class AbstractEmbeddableCartridgeConstraint implements IEmbeddableCartridgeConstraint {

	public List<IEmbeddableCartridge> getEmbeddableCartridges(IOpenShiftConnection connection) {
		Assert.isTrue(connection != null);

		List<IEmbeddableCartridge> matchingCartridges = new ArrayList<IEmbeddableCartridge>();
		for (IEmbeddableCartridge cartridge : connection.getEmbeddableCartridges()) {
			if (matches(cartridge)) {
				matchingCartridges.add(cartridge);
			}
		}

		if (matchingCartridges.isEmpty()) {
			throw new OpenShiftException(createNoMatchErrorMessage(connection));
		}

		return matchingCartridges;
	}

	protected abstract String createNoMatchErrorMessage(IOpenShiftConnection connection);

	protected abstract boolean matches(IEmbeddableCartridge cartridge);
}
