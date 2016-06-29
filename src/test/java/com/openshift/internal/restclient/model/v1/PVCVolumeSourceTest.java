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

import com.openshift.internal.restclient.model.volume.PersistentVolumeClaimVolumeSource;
import com.openshift.internal.restclient.model.volume.VolumeSource;
import com.openshift.restclient.model.volume.IPersistentVolumeClaimVolumeSource;
import com.openshift.restclient.utils.Samples;
import org.jboss.dmr.ModelNode;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * @author Ulf Lilleengen
 */
public class PVCVolumeSourceTest {
    private IPersistentVolumeClaimVolumeSource source;

    @Before
    public void setup() {
        source = (IPersistentVolumeClaimVolumeSource)VolumeSource.create(ModelNode.fromJSONString(Samples.V1_PVC_VOLUME_SOURCE.getContentAsString()));
    }

    @Test
    public void testDeserialization() {
        assertThat(source.getName(), is("mysource"));
        assertThat(source.getClaimName(), is("myclaim"));
        assertThat(source.isReadOnly(), is(true));
    }

    @Test
    public void testPropertiesAreSet() {
        source = new PersistentVolumeClaimVolumeSource("mysource");
        source.setClaimName("myotherclaim");
        source.setReadOnly(false);
        source.setName("newsource");

        assertThat(source.getName(), is("newsource"));
        assertThat(source.getClaimName(), is("myotherclaim"));
        assertThat(source.isReadOnly(), is(false));
    }

    @Test
    public void testSerialization() {
        source.setClaimName("myotherclaim");
        source.setReadOnly(false);
        source.setName("newsource");

        String json = source.toJSONString();
        IPersistentVolumeClaimVolumeSource sourceDeserialized = new PersistentVolumeClaimVolumeSource(ModelNode.fromJSONString(json));
        assertThat(sourceDeserialized.getName(), is("newsource"));
        assertThat(sourceDeserialized.getClaimName(), is("myotherclaim"));
        assertThat(sourceDeserialized.isReadOnly(), is(false));
    }
}
