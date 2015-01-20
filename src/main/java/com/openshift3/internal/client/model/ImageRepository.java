package com.openshift3.internal.client.model;

import org.jboss.dmr.ModelNode;

import com.openshift3.client.IClient;
import com.openshift3.client.ResourceKind;
import com.openshift3.client.images.ImageUri;

public class ImageRepository extends KubernetesResource implements IImageRepository {

	public ImageRepository(){
		this(new ModelNode(), null);
	}
	
	public ImageRepository(ModelNode node, IClient client) {
		super(node, client);
		set("kind", ResourceKind.ImageRepository.toString());
	}

	public ImageRepository(String json) {
		super(json);
	}

	public void setDockerImageRepository(ImageUri uri) {
		getNode().get("dockerImageRepository").set(uri.getAbsoluteUri());		
	}

}
