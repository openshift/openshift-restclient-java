/******************************************************************************* 
 * Copyright (c) 2011-2014 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/
package com.openshift.internal.client;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.openshift.client.IDomain;
import com.openshift.client.IOpenShiftConnection;
import com.openshift.client.IOpenShiftSSHKey;
import com.openshift.client.ISSHPublicKey;
import com.openshift.client.IUser;
import com.openshift.client.OpenShiftException;
import com.openshift.client.OpenShiftSSHKeyException;
import com.openshift.client.OpenShiftUnknonwSSHKeyTypeException;
import com.openshift.client.SSHKeyType;
import com.openshift.client.IAuthorization;
import com.openshift.internal.client.httpclient.request.StringParameter;
import com.openshift.internal.client.response.KeyResourceDTO;
import com.openshift.internal.client.response.UserResourceDTO;
import com.openshift.internal.client.utils.Assert;
import com.openshift.internal.client.utils.CollectionUtils;
import com.openshift.internal.client.utils.IOpenShiftJsonConstants;

/**
 * @author André Dietisheim
 * @author Sean Kavanagh
 */
public class UserResource extends AbstractOpenShiftResource implements IUser {

	private final APIResource api;
    	private final String id;
	private final String rhLogin;
	private final String password;
	private final int maxGears;
	private final int consumedGears;

	private Map<String, SSHKeyResource> sshKeys;
	
	public UserResource(final APIResource api, final UserResourceDTO dto, final String password) {
		super(api.getService(), dto.getLinks(), dto.getMessages());
		this.api = api;
        	this.id = dto.getId();
		this.rhLogin = dto.getRhLogin();
		this.maxGears = dto.getMaxGears();
		this.consumedGears = dto.getConsumedGears();
		this.password = password;
	}

	@Override
	public IOpenShiftConnection getConnection() {
		return api;
	}

    	@Override
    	public String getId() {
        	return id;
    	}

	@Override
	public String getRhlogin() {
		return rhLogin;
	}

	@Override
	public String getPassword() {
		return password;
	}
	
	@Override
	public String getServer() {
		return api.getServer();
	}
	
	@Override
	public int getMaxGears() {
		return maxGears;
	}
	
	@Override
	public int getConsumedGears() {
		return consumedGears;
	}

	@Override
	public IDomain createDomain(String id) throws OpenShiftException {
		Assert.notNull(id);
		
		return api.createDomain(id);
	}

	@Override
	public List<IDomain> getDomains() throws OpenShiftException {
		List<IDomain> domains = api.getDomains();
		return domains;
	}

	@Override
	public IDomain getDefaultDomain() throws OpenShiftException {
		return api.getDefaultDomain();
	}

	@Override
	public IDomain getDomain(String id) throws OpenShiftException {
		return api.getDomain(id);
	}

	@Override
	public boolean hasDomain() throws OpenShiftException {
		return (api.getDomains().size() > 0);
	}

	@Override
	public boolean hasDomain(String id) throws OpenShiftException {
		Assert.notNull(id);
		
		return api.getDomain(id) != null;
	}

    @Override
    public IAuthorization createAuthorization(String note, String scopes)  throws OpenShiftException {
        return api.createAuthorization(note, scopes);
    }
    
    @Override
    public IAuthorization createAuthorization(String note, String scopes, int expiresIn) throws OpenShiftException {
        return api.createAuthorization(note, scopes, expiresIn); 
    }

    @Override
    public IAuthorization getAuthorization() throws OpenShiftException {
        return api.getAuthorization();
    }

	@Override
	public void refresh() throws OpenShiftException {
		this.sshKeys = loadKeys();
		
		api.refresh();
		DomainResource defaultDomain = (DomainResource) getDefaultDomain();
		if (defaultDomain != null) {
			defaultDomain.refresh();
		}
	}

	@Override
	public List<IOpenShiftSSHKey> getSSHKeys() throws OpenShiftUnknonwSSHKeyTypeException,
			OpenShiftException {
		Map<String, IOpenShiftSSHKey> keys = new HashMap<String, IOpenShiftSSHKey>();
		keys.putAll(getCachedOrLoadSSHKeys());
		return CollectionUtils.toUnmodifiableCopy(keys.values());
	}

	private Map<String, SSHKeyResource> getCachedOrLoadSSHKeys() throws OpenShiftException,
			OpenShiftUnknonwSSHKeyTypeException {
		if (sshKeys == null) {
			this.sshKeys = loadKeys();
		}
		return sshKeys;
	}

