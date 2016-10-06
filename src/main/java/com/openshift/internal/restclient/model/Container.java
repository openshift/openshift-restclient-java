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
package com.openshift.internal.restclient.model;

import static com.openshift.internal.util.JBossDmrExtentions.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.jboss.dmr.ModelNode;
import org.jboss.dmr.ModelType;

import com.openshift.internal.restclient.model.properties.ResourcePropertyKeys;
import com.openshift.internal.restclient.model.volume.EmptyDirVolume;
import com.openshift.internal.restclient.model.volume.VolumeMount;
import com.openshift.restclient.images.DockerImageURI;
import com.openshift.restclient.model.IContainer;
import com.openshift.restclient.model.ILifecycle;
import com.openshift.restclient.model.IPort;
import com.openshift.restclient.model.volume.IVolume;
import com.openshift.restclient.model.volume.IVolumeMount;

public class Container extends ModelNodeAdapter implements IContainer, ResourcePropertyKeys {
	
	private static final String IMAGE = "image";
	private static final String ENV = "env";
	private static final String IMAGE_PULL_POLICY = "imagePullPolicy";
	private static final String COMMAND = "command";
	private static final String COMMANDARGS = "args";
	private static final String LIFECYCLE = "lifecycle";
	private static final String VOLUMEMOUNTS = "volumeMounts";

	private ModelNode node;
	private Map<String, String[]> propertyKeys;

	public Container(ModelNode node) {
		this(node, Collections.emptyMap());
	}
	/**
	 * 
	 * @param node
	 * @param propertyKeys   the override paths from the defaults
	 */
	public Container(ModelNode node, Map<String, String[]> propertyKeys) {
		super(node, propertyKeys);
		this.node = node;
		this.propertyKeys = propertyKeys;
	}
	@Override
	public void setName(String name) {
		set(node, propertyKeys, NAME, name);
	}

	@Override
	public String getName() {
		return asString(node, propertyKeys, NAME);
	}

	@Override
	public void setImage(DockerImageURI tag) {
		set(node, propertyKeys, IMAGE, tag.getAbsoluteUri());
	}

	@Override
	public DockerImageURI getImage() {
		return new DockerImageURI(asString(node, propertyKeys, IMAGE));
	}

	@Override
	public void setEnvVars(Map<String, String> vars) {
		if(!vars.isEmpty()) {
			ModelNode env = get(node, propertyKeys, ENV);
			env.clear();
			for (Entry<String, String> var : vars.entrySet()) {
				addEnvVar(var.getKey(), var.getValue());
			}
		}
	}

	@Override
	public Map<String, String> getEnvVars() {
		HashMap<String, String> hashMap = new HashMap<>();
		ModelNode env = get(node, propertyKeys, ENV);
		if(env.isDefined()) {
			for (ModelNode var : env.asList()) {
				hashMap.put(
						asString(var,propertyKeys,NAME), 
						asString(var,propertyKeys,VALUE)
						);
			}
		}
		return hashMap;
	}

	@Override
	public void addEnvVar(String key, String value) {
		ModelNode env = get(node, propertyKeys, ENV);
		ModelNode varNode = new ModelNode();
		varNode.get(NAME).set(key);
		varNode.get(VALUE).set(value);
		env.add(varNode);
	}

	@Override
	public void setPorts(Set<IPort> ports) {
		ModelNode nodePorts = get(node, propertyKeys, PORTS);
		nodePorts.clear();
		for (IPort port : ports) {
			ModelNode portNode = nodePorts.add();
			new Port(portNode, port);
		}
	}

	@Override
	public Set<IPort> getPorts() {
		ModelNode nodePorts = get(node, propertyKeys, PORTS);
		Set<IPort> ports = new HashSet<>();
		if(nodePorts.isDefined()) {
			for (ModelNode port : nodePorts.asList()) {
				ports.add(new Port(port));
			}
		}
		return ports;
	}

	@Override
	public void setImagePullPolicy(String policy) {
		set(node, propertyKeys, IMAGE_PULL_POLICY, policy);
	}
	
	@Override
	public String getImagePullPolicy() {
		return asString(node, propertyKeys, IMAGE_PULL_POLICY);
	}
	
	@Override
    public void setCommand(List<String> command) {
        set(node, propertyKeys, COMMAND, command.toArray(new String[0]));
    }
	
	@Override
    public List<String> getCommand() {
        return asList(node, propertyKeys, COMMAND, ModelType.STRING);
    }
	
	@Override
    public void setCommandArgs(List<String> args) {
        set(node, propertyKeys, COMMANDARGS, args.toArray(new String[0]));
    }
	
	@Override
    public List<String> getCommandArgs() {
        return asList(node, propertyKeys, COMMANDARGS, ModelType.STRING);
    }

	@Override
	public void setLifecycle(ILifecycle lifecycle) {
		ModelNode lifecycleNode = ModelNode.fromJSONString(lifecycle.toJson());
		get(node, propertyKeys, LIFECYCLE).set(lifecycleNode);
	}

	@Override
	public ILifecycle getLifecycle() {
		if (node.has(LIFECYCLE)) {
			return Lifecycle.fromJson(get(node, propertyKeys, LIFECYCLE));
		} else {
		    return new Lifecycle.Builder().build();
		}
	}
	
	@Override
	public void setVolumes(Set<IVolume> volumes) {
		ModelNode mounts = get(node, propertyKeys, VOLUMEMOUNTS);
		mounts.clear();
		for (IVolume volume : volumes) {
			new EmptyDirVolume(mounts.add(), volume);
		}
	}
	
	@Override
	public Set<IVolume> getVolumes() {
		Set<IVolume> volumes = new HashSet<>();
		ModelNode mounts = get(node, propertyKeys, VOLUMEMOUNTS);
		if(mounts.isDefined()) {
			for (ModelNode node : mounts.asList()) {
				volumes.add(new VolumeMount(node));
			}
		}
		return volumes;
	}
	@Override
	public void setVolumeMounts(Set<IVolumeMount> volumes) {
		ModelNode mounts = get(node, propertyKeys, VOLUMEMOUNTS);
		mounts.clear();
		for (IVolumeMount volume : volumes) {
			new VolumeMount(mounts.add(), volume);
		}
	}
	
	@Override
	public Set<IVolumeMount> getVolumeMounts() {
		Set<IVolumeMount> volumes = new HashSet<>();
		ModelNode mounts = get(node, propertyKeys, VOLUMEMOUNTS);
		if(mounts.isDefined()) {
			for (ModelNode node : mounts.asList()) {
				volumes.add(new VolumeMount(node));
			}
		}
		return volumes;
	}
	
	@Override
	public String toJSONString() {
		return super.toJson(false);
	}
	
}
