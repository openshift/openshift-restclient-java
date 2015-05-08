/*******************************************************************************
 * Copyright (c) 2015 Red Hat, Inc. Distributed under license by Red Hat, Inc.
 * All rights reserved. This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Red Hat, Inc.
 ******************************************************************************/
package com.openshift.restclient.capability.server;

import com.openshift.restclient.capability.ICapability;
import com.openshift.restclient.model.IResource;
import com.openshift.restclient.model.template.ITemplate;

/**
 * Add capability to process a template
 * 
 * @author Jeff Cantrill
 */
public interface ITemplateProcessing extends ICapability {
	
	/**
	 * Processes the template to substitute the parameters
	 * where necessary
	 * 
	 * @param template        The template to process
	 * @param namespace     The namespace to use when processing the template
	 * @return IConfig pre v1beta3; ITemplate otherwise
	 */
	<T extends IResource> T process(ITemplate template, String namespace);
}
