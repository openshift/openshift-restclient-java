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

package com.openshift.restclient.utils;

public enum MemoryUnit {
    B(1l), K(1000l), Ki(1024l), M(1000l*1000l), Mi(1024l*1024l), 
        G(1000l*1000l*1000l), Gi(1024l*1024l*1024l), 
        T(1000l*1000l*1000l*1000l), Ti(1024l*1024l*1024l*1024l),
        P(1000l*1000l*1000l*1000l*1000l), 
        Pi(1024l*1024l*1024l*1024l*1024l), 
        E(1000l*1000l*1000l*1000l*1000l*1000l),
        Ei(1024l*1024l*1024l*1024l*1024l*1024l);

    private final long factor;

    private MemoryUnit(long factor) {
        this.factor = factor;
    }

    public long getFactor() {
        return factor;
    }
}
