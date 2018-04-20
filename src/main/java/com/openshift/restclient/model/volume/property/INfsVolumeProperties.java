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

package com.openshift.restclient.model.volume.property;

public interface INfsVolumeProperties extends IPersistentVolumeProperties {

    String getPath();

    String getServer();

    boolean isReadOnly();

    void setPath(String path);

    void setServer(String path);

    void setReadOnly(boolean isReadOnly);

}
