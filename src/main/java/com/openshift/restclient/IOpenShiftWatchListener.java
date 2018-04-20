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

package com.openshift.restclient;

import java.util.List;

import com.openshift.restclient.model.IResource;

/**
 * Handler to receive notification when a resource changes
 *
 */
public interface IOpenShiftWatchListener {

    /**
     * Called when an endpoint connects The initial set of resources returned when
     * determining the resourceVersion to watch
     * 
     * @param resources
     *            an Unmodifiable List
     */
    void connected(List<IResource> resources);

    /**
     * Called when and endpoint disconnects
     */
    void disconnected();

    /**
     * 
     * @param resource
     *            the resource that changed
     * @param change
     *            the change type
     */
    void received(IResource resource, ChangeType change);

    public class ChangeType {

        public static final ChangeType ADDED = new ChangeType("ADDED");
        public static final ChangeType MODIFIED = new ChangeType("MODIFIED");
        public static final ChangeType DELETED = new ChangeType("DELETED");

        private String value;

        public ChangeType(String value) {
            if (value != null) {
                value = value.toUpperCase();
            }
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((getValue() == null) ? 0 : getValue().hashCode());
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
            ChangeType other = (ChangeType) obj;
            if (value == null) {
                if (other.value != null) {
                    return false;
                }
            } else if (!getValue().equals(other.getValue())) {
                return false;
            }
            return true;
        }
    }

    void error(Throwable err);

    /**
     * Convenience class for implementing watch callbacks
     * 
     *
     */
    static class OpenShiftWatchListenerAdapter implements IOpenShiftWatchListener {

        @Override
        public void connected(List<IResource> resources) {
        }

        @Override
        public void disconnected() {
        }

        @Override
        public void received(IResource resource, ChangeType change) {
        }

        @Override
        public void error(Throwable err) {
        }

    }
}
