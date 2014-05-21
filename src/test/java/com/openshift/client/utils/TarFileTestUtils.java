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

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.ArchiveException;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
import org.apache.commons.compress.compressors.CompressorException;
import org.apache.commons.compress.utils.IOUtils;

import com.openshift.internal.client.utils.Assert;
import com.openshift.internal.client.utils.StreamUtils;

/**
 * @author Andre Dietisheim
 */
public class TarFileTestUtils {

	private TarFileTestUtils() {
		// inhibit instantiation
	}

	/**
	 * Replaces the given file(-name), that might exist anywhere nested in the
	 * given archive, by a new entry with the given content. The replacement is
	 * faked by adding a new entry into the archive which will overwrite the
	 * existing (older one) on extraction.
	 * 
	 * @param name
	 *            the name of the file to replace (no path required)
	 * @param newContent
	 *            the content of the replacement file
	 * @param in
	 * @return
	 * @throws IOException
	 * @throws ArchiveException
	 * @throws CompressorException
	 */
	public static File fakeReplaceFile(String name, String newContent, InputStream in) throws IOException {
		Assert.notNull(name);
		Assert.notNull(in);

		File newArchive = FileUtils.createRandomTempFile(".tar.gz");
		newArchive.deleteOnExit();

		TarArchiveOutputStream newArchiveOut =
				new TarArchiveOutputStream(new GZIPOutputStream(new FileOutputStream(newArchive)));
		newArchiveOut.setLongFileMode(TarArchiveOutputStream.LONGFILE_GNU);

		TarArchiveInputStream archiveIn = new TarArchiveInputStream(new GZIPInputStream(in));
		String pathToReplace = null;
		try {
			// copy the existing entries
			for (ArchiveEntry nextEntry = null; (nextEntry = archiveIn.getNextEntry()) != null;) {
				if (nextEntry.getName().endsWith(name)) {
					pathToReplace = nextEntry.getName();
				}
				newArchiveOut.putArchiveEntry(nextEntry);
				IOUtils.copy(archiveIn, newArchiveOut);
				newArchiveOut.closeArchiveEntry();
			}

			if (pathToReplace == null) {
				throw new IllegalStateException("Could not find file " + name + " in the given archive.");
			}
			TarArchiveEntry newEntry = new TarArchiveEntry(pathToReplace);
			newEntry.setSize(newContent.length());
			newArchiveOut.putArchiveEntry(newEntry);
			IOUtils.copy(new ByteArrayInputStream(newContent.getBytes()), newArchiveOut);
			newArchiveOut.closeArchiveEntry();

			return newArchive;
		} finally {
			newArchiveOut.finish();
			newArchiveOut.flush();
			StreamUtils.close(archiveIn);
			StreamUtils.close(newArchiveOut);
		}
	}

	/**
	 * Returns all paths within the given archive.
	 * 
	 * @param in
	 *            the archive
	 * @return all paths
	 * @throws IOException
	 * @throws CompressorException
	 */
	public static List<String> getAllPaths(InputStream in) throws IOException {
		Assert.notNull(in);

		List<String> paths = new ArrayList<String>();
		TarArchiveInputStream archiveIn = new TarArchiveInputStream(new GZIPInputStream(in));
		try {
			for (ArchiveEntry nextEntry = null; (nextEntry = archiveIn.getNextEntry()) != null;) {
				paths.add(nextEntry.getName());
			}
			return paths;
		} finally {
			StreamUtils.close(archiveIn);
		}
	}
}
