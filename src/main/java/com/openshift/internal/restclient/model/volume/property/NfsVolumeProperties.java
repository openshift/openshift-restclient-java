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

import com.openshift.restclient.model.volume.property.INfsVolumeProperties;

public class NfsVolumeProperties extends AbstractPersistentVolumeProperties implements INfsVolumeProperties {

    private static final String PV_SPEC = "spec";
    private static final String PV_NFS = "nfs";
    private static final String SERVER = "server";
    private static final String PATH = "path";
    private static final String READ_ONLY = "readOnly";

    private String server;
    private String path;
    private boolean readOnly;

    public NfsVolumeProperties(String server, String path, boolean readOnly) {
        this.server = server;
        this.path = path;
        this.readOnly = readOnly;
    }

    public String getServer() {
        return server;
    }

    public void setServer(String server) {
        this.server = server;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public boolean isReadOnly() {
        return readOnly;
    }

    public void setReadOnly(boolean readOnly) {
        this.readOnly = readOnly;
    }

    @Override
    public void setProperties(ModelNode node) {
        ModelNode nfs = node.get(PV_SPEC, PV_NFS);
        nfs.get(SERVER).set(server);
        nfs.get(PATH).set(path);
        nfs.get(READ_ONLY).set(readOnly);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        NfsVolumeProperties nfsVolumeProperties = (NfsVolumeProperties) o;
        return Objects.equals(readOnly, nfsVolumeProperties.readOnly)
                && Objects.equals(server, nfsVolumeProperties.server) && Objects.equals(path, nfsVolumeProperties.path);
    }

    @Override
    public int hashCode() {
        return Objects.hash(server, path, readOnly);
    }

    @Override
    public String toString() {
        return "NfsVolumeProperties{" + "server='" + server + '\'' + ", path='" + path + '\'' + ", readOnly=" + readOnly
                + '}';
    }
}
