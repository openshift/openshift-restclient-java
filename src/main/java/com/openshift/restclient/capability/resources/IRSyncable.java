/*******************************************************************************
 * Copyright (c) 2015 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package com.openshift.restclient.capability.resources;

import java.io.InputStream;

import com.openshift.restclient.capability.IBinaryCapability;
import com.openshift.restclient.model.IPod;

/**
 * Defines if a pod can support port forwarding
 * @author Andre Dietisheim
 *
 */
public interface IRSyncable extends IBinaryCapability {

	/**
	 * Synchronize the give {@code destination} with the given {@code source}
	 * @param source the source of the rsync
	 * @param destination the destination of the rsync
	 * @return the underlying {@link Process} streams to be displayed in a console.
	 */
	InputStream sync(Peer source, Peer destination);
		
	/**
	 * Stop rsync'ing, forcibly if necessary.
	 */
	void stop();

	public class PodPeer extends Peer {

		private static final char NAMESPACE_POD_SEPARATOR = '/';
		private static final char POD_PATH_SEPARATOR = ':';

		private IPod pod;

		public PodPeer(String location, IPod pod) {
			super(location);
			this.pod = pod;
		}

		@Override
		public String getParameter() {
			return new StringBuilder()
					.append(pod.getName())
					.append(POD_PATH_SEPARATOR)
					.append(super.getParameter())
					.append(" -n ")
					.append(pod.getNamespace())
					.toString();
		}
		
		@Override
		public String getLocation() {
			return new StringBuilder()
					.append(pod.getNamespace())
					.append(NAMESPACE_POD_SEPARATOR)
					.append(pod.getName())
					.append(POD_PATH_SEPARATOR)
					.append(super.getParameter())
					.toString();
		}

		@Override
		public boolean isPod() {
			return true;
		}
	}
	
	public class LocalPeer extends Peer {

		public LocalPeer(String location) {
			super(location);
		}

		public boolean isPod() {
			return false;
		}
	}

	public abstract class Peer {

		private String location;

		private Peer(String location) {
			this.location = location;
		}

		public String getParameter() {
			return location;
		}

		public String getLocation() {
			return getParameter();
		}
		
		public abstract boolean isPod();
	}

	/**
	 * Indicates if the {@link Process} completed or not
	 * 
	 * @return <code>true</code> if the {@link Process} completed,
	 *         <code>false</code> otherwise.
	 */
	boolean isDone();

	/**
	 * @return the {@link Process} exit value when it completed, {@code -1} if
	 *         it's still running
	 */
	int exitValue();

	/**
	 * Blocks until the process is done.
	 * 
	 * @throws InterruptedException
	 *             if the current thread is interrupted while waiting
	 */
	void await() throws InterruptedException;


}
