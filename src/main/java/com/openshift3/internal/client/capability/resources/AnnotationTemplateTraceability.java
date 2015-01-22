/*******************************************************************************
 * Copyright (c) 2015 Red Hat, Inc. Distributed under license by Red Hat, Inc.
 * All rights reserved. This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Red Hat, Inc.
 ******************************************************************************/
package com.openshift3.internal.client.capability.resources;

import com.openshift3.client.capability.resources.ITemplateTraceability;
import com.openshift3.client.model.IResource;

/**
 * Capability of resource tracing the template from which
 * it was generated
 */
public class AnnotationTemplateTraceability implements	ITemplateTraceability {

	private static final String TEMPLATE_ANNOTATION = "template";
	private IResource resource;

	public AnnotationTemplateTraceability(IResource resource) {
		this.resource = resource;
	}

	@Override
	public String getTemplateName() {
		if(!isSupported()) return "";
		return resource.getAnnotation(TEMPLATE_ANNOTATION);
	}

	@Override
	public boolean isSupported() {
		return resource.isAnnotatedWith(TEMPLATE_ANNOTATION);
	}

	@Override
	public String getName() {
		return AnnotationTemplateTraceability.class.getSimpleName();
	}

}
