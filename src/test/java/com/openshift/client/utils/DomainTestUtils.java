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

import java.util.Iterator;

import com.openshift.client.IDomain;
import com.openshift.client.IUser;
import com.openshift.client.OpenShiftException;

/**
 * @author Andre Dietisheim
 */
public class DomainTestUtils {

	public static void silentlyDestroyAllDomains(IUser user) {
		if (user == null) {
			return;
		}
		try {
			for (IDomain domain : user.getDomains()) {
				silentlyDestroy(domain);
			}
		} catch (Exception e) {
//			e.printStackTrace();
		}
	}

	public static void silentlyDestroy(IDomain domain) {
		if (domain == null) {
			return;
		}
		try {
			domain.destroy(true);
		} catch (Exception e) {
//			e.printStackTrace();
		}
	}

	public static IDomain ensureHasDomain(IUser user) throws OpenShiftException {
		return getFirstDomainOrCreate(user);
	}

	public static IDomain getFirstDomainOrCreate(IUser user) throws OpenShiftException {
		IDomain domain = null;
		domain = getFirstDomain(user);

		if (domain == null) {
			domain = user.createDomain(createRandomName());
		}

		return domain;
	}

	public static IDomain getFirstDomain(IUser user) throws OpenShiftException {
		IDomain domain = null;
		Iterator<IDomain> domainIterator = user.getDomains().iterator(); 
		if (domainIterator.hasNext()) {
			domain = domainIterator.next();
		}
		return domain;
	}

	public static String createRandomName() {
		return String.valueOf(System.currentTimeMillis());
	}
}
