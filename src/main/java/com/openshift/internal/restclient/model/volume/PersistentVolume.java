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

package com.openshift.internal.restclient.model.volume;

import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jboss.dmr.ModelNode;
import org.jboss.dmr.ModelType;

import com.openshift.internal.restclient.model.KubernetesResource;
import com.openshift.internal.restclient.model.volume.property.HostPathVolumeProperties;
import com.openshift.internal.restclient.model.volume.property.ISettablePersistentVolumeProperties;
import com.openshift.internal.restclient.model.volume.property.NfsVolumeProperties;
import com.openshift.restclient.IClient;
import com.openshift.restclient.model.volume.IPersistentVolume;
import com.openshift.restclient.model.volume.VolumeType;
import com.openshift.restclient.model.volume.property.IHostPathVolumeProperties;
import com.openshift.restclient.model.volume.property.INfsVolumeProperties;
import com.openshift.restclient.model.volume.property.IPersistentVolumeProperties;
import com.openshift.restclient.utils.MemoryUnit;

public class PersistentVolume extends KubernetesResource implements IPersistentVolume {

    private static final String PV_ACCESS_MODES = "spec.accessModes";
    private static final String PV_CAPACITY = "spec.capacity.storage";
    private static final String PV_RECLAIM_POLICY = "spec.persistentVolumeReclaimPolicy";
    private static final String PV_NFS = "spec.nfs";
    private static final String PV_HOST_PATH = "spec.hostPath";
    private static final String PV_SPEC = "spec";
    private static final String SERVER = "server";
    private static final String PATH = "path";
    private static final String READ_ONLY = "readOnly";

    public PersistentVolume(ModelNode node, IClient client, Map<String, String[]> propertyKeys) {
        super(node, client, propertyKeys);
    }

    /**
     * @param unit
     *            the designated unit. One of "Ki", "Mi", "Gi", "Ti", "Pi", "Ei".
     * @return 0, if conversion not possible. Otherwise the capacity in given units.
     * @see {@link PersistentVolume#convert(String, MemoryUnit)}
     * @throws IllegalArgumentException
     *             if not supported {@code unit} given.
     */
    @Override
    public long getCapacity(String unit) {
        String capacity = asString(PV_CAPACITY);
        return convert(capacity, MemoryUnit.valueOf(unit));
    }

    /**
     * @param unit
     *            the designated unit. One of {@link MemoryUnit}'s Ki, Mi, Gi, Ti,
     *            Pi, Ei.
     * @return 0, if conversion not possible. Otherwise the capacity in given units.
     * @see {@link PersistentVolume#convert(String, MemoryUnit)}
     * @throws IllegalArgumentException
     *             if not supported {@code unit} given.
     */
    @Override
    public long getCapacity(MemoryUnit unit) {
        String capacity = asString(PV_CAPACITY);
        return convert(capacity, unit);
    }

    /**
     * @return the capacity in bytes.
     */
    @Override
    public long getCapacity() {
        String capacity = asString(PV_CAPACITY);
        return Math.multiplyExact(convert(capacity, MemoryUnit.Ki), 1024L);
    }

    /**
     * Sets the capacity. There is no conversion between units.
     * 
     * @param capacity
     *            the capacity
     * @param unit
     *            the unit
     */
    @Override
    public void setCapacity(long capacity, MemoryUnit unit) {
        set(PV_CAPACITY, capacity + unit.name());
    }

    /**
     * @return the unit in which the capacity is represent.
     * @see {@link MemoryUnit}
     */
    @Override
    public MemoryUnit getCapacityUnit() {
        return parseCapacityUnit(asString(PV_CAPACITY));
    }

    /**
     * @return access modes
     * @see {@link com.openshift.restclient.model.volume.PVCAccessModes}
     * @see
     */
    @Override
    public Set<String> getAccessModes() {
        return asSet(PV_ACCESS_MODES, ModelType.STRING);
    }

    /**
     * Sets the access modes. If there are any modes present, they are overridden by
     * the {@code modes} parameter.
     * 
     * @param modes
     *            the access modes
     */
    @Override
    public void setAccessModes(String... modes) {
        get(PV_ACCESS_MODES).setEmptyList();
        set(PV_ACCESS_MODES, modes);
    }

    /**
     * @return the value of 'spec.persistentVolumeReclaimPolicy'
     */
    @Override
    public String getReclaimPolicy() {
        return asString(PV_RECLAIM_POLICY);
    }

