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

import java.util.Collection;

import com.openshift.restclient.OpenShiftException;
import com.openshift.restclient.capability.IBinaryCapability;
import com.openshift.restclient.model.IPort;

/**
 * Defines if a pod can support port forwarding
 *
 */
public interface IPortForwardable extends IBinaryCapability {

    /**
     * Forward the ports to a pod. This is a non-blocking call
     * 
     * @param pairs
     *            the pairs of local/remote ports to link together
     * @param options
     *            the options to pass to the underlying {@code oc} command
     * @throws OpenShiftException
     *             if unable to forward ports
     */
    void forwardPorts(Collection<PortPair> pairs, OpenShiftBinaryOption... options);

    /**
     * The port pairs.
     * 
     * @return The pairs when forwarding or an empty array;
     */
    Collection<PortPair> getPortPairs();

    /**
     * Stop forwarding ports, forcibly if necessary
     */
    void stop();

    /**
     * 
     * @return true if forwarding; false otherwise
     */
    boolean isForwarding();

    /**
     * Pairing a local port to the remote port of a container.
     * 
     */
    static class PortPair {
        private int localPort = -1;
        private IPort remotePort;

        /**
         * Forward traffic to/from the specified port
         * 
         */
        public PortPair(IPort remotePort) {
            this(remotePort.getContainerPort(), remotePort);
        }

        /**
         * Forward traffic on the local port to the remote port. Set localPort to '0' to
         * listen on a random local port
         * 
         */
        public PortPair(int localPort, IPort remotePort) {
            this.localPort = localPort;
            this.remotePort = remotePort;
        }

        public int getLocalPort() {
            return localPort;
        }

        public void setLocalPort(int port) {
            this.localPort = port;
        }

        public int getRemotePort() {
            return remotePort.getContainerPort();
        }

        public String getName() {
            return remotePort.getName();
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + localPort;
            result = prime * result + ((remotePort == null) ? 0 : remotePort.hashCode());
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            PortPair other = (PortPair) obj;
            if (localPort != other.localPort) {
                return false;
            }
            if (remotePort == null) {
                if (other.remotePort != null) {
                    return false;
                }
            } else if (!remotePort.equals(other.remotePort)) {
                return false;
            }
            return true;
        }

        @Override
        public String toString() {
            return getName() + ": " + this.localPort + "->" + this.getRemotePort();
        }

    }
}
