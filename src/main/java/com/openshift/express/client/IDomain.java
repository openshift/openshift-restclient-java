/*******************************************************************************
 * Copyright (c) 2011 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package com.openshift.express.client;

/**
 * @author André Dietisheim
 */
public interface IDomain {

	public void setNamespace(String namespace) throws OpenShiftException;

	public String getNamespace();

	public String getRhcDomain() throws OpenShiftException;

	public void destroy() throws OpenShiftException;
	
	/**
	 * Waits for the domain to become accessible. A domain is considered as
	 * accessible as soon as at least 1 application url in it resolves to a
	 * valid ip address.
	 * 
	 * @return boolean true if at least 1 application within this domain
	 *         resolves
	 * @throws OpenShiftException
	 */
	public boolean waitForAccessible(long timeout) throws OpenShiftException;
}