package com.openshift3.internal.client.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.jboss.dmr.ModelNode;

import com.openshift3.client.IClient;
import com.openshift3.client.ResourceKind;
import com.openshift3.client.images.ImageUri;
import com.openshift3.client.model.IDeploymentConfig;

public class DeploymentConfig extends KubernetesResource implements IDeploymentConfig{
	
	public DeploymentConfig(ModelNode node, IClient client, Map<String, String []> propertyKeys) {
		super(node, client, propertyKeys);
	}
	
	public DeploymentConfig(){
		this(new ModelNode(), null, null);
	}
	public List<String> getTriggerTypes(){
		List<String> types = new ArrayList<String>();
		ModelNode triggers = get(DEPLOYMENTCONFIG_TRIGGERS);
		for (ModelNode node : triggers.asList()) {
			types.add(node.get("type").asString());
		}
		return types;
	}
	public List<String> getImageNames(){
		List<String> names = new ArrayList<String>();
		List<ModelNode> containers = get(DEPLOYMENTCONFIG_CONTAINERS).asList();
		for (ModelNode container : containers) {
			names.add(container.get("image").asString());
		}
		return names;
	}
	
	public int getReplicas(){
		return asInt(DEPLOYMENTCONFIG_REPLICAS);
	}
	
	public void addContainer(ImageUri tag,  int containerPort){
		addImageChangeTrigger(tag);
		buildTemplate(tag, containerPort);
	}
	
	//FIXME
	private void addImageChangeTrigger(ImageUri imageTag){
		ModelNode triggers = get(DEPLOYMENTCONFIG_TRIGGERS);
		ModelNode imageChange = new ModelNode();
		imageChange.get("type").set("ImageChange");
		ModelNode params = imageChange.get("imageChangeParams");
		params.get("automatic").set(true);
		params.get("containerNames").add(getName());
		params.get("repositoryName").set(imageTag.getUriWithoutTag());
		params.get("tag").set(imageTag.getTag());
		triggers.add(imageChange);
		
		ModelNode configChange = new ModelNode();
		configChange.get("type").set("ConfigChange");
		triggers.add(configChange);
	}
	
	//FIXME
	private void buildTemplate(ImageUri imageTag, int containerPort) {
		ModelNode template = getNode().get("template");
		template.get(new String[]{"strategy","type"}).set( "Recreate");
		template.get(new String[]{"controllerTemplate","replicas"}).set(1);
		template.get(new String[]{"controllerTemplate","replicaSelector","name"}).set(getName());
		
		ModelNode controllerTemplate = template.get("controllerTemplate");
		controllerTemplate.get(new String[]{"podTemplate","desiredState","manifest","version"}).set(getApiVersion());
		ModelNode container = new ModelNode();
		container.get("name").set(imageTag.getName());
		container.get("image").set(imageTag.getAbsoluteUri());
		ModelNode port = new ModelNode();
		port.get("containerPort").set(containerPort);
		container.get("ports").add(port);
		controllerTemplate.get(new String[]{"podTemplate","desiredState","manifest","containers"}).add(container);
		controllerTemplate.get(new String[]{"podTemplate","labels","name"}).set(imageTag.getName());
	}
}
