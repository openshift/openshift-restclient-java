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

package com.openshift.internal.restclient.model.volume.property;

import java.util.Objects;

import org.jboss.dmr.ModelNode;

import com.openshift.restclient.model.volume.property.IHostPathVolumeProperties;

public class HostPathVolumeProperties implements ISettablePersistentVolumeProperties, IHostPathVolumeProperties {

    private static final String PV_SPEC = "spec";
    private static final String PV_HOST_PATH = "hostPath";
    private static final String PATH = "path";

    private String path;

    public HostPathVolumeProperties(String path) {
        this.path = path;
    }

    @Override
    public void setProperties(ModelNode node) {
        ModelNode hostPath = node.get(PV_SPEC, PV_HOST_PATH);
        hostPath.set(PATH, path);
    }

    @Override
    public String getPath() {
        return path;
    }

    @Override
    public void setPath(String path) {
        this.path = path;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        HostPathVolumeProperties that = (HostPathVolumeProperties) o;
        return Objects.equals(path, that.path);
    }

    @Override
    public int hashCode() {
        return Objects.hash(path);
    }

    @Override
    public String toString() {
        return "HostPathVolumeProperties{" + "path='" + path + '\'' + '}';
    }
}
