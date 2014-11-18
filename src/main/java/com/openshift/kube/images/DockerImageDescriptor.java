package com.openshift.kube.images;

public class DockerImageDescriptor{
	
	private final String description;
	private final ImageUri name;

	public DockerImageDescriptor(ImageUri name, String description){
		this.name = name;		
		this.description = description;
	}

	public String getDescription() {
		return description;
	}
	
	public ImageUri getImageUri() {
		return name;
	}
	
}