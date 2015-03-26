/*******************************************************************************
 * Copyright (c) 2015 Red Hat, Inc. Distributed under license by Red Hat, Inc.
 * All rights reserved. This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Red Hat, Inc.
 ******************************************************************************/
package com.openshift3.client.capability;

import java.util.Map;

import com.openshift3.client.IClient;
import com.openshift3.client.capability.resources.IDeploymentConfigTraceability;
import com.openshift3.client.capability.resources.IDeploymentTraceability;
import com.openshift3.client.capability.resources.ITags;
import com.openshift3.client.capability.resources.ITemplateTraceability;
import com.openshift3.client.capability.server.IImageRegistryHosting;
import com.openshift3.client.capability.server.ITemplateProcessing;
import com.openshift3.client.model.IResource;
import com.openshift3.internal.client.capability.resources.DeploymentConfigTraceability;
import com.openshift3.internal.client.capability.resources.DeploymentTraceability;
import com.openshift3.internal.client.capability.resources.TagCapability;
import com.openshift3.internal.client.capability.resources.TemplateTraceability;
import com.openshift3.internal.client.capability.server.DefaultImageRegistryHosting;
import com.openshift3.internal.client.capability.server.ServerTemplateProcessing;

/**
 * Convenience class to initialize capabilies.  Only adds entry
 * to underlying map if the capability is supported 
 */
public class CapabilityInitializer {

	/**
	 * Register the capability if it is supported
	 * @param capabilities
	 * @param capability
	 * @param impl
	 */
	private static void initializeCapability(Map<Class<? extends ICapability>, ICapability> capabilities, Class<? extends ICapability> capability, ICapability impl){
		if(impl.isSupported()){
			capabilities.put(capability, impl);
		}
	}
	
	public static void initializeCapabilities(Map<Class<? extends ICapability>, ICapability> capabilities, IResource resource, IClient client){
		initializeCapability(capabilities, ITemplateTraceability.class, new TemplateTraceability(resource));
		initializeCapability(capabilities, IDeploymentConfigTraceability.class, new DeploymentConfigTraceability(resource, client));
		initializeCapability(capabilities, IDeploymentTraceability.class, new DeploymentTraceability(resource, client));
		initializeCapability(capabilities, ITags.class, new TagCapability(resource));
	}
	
	public static void initializeClientCapabilities(Map<Class<? extends ICapability>, ICapability> capabilities, IClient client){
		initializeCapability(capabilities, IImageRegistryHosting.class, new DefaultImageRegistryHosting(client));
		initializeCapability(capabilities, ITemplateProcessing.class, new ServerTemplateProcessing(client));
	}
}
