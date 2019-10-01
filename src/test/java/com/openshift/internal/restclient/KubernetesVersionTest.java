/*******************************************************************************
 * Copyright (c) 2019 Red Hat, Inc. Distributed under license by Red Hat, Inc.
 * All rights reserved. This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Red Hat, Inc.
 ******************************************************************************/

package com.openshift.internal.restclient;

import static org.fest.assertions.Assertions.assertThat;

import org.junit.Test;

public class KubernetesVersionTest {

    @Test
    public void shouldReturnNotDetectedIfNullVersionString() {
        // given
        KubernetesVersion version = new KubernetesVersion(null);
        // when
        boolean isDetected = version.isDetected();
        // then
        assertThat(isDetected).isFalse();
    }
    
    @Test
    public void shouldReturnNotDetectedIfEmptyVersionString() {
        // given
        KubernetesVersion version = new KubernetesVersion("");
        // when
        boolean isDetected = version.isDetected();
        // then
        assertThat(isDetected).isFalse();
    }

    @Test
    public void shouldReturnNotDetectedIfDoesntStartWithV() {
        // given
        KubernetesVersion version = new KubernetesVersion("3");
        // when
        boolean isDetected = version.isDetected();
        // then
        assertThat(isDetected).isFalse();
    }

    @Test
    public void shouldNotParseIfOnlyMajorPresent() {
        // given
        KubernetesVersion version = new KubernetesVersion("v3");
        // when
        // then
        assertThat(version.isDetected()).isFalse();
        assertVersions(KubernetesVersion.NO_VERSION, KubernetesVersion.NO_VERSION, KubernetesVersion.NO_VERSION, null,
                version);
    }

    @Test
    public void shouldNotParseIfOnlyMajorAndMinorVersion() {
        // given
        KubernetesVersion version = new KubernetesVersion("v3.2");
        // when
        // then
        assertThat(version.isDetected()).isFalse();
        assertVersions(KubernetesVersion.NO_VERSION, KubernetesVersion.NO_VERSION, KubernetesVersion.NO_VERSION, null,
                version);
    }

    @Test
    public void shouldParseIfNoPatchVersion() {
        // given
        KubernetesVersion version = new KubernetesVersion("v3.2.1");
        // when
        // then
        assertThat(version.isDetected()).isTrue();
        assertVersions(3,2,1, null,
                version);
    }

    @Test
    public void shouldMajorAndMinorAndPatchAndGitVersion() {
        // given
        KubernetesVersion version = new KubernetesVersion("v3.2.1+d42");
        // when
        // then
        assertThat(version.isDetected()).isTrue();
        assertVersions(3, 2, 1, "d42", version);
    }

    private void assertVersions(int major, int minor, int patch, String git, KubernetesVersion version) {
        assertThat(version.getMajor()).isEqualTo(major);
        assertThat(version.getMinor()).isEqualTo(minor);
        assertThat(version.getPatch()).isEqualTo(patch);
        assertThat(version.getGit()).isEqualTo(git);
    }
    
}
