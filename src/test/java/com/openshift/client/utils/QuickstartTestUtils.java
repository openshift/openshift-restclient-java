/******************************************************************************* 
 * Copyright (c) 2014 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/
package com.openshift.client.utils;

import java.util.List;

import com.openshift.client.IQuickstart;

/**
 * @author André Dietisheim
 */
public class QuickstartTestUtils {

	public static IQuickstart getByName(String name, List<IQuickstart> quickstarts) {
		IQuickstart matchingQuickstart = null;
		for (IQuickstart quickstart : quickstarts) {
			if (name.equals(quickstart.getName())) {
				matchingQuickstart = quickstart;
				break;
			}
		}
		return matchingQuickstart;
	}


}
