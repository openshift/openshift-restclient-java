/*******************************************************************************
 * Copyright (c) 2015 Red Hat, Inc. Distributed under license by Red Hat, Inc.
 * All rights reserved. This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Red Hat, Inc.
 ******************************************************************************/
package com.openshift.internal.restclient.capability;

import java.util.Map;

import com.openshift.internal.restclient.capability.server.ServerTemplateProcessing;
import com.openshift.restclient.IClient;
import com.openshift.restclient.capability.ICapability;
import com.openshift.restclient.capability.server.ITemplateProcessing;

/**
 * Convenience class to initialize capabilies.  Only adds entry
 * to underlying map if the capability is supported 
 * 
 * @author Jeff Cantrill
 */
public class CapabilityInitializer {

	/**
	 * Registers the capability if it is supported
	 * @param capabilities
	 * @param capability
	 * @param impl
	 */
	private static void initializeCapability(Map<Class<? extends ICapability>, ICapability> capabilities, Class<? extends ICapability> capability, ICapability impl){
		if(impl.isSupported()){
			capabilities.put(capability, impl);
		}
	}
	public static void initializeClientCapabilities(Map<Class<? extends ICapability>, ICapability> capabilities, IClient client){
		initializeCapability(capabilities, ITemplateProcessing.class, new ServerTemplateProcessing(client));
	}
}
