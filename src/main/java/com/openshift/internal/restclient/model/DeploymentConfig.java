package com.openshift.internal.restclient.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.jboss.dmr.ModelNode;

import com.openshift.internal.restclient.model.deploy.DeploymentTrigger;
import com.openshift.internal.restclient.model.deploy.ImageChangeTrigger;
import com.openshift.internal.restclient.model.volume.EmptyDirVolume;
import com.openshift.restclient.IClient;
import com.openshift.restclient.images.DockerImageURI;
import com.openshift.restclient.model.IDeploymentConfig;
import com.openshift.restclient.model.IPort;
import com.openshift.restclient.model.deploy.DeploymentTriggerType;
import com.openshift.restclient.model.deploy.IDeploymentTrigger;

/**
 * @author Jeff Cantrill
 */
public class DeploymentConfig extends KubernetesResource implements IDeploymentConfig{
	
	public static final String DEPLOYMENTCONFIG_CONTAINERS = "spec.template.spec.containers";
	private static final String VOLUMES = "spec.template.spec.volumes";
	private static final String DEPLOYMENTCONFIG_REPLICAS = "spec.replicas";
	private static final String DEPLOYMENTCONFIG_REPLICA_SELECTOR = "spec.selector";
	private static final String DEPLOYMENTCONFIG_TEMPLATE_LABELS = "spec.template.metadata.labels";
	private static final String DEPLOYMENTCONFIG_TRIGGERS = "spec.triggers";
	private static final String DEPLOYMENTCONFIG_STRATEGY = "spec.strategy.type";
	
	private static final String IMAGE = "image";
	private static final String ENV = "env";
	private static final String TYPE = "type";

	private final Map<String, String[]> propertyKeys;

	public DeploymentConfig(ModelNode node, IClient client, Map<String, String []> propertyKeys) {
		super(node, client, propertyKeys);
		this.propertyKeys = propertyKeys;
	}
	
	@Override
	public Map<String, String> getReplicaSelector(){
		return asMap(DEPLOYMENTCONFIG_REPLICA_SELECTOR);
	}
	
	@Override
	public void setReplicaSelector(Map<String, String> selector) {
		get(DEPLOYMENTCONFIG_REPLICA_SELECTOR).clear();
		set(DEPLOYMENTCONFIG_REPLICA_SELECTOR, selector);
	}

	@Override
	public void setReplicaSelector(String key, String value) {
		Map<String, String> selector = new HashMap<>();
		selector.put(key, value);
		setReplicaSelector(selector);
	}

	@Override
	public List<String> getTriggerTypes(){
		List<String> types = new ArrayList<String>();
		ModelNode triggers = get(DEPLOYMENTCONFIG_TRIGGERS);
		for (ModelNode node : triggers.asList()) {
			types.add(node.get(TYPE).asString());
		}
		return types;
	}
	
	//FIXME
	public List<String> getImageNames(){
		List<String> names = new ArrayList<String>();
		List<ModelNode> containers = get(DEPLOYMENTCONFIG_CONTAINERS).asList();
		for (ModelNode container : containers) {
			names.add(container.get("image").asString());
		}
		return names;
	}
	
	@Override
	public int getReplicas(){
		return asInt(DEPLOYMENTCONFIG_REPLICAS);
	}
	
	
	@Override
	public void setReplicas(int replicas) {
		set(DEPLOYMENTCONFIG_REPLICAS, replicas);
	}
	
	

	@Override
	public void addTemplateLabel(String key, String value) {
		ModelNode labels = get(DEPLOYMENTCONFIG_TEMPLATE_LABELS);
		labels.get(key).set(value);
	}

	@Override
	public void addContainer(DockerImageURI tag,  Set<IPort> containerPorts, Map<String, String> envVars){
		addContainer(tag.getName(), tag, containerPorts, envVars, new ArrayList<String>());
	}
	
	@Override
	public void addContainer(String name, DockerImageURI tag, Set<IPort> containerPorts, Map<String, String> envVars, List<String> emptyDirVolumes) {
		
		ModelNode container = new ModelNode();
		container.get(NAME).set(name); //required?
		container.get(getPath(IMAGE)).set(tag.getUriWithoutHost());
		
		if(emptyDirVolumes.size() > 0) {
			ModelNode volumeMounts = container.get("volumeMounts");
			ModelNode volumes = get(VOLUMES);
			for (String path : emptyDirVolumes) {
				EmptyDirVolume volume = new EmptyDirVolume(volumes.add());
				final String volName = String.format("%s-%s", name, emptyDirVolumes.indexOf(path) + 1);
				volume.setName(volName);
				ModelNode volMount = volumeMounts.add();
				volMount.get(NAME).set(volName);
				volMount.get("mountPath").set(path);
			}
		}
		
		ModelNode ports = container.get(getPath(PORTS));
		for (IPort port : containerPorts) {
			ModelNode portNode = ports.add();
			new Port(portNode, port);
		}
		
		if(!envVars.isEmpty()) {
			ModelNode env = container.get(getPath(ENV));
			for (Entry<String,String> var : envVars.entrySet()) {
				ModelNode varNode = new ModelNode();
				//dont use path here
				varNode.get(NAME).set(var.getKey());
				varNode.get(VALUE).set(var.getValue());
				env.add(varNode);
			}
		}
		
		get(DEPLOYMENTCONFIG_CONTAINERS).add(container);
		
	}
	
	@Override
	public IDeploymentTrigger addTrigger(String type) {
		ModelNode triggers = get(DEPLOYMENTCONFIG_TRIGGERS);
		ModelNode triggerNode = triggers.add();
		triggerNode.get(TYPE).set(type);
		switch(type) {
		case DeploymentTriggerType.IMAGE_CHANGE:
			return new ImageChangeTrigger(triggerNode, propertyKeys);
		case DeploymentTriggerType.CONFIG_CHANGE:
		default:
		}
		return new DeploymentTrigger(triggerNode, propertyKeys);
	}

	@Override
	public String getDeploymentStrategyType() {
		return asString(DEPLOYMENTCONFIG_STRATEGY);
	}
}
