/*******************************************************************************
 * Copyright (c) 2016-2019 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/

package com.openshift.internal.restclient.model.kubeconfig;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.StringReader;

import org.junit.Before;
import org.junit.Test;

import com.openshift.internal.restclient.IntegrationTestHelper;
import com.openshift.restclient.model.kubeclient.ICluster;
import com.openshift.restclient.model.kubeclient.IContext;
import com.openshift.restclient.model.kubeclient.IKubeClientConfig;
import com.openshift.restclient.model.kubeclient.IUser;
import com.openshift.restclient.model.kubeclient.KubeClientConfigSerializer;
import com.openshift.restclient.utils.Samples;

public class KubeClientConfigTest {

    private IKubeClientConfig config;

    @Before
    public void setUp() throws Exception {

        String kubeConfig = Samples.V1_KUBE_CONFIG.getContentAsString();
        StringReader reader = new StringReader(kubeConfig);

        KubeClientConfigSerializer serializer = new KubeClientConfigSerializer();
        config = serializer.loadKubeClientConfig(reader);
    }

    @Test
    public void testDeserialization() {
        assertEquals("default/10-3-9-15:8443/jcantril@redhat.com", config.getCurrentContext());

        assertEquals(2, config.getClusters().size());
        ICluster cluster = config.getClusters().iterator().next();
        assertEquals("10-3-9-15:8443", cluster.getName());
        assertEquals("https://10.3.9.15:8443", cluster.getServer());
        assertEquals("Exp. cluster skipTLSVerify", true, cluster.isInsecureSkipTLSVerify());

        assertEquals(4, config.getContexts().size());
        IContext context = config.getContexts().iterator().next();
        assertNotNull(context);
        assertEquals(IntegrationTestHelper.getDefaultNamespace(), context.getNamespace());
        assertEquals("10-3-9-15:8443", context.getCluster());
        assertEquals("jcantril@redhat.com/10-3-9-15:8443", context.getUser());

        assertEquals("Exp. user count", 2, config.getUsers().size());
        IUser user = config.getUsers().iterator().next();
        assertEquals("admin/localhost:8443", user.getName());
        assertEquals("Q6cbJl4yMwP9o7crPbT5XMx9HSuv9W6jgXXE6omHK0Q", user.getToken());
    }

}
