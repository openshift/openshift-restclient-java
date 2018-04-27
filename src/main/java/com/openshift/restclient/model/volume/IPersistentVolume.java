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

package com.openshift.restclient.model.volume;

import java.util.Set;

import com.openshift.internal.restclient.model.volume.PersistentVolume;
import com.openshift.restclient.model.IResource;
import com.openshift.restclient.model.volume.property.IPersistentVolumeProperties;
import com.openshift.restclient.utils.MemoryUnit;

public interface IPersistentVolume extends IResource {

    /**
     * @param unit
     *            the designated unit. One of "Ki", "Mi", "Gi", "Ti", "Pi", "Ei".
     * @return 0, if conversion not possible. Otherwise the capacity in given units.
     * @see {@link PersistentVolume#convert(String, MemoryUnit)}
     * @throws IllegalArgumentException
     *             if not supported {@code unit} given.
     */
    long getCapacity(String unit);

    /**
     * @param unit
     *            the designated unit. One of {@link MemoryUnit}'s Ki, Mi, Gi, Ti,
     *            Pi, Ei.
     * @return 0, if conversion not possible. Otherwise the capacity in given units.
     * @see {@link PersistentVolume#convert(String, MemoryUnit)}
     * @throws IllegalArgumentException
     *             if not supported {@code unit} given.
     */
    long getCapacity(MemoryUnit unit);

    /**
     * @return the capacity in bytes.
     */
    long getCapacity();

    /**
     * @return the unit in which the capacity is represent.
     * @see {@link MemoryUnit}
     */
    MemoryUnit getCapacityUnit();

    /**
     * Sets the capacity. There is no conversion between units.
     * 
     * @param capacity
     *            the capacity
     * @param unit
     *            the unit
     */
    void setCapacity(long capacity, MemoryUnit unit);

    /**
     * @return access modes
     * @see {@link com.openshift.restclient.model.volume.PVCAccessModes}
     * @see
     */
    Set<String> getAccessModes();

    /**
     * Sets the access modes. If there are any modes present, they are overridden by
     * the {@code modes} parameter.
     * 
     * @param modes
     *            the access modes
     */
    void setAccessModes(String... modes);

    /**
     * @return the value of 'spec.persistentVolumeReclaimPolicy'
     */
    String getReclaimPolicy();

    /**
     * Sets the 'spec.persistentVolumeReclaimPolicy' value
     * 
     * @param policy
     *            the policy string
     */
    void setReclaimPolicy(String policy);

    /**
     * @return specific persistent volume type properties
     */
    IPersistentVolumeProperties getPersistentVolumeProperties();

    /**
     * Sets the volume type properties.
     * 
     * @param properties
     *            the properties
     */
    void setPersistentVolumeProperties(IPersistentVolumeProperties properties);

}
