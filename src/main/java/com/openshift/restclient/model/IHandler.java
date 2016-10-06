/*******************************************************************************
 * Copyright (c) 2016 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package com.openshift.restclient.model;

/**
 * @author Ulf Lilleengen
 */
public interface IHandler extends JSONSerializeable {
    static final String EXEC = "exec";
    static final String HTTP = "httpGet";
    static final String TCP = "tcpSocket";

    String getType();
}
