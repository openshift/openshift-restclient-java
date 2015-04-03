package com.openshift.internal.restclient.builders;

import java.util.ArrayList;
import java.util.List;

import com.openshift.internal.restclient.model.DeploymentConfig;
import com.openshift.internal.restclient.model.KubernetesResource;
import com.openshift.restclient.images.DockerImageURI;

/**
 * ImageDeploymentBuilder supports building OpenShift resources that
 * can be deployed from an Image and do not require source code to be
 * built into the image 
 */
public class ImageDeploymentBuilder {

	private String namespace;
	private DockerImageURI imageTag;
	private int containerPort;
	
	public ImageDeploymentBuilder(String namespace, DockerImageURI tag, int containerPort){
		this.namespace = namespace;
		this.imageTag = tag;
		this.containerPort = containerPort;
	}
	
	public ImageDeploymentBuilder namespace(String namespace){
		this.namespace = namespace;
		return this;
	}
	
	public ImageDeploymentBuilder imageTag(DockerImageURI imageTag){
		this.imageTag = imageTag;
		return this;
	}
	
	public ImageDeploymentBuilder containerPort(int port){
		containerPort = port;
		return this;
	}
	
	public List<KubernetesResource> build(){
		List<KubernetesResource> resources = new ArrayList<KubernetesResource>();
		resources.add(buildDeploymentConfig());
		return resources;
	}

	private DeploymentConfig buildDeploymentConfig() {
		return null;
//		DeploymentConfig config = new DeploymentConfig();
//		config.setNamespace(namespace);
//		config.setName(imageTag.getName());
//		config.addContainer(imageTag, this.containerPort);
//		config.addLabel("name", imageTag.getName());
//		return config;
	}
}
