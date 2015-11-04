/*******************************************************************************
 * Copyright (c) 2015 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package com.openshift.restclient.model;

import java.util.List;

import com.openshift.restclient.model.limit.ILimit;

/**
 * @author HyunsooKim1112
 */
public interface ILimitRange extends IResource {
	
	public List<ILimit> getLimits();
	
	public void setLimits(List<ILimit> iLimits);

}