    /**
     * Sets the 'spec.persistentVolumeReclaimPolicy' value
     * 
     * @param policy
     *            the policy string
     */
    @Override
    public void setReclaimPolicy(String policy) {
        set(PV_RECLAIM_POLICY, policy);
    }

    /**
     * @return specific persistent volume type
     */
    @Override
    public IPersistentVolumeProperties getPersistentVolumeProperties() {
        IPersistentVolumeProperties properties;
        switch (getPVType()) {
        case VolumeType.HOST_PATH:
            properties = createHostPathVolumeProperties();
            break;
        case VolumeType.NFS:
            properties = createNFSVolumeProperties();
            break;
        default:
            properties = null;
            break;
        }
        return properties;
    }

    /**
     * Sets the volume type properties.
     * 
     * @param properties
     *            the properties
     */
    @Override
    public void setPersistentVolumeProperties(IPersistentVolumeProperties properties) {
        if (properties instanceof ISettablePersistentVolumeProperties) {
            ((ISettablePersistentVolumeProperties) properties).setProperties(getNode());
        }
    }

    /**
     * @return a string value of the type. One of {@link VolumeType}.
     */
    public String getPVType() {
        final ModelNode spec = get(PV_SPEC);
        return VolumeType.getTypes().stream().filter(spec::hasDefined).findFirst().get();
    }

    /**
     * @return values of 'spec.nfs' wrapped in {@link INfsVolumeProperties}
     */
    private INfsVolumeProperties createNFSVolumeProperties() {
        ModelNode node = get(PV_NFS);
        String server = asString(node, SERVER);
        String path = asString(node, PATH);
        boolean readOnly = asBoolean(node, READ_ONLY);
        return new NfsVolumeProperties(server, path, readOnly);
    }

    /**
     * @return the value of 'spec.hostPath.path' wrapped in
     *         {@link IHostPathVolumeProperties}
     */
    private IHostPathVolumeProperties createHostPathVolumeProperties() {
        ModelNode node = get(PV_HOST_PATH);
        String path = asString(node, PATH);
        return new HostPathVolumeProperties(path);
    }

    /**
     * Converts capacity string (i.e. '10Gi') to it's numeric representation. The
     * following assumptions are used to compute values: One Ki = 2^10 One Mi = 2^20
     * One Gi = 2^30 One Ti = 2^40 One Pi = 2^50 One Ei = 2^60
     *
     * Java's type long can contain max value of 2^64, enough for a few Exbibytes.
     * Following equation is used to convert values:
     *
     * kubernetesGivenNumber << (10 * i), where << is implicit multiplication by
     * powers of 2 and (10 * i) is the order of magnitude. Example: convert 1Ti to
     * Mi = 1Ti << (10*2) = 1 * 2^20 = 2^20
     *
     * @param capacity
     *            the capacity string
     * @param designatedUnit
     *            the designated unit of type {@link MemoryUnit}. Possible values
     *            'Ki, Mi, Gi, Ti, Pi, Ei'.
     * @return 0 if conversion is not possible, otherwise the numeric representation
     *         converted to designatedUnit. Conversion is not possible when the
     *         designatedUnit cannot contain the unit given by Kubernetes in
     *         integral form.
     */
    private static long convert(final String capacity, final MemoryUnit designatedUnit) {
        long number = parseCapacityValue(capacity);
        MemoryUnit currentUnit = parseCapacityUnit(capacity);

        int operation = currentUnit.compareTo(designatedUnit);
        if (operation == 0L) {
            return number;
        } else if (operation > 0L) {
            return safeMultiplicationBy1024(number, operation); // number << (10 * operation);
        } else {
            return 0L;
        }
    }

    /**
     * @throws ArithmeticException
     *             if computation overflows
     */
    private static long safeMultiplicationBy1024(long value, long times) {
        long multiplicand = (long) Math.pow(1024L, times);
        return Math.multiplyExact(value, multiplicand);
    }

    private static long parseCapacityValue(String capacityString) {
        if (capacityString == null) {
            return 0L;
        }
        Pattern pattern = Pattern.compile("(\\d+)(\\w{2})");
        Matcher m = pattern.matcher(capacityString);
        if (!m.find()) {
            return 0L;
        }
        return Long.parseLong(m.group(1));
    }

    private static MemoryUnit parseCapacityUnit(String capacityString) {
        if (capacityString == null) {
            return null;
        }
        Pattern pattern = Pattern.compile("(\\d+)(\\w{2})");
        Matcher m = pattern.matcher(capacityString);
        if (!m.find()) {
            return null;
        }
        return MemoryUnit.valueOf(m.group(2));
    }
}