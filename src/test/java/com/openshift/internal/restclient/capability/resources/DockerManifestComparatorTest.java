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

package com.openshift.internal.restclient.capability.resources;

import static org.junit.Assert.assertEquals;

import java.util.List;
import java.util.stream.Collectors;

import org.jboss.dmr.ModelNode;
import org.junit.Before;
import org.junit.Test;

import com.openshift.internal.restclient.capability.resources.DockerRegistryImageStreamImportCapability.ManifestComparator;
import com.openshift.restclient.utils.Samples;

public class DockerManifestComparatorTest {

    private ModelNode root;
    private ManifestComparator comparator = new ManifestComparator();

    @Before
    public void setUp() throws Exception {
        root = ModelNode.fromJSONString(Samples.V1_DOCKER_IMAGE_MANIFEST.getContentAsString());
    }

    @Test
    public void testCompareWithMultipleHistoryEntries() {
        ModelNode history = root.get("history");
        List<ModelNode> entries = history.asList().stream()
                .map(n -> ModelNode.fromJSONString(n.get("v1Compatibility").asString())).collect(Collectors.toList());
        entries.sort(comparator);

        ModelNode last = entries.get(entries.size() - 1);
        assertEquals("Exp. to retrieve the 'newest' entry with a non-null parent",
                "5f162644b2633962f753b9a09c7783d342c8aaebccaf6270fde68404d2af7a8c", last.get("id").asString());
    }

}
