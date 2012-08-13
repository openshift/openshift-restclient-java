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
package com.openshift.internal.client;

import com.openshift.client.IGearProfile;
import com.openshift.internal.client.utils.Assert;

/**
 * @author Andre Dietisheim
 */
public class GearProfile implements IGearProfile {
	
	private String name;

	public GearProfile(String name) {
		Assert.notNull(name);

		this.name = name;
	}

	public String getName() {
		return name;
	}
}
