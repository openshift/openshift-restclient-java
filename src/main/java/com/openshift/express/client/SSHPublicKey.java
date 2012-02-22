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
package com.openshift.express.client;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.openshift.express.internal.client.utils.StreamUtils;

/**
 * @author André Dietisheim
 */
public class SSHPublicKey implements ISSHPublicKey {

	private static final Pattern PUBLICKEY_PATTERN = Pattern.compile("([^ ]+) ([^ ]+)( .+)*");

	private String publicKey;
	private SSHKeyType keyType;

	public SSHPublicKey(File publicKeyFilePath) throws IOException, OpenShiftException {
		initializePublicKey(publicKeyFilePath);
	}

	public SSHPublicKey(String publicKey, String keyTypeId) throws OpenShiftUnknonwSSHKeyTypeException {
		this(publicKey, SSHKeyType.getByTypeId(keyTypeId));
	}

	public SSHPublicKey(String publicKey, SSHKeyType keyType) {
		this.publicKey = publicKey;
		this.keyType = keyType;
	}

	private void initializePublicKey(File file) throws OpenShiftException, FileNotFoundException, IOException {
		String keyWithIdAndComment = StreamUtils.readToString(new FileReader(file));
		Matcher matcher = PUBLICKEY_PATTERN.matcher(keyWithIdAndComment);
		if (!matcher.find()
				|| matcher.groupCount() < 1) {
			throw new OpenShiftException("Could not load public key from file \"{0}\"", file.getAbsolutePath());
		}

		this.keyType = SSHKeyType.getByTypeId(matcher.group(1));
		this.publicKey = matcher.group(2);
	}

	public String getPublicKey() {
		return publicKey;
	}

	public SSHKeyType getKeyType() {
		return keyType;
	}
}
