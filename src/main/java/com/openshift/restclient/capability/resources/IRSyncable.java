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

import com.openshift.restclient.capability.IBinaryCapability;
import com.openshift.restclient.model.IPod;

/**
 * Defines if a pod can support port forwarding
 * @author Andre Dietisheim
 *
 */
public interface IRSyncable extends IBinaryCapability {

	void sync(Peer source, Peer destination);
		
	/**
	 * Stop forwarding ports, forcibly if necessary
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
}
