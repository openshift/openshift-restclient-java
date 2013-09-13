/******************************************************************************* 
 * Copyright (c) 2012 Red Hat, Inc. 
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
import static org.junit.Assert.assertEquals;

import org.fest.assertions.AssertExtension;

import com.openshift.client.IApplication;
import com.openshift.client.IDomain;
import com.openshift.client.OpenShiftException;

/**
 * @author Andre Dietisheim
 */
public class DomainAssert implements AssertExtension {

	private IDomain domain;

	public DomainAssert(IDomain domain) {
		this.domain = domain;
	}

	public DomainAssert hasId(String id) {
		assertEquals(domain.getId(), id);
		return this;
	}

	public DomainAssert hasSuffix(String suffix) throws OpenShiftException {
		assertEquals(domain.getSuffix(), suffix);
		return this;
	}

	public DomainAssert hasApplications(int size) {
		assertThat(domain.getApplications()).hasSize(size);
		return this;
	}
	
	public DomainAssert hasApplications(IApplication... applications) {
		assertThat(domain.getApplications()).contains(applications);
		return this;
	}
}
