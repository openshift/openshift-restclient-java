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
package com.openshift.internal.client.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * @author Andre Dietisheim
 */
public class CollectionUtils {

	private CollectionUtils() {
	}

	public static <T> List<T> toUnmodifiableCopy(Collection<T> collection) {
		return Collections.unmodifiableList(new ArrayList<T>(collection));
	}

	public static <T> List<T> toList(T element, T... elements) {
		List<T> allElements = new ArrayList<T>();
		if (element != null) {
			allElements.add(element);
		}
		if (elements != null) {
			Collections.addAll(allElements, elements);
		}
		return allElements;
	}

}
