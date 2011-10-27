/******************************************************************************* 
 * Copyright (c) 2011 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/ 
package org.jboss.tools.openshift.express.internal.client;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class OpenShiftCoreActivator implements BundleActivator {

	public static final String PLUGIN_ID = "org.jboss.tools.openshift.express.client"; 

	private static BundleContext context;

	static BundleContext getContext() {
		return context;
	}

	public void start(BundleContext bundleContext) throws Exception {
		OpenShiftCoreActivator.context = bundleContext;
	}

	public void stop(BundleContext bundleContext) throws Exception {
		OpenShiftCoreActivator.context = null;
	}
}
