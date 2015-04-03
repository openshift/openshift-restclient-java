package com.openshift.internal.restclient.builders;

import java.util.ArrayList;
import java.util.List;

import com.openshift.internal.restclient.model.BuildConfig;
import com.openshift.internal.restclient.model.DeploymentConfig;
import com.openshift.internal.restclient.model.ImageRepository;
import com.openshift.internal.restclient.model.KubernetesResource;
import com.openshift.restclient.images.DockerImageURI;

/**
 * SourceDeploymentBuilder supports building OpenShift resources that
 * can be deployed by injecting a user's source into an image.  This builder
 * produces the resources required to build and deploy an image.
 */
public class SourceDeploymentBuilder {


	private String namespace;
	private String sourceUri;
	private DockerImageURI baseUri;
	private DockerImageURI outputUri;
	private String name;
	
	public SourceDeploymentBuilder(String namespace, String sourceUri,  String username, DockerImageURI baseUri, String outputRepoHost){
		this.namespace = namespace;
		this.sourceUri = sourceUri;
		this.baseUri = baseUri;
		this.name = getNameFromGitUrl();
		this.outputUri = new DockerImageURI(outputRepoHost, username, name);
	}

	public List<KubernetesResource> build(){
		List<KubernetesResource> resources = new ArrayList<KubernetesResource>();
		resources.add(buildBuildConfig());
		resources.add(buildImageRepo());
		resources.add(buildDeploymentConfig());
		return resources;
	}

	private ImageRepository buildImageRepo() {
		ImageRepository repo = new ImageRepository();
		repo.setName(name);
		repo.setNamespace(namespace);
		repo.setDockerImageRepository(outputUri);
		repo.addLabel("name", name);
		return repo;
	}
	private DeploymentConfig buildDeploymentConfig() {
		ImageDeploymentBuilder builder = new ImageDeploymentBuilder(namespace, outputUri, 8080);
		return (DeploymentConfig) builder.build().get(0);
	}
	
	private BuildConfig buildBuildConfig() {
//		BuildConfig config = new BuildConfig();
//		config.setNamespace(namespace);
//		config.setName(name);
//		config.addTrigger(BuildTrigger.GitHub, "secret101");
//		config.addTrigger(BuildTrigger.Generic, "secret101");
//		config.setSource("Git", sourceUri);
//		config.setStrategy("STI", baseUri.getAbsoluteUri());
//		config.setOutput(this.outputUri);
//		config.addLabel("name", name);
//		return config;
		return null;
	}
	
	//TODO refactor into util?
	private String getNameFromGitUrl(){
		String [] segments = sourceUri.split("/");
		String repo = segments[segments.length-1];
		if(repo.endsWith(".git")){
			return repo.substring(0, repo.length() - 4);
		}
		return repo;
	}
}
