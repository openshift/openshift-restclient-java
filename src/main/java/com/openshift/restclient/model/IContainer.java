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

package com.openshift.restclient.model;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.openshift.restclient.api.models.INameSetable;
import com.openshift.restclient.images.DockerImageURI;
import com.openshift.restclient.model.probe.IProbe;
import com.openshift.restclient.model.volume.IVolume;
import com.openshift.restclient.model.volume.IVolumeMount;

public interface IContainer extends INameSetable, JSONSerializeable {

    String getName();

    void setImage(DockerImageURI tag);

    DockerImageURI getImage();

    /**
     * replace the env vars
     * 
     */
    void setEnvVars(Map<String, String> vars);

    /**
     * 
     * @return an unmodifiable map of env vars
     */
    Map<String, String> getEnvVars();

    void addEnvVar(String key, String value);

    /**
     * replaces the set of ports
     * 
     */
    void setPorts(Set<IPort> ports);

    /**
     * 
     * @return an unmodifiable set of the container ports
     */
    Set<IPort> getPorts();

    void setImagePullPolicy(String policy);

    String getImagePullPolicy();

    void setLifecycle(ILifecycle lifecycle);

    ILifecycle getLifecycle();

    void setCommand(List<String> command);

    List<String> getCommand();

    void setCommandArgs(List<String> args);

    List<String> getCommandArgs();

    @Deprecated
    void setVolumes(Set<IVolume> volumes);

    @Deprecated
    Set<IVolume> getVolumes();

    void setVolumeMounts(Set<IVolumeMount> volumes);

    Set<IVolumeMount> getVolumeMounts();

    /**
     * Add a volumemount with the given name
     * 
     * @return IVolumeMount
     */
    IVolumeMount addVolumeMount(String name);

    String getRequestsCPU();

    void setRequestsCPU(String requestsCPU);

    String getRequestsMemory();

    void setRequestsMemory(String requestsMemory);

    String getLimitsCPU();

    void setLimitsCPU(String limitsCPU);

    String getLimitsMemory();

    void setLimitsMemory(String limitsMemory);

    IProbe getReadinessProbe();

    IProbe getLivenessProbe();

}
