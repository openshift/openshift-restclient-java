/******************************************************************************* 
 * Copyright (c) 2016 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/

package com.openshift.internal.restclient.model.v1;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import org.jboss.dmr.ModelNode;
import org.junit.Before;
import org.junit.Test;

import com.openshift.internal.restclient.model.ModelNodeBuilder;
import com.openshift.internal.restclient.model.volume.VolumeSource;
import com.openshift.restclient.model.volume.IHostPathVolumeSource;

public class HostPathVolumeSourceTest {

    private IHostPathVolumeSource source;

    @Before
    public void setUp() throws Exception {
        ModelNode node = new ModelNodeBuilder().set("name", "somevolumesourcename")
                .set("hostPath", new ModelNodeBuilder().set("path", "/foo").build()).build();
        source = (IHostPathVolumeSource) VolumeSource.create(node);
    }

    @Test
    public void testName() {
        assertThat(source.getName(), is("somevolumesourcename"));
        source.setName("thenewname");
        assertThat(source.getName(), is("thenewname"));
    }

    @Test
    public void testPath() {
        assertThat(source.getPath(), is("/foo"));
        source.setPath("thenewpath");
        assertThat(source.getPath(), is("thenewpath"));
    }

}