	private Map<String, SSHKeyResource> loadKeys() throws OpenShiftException,
			OpenShiftUnknonwSSHKeyTypeException {
		Map<String, SSHKeyResource> keys = new HashMap<String, SSHKeyResource>();
		List<KeyResourceDTO> keyDTOs = new GetSShKeysRequest().execute();
		for (KeyResourceDTO keyDTO : keyDTOs) {
			keys.put(keyDTO.getName(), new SSHKeyResource(keyDTO, this));
		}
		return keys;
	}

	@Override
	public boolean removeSSHKey(String name) {
		IOpenShiftSSHKey key = getSSHKeyByName(name);
		if (key == null) {
			return false;
		}
		key.destroy();
		getCachedOrLoadSSHKeys().remove(name);
		return true;
	}
	
	@Deprecated
	@Override
	public void deleteKey(String name) {
		removeSSHKey(name);
	}
	
	@Override
	public IOpenShiftSSHKey getSSHKeyByName(String name) 
			throws OpenShiftUnknonwSSHKeyTypeException, OpenShiftException {
		Assert.notNull(name);

		return getCachedOrLoadSSHKeys().get(name);
	}

	@Override
	public IOpenShiftSSHKey getSSHKeyByPublicKey(String publicKey)
			throws OpenShiftUnknonwSSHKeyTypeException, OpenShiftException {
		Assert.notNull(publicKey);

		IOpenShiftSSHKey matchingKey = null;
		if (publicKey == null) {
			return null;
		}

		for (SSHKeyResource key : getCachedOrLoadSSHKeys().values()) {
			if (publicKey.equals(key.getPublicKey())) {
				matchingKey = key;
				break;
			}
		}
		return matchingKey;
	}

	@Override
	public boolean hasSSHKeyName(String name) throws OpenShiftUnknonwSSHKeyTypeException,
			OpenShiftException {
		Assert.notNull(name);

		return getSSHKeyByName(name) != null;
	}

	@Override
	public boolean hasSSHPublicKey(String publicKey)
			throws OpenShiftUnknonwSSHKeyTypeException, OpenShiftException {
		return getSSHKeyByPublicKey(publicKey) != null;
	}

	@Override
	public IOpenShiftSSHKey putSSHKey(String name, ISSHPublicKey key) throws OpenShiftException {
		Assert.notNull(name);
		Assert.notNull(key);

		KeyResourceDTO keyDTO = new AddSShKeyRequest().execute(key.getKeyType(), name, key.getPublicKey());
		return put(keyDTO);
	}
	
	@Override
	public IOpenShiftSSHKey addSSHKey(String name, ISSHPublicKey key) throws OpenShiftException {
		Assert.notNull(name);
		Assert.notNull(key);

		if (hasSSHKeyName(name)) {
			throw new OpenShiftSSHKeyException(
					"Could not add new key {0} with the name {1}. There already is a key for this name, key names must be unique.",
					key.getPublicKey(), name);
		}
		if (hasSSHPublicKey(name)) {
			throw new OpenShiftSSHKeyException(
					"Could not add new key {0} with the name {1}. The key is already stored with a different name. Public key have to be unique.",
					key.getPublicKey(), name);
		}
		KeyResourceDTO keyDTO = new AddSShKeyRequest().execute(key.getKeyType(), name, key.getPublicKey());
		return put(keyDTO);
	}

	private SSHKeyResource put(KeyResourceDTO keyDTO) throws OpenShiftUnknonwSSHKeyTypeException {
		SSHKeyResource sshKey = new SSHKeyResource(keyDTO, this);
		getCachedOrLoadSSHKeys().put(keyDTO.getName(), sshKey);
		return sshKey;
	}

	protected void removeSSHKey(SSHKeyResource key) {
		sshKeys.remove(key.getName());
	}

	private class GetSShKeysRequest extends ServiceRequest {

		private GetSShKeysRequest() throws OpenShiftException {
			super("LIST_KEYS");
		}

		protected List<KeyResourceDTO> execute() throws OpenShiftException {
			return super.execute();
		}
	}

	private class AddSShKeyRequest extends ServiceRequest {

		private AddSShKeyRequest() throws OpenShiftException {
			super("ADD_KEY");
		}

		protected KeyResourceDTO execute(SSHKeyType type, String name, String content) throws OpenShiftException {
			return super.execute(
					new StringParameter(IOpenShiftJsonConstants.PROPERTY_TYPE, type.getTypeId()),
					new StringParameter(IOpenShiftJsonConstants.PROPERTY_NAME, name), 
					new StringParameter(IOpenShiftJsonConstants.PROPERTY_CONTENT, content));
		}
	}


	@Override
	public String toString() {
		return "UserResource ["
				+ "rhLogin=" + rhLogin 
				+ "]";
	}

}
