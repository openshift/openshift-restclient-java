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
package org.jboss.tools.openshift.express.internal.client.test.fakes;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

import org.jboss.tools.openshift.express.client.OpenShiftException;
import org.jboss.tools.openshift.express.client.UserConfiguration;
import org.jboss.tools.openshift.express.internal.client.utils.StreamUtils;

/**
 * @author André Dietisheim
 */
public class UserConfigurationFake extends UserConfiguration {

	
	public UserConfigurationFake() throws OpenShiftException, IOException {
		super();
	}

	@Override
	protected File getUserConfigurationFile() throws OpenShiftException, IOException {
		Writer writer = null;
		try {
			File file = File.createTempFile(createRandomString(), null);
			writer = new FileWriter(file);
			initFile(writer);
			return file;
		} finally {
			StreamUtils.close(writer);
		}
	}

	protected void initFile(Writer writer) throws IOException {
	}

	public File getFile() {
		return file;
	}
	
	private String createRandomString() {
		return String.valueOf(System.currentTimeMillis());
	}

}
