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

     	public String getId();

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

    /**
     * Returns current authorization.  Creates new authorization for user if none exists.
     * Authorization is set by default when token is used to create API connection.
     * 
     * @return authorization 
     * @throws OpenShiftException
     */
	public IAuthorization getAuthorization() throws OpenShiftException;

    /**
     * Creates and returns new authorization set for user 
     *
     * @param note 
     *      A reminder description of what the authorization is for.
     * @param scopes 
     *      Scope of the authorization token to determine type of access.
     * @return authorization
     * @throws OpenShiftException
     */
	public IAuthorization createAuthorization(String note, String scopes) throws OpenShiftException;

    /**
     * Creates and returns new authorization set for user 
     * @param note
     *      A reminder description of what the authorization is for.
     * @param scopes
     *      Scope of the authorization token to determine type of access.
     * @param expiresIn
     *      The number of seconds before this authorization expires.
     * @return authorization
     * @throws OpenShiftException
     */
    public IAuthorization createAuthorization(String note, String scopes, int expiresIn) throws OpenShiftException;

	/**
	 * Deprecated, use {@link #addSSHKey(String, ISSHPublicKey)}
	 * 
	 * @param name key name to use
	 * @param key the key to put/add
	 * @return
	 * @throws OpenShiftException
	 */
	@Deprecated
	public IOpenShiftSSHKey putSSHKey(String name, ISSHPublicKey key) throws OpenShiftException;

	/**
	 * Adds the given ssh key with the given name. Key names and public keys have to be unique. Throws
	 * OpenShiftSSHKeyException if either the key name or the public key are already used.
	 * 
	 * @param name
	 *            the name to identify the key
	 * @param key
	 *            the key to add
	 * @return
	 * @throws OpenShiftException
	 */
	public IOpenShiftSSHKey addSSHKey(String name, ISSHPublicKey key) throws OpenShiftException;

	public IOpenShiftSSHKey getSSHKeyByName(String name) throws OpenShiftUnknonwSSHKeyTypeException, OpenShiftException;
	
	public IOpenShiftSSHKey getSSHKeyByPublicKey(String publicKey) throws OpenShiftUnknonwSSHKeyTypeException, OpenShiftException;

	public boolean hasSSHKeyName(String name) throws OpenShiftUnknonwSSHKeyTypeException, OpenShiftException;
	
	public boolean hasSSHPublicKey(String publicKey) throws OpenShiftUnknonwSSHKeyTypeException, OpenShiftException;
	
	public boolean removeSSHKey(String name);
	
	@Deprecated
	public void deleteKey(String name);
	
	public int getMaxGears();

	public int getConsumedGears();

}
