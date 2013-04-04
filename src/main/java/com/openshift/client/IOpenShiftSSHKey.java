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
package com.openshift.client;


/**
 * @author Andre Dietisheim
 */
public interface IOpenShiftSSHKey extends ISSHPublicKey {

	/**
	 * Returns the name that is used to store this key on OpenShift. 
	 * 
	 * @return
	 */
	public String getName();
	
	/**
	 * Sets the public key portion of this ssh key to the OpenShift PaaS.
	 * 
	 * @param publicKey the new public key porition of this key
	 * @throws OpenShiftException
	 */
	public void setPublicKey(String publicKey) throws OpenShiftException;
	
	/**
	 * Sets the new type and public key of this ssh key to the OpenShift PaaS
	 * @param type
	 * @throws OpenShiftException
	 */
	public void setKeyType(SSHKeyType type, String publicKey) throws OpenShiftException;

	public void destroy() throws OpenShiftException;
}