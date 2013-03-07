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

import static org.junit.Assert.assertTrue;

import java.util.Iterator;
import java.util.List;

import com.openshift.client.IApplication;
import com.openshift.client.ICartridge;
import com.openshift.client.IDomain;
import com.openshift.client.IOpenShiftConnection;
import com.openshift.client.OpenShiftException;

/**
 * @author André Dietisheim
 */
public class ApplicationTestUtils {

	private static final long WAIT_FOR_APPLICATION = 3 * 60 * 1000;

	public static String createRandomApplicationName() {
		return String.valueOf(System.currentTimeMillis());
	}

	public static IApplication createApplication(ICartridge cartridge, IDomain domain) {
		IApplication application = domain.createApplication(createRandomApplicationName(), cartridge);
		assertTrue(application.waitForAccessible(WAIT_FOR_APPLICATION));
		return application;
	}

	public static void silentlyDestroy(IApplication application) {
		try {
			if (application == null) {
				return;
			}
			application.destroy();
		} catch (Exception e) {
			// e.printStackTrace();
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
	 * Deletes applications that are above the given maximum number of
	 * applications.
	 * 
	 * @param maxApplications
	 * @param domain
	 */
	public static void silentlyDestroyIfMoreThan(int maxApplications, IDomain domain) {
		if (domain == null) {
			return;
		}

		int toDestroy = domain.getApplications().size() - maxApplications;

		for (Iterator<IApplication> it = domain.getApplications().iterator(); it.hasNext() && toDestroy > 0; toDestroy--) {
			silentlyDestroy(it.next());
		}
	}

	/**
	 * Makes sure the given domain has exactly the given number of applications.
	 * Either the excessive applications are destroyed or new ones (with the
	 * given cartridge) are created to match the given number.
	 * 
	 * @param numOfApplications
	 * @param cartridge
	 * @param domain
	 */
	public static void ensureHasExactly(int numOfApplications, ICartridge cartridge, IDomain domain) {
		if (domain == null) {
			return;
		}

		destroyAllByCartridge(cartridge, domain);
		List<IApplication> applications = domain.getApplications();
		int delta = numOfApplications - applications.size();
		if (delta < 0) {
			for (Iterator<IApplication> it = applications.iterator(); it.hasNext() && delta < 0; delta++) {
				it.next().destroy();
			}
		} else {
			for (Iterator<IApplication> it = applications.iterator(); it.hasNext() && delta > 0; delta--) {
				createApplication(cartridge, domain);
			}
		}
	}

	public static void destroyAllByCartridge(ICartridge cartridge, IDomain domain) {
		for (Iterator<IApplication> it = domain.getApplications().iterator(); it.hasNext();) {
			IApplication application = it.next();
			if (!application.getCartridge().equals(cartridge)) {
				application.destroy();
			}
		}
	}

	public static IApplication ensureHasExactly1Application(ICartridge cartridge, IDomain domain) {
		ensureHasExactly(1, ICartridge.JBOSSAS_7, domain);
		return domain.getApplications().get(0);
	}

	public static IOpenShiftConnection getConnection(IApplication application) {
		if (application == null) {
			return null;
		}
		return application.getDomain().getUser().getConnection();
	}

}
