/*******************************************************************************
 * Copyright (c) 2019 Red Hat, Inc. Distributed under license by Red Hat, Inc.
 * All rights reserved. This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Red Hat, Inc.
 ******************************************************************************/

package com.openshift.internal.restclient;


import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

public class OpenShiftVersionTest {

    @Test
    public void shouldNotDetectedIfNullVersionStrings() {
        // given
        OpenShiftMajorVersion version = new OpenShiftMajorVersion(null, null);
        // when
        boolean isDetected = version.isDetected();
        int majorVersion = version.get();
        // then
        assertThat(isDetected).isFalse();
        assertThat(majorVersion).isEqualTo(KubernetesVersion.NO_VERSION);
    }

    @Test
    public void shouldNotDetectedIfEmptyVersionStrings() {
        // given
        OpenShiftMajorVersion version = new OpenShiftMajorVersion("", "");
        // when
        boolean isDetected = version.isDetected();
        int majorVersion = version.get();
        // then
        assertThat(isDetected).isFalse();
        assertThat(majorVersion).isEqualTo(KubernetesVersion.NO_VERSION);
    }

    @Test
    public void shouldReturnVersionDetectedInOpenShiftString() {
        // given
        OpenShiftMajorVersion version = new OpenShiftMajorVersion("v3.11.43", "v1.11.0+d4cacc0");
        // when
        int majorVersion = version.get();
        // then
        assertThat(majorVersion).isEqualTo(3);
    }

    @Test
    public void shouldReturnVersion3ForKubernetes1_11() {
        // given
        OpenShiftMajorVersion version = new OpenShiftMajorVersion(null, "v1.11.0+d4cacc0");
        // when
        int majorVersion = version.get();
        // then
        assertThat(majorVersion).isEqualTo(3);
    }

    @Test
    public void shouldReturnVersion4ForKubernetes1_13() {
        // given
        OpenShiftMajorVersion version = new OpenShiftMajorVersion(null, "v1.13.4+f61b934");
        // when
        int majorVersion = version.get();
        // then
        assertThat(majorVersion).isEqualTo(4);
    }

    @Test
    public void shouldReturnVersion3IndependentlyOfKubernetesVersion() {
        // given
        OpenShiftMajorVersion version = new OpenShiftMajorVersion("v3.11.43", "v1.13.4+f61b934");
        // when
        int majorVersion = version.get();
        // then
        assertThat(majorVersion).isEqualTo(3);
    }

    @Test
    public void shouldReturnVersion4ByKubernetesIfOpenShiftVersionIsNotDetected() {
        // given
        OpenShiftMajorVersion version = new OpenShiftMajorVersion("smurf_version", "v1.13.4+f61b934");
        // when
        int majorVersion = version.get();
        // then
        assertThat(majorVersion).isEqualTo(4);
    }
}
