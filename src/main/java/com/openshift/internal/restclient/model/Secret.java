/*******************************************************************************
 * Copyright (c) 2015 Red Hat, Inc. Distributed under license by Red Hat, Inc.
 * All rights reserved. This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Red Hat, Inc.
 ******************************************************************************/
package com.openshift.internal.restclient.model;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.jboss.dmr.ModelNode;

import com.openshift.restclient.IClient;
import com.openshift.restclient.model.secret.ISecret;
import com.openshift.restclient.utils.Base64Coder;

/**
 * @author Jiri Pechanec
 */
public class Secret extends KubernetesResource implements ISecret {

	public Secret(ModelNode node, IClient client, Map<String, String []> propertyKeys) {
		super(node, client, propertyKeys);
	}

	@Override
	public void addData(String key, InputStream data) {
		try {
			ByteArrayOutputStream os = new ByteArrayOutputStream();
			IOUtils.copy(data, os);
			addData(key, os.toByteArray());
		}
		catch (IOException e) {
			throw new IllegalArgumentException("Could not process data stream", e);
		}
	}

	@Override
	public void addData(final String key, final byte[] data) {
		ModelNode dataNode = get(SECRET_DATA);
		dataNode.get(key).set(Base64Coder.encode(data));
	}

	@Override
	public byte[] getData(final String key) {
		return Base64Coder.decodeBinary(asMap(SECRET_DATA).get(key));
	}

	@Override
	public void setType(final String type) {
		get(SECRET_TYPE).set(type.toString());
	}

	@Override
	public String getType() {
		return asString(SECRET_TYPE);
	}
	
}
