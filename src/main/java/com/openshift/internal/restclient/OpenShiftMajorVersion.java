/*******************************************************************************
 * Copyright (c) 2019 Red Hat, Inc. Distributed under license by Red Hat, Inc.
 * All rights reserved. This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Red Hat, Inc.
 ******************************************************************************/

package com.openshift.internal.restclient;

public class OpenShiftMajorVersion {

    private KubernetesVersion openshiftVersion;
    private KubernetesVersion kubernetesVersion;
    
    public OpenShiftMajorVersion(String openshiftAPIVersion, String kubernetesMasterVersion) {
        this.openshiftVersion = new KubernetesVersion(openshiftAPIVersion);
        this.kubernetesVersion = new KubernetesVersion(kubernetesMasterVersion);
    }

    public boolean isDetected() {
        return openshiftVersion.isDetected()
                || kubernetesVersion.isDetected();
    }

    public int get() {
        if (openshiftVersion.isDetected()) {
            return openshiftVersion.getMajor();
        }

        if (!kubernetesVersion.isDetected()) {
            return KubernetesVersion.NO_VERSION;
        }

        return mapKubernetesToOpenShift(kubernetesVersion);
    }

    private int mapKubernetesToOpenShift(KubernetesVersion kubernetesVersion) {
        if (kubernetesVersion.getMajor() < 1) {
            return 3;
        }

        if (kubernetesVersion.getMajor() == 1
            && kubernetesVersion.getMinor() <= 11) {
            return 3;
        }

        return 4;
    }

}
