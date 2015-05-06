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

import com.openshift.internal.restclient.capability.resources.ClientCapability;
import com.openshift.internal.restclient.capability.resources.DeploymentConfigTraceability;
import com.openshift.internal.restclient.capability.resources.DeploymentTraceability;
import com.openshift.internal.restclient.capability.resources.ProjectTemplateListCapability;
import com.openshift.internal.restclient.capability.resources.ProjectTemplateProcessing;
import com.openshift.internal.restclient.capability.resources.TagCapability;
import com.openshift.internal.restclient.capability.resources.TemplateTraceability;
import com.openshift.internal.restclient.capability.server.ServerTemplateProcessing;
import com.openshift.restclient.IClient;
import com.openshift.restclient.capability.ICapability;
import com.openshift.restclient.capability.resources.IClientCapability;
import com.openshift.restclient.capability.resources.IDeploymentConfigTraceability;
import com.openshift.restclient.capability.resources.IDeploymentTraceability;
import com.openshift.restclient.capability.resources.IProjectTemplateList;
import com.openshift.restclient.capability.resources.IProjectTemplateProcessing;
import com.openshift.restclient.capability.resources.ITags;
import com.openshift.restclient.capability.resources.ITemplateTraceability;
import com.openshift.restclient.capability.server.ITemplateProcessing;
import com.openshift.restclient.model.IProject;
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
	
	/**
	 * Initialize Project specific capabilities
	 * @param capabilities
	 * @param resource
	 */
	public static void initializeCapabilities(Map<Class<? extends ICapability>, ICapability> capabilities, IProject project, IClient client){
		initializeCapability(capabilities, IProjectTemplateProcessing.class, new ProjectTemplateProcessing(project, client));
		initializeCapability(capabilities, IProjectTemplateList.class, new ProjectTemplateListCapability(project, client));
	}
	
	public static void initializeCapabilities(Map<Class<? extends ICapability>, ICapability> capabilities, IResource resource, IClient client){
		initializeCapability(capabilities, ITemplateTraceability.class, new TemplateTraceability(resource));
		initializeCapability(capabilities, IDeploymentConfigTraceability.class, new DeploymentConfigTraceability(resource, client));
		initializeCapability(capabilities, IDeploymentTraceability.class, new DeploymentTraceability(resource, client));
		initializeCapability(capabilities, ITags.class, new TagCapability(resource));
		initializeCapability(capabilities, IClientCapability.class, new ClientCapability(client));
	}
	
	public static void initializeClientCapabilities(Map<Class<? extends ICapability>, ICapability> capabilities, IClient client){
		initializeCapability(capabilities, ITemplateProcessing.class, new ServerTemplateProcessing(client));
	}
}
