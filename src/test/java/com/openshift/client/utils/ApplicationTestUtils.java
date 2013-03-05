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

import java.util.Iterator;

import com.openshift.client.IApplication;
import com.openshift.client.ICartridge;
import com.openshift.client.IDomain;
import com.openshift.client.IOpenShiftConnection;
import com.openshift.client.OpenShiftException;

/**
 * @author André Dietisheim
 */
public class ApplicationTestUtils {

	public static String createRandomApplicationName() {
		return String.valueOf(System.currentTimeMillis());
	}

	public static void silentlyDestroy(IApplication application) {
		try {
			if (application == null) {
				return;
			}
			application.destroy();
		} catch (Exception e) {
//			e.printStackTrace();
		}
	}

	public static void silentlyDestroyAllApplications(IDomain domain) {
		if (domain == null) {
			return;
		}
		
		for (IApplication application : domain.getApplications()) {
			silentlyDestroy(application);
		}
	}

	public static void silentlyDestroyApplications(int appsToDestroy, IDomain domain) {
		if (domain == null) {
			return;
		}
		
		for (Iterator<IApplication> it = domain.getApplications().iterator(); it.hasNext() && appsToDestroy >= 0; appsToDestroy--) {
			silentlyDestroy(it.next());
		}
	}

	public static void silentlyDestroyAllApplicationsByCartridge(ICartridge cartridge, IDomain domain) {
		if (domain == null) {
			return;
		}

		for (Iterator<IApplication> it = domain.getApplicationsByCartridge(cartridge).iterator(); it
				.hasNext();) {
			silentlyDestroy(it.next());
		}
	}

	public static IApplication getOrCreateApplication(IDomain domain) throws OpenShiftException {
		return getOrCreateApplication(domain, ICartridge.JBOSSAS_7);
	}

	public static IApplication getOrCreateApplication(IDomain domain, ICartridge cartridge)
			throws OpenShiftException {
		for (Iterator<IApplication> it = domain.getApplications().iterator(); it.hasNext();) {
			IApplication application = it.next();
			if (cartridge.equals(application.getCartridge())) {
				return application;
			}
		}

		return domain.createApplication(StringUtils.createRandomString(), cartridge);
	}

	/**
	 * Deletes applications that are above the given maximum number of applications.
	 * 
	 * @param maxApplications 
	 * @param domain
	 */
	public static void silentlyEnsureHasMaxApplication(int maxApplications, IDomain domain) {
		if (domain == null) {
			return;
		}
		
		int toDestroy = domain.getApplications().size() - maxApplications; 
		
		for (Iterator<IApplication> it = domain.getApplications().iterator(); it.hasNext() && toDestroy > 0; toDestroy--) {			
			silentlyDestroy(it.next());
		}
	}
	
	public static IOpenShiftConnection getConnectin(IApplication application) {
		if (application == null) {
			return null;
		}
		return application.getDomain().getUser().getConnection();
	}

}
