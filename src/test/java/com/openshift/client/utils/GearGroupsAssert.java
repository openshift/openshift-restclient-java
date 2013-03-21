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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import java.util.Collection;
import java.util.Iterator;

import org.fest.assertions.AssertExtension;

import com.openshift.client.IGearGroup;
import com.openshift.internal.client.utils.StringUtils;

/**
 * @author André Dietisheim
 */
public class GearGroupsAssert implements AssertExtension {

	private Collection<IGearGroup> gearGroups;

	public GearGroupsAssert(Collection<IGearGroup> gearGroups) {
		this.gearGroups = gearGroups;
	}

	public GearGroupsAssert contains(String name) {
		assertFalse(StringUtils.isEmpty(name));
		assertNotNull("Asserted GearGroups didn't contain group with name " + name, getByName(name));
		return this;
	}

	public GearGroupAssert assertGroup(String name) {
		assertFalse(StringUtils.isEmpty(name));
		contains(name);
		return new GearGroupAssert(getByName(name));
	}

	public GearGroupAssert assertGroup(int number) {
		assertThat(number).isGreaterThanOrEqualTo(0);
		assertThat(number).isLessThanOrEqualTo(gearGroups.size());
		return new GearGroupAssert(getByNumber(number));
	}

	public GearGroupsAssert hasSize(int size) {
		assertThat(gearGroups).isNotNull().hasSize(size);
		return this;
	}

	private IGearGroup getByName(String name) {
		for (IGearGroup gearGroup : gearGroups) {
			if (name.equals(gearGroup.getName())) {
				return gearGroup;
			}
		}
		return null;
	}

	private IGearGroup getByNumber(int number) {
		int i = 0;
		Iterator<IGearGroup> iterator = gearGroups.iterator();
		while(iterator.hasNext()
				&& i <= number) {
			if (i == number) {
				return iterator.next();
			}
			i++;
		}
		return null;
	}
}
