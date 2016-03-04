/*******************************************************************************
 * Copyright (c) 2015 Red Hat, Inc. Distributed under license by Red Hat, Inc.
 * All rights reserved. This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Red Hat, Inc.
 ******************************************************************************/
package com.openshift.internal.restclient.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
import org.jboss.dmr.ModelNode;
import org.jboss.dmr.ModelType;

import com.openshift.internal.restclient.model.volume.VolumeMount;
import com.openshift.internal.restclient.model.volume.VolumeSource;
import com.openshift.restclient.IClient;
import com.openshift.restclient.images.DockerImageURI;
import com.openshift.restclient.model.IContainer;
import com.openshift.restclient.model.IEnvironmentVariable;
import com.openshift.restclient.model.IPort;
import com.openshift.restclient.model.IReplicationController;
import com.openshift.restclient.model.volume.IVolumeMount;
import com.openshift.restclient.model.volume.IVolumeSource;

/**
 * @author Jeff Cantrill
 */
public class ReplicationController extends KubernetesResource implements IReplicationController{

	public static final String SPEC_TEMPLATE_CONTAINERS = "spec.template.spec.containers";
	protected static final String SPEC_TEMPLATE_LABELS = "spec.template.metadata.labels";
	protected static final String VOLUMES = "spec.template.spec.volumes";
	protected static final String SPEC_REPLICAS = "spec.replicas";
	protected static final String SPEC_SELECTOR = "spec.selector";
	protected static final String STATUS_REPLICA = "status.replicas";

	protected static final String IMAGE = "image";
	protected static final String ENV = "env";
	private Map<String, String[]> propertyKeys;

	public ReplicationController(ModelNode node, IClient client, Map<String, String []> propertyKeys) {
		super(node, client, propertyKeys);
		this.propertyKeys = propertyKeys;
	}
	
	@Override
	public void setEnvironmentVariable(String name, String value) {
		setEnvironmentVariable(null, name, value);
	}



	@Override
	public void setEnvironmentVariable(String containerName, String name, String value) {
		String defaultedContainerName = StringUtils.defaultIfBlank(containerName, "");
		ModelNode specContainers = get(SPEC_TEMPLATE_CONTAINERS);
		if(specContainers.isDefined()) { //should ALWAYS exist
			List<ModelNode> containers = specContainers.asList();
			if(!containers.isEmpty()) {

				Optional<ModelNode> opt = containers.stream().filter(n->defaultedContainerName.equals(asString(n, NAME))).findFirst();
				ModelNode node =  opt.isPresent() ? opt.get() : containers.get(0);
				ModelNode envNode = get(node, ENV);

				List<ModelNode> varList = new ArrayList<>(envNode.asList());

				//Check if variable already exists
				Optional<ModelNode> targetVar = varList.stream().filter(n->name.equals(asString(n, NAME))).findFirst();

				ModelNode var;
				if (targetVar.isPresent()) {
					var = targetVar.get();
					int i = varList.indexOf(var);
					set(var, VALUE, value);
					varList.set(i, var);
				} else {
					var = new ModelNode();
					set(var, NAME, name);
					set(var, VALUE, value);
					varList.add(var);
				}
				envNode.set(varList);
			}
		}
	}

	@Override
	public Collection<IEnvironmentVariable> getEnvironmentVariables() {
		return getEnvironmentVariables(null);
	}

	@Override
	public Collection<IEnvironmentVariable> getEnvironmentVariables(String containerName) {
		String name = StringUtils.defaultIfBlank(containerName, "");
		ModelNode specContainers = get(SPEC_TEMPLATE_CONTAINERS);
		if(specContainers.isDefined()) {
			List<ModelNode> containers = specContainers.asList();
			if(!containers.isEmpty()) {
				Optional<ModelNode> opt = containers.stream().filter(n->name.equals(asString(n, NAME))).findFirst();
				ModelNode node =  opt.isPresent() ? opt.get() : containers.get(0);
				ModelNode envNode = get(node, ENV);
				if(envNode.isDefined()) {
					return envNode.asList()
							.stream()
							.map(n-> new EnvironmentVariable(n, propertyKeys))
							.collect(Collectors.toList());
				}
			}
		}
		return Collections.emptyList();
	}

