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
package com.openshift.restclient.capability.resources;

import com.openshift.restclient.capability.ICapability;
/**
 * Backwards compatibility for services that only support a single port.  Deprecate
 * with v1beta1?
 * 
 * @author jeff.cantrill
 *
 */
public interface IServiceSinglePortSupport extends ICapability {
	
	int getPort();
	void setContainerPort(int port);
	int getContainerPort();
	void setPort(int port);
}
