package com.openshift3.client.images;

public class DockerImageDescriptor{
	
	private final String description;
	private final DockerImageURI name;

	public DockerImageDescriptor(DockerImageURI name, String description){
		this.name = name;		
		this.description = description;
	}

	public String getDescription() {
		return description;
	}
	
	public DockerImageURI getImageUri() {
		return name;
	}
	
}