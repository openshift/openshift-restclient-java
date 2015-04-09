/*******************************************************************************
 * Copyright (c) 2015 Red Hat, Inc. Distributed under license by Red Hat, Inc.
 * All rights reserved. This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Red Hat, Inc.
 ******************************************************************************/
package com.openshift.restclient.capability;

import java.util.Map;

import com.openshift.internal.restclient.capability.resources.DeploymentConfigTraceability;
import com.openshift.internal.restclient.capability.resources.DeploymentTraceability;
import com.openshift.internal.restclient.capability.resources.TagCapability;
import com.openshift.internal.restclient.capability.resources.TemplateTraceability;
import com.openshift.internal.restclient.capability.server.DefaultImageRegistryHosting;
import com.openshift.internal.restclient.capability.server.ServerTemplateProcessing;
import com.openshift.restclient.IClient;
import com.openshift.restclient.capability.resources.IDeploymentConfigTraceability;
import com.openshift.restclient.capability.resources.IDeploymentTraceability;
import com.openshift.restclient.capability.resources.ITags;
import com.openshift.restclient.capability.resources.ITemplateTraceability;
import com.openshift.restclient.capability.server.IImageRegistryHosting;
import com.openshift.restclient.capability.server.ITemplateProcessing;
import com.openshift.restclient.model.IResource;

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
