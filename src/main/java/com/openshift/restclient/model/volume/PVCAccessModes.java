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

package com.openshift.restclient.model.volume;

public interface PVCAccessModes {

    static final String READ_WRITE_ONCE = "ReadWriteOnce";
    static final String READ_ONLY_MANY = "ReadOnlyMany";
    static final String READ_WRITE_MANY = "ReadWriteMany";

}