	@Override
	public int getDesiredReplicaCount() {
		return asInt(SPEC_REPLICAS);
	}
	
	@Override
	public int getReplicas() {
		return getDesiredReplicaCount();
	}

	@Override
	public void setDesiredReplicaCount(int count) {
		set(SPEC_REPLICAS, count);
	}

	@Override
	public void setReplicas(int count) {
		setDesiredReplicaCount(count);
	}
	
	@Override
	public Map<String, String> getReplicaSelector() {
		return asMap(SPEC_SELECTOR);
	}
	
	
	@Override
	public void setReplicaSelector(String key, String value) {
		Map<String, String> selector = new HashMap<>();
		selector.put(key, value);
		setReplicaSelector(selector);
	}

	@Override
	public void setReplicaSelector(Map<String, String> selector) {
		get(SPEC_SELECTOR).clear();
		set(SPEC_SELECTOR, selector);
	}
	@Override
	public int getCurrentReplicaCount() {
		return asInt(STATUS_REPLICA);
	}

	@Override
	public Collection<String> getImages() {
		ModelNode node = get(SPEC_TEMPLATE_CONTAINERS);
		if(node.getType() != ModelType.LIST) return new ArrayList<>();
		Collection<String> list = new ArrayList<>();
		for (ModelNode entry : node.asList()) {
			list.add(entry.get(IMAGE).asString());
		}
		return list;
	}
	
	@Override
	public void addTemplateLabel(String key, String value) {
		ModelNode labels = get(SPEC_TEMPLATE_LABELS);
		labels.get(key).set(value);
	}
	
	@Override
	public IContainer addContainer(DockerImageURI tag,  Set<IPort> containerPorts, Map<String, String> envVars){
		return addContainer(tag.getName(), tag, containerPorts, envVars, new ArrayList<String>());
	}
	
	@Override
	public IContainer addContainer(String name, DockerImageURI tag, Set<IPort> containerPorts, Map<String, String> envVars, List<String> emptyDirVolumes) {
		
		IContainer container = addContainer(name);
		container.setImage(tag);
		
		if(!emptyDirVolumes.isEmpty()) {
			Set<IVolumeMount> volumes = new HashSet<>();
			for (String path : emptyDirVolumes) {
				VolumeMount volume = new VolumeMount(new ModelNode());
				volume.setMountPath(path);
				volume.setName(String.format("%s-%s", name, emptyDirVolumes.indexOf(path) + 1));
				volumes.add(volume);
				addEmptyDirVolumeToPodSpec(volume);
			}
			container.setVolumeMounts(volumes);
		}
		if(!containerPorts.isEmpty()) {
			Set<IPort> ports = new HashSet<>();
			for (IPort port : containerPorts) {
				ports.add(new Port(new ModelNode(), port));
			}
			container.setPorts(ports);
		}
		container.setEnvVars(envVars);
		return container;
	}
	
	private void addEmptyDirVolumeToPodSpec(VolumeMount volume) {
		ModelNode volNode = get(VOLUMES);
		if(volNode.isDefined()) {
			List<ModelNode> podVolumes = volNode.asList();
			for (ModelNode node : podVolumes) {
				if(volume.getName().equals(asString(node,NAME))) {
					//already exists
					return;
				}
			}
		}
		ModelNode podVolume = volNode.add();
		set(podVolume,NAME,volume.getName());
		set(podVolume,"emptyDir.medium","");
	}
	
	@Override
	public IContainer addContainer(String name) {
		ModelNode containers = get(SPEC_TEMPLATE_CONTAINERS);
		Container container = new Container(containers.add());
		container.setName(name);
		return container;
	}

	@Override
	public Set<IVolumeSource> getVolumes() {
		ModelNode vol = get(VOLUMES);
		Set<IVolumeSource> volumes = new HashSet<>();
		if(vol.isDefined()) {
			for (ModelNode node : vol.asList()) {
				volumes.add(new VolumeSource(node));
			}
		}
		return volumes;
	}
}
