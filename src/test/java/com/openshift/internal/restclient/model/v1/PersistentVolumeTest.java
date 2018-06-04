/*******************************************************************************
 * Copyright (c) 2015 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/

package com.openshift.internal.restclient.model.v1;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.jboss.dmr.ModelNode;
import org.junit.Before;
import org.junit.Test;

import com.openshift.internal.restclient.model.properties.ResourcePropertiesRegistry;
import com.openshift.internal.restclient.model.volume.PersistentVolume;
import com.openshift.internal.restclient.model.volume.property.HostPathVolumeProperties;
import com.openshift.internal.restclient.model.volume.property.NfsVolumeProperties;
import com.openshift.restclient.IClient;
import com.openshift.restclient.PredefinedResourceKind;
import com.openshift.restclient.model.volume.IPersistentVolume;
import com.openshift.restclient.model.volume.property.IHostPathVolumeProperties;
import com.openshift.restclient.model.volume.property.INfsVolumeProperties;
import com.openshift.restclient.utils.MemoryUnit;
import com.openshift.restclient.utils.Samples;

public class PersistentVolumeTest {

    private static final String VERSION = "v1";
    private static final Samples sample = Samples.V1_PERSISTENT_VOLUME;
    private IPersistentVolume pv;
    private IClient client;

    @Before
    public void setUp() {
        client = mock(IClient.class);
        ModelNode node = ModelNode.fromJSONString(sample.getContentAsString());
        pv = new PersistentVolume(node, client,
                ResourcePropertiesRegistry.getInstance().get(VERSION, PredefinedResourceKind.PERSISTENT_VOLUME.getIdentifier()));
    }

    @Test
    public void testGetCapacityString() {
        pv.setCapacity(13L, MemoryUnit.Pi);
        long capacity = pv.getCapacity("Ki");
        assertEquals(14293651161088L, capacity);

        capacity = pv.getCapacity("Ti");
        assertEquals(13312L, capacity);

        capacity = pv.getCapacity("Pi");
        assertEquals(13L, capacity);
    }

    @Test
    public void testGetCapacity() {
        pv.setCapacity(13L, MemoryUnit.Pi);
        long capacity = pv.getCapacity();
        assertEquals(14636698788954112L, capacity);
    }

    @Test
    public void testSetCapacity() {
        pv.setCapacity(1L, MemoryUnit.Ki);
        long capacity = pv.getCapacity();
        assertEquals(1024L, capacity);

        pv.setCapacity(31L, MemoryUnit.Ti);
        capacity = pv.getCapacity(MemoryUnit.Ti);
        assertEquals(31L, capacity);
    }

    @Test(expected = ArithmeticException.class)
    public void testSetCapacityOverflow() {
        /*
         * 2^4 * 2 ^60 = 2^64, LONG_MAX is only 2^64-1
         */
        pv.setCapacity(16L, MemoryUnit.Ei);
        pv.getCapacity();
    }

    @Test
    public void testGetCapacityUnit() {
        pv.setCapacity(1L, MemoryUnit.Ki);
        MemoryUnit unit = pv.getCapacityUnit();
        assertEquals(MemoryUnit.Ki, unit);

        pv.setCapacity(1L, MemoryUnit.Pi);
        unit = pv.getCapacityUnit();
        assertEquals(MemoryUnit.Pi, unit);
    }

    @Test
    public void testAccessModes() {
        pv.setAccessModes("ReadWriteOnce");
        Set<String> modes = pv.getAccessModes();
        assertTrue(modes.contains("ReadWriteOnce"));

        pv.setAccessModes("ReadWriteOnce", "ReadOnlyMany", "ReadWriteMany");
        modes = pv.getAccessModes();
        Set<String> expected = new HashSet<>();
        expected.addAll(Arrays.asList("ReadWriteOnce", "ReadOnlyMany", "ReadWriteMany"));
        assertTrue(modes.containsAll(expected));
    }

    @Test
    public void testReclaimPolicy() {
        pv.setReclaimPolicy("Recycle");
        String policy = pv.getReclaimPolicy();
        assertEquals(policy, "Recycle");
    }

    @Test
    public void testGetPersistentVolumeProperties() {
        pv.getPersistentVolumeProperties();

    }

    @Test
    public void testNFSVolume() {
        INfsVolumeProperties volume = new NfsVolumeProperties("10.10.10.10", "/tmp/dir", true);
        pv.setPersistentVolumeProperties(volume);
        volume = (INfsVolumeProperties) pv.getPersistentVolumeProperties();
        assertEquals("/tmp/dir", volume.getPath());
        assertEquals("10.10.10.10", volume.getServer());
        assertEquals(true, volume.isReadOnly());
    }

    @Test
    public void testHostPathVolume() {
        IHostPathVolumeProperties volume = new HostPathVolumeProperties("/tmp/dir");
        pv.setPersistentVolumeProperties(volume);
        volume = (IHostPathVolumeProperties) pv.getPersistentVolumeProperties();
        assertEquals("/tmp/dir", volume.getPath());
    }
}
