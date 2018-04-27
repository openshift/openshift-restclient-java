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

package com.openshift.restclient;

import com.openshift.restclient.model.IStatus;

public class NotFoundException extends OpenShiftException {

    private static final long serialVersionUID = 1L;

    public NotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public NotFoundException(String message) {
        super(message);
    }

    public NotFoundException(Throwable cause) {
        super(cause, "");
    }

    public NotFoundException(Throwable cause, IStatus status, String message, Object... arguments) {
        super(cause, status, message, arguments);
    }
}
