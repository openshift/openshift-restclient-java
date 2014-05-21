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
package com.openshift.internal.client.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;

/**
 * @author Andre Dietisheim
 */
public class StreamUtils {

	public static final String UTF_8 = "UTF-8";

	private static final byte[] buffer = new byte[1024];
	
	/**
	 * Writes the content of the given input stream to the given output stream.
	 * 
	 * @param outputStream
	 *            the output stream to write to
	 * @param inputStream
	 *            the input stream to read from
	 * @throws IOException
	 */
	public static void writeTo(InputStream inputStream, OutputStream outputStream) throws IOException {
		for (int bytesRead = 0; (bytesRead = inputStream.read(buffer, 0, buffer.length)) != -1; ) {
			outputStream.write(buffer, 0, bytesRead);
		}
		outputStream.flush();
	}

	public static String readToString(InputStream inputStream) throws IOException {
		if (inputStream == null) {
			return null;
		}
		return readToString(new InputStreamReader(inputStream));
	}

	public static String readToString(InputStream inputStream, String encoding) throws IOException {
		if (inputStream == null) {
			return null;
		}
		return readToString(new InputStreamReader(inputStream, encoding));
	}

	public static String readToString(Reader reader) throws IOException {
		if (reader == null) {
			return null;
		}
		BufferedReader bufferedReader = new BufferedReader(reader);
		StringWriter writer = new StringWriter();
		String line = null;
		while ((line = bufferedReader.readLine()) != null) {
			writer.write(line);
			writer.write('\n');
		}
		return writer.toString();
	}

	/**
	 * Writes the given string to the given output stream. The stream is closed
	 * after writing all data.
	 * 
	 * @param data
	 *            the data to write
	 * @param outputStream
	 *            the stream to write to
	 * @throws IOException
	 */
	public static void writeTo(byte[] data, OutputStream outputStream) throws IOException {
		outputStream.write(data);
		outputStream.flush();
		outputStream.close();
	}

	public static void close(InputStream inputStream) throws IOException {
		if (inputStream != null) {
			inputStream.close();
		}
	}
	
	public static void quietlyClose(InputStream inputStream) {
		try {
			close(inputStream);
		} catch (IOException e) {
			// ignore
		}
	}

	public static void close(OutputStream outputStream) throws IOException {
		if (outputStream != null) {
			outputStream.close();
		}
	}

	public static void quietlyClose(OutputStream outputStream) {
		try {
			close(outputStream);
		} catch (IOException e) {
			// ignore
		}
	}

	public static void close(Reader reader) throws IOException {
		if (reader != null) {
			reader.close();
		}
	}

	public static void quietlyClose(Reader reader) {
		try {
			close(reader);
		} catch (IOException e) {
			// ignore
		}
	}

	public static void close(Writer writer) throws IOException {
		if (writer != null) {
			writer.close();
		}
	}

	public static void quietlyClose(Writer writer) {
		try {
			close(writer);
		} catch (IOException e) {
			// ignore
		}
	}

}
