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
package com.openshift.client.utils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;

/**
 * @author Andre Dietisheim
 */
public class FileUtils {

	private static final String JAVA_IO_TEMP = "java.io.tmpdir";

	public static void writeTo(String data, String path) throws IOException {
		writeTo(data, new File(path));
	}

	public static void writeTo(String data, File file) throws IOException {
		StringReader reader = null;
		FileWriter writer = null;
		try {
			writer = new FileWriter(file);
			reader = new StringReader(data);
			for (int character = -1; (character = reader.read()) != -1;) {
				writer.write(character);
			}
		} finally {
			if (writer != null) {
				writer.flush();
				writer.close();
			}
			if (reader != null) {
				reader.close();
			}
		}
	}
	
	public static File createRandomTempFile() throws IOException {
		return createRandomTempFile(null);
	}
	
	public static File createRandomTempFile(String suffix) throws IOException {
		File file = File.createTempFile(createRandomFilename(), suffix);
		file.deleteOnExit();
		return file;
	}
	
	public static File createRandomTempDirectory() throws IOException {
		File file = File.createTempFile(null, createRandomFilename());
		file.mkdir();
		file.deleteOnExit();
		return file;
	}
	
	public static String createRandomFilename()  {
		return String.valueOf(System.currentTimeMillis());
	}

	public static void silentlyDelete(File file) {
		if (file == null) {
			return;
		}
		file.delete();
	}
	
	public static String getTempDirectory() {
		return System.getProperty(JAVA_IO_TEMP);
	}

	public static String getTempDirFilePath(String name) {
		return new File(getTempDirectory(), name).getAbsolutePath();
	}

	public static String getTempDirFilePath() {
		return getTempDirFilePath(createRandomFilename());
	}
}
