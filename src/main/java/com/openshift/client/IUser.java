/******************************************************************************* 
 * Copyright (c) 2011 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/
package com.openshift.client;

import java.util.List;


/**
 * @author André Dietisheim
 */
public interface IUser extends IOpenShiftResource {

	public static final String ID = "com.openshift.client";

	public String getRhlogin();

	public String getPassword();

	public String getServer();
		
	public IOpenShiftConnection getConnection();

	public IDomain createDomain(String id) throws OpenShiftException;

	public List<IDomain> getDomains() throws OpenShiftException;
	
	public IDomain getDefaultDomain() throws OpenShiftException;
	
	public IDomain getDomain(String id) throws OpenShiftException;
	
	public boolean hasDomain() throws OpenShiftException;

	public boolean hasDomain(String id) throws OpenShiftException;

	public List<IOpenShiftSSHKey> getSSHKeys() throws OpenShiftException;

	public IOpenShiftSSHKey putSSHKey(String name, ISSHPublicKey key) throws OpenShiftException;

	public IOpenShiftSSHKey getSSHKeyByName(String name) throws OpenShiftUnknonwSSHKeyTypeException, OpenShiftException;
	
	public IOpenShiftSSHKey getSSHKeyByPublicKey(String publicKey) throws OpenShiftUnknonwSSHKeyTypeException, OpenShiftException;

	public boolean hasSSHKeyName(String name) throws OpenShiftUnknonwSSHKeyTypeException, OpenShiftException;
	
	public boolean hasSSHPublicKey(String publicKey) throws OpenShiftUnknonwSSHKeyTypeException, OpenShiftException;
	
	public void deleteKey(String name);
	
	public int getMaxGears();

	public int getConsumedGears();

}