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

import com.openshift.client.IApplication;
import com.openshift.client.OpenShiftException;
import com.openshift.client.cartridge.IEmbeddableCartridge;
import com.openshift.client.cartridge.IEmbeddedCartridge;
import com.openshift.client.cartridge.selector.LatestEmbeddableCartridge;
import com.openshift.client.cartridge.selector.LatestVersionOf;

/**
 * @author André Dietisheim
 */
public class EmbeddedCartridgeTestUtils {

	public static void silentlyDestroy(LatestEmbeddableCartridge selector, IApplication application) {
		try {
			if (selector == null
					|| application == null) {
				return;
			}
			application.removeEmbeddedCartridge(selector.get(application));
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

	/**
	 * Destroys all embedded cartridges from the given application. Ha-Proxy is
	 * not removed since it cannot get removed.
	 * 
	 * @param application
	 */
	public static void destroyAllEmbeddedCartridges(IApplication application) {
		if (application == null) {
			return;
		}

		IEmbeddableCartridge haProxy = LatestVersionOf.haProxy().get(application);
		for (IEmbeddedCartridge cartridge : application.getEmbeddedCartridges()) {
			// ha proxy can't get removed
			if (cartridge.equals(haProxy)) {
				continue;
			}
			silentlyDestroy(cartridge, application);
		}
	}

	/**
	 * Ensures the given application has the embedded cartridges that match the
	 * given constraint. The given application is checked for their presence and
	 * if they aren't they are added.
	 * 
	 * @param constraint
	 *            the constraint that selects the available catridges that
	 *            should be present
	 * @param application
	 *            the application that should have the constrained cartridges
	 * @throws OpenShiftException
	 */
	public static void ensureHasEmbeddedCartridges(LatestEmbeddableCartridge constraint, IApplication application)
			throws OpenShiftException {
		if (constraint == null
				|| application == null) {
			return;
		}

		IEmbeddableCartridge embeddedCartridge = constraint.get(application);
		ensureHasEmbeddedCartridge(embeddedCartridge, application);
	}

	public static void ensureHasEmbeddedCartridges(IApplication application, LatestEmbeddableCartridge... selectors)
			throws OpenShiftException {
		for (LatestEmbeddableCartridge selector : selectors) {
			ensureHasEmbeddedCartridges(selector, application);
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
}
