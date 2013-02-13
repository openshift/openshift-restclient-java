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
package com.openshift.client.utils;

import java.util.Collection;
import java.util.List;

import com.openshift.client.IApplication;
import com.openshift.client.ICartridgeConstraint;
import com.openshift.client.IEmbeddableCartridge;
import com.openshift.client.IEmbeddedCartridge;
import com.openshift.client.IOpenShiftConnection;
import com.openshift.client.LatestVersionOf;
import com.openshift.client.OpenShiftException;

/**
 * @author André Dietisheim
 */
public class EmbeddedCartridgeTestUtils {

	public static String createRandomApplicationName() {
		return String.valueOf(System.currentTimeMillis());
	}

	public static void silentlyDestroy(ICartridgeConstraint cartridgeConstraint,
			IApplication application) {
		try {
			if (cartridgeConstraint == null
					|| application == null) {
				return;
			}
			application.removeEmbeddedCartridges(cartridgeConstraint);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void silentlyDestroy(IEmbeddableCartridge cartridge, IApplication application) {
		try {
			if (cartridge == null
					|| application == null) {
				return;
			}
			application.removeEmbeddedCartridge(cartridge);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void silentlyDestroyAllEmbeddedCartridges(IApplication application) {
		if (application == null) {
			return;
		}

		try {
			for (IEmbeddedCartridge cartridge : application.getEmbeddedCartridges()) {
				silentlyDestroy(cartridge, application);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void ensureHasEmbeddedCartridges(ICartridgeConstraint constraint,	IApplication application) 
			throws OpenShiftException {
		if (constraint == null
				|| application == null) {
			return;
		}
		Collection<IEmbeddedCartridge> embeddedCartridges = application.getEmbeddedCartridges(constraint);
		for (IEmbeddedCartridge embeddedCartridge : embeddedCartridges) {
			ensureHasEmbeddedCartridge(embeddedCartridge, application);
		}
	}

	public static void ensureHasEmbeddedCartridge(IEmbeddableCartridge cartridge, IApplication application)
			throws OpenShiftException {
		if (cartridge == null
				|| application == null) {
			return;
		}

		if (application.hasEmbeddedCartridge(cartridge)) {
			return;
		}

		application.addEmbeddableCartridge(cartridge);
	}

	public static Collection<IEmbeddableCartridge> getEmbeddableCartridges(ICartridgeConstraint constraint, IOpenShiftConnection connection) {
		List<IEmbeddableCartridge> allCartridges = connection.getEmbeddableCartridges();
		return constraint.getMatching(allCartridges);
	}


	public static IEmbeddableCartridge getLatestMySqlCartridge(IOpenShiftConnection connection) {
		Collection<IEmbeddableCartridge> embeddableCartridges = getEmbeddableCartridges(LatestVersionOf.mySQL(), connection);
		if (embeddableCartridges.size() < 1) {
			throw new IllegalStateException("No mysql embeddable cartridge found!");
		}
		return embeddableCartridges.iterator().next();
	}
}
