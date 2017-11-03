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

import com.openshift.internal.restclient.model.volume.EmptyDirVolumeSource;
import com.openshift.restclient.model.volume.IEmptyDirVolumeSource;
import com.openshift.restclient.utils.Samples;
import org.jboss.dmr.ModelNode;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * @author Ulf Lilleengen
 */
public class EmptyDirVolumeSourceTest {

    private IEmptyDirVolumeSource source;

    @Before
    public void setup() {
        source = new EmptyDirVolumeSource(ModelNode.fromJSONString(Samples.V1_EMPTYDIR_VOLUME_SOURCE.getContentAsString()));
    }

    @Test
    public void testDeserialization() {
        assertThat(source.getName(), is("mysource"));
        assertThat(source.getMedium(), is("mymedium"));
    }

    @Test
    public void testPropertiesAreSet() {
        source = new EmptyDirVolumeSource("mysource");
        source.setName("newsource");
        source.setMedium("newmedium");

        assertThat(source.getName(), is("newsource"));
        assertThat(source.getMedium(), is("newmedium"));
    }

    @Test
    public void testSerialization() {
        source.setName("newsource");
        source.setMedium("newmedium");

        String json = source.toJSONString();
        IEmptyDirVolumeSource sourceDeserialized = new EmptyDirVolumeSource(ModelNode.fromJSONString(json));
        assertThat(sourceDeserialized.getName(), is("newsource"));
        assertThat(sourceDeserialized.getMedium(), is("newmedium"));
    }
}
