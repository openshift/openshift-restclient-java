/******************************************************************************* 
 * Copyright (c) 2014 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/
package com.openshift.client.utils;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;

import com.openshift.internal.client.utils.StreamUtils;

/**
 * @author Andre Dietisheim
 */
public class TarFileUtils {

	private static final String GIT_FOLDER_NAME = "git";

	private TarFileUtils() {
		// inhibit instantiation
	}

	public static boolean hasGitFolder(InputStream inputStream) throws IOException {
		TarArchiveInputStream tarInputStream = null;
		try {
			boolean gitFolderPresent = false;
			tarInputStream = new TarArchiveInputStream(new GzipCompressorInputStream(inputStream));
			for (TarArchiveEntry entry = null; (entry = tarInputStream.getNextTarEntry()) != null;) {
				if (GIT_FOLDER_NAME.equals(entry.getName())
						&& entry.isDirectory()) {
					gitFolderPresent = true;
					break;
				}
			}
			return gitFolderPresent;
		} finally {
			StreamUtils.close(tarInputStream);
		}
	}
}
