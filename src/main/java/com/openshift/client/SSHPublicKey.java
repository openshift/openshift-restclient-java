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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.openshift.internal.client.ssh.AbstractSSHKey;
import com.openshift.internal.client.utils.StreamUtils;

/**
 * @author André Dietisheim
 */
public class SSHPublicKey extends AbstractSSHKey {

	private static final Pattern PUBLICKEY_PATTERN = Pattern.compile("([^ ]+) ([^ ]+)( .+)*");

	private String publicKey;
	
	public SSHPublicKey(String publicKeyFilePath) throws FileNotFoundException, OpenShiftException, IOException {
		this(new File(publicKeyFilePath));
	}
	
	public SSHPublicKey(File publicKeyFile) throws FileNotFoundException, OpenShiftException, IOException {
		super(null);
		init(publicKeyFile);
	}

	public String getPublicKey() {
		return publicKey;
	}
	
	private void init(File publicKeyFile) throws OpenShiftException, FileNotFoundException, IOException {
		String keyWithIdAndComment = StreamUtils.readToString(new FileReader(publicKeyFile));
		Matcher matcher = PUBLICKEY_PATTERN.matcher(keyWithIdAndComment);
		if (!matcher.find()
				|| matcher.groupCount() < 1) {
			throw new OpenShiftException("Could not load public key from file \"{0}\"", publicKeyFile.getAbsolutePath());
		}

		setKeyType(matcher.group(1));
		this.publicKey = matcher.group(2);
	}

}
