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
package com.openshift.internal.client;

import com.openshift.client.IOpenShiftSSHKey;
import com.openshift.client.OpenShiftException;
import com.openshift.client.OpenShiftUnknonwSSHKeyTypeException;
import com.openshift.client.SSHKeyType;
import com.openshift.internal.client.httpclient.request.StringParameter;
import com.openshift.internal.client.response.KeyResourceDTO;
import com.openshift.internal.client.utils.Assert;
import com.openshift.internal.client.utils.IOpenShiftJsonConstants;

/**
 * @author Andre Dietisheim
 */
public class SSHKeyResource extends AbstractOpenShiftResource implements IOpenShiftSSHKey {

	private String name;
	private SSHKeyType type;
	private String publicKey;
	private UserResource user;

	protected SSHKeyResource(KeyResourceDTO dto, UserResource user) throws OpenShiftUnknonwSSHKeyTypeException {
		super(user.getService(), dto.getLinks(), dto.getMessages());
		this.name = dto.getName();
		this.type = SSHKeyType.getByTypeId(dto.getType());
		this.publicKey = dto.getContent();
		this.user = user;
	}

	public void setKeyType(SSHKeyType type, String publicKey) throws OpenShiftException {
		Assert.notNull(type);
		Assert.notNull(publicKey);

		KeyResourceDTO dto = new UpdateKeyRequest().execute(type, publicKey);
		update(dto);
	}
	
	public String getName() {
		return name;
	}

	public SSHKeyType getKeyType() {
		return type;
	}

	public void setPublicKey(String publicKey) throws OpenShiftException {
		Assert.notNull(publicKey);

		KeyResourceDTO dto = new UpdateKeyRequest().execute(getKeyType(), publicKey);
		update(dto);
	}

	public String getPublicKey() {
		return publicKey;
	}

	@Override
	public void refresh() throws OpenShiftException {
		//TODO: implement
	}
	
	public void destroy() throws OpenShiftException {
		new DeleteKeyRequest().execute();
		user.removeSSHKey(this);
		this.name = null;
		this.type = null;
		this.publicKey = null;
	}
	
	protected void update(KeyResourceDTO dto) throws OpenShiftUnknonwSSHKeyTypeException {
		if (dto == null) {
			return;
		}
		this.type = SSHKeyType.getByTypeId(dto.getType());
		this.publicKey = dto.getContent();
	}

	private class UpdateKeyRequest extends ServiceRequest {

		private UpdateKeyRequest() {
			super("UPDATE");
		}
		
		protected KeyResourceDTO execute(SSHKeyType type, String publicKey) throws OpenShiftException {
			return execute(
					new StringParameter(IOpenShiftJsonConstants.PROPERTY_CONTENT, publicKey), 
					new StringParameter(IOpenShiftJsonConstants.PROPERTY_TYPE, type.getTypeId()));
		}
	}

	@Override
	public String toString() {
		return "SSHKeyResource ["
				+ "name=" + name 
				+ ", type=" + type 
				+ ", publicKey=" + publicKey 
				+ "]";
	}

	private class DeleteKeyRequest extends ServiceRequest {

		private DeleteKeyRequest() {
			super("DELETE");
		}
		
		protected void execute() throws OpenShiftException {
			super.execute();
		}
	}

}
