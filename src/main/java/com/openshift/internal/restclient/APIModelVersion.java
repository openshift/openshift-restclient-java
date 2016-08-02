/*******************************************************************************
 * Copyright (c) 2015 Red Hat, Inc. Distributed under license by Red Hat, Inc.
 * All rights reserved. This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Red Hat, Inc.
 ******************************************************************************/
package com.openshift.internal.restclient;

import java.util.Comparator;

/**
 * @author Jeff Cantrill
 */
public interface APIModelVersion {

	int getOrder();

	static class VersionComparitor implements Comparator<APIModelVersion> {
		@Override
		public int compare(APIModelVersion v1, APIModelVersion v2) {
			if(v2 == null) return 1;
			if(v1 == null) return -1;
			if(v1.getOrder() < v2.getOrder()) return -1;
			if(v1.getOrder() > v2.getOrder()) return 1;
			return 0;
		}
	};
}
