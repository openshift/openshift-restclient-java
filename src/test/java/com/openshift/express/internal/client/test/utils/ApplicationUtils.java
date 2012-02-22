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
package com.openshift.express.internal.client.test.utils;

import com.openshift.express.client.Cartridge;
import com.openshift.express.client.IApplication;
import com.openshift.express.client.ICartridge;
import com.openshift.express.client.IOpenShiftService;
import com.openshift.express.client.OpenShiftException;
import com.openshift.express.client.User;

/**
 * @author André Dietisheim
 */
public class ApplicationUtils {

	public static String createRandomApplicationName() {
		return String.valueOf(System.currentTimeMillis());
	}

	public static IApplication createApplication(User user, IOpenShiftService service) throws OpenShiftException {
		return service.createApplication(createRandomApplicationName(), Cartridge.JBOSSAS_7, user);
	}

	public static void silentlyDestroyAS7Application(String name, User user, IOpenShiftService service) {
		silentlyDestroyApplication(name, ICartridge.JBOSSAS_7, user, service);
	}
	
	public static void silentlyDestroyRubyApplication(String name, User user, IOpenShiftService service) {
		silentlyDestroyApplication(name, ICartridge.RUBY_18, user, service);
	}

	public static void silentlyDestroyJenkinsApplication(String name, User user, IOpenShiftService service) {
		silentlyDestroyApplication(name, ICartridge.JENKINS_14, user, service);
	}

	public static void silentlyDestroyApplication(String name, ICartridge cartridge, User user,
			IOpenShiftService service) {
		try {
			if (name == null) {
				return;
			}
			service.destroyApplication(name, cartridge, user);
		} catch (OpenShiftException e) {
			e.printStackTrace();
		}
	}

	public static void silentlyDestroyAnyJenkinsApplication(User user) {
		try {
			for (IApplication application : user.getApplicationsByCartridge(ICartridge.JENKINS_14)) {
				application.destroy();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void silentlyDestroyAllApplications(User user) {
		try {
			for (IApplication application : user.getApplications()) {
				application.destroy();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
