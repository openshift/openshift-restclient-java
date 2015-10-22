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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jboss.dmr.ModelNode;
import org.jboss.dmr.ModelType;

import com.openshift.internal.restclient.model.volume.EmptyDirVolume;
import com.openshift.restclient.IClient;
import com.openshift.restclient.images.DockerImageURI;
import com.openshift.restclient.model.IContainer;
import com.openshift.restclient.model.IPort;
import com.openshift.restclient.model.IReplicationController;
import com.openshift.restclient.model.volume.IVolume;

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

	public ReplicationController(ModelNode node, IClient client, Map<String, String []> propertyKeys) {
		super(node, client, propertyKeys);
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
		if(node.getType() != ModelType.LIST) return new ArrayList<String>();
		Collection<String> list = new ArrayList<String>();
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
	public void addContainer(DockerImageURI tag,  Set<IPort> containerPorts, Map<String, String> envVars){
		addContainer(tag.getName(), tag, containerPorts, envVars, new ArrayList<String>());
	}
	
	@Override
	public void addContainer(String name, DockerImageURI tag, Set<IPort> containerPorts, Map<String, String> envVars, List<String> emptyDirVolumes) {
		
		IContainer container = addContainer(name);
		container.setImage(tag);
		
		if(!emptyDirVolumes.isEmpty()) {
			Set<IVolume> volumes = new HashSet<>();
			for (String path : emptyDirVolumes) {
				EmptyDirVolume volume = new EmptyDirVolume(new ModelNode());
				volume.setName(String.format("%s-%s", name, emptyDirVolumes.indexOf(path) + 1));
				volumes.add(volume);
			}
			container.setVolumes(volumes);
		}
		if(!containerPorts.isEmpty()) {
			Set<IPort> ports = new HashSet<>();
			for (IPort port : containerPorts) {
				ports.add(new Port(new ModelNode(), port));
			}
			container.setPorts(ports);
		}
		container.setEnvVars(envVars);
	}
	
	@Override
	public IContainer addContainer(String name) {
		ModelNode containers = get(SPEC_TEMPLATE_CONTAINERS);
		Container container = new Container(containers.add());
		container.setName(name);
		return container;
	}
	
}
