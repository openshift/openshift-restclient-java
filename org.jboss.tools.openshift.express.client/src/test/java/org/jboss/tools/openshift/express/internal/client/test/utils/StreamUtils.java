/******************************************************************************* 
 * Copyright (c) 2007 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/
package org.jboss.tools.openshift.express.internal.client.test.utils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;

public class StreamUtils {

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
}
