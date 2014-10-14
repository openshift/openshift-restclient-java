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

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Iterator;
import java.util.List;

import com.openshift.client.ApplicationScale;
import com.openshift.client.IApplication;
import com.openshift.client.IDomain;
import com.openshift.client.IOpenShiftConnection;
import com.openshift.client.OpenShiftException;
import com.openshift.client.cartridge.ICartridge;
import com.openshift.client.cartridge.IStandaloneCartridge;
import com.openshift.client.cartridge.query.LatestStandaloneCartridge;
import com.openshift.client.cartridge.query.LatestVersionOf;

/**
 * @author André Dietisheim
 */
public class ApplicationTestUtils {

	// 3 minutes
	private static final long WAIT_FOR_APPLICATION = 3 * 60 * 1000;

	public static String createRandomApplicationName() {
		return "app" + String.valueOf(System.currentTimeMillis());
	}

	public static IApplication createApplication(IStandaloneCartridge cartridge, IDomain domain) {
		return createApplication(createRandomApplicationName(), cartridge, domain);
	}

	public static IApplication createApplication(String name, IStandaloneCartridge cartridge, IDomain domain) {
		if (cartridge == null) {
			cartridge = getDefaultCartridge(domain);
		}
		IApplication application = domain.createApplication(name, cartridge);
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

	public static void silentlyDestroyAllApplicationsByCartridge(IStandaloneCartridge cartridge, IDomain domain) {
		if (domain == null) {
			return;
		}

		for (Iterator<IApplication> it = domain.getApplicationsByCartridge(cartridge).iterator(); it
				.hasNext();) {
			silentlyDestroy(it.next());
		}
	}

	public static IApplication getOrCreateApplication(IDomain domain) throws OpenShiftException {
		return getOrCreateApplication(domain, null);
	}

	public static IApplication getOrCreateApplication(IDomain domain, IStandaloneCartridge cartridge)
			throws OpenShiftException {
		for (Iterator<IApplication> it = domain.getApplications().iterator(); it.hasNext();) {
			IApplication application = it.next();
			if (cartridge == null 
					|| cartridge.equals(application.getCartridge())) {
				return application;
			}
		}

		return createApplication("app" + StringUtils.createRandomString(), cartridge, domain);
	}

	/**
	 * Deletes applications that are above the given maximum number of
	 * applications.
	 * 
	 * @param maxApplications
	 * @param domain
	 */
	public static void destroyIfMoreThan(int maxApplications, IDomain domain) {
		if (domain == null) {
			return;
		}

		int toDestroy = domain.getApplications().size() - maxApplications;

		for (Iterator<IApplication> it = domain.getApplications().iterator(); it.hasNext() && toDestroy > 0; toDestroy--) {
			it.next().destroy();
		}
	}

	/**
	 * Makes sure the given domain has exactly the given number of applications
	 * with the given type. Either the excessive applications are destroyed or
	 * new ones (with the given cartridge) are created to match the given
	 * number.
	 * 
	 * @param numOfApplications
	 * @param cartridge
	 *            the required cartridge
	 * @param domain
	 */
	public static void ensureHasExactly(int numOfApplications, IStandaloneCartridge cartridge, IDomain domain) {
		assertNotNull(cartridge);
		if (domain == null) {
			return;
		}

		destroyAllNotOfType(cartridge, domain.getApplications());
		int delta = numOfApplications - domain.getApplications().size();
		if (delta < 0) {
			for (Iterator<IApplication> it = domain.getApplications().iterator(); it.hasNext() && delta < 0; delta++) {
				it.next().destroy();
			}
		} else {
			for (; delta > 0; delta--) {
				createApplication(cartridge, domain);
			}
		}
	}

	protected static void destroyAllNotOfType(IStandaloneCartridge cartridge, List<IApplication> applications) {
		for (Iterator<IApplication> it = applications.iterator(); it.hasNext();) {
			IApplication application = it.next();
			if (!cartridge.equals(application.getCartridge())) {
				application.destroy();
			}
		}
	}
	
	public static void destroyAllApplications(IDomain domain) {
		if (domain == null) {
			return;
		}

		for (IApplication application : domain.getApplications()) {
			application.destroy();
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

	public static IApplication ensureHasExactly1Application(IStandaloneCartridge cartridge, IDomain domain) {
		ensureHasExactly(1, cartridge, domain);
		return domain.getApplications().get(0);
	}

	public static IApplication ensureHasExactly1Application(IDomain domain) {
		return ensureHasExactly1Application(getDefaultCartridge(domain), domain);
	}
	
	public static IApplication ensureHasExactly1Application(LatestStandaloneCartridge selector, IDomain domain) {
		IStandaloneCartridge cartridge = selector.get(domain.getUser());
		ensureHasExactly(1, cartridge, domain);
		return domain.getApplications().get(0);
	}

	public static IApplication ensureHasExactly1NonScalableApplication(LatestStandaloneCartridge selector,
			IDomain domain) {
		IApplication application = ensureHasExactly1Application(selector, domain);
		return destroyAndRecreateIfScalable(application);
	}

	/**
	 * Returns the given application if it is not scalable, destroys it and
	 * creates a new one with the same cartridge and name otherwise.
	 * 
	 * @param application
	 * @return
	 */
	public static IApplication destroyAndRecreateIfScalable(IApplication application) {
		if (!ApplicationScale.NO_SCALE.equals(application.getGearProfile())) {
			IStandaloneCartridge cartridge = application.getCartridge();
			IDomain domain = application.getDomain();
			application.destroy();
			application = domain.createApplication(
					createRandomApplicationName(), cartridge, ApplicationScale.NO_SCALE);
		}
		return application;
	}

	public static IOpenShiftConnection getConnection(IApplication application) {
		if (application == null) {
			return null;
		}
		return application.getDomain().getUser().getConnection();
	}

	public static IOpenShiftConnection getConnection(IDomain domain) {
		if (domain == null) {
			return null;
		}
		return domain.getUser().getConnection();
	}
	
	public static IApplication destroyAllEnvironmentVariables(IApplication application) {
		for (String name : application.getEnvironmentVariables().keySet()) {
			application.removeEnvironmentVariable(name);;
		}
		return application;
	}

	private static IStandaloneCartridge getDefaultCartridge(IDomain domain) {
		return LatestVersionOf.php().get(domain.getUser());
	}
}
