package com.openshift.internal.restclient;

import static org.fest.assertions.Assertions.assertThat;

import org.junit.Test;

import com.openshift.restclient.utils.Samples;

public class DefaultClientConstructionTest extends TypeMapperFixture {
    @Test
    public void testKubernetesMasterVersionOk() throws Exception {
        getHttpClient().mockAsyncRequest(base + "/version",
            () -> responseOf(Samples.KUBERNETES_VERSION.getContentAsString()));

        assertThat(getIClient().getKubernetesMasterVersion()).isEqualTo("v1.6.1+5115d708d7");
    }

    @Test
    public void testKubernetesMasterVersion404() throws Exception {
        getHttpClient().mockAsyncRequest(base + "/version",
            () -> responseOf(404, "something wrong"));

        assertThat(getIClient().getKubernetesMasterVersion()).isEqualTo("");
    }

    @Test
    public void testOpenShiftMasterVersionOk() throws Exception {
        getHttpClient().mockAsyncRequest(base + "/version/openshift",
            () -> responseOf(Samples.OPENSHIFT_VERSION.getContentAsString()));

        assertThat(getIClient().getOpenshiftMasterVersion()).isEqualTo("v3.6.0-alpha.2+3c221d5");
    }

    @Test
    public void testOpenShiftMasterVersion404() throws Exception {
        getHttpClient().mockAsyncRequest(base + "/version/openshift",
            () -> responseOf(404, "something wrong"));

        assertThat(getIClient().getOpenshiftMasterVersion()).isEqualTo("");
    }
}
