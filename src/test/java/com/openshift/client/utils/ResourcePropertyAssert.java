/******************************************************************************* 
 * Copyright (c) 2013 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/
package com.openshift.client.utils;

import static org.fest.assertions.Assertions.assertThat;

import org.fest.assertions.AssertExtension;

import com.openshift.internal.client.response.ResourceProperty;

/**
 * @author Andre Dietisheim
 */
public class ResourcePropertyAssert implements AssertExtension {

	private ResourceProperty property;

	public ResourcePropertyAssert(ResourceProperty property) {
		assertThat(property).isNotNull();
		this.property = property;
	}

	public ResourcePropertyAssert hasName(String name) {
		assertThat(property.getName()).isEqualTo(name);
		return this;
	}

	public ResourcePropertyAssert hasDescription(String description) {
		assertThat(property.getDescription()).isEqualTo(description);
		return this;
	}

	public ResourcePropertyAssert hasType(String type) {
		assertThat(property.getType()).isEqualTo(type);
		return this;
	}

	public ResourcePropertyAssert hasValue(String value) {
		assertThat(property.getValue()).isEqualTo(value);
		return this;
	}
}
