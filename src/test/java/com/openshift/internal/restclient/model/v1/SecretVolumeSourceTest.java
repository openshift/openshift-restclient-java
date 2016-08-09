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
package com.openshift.internal.restclient.model.v1;

import com.openshift.internal.restclient.model.volume.SecretVolumeSource;
import com.openshift.restclient.model.volume.ISecretVolumeSource;
import com.openshift.restclient.utils.Samples;
import org.jboss.dmr.ModelNode;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * @author Ulf Lilleengen
 */
public class SecretVolumeSourceTest {
    private ISecretVolumeSource source;

    @Before
    public void setup() {
        source = new SecretVolumeSource(ModelNode.fromJSONString(Samples.V1_SECRET_VOLUME_SOURCE.getContentAsString()));
    }

    @Test
    public void testDeserialization() {
        assertThat(source.getName(), is("mysource"));
        assertThat(source.getSecretName(), is("mysecret"));
    }

    @Test
    public void testPropertiesAreSet() {
        source = new SecretVolumeSource("mysource");
        source.setName("newsource");
        source.setSecretName("newsecret");

        assertThat(source.getName(), is("newsource"));
        assertThat(source.getSecretName(), is("newsecret"));
    }

    @Test
    public void testSerialization() {
        source.setName("newsource");
        source.setSecretName("newsecret");

        String json = source.toJSONString();
        ISecretVolumeSource sourceDeserialized = new SecretVolumeSource(ModelNode.fromJSONString(json));
        assertThat(sourceDeserialized.getName(), is("newsource"));
        assertThat(sourceDeserialized.getSecretName(), is("newsecret"));
    }
}
