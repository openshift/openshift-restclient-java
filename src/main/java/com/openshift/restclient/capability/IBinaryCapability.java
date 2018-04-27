/*******************************************************************************
 * Copyright (c) 2015 Red Hat, Inc. Distributed under license by Red Hat, Inc.
 * All rights reserved. This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Red Hat, Inc.
 ******************************************************************************/

package com.openshift.restclient.capability;

/**
 * @author Andre Dietisheim
 */
public interface IBinaryCapability extends ICapability {

    static final OpenShiftBinaryOption SKIP_TLS_VERIFY = new SkipTlsVerify();

    /**
     * Skips the SSL/TLS Verification when using a binary capability
     */
    static class SkipTlsVerify implements OpenShiftBinaryOption {

        @Override
        public void append(StringBuilder commandLine) {
            commandLine.append(" --insecure-skip-tls-verify=true");
        }
    }

    static interface OpenShiftBinaryOption {
        void append(StringBuilder builder);
    }

    static final String OPENSHIFT_BINARY_LOCATION = "openshift.restclient.oc.location";

}
