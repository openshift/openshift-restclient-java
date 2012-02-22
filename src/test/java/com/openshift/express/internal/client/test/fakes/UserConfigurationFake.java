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
package com.openshift.express.internal.client.test.fakes;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Properties;

import com.openshift.express.client.OpenShiftException;
import com.openshift.express.client.configuration.SystemConfiguration;
import com.openshift.express.client.configuration.UserConfiguration;
import com.openshift.express.internal.client.utils.StreamUtils;

/**
 * @author André Dietisheim
 */
public class UserConfigurationFake extends UserConfiguration {

	public UserConfigurationFake() throws OpenShiftException, IOException {
		this(null);
	}

	public UserConfigurationFake(SystemConfiguration systemConfiguration) throws OpenShiftException, IOException {
		super(systemConfiguration);
	}

	protected void initProperties(File file, Properties defaultProperties) throws FileNotFoundException, IOException {
		file = createFile();
		initFile(file);
		super.initProperties(file,defaultProperties);
	}

	protected void initFile(File file) {
		Writer writer = null;
		try {
			writer = new FileWriter(file);
			initFile(writer);
			writer.flush();
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
			try {
				StreamUtils.close(writer);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}		
	}
	
	protected File createFile() {
		try {
			return File.createTempFile(createRandomString(), null);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected void initFile(Writer writer) throws IOException {
	}

	private String createRandomString() {
		return String.valueOf(System.currentTimeMillis());
	}

	public File getFile() {
		return super.getFile();
	}
}
