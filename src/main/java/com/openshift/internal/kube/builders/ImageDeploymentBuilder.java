package com.openshift.internal.kube.builders;

import java.util.ArrayList;
import java.util.List;

import com.openshift.internal.kube.Resource;
import com.openshift.kube.DeploymentConfig;
import com.openshift.kube.images.ImageUri;

/**
 * ImageDeploymentBuilder supports building OpenShift resources that
 * can be deployed from an Image and do not require source code to be
 * built into the image 
 */
public class ImageDeploymentBuilder {

	private String namespace;
	private ImageUri imageTag;
	private int containerPort;
	
	public ImageDeploymentBuilder(String namespace, ImageUri tag, int containerPort){
		this.namespace = namespace;
		this.imageTag = tag;
		this.containerPort = containerPort;
	}
	
	public ImageDeploymentBuilder namespace(String namespace){
		this.namespace = namespace;
		return this;
	}
	
	public ImageDeploymentBuilder imageTag(ImageUri imageTag){
		this.imageTag = imageTag;
		return this;
	}
	
	public ImageDeploymentBuilder containerPort(int port){
		containerPort = port;
		return this;
	}
	
	public List<Resource> build(){
		List<Resource> resources = new ArrayList<Resource>();
		resources.add(buildDeploymentConfig());
		return resources;
	}

	private DeploymentConfig buildDeploymentConfig() {
		DeploymentConfig config = new DeploymentConfig();
		config.setNamespace(namespace);
		config.setName(imageTag.getName());
		config.addContainer(imageTag, this.containerPort);
		config.addLabel("name", imageTag.getName());
		return config;
	}
}
