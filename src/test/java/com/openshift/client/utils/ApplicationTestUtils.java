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
			e.printStackTrace();
		}
	}

	public static void silentlyDestroyAllApplications(IDomain domain) {
		if (domain == null) {
			return;
		}
		
		try {
			for (IApplication application : domain.getApplications()) {
				application.destroy();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void silentlyDestroy1Application(IDomain domain) {
		if (domain == null) {
			return;
		}
		
		try {
			Iterator<IApplication> it = domain.getApplications().iterator();
			if (it.hasNext()) {
				it.next().destroy();
			}
		} catch (Exception e) {
			e.printStackTrace();
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

		return domain.createApplication(StringUtils.createRandomString(), cartridge, null, null);
	}

}
