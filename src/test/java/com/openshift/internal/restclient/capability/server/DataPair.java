/*******************************************************************************
 * Copyright (c) 2019 Red Hat, Inc. Distributed under license by Red Hat, Inc.
 * All rights reserved. This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Red Hat, Inc.
 ******************************************************************************/

package com.openshift.internal.restclient.capability.server;

import com.openshift.restclient.model.IResource;

public class DataPair {
    private IResource input;
    private String expected;

    public DataPair(IResource input, String expected) {
        this.input = input;
        this.expected = expected;
    }

    public IResource getResource() {
        return input;
    }

    public String getExpected() {
        return expected;
    }
}