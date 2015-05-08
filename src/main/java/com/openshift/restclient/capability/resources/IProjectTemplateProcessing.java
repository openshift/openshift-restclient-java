/*******************************************************************************
 * Copyright (c) 2015 Red Hat, Inc. Distributed under license by Red Hat, Inc.
 * All rights reserved. This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package com.openshift.restclient.capability.resources;

import java.util.Collection;

import com.openshift.restclient.capability.ICapability;
import com.openshift.restclient.model.IResource;
import com.openshift.restclient.model.template.ITemplate;

public interface IProjectTemplateProcessing extends ICapability {

	/**
	 * Processes a template to substitute the parameters
	 * where necessary for project
	 * 
	 * @param template        The template to process
	 * @return a config of resources
	 */
	ITemplate process(ITemplate template);
	
	/**
	 * Apply the objects defined in the template to the server
	 * @param config
	 * @return
	 */
	Collection<IResource> apply(ITemplate template);
	
}
