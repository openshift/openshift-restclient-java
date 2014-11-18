package com.openshift.kube;

import com.openshift.internal.kube.Resource;

/**
 * ResourceKind are the various types of Kubernetes
 * resources that are of interest
 *
 */
public enum ResourceKind {
	
	Build("builds", Build.class),
	BuildConfig("buildConfigs", BuildConfig.class),
	Deployment("deployments", Deployment.class),
	DeploymentConfig("deploymentConfigs", DeploymentConfig.class),
	ImageRepository("imageRepositories", ImageRepository.class),
	Project("projects", Project.class),
	Pod("pods", Pod.class),
	ReplicationController("replicationControllers", ReplicationController.class),
	Status("", Status.class),
	Service("services", Service.class);

	// punting here for now
	private final String plural;
	
	private final Class<? extends Resource> klass;
	
	ResourceKind(String plural,  Class<? extends Resource> klass){
		this.plural = plural;
		this.klass = klass;
	}
	
	public String pluralize() {
		return plural;
	}
	
	public Class<? extends Resource> getResourceClass(){
		return klass;
	}
}
