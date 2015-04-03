/*******************************************************************************
 * Copyright (c) 2015 Red Hat, Inc. Distributed under license by Red Hat, Inc.
 * All rights reserved. This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Red Hat, Inc.
 ******************************************************************************/
package com.openshift.internal.restclient.capability.resources;

import java.util.Arrays;
import java.util.Collection;

import com.openshift.restclient.capability.resources.ITags;
import com.openshift.restclient.model.IResource;

public class TagCapability extends AnnotationCapability implements ITags {

	public TagCapability(IResource resource) {
		super(TagCapability.class.getSimpleName(), resource);
	}

	@Override
	public Collection<String> getTags() {
		String value = getAnnotationValue();
		return Arrays.asList(value.split(","));
	}

	@Override
	protected String getAnnotationKey() {
		return "tags";
	}

}
