package com.openshift3.internal.client.model;

import java.util.Map;

import org.jboss.dmr.ModelNode;

import com.openshift3.client.IClient;
import com.openshift3.client.ResourceKind;
import com.openshift3.client.images.ImageUri;

public class ImageRepository extends KubernetesResource implements IImageRepository {

	public ImageRepository(){
		this(new ModelNode(), null, null);
	}
	
	public ImageRepository(ModelNode node, IClient client, Map<String, String []> propertyKeys) {
		super(node, client, propertyKeys);
		set("kind", ResourceKind.ImageRepository.toString());
	}

	public ImageRepository(String json) {
		super(json);
	}

	public void setDockerImageRepository(ImageUri uri) {
		set(IMAGEREPO_DOCKER_IMAGE_REPO, uri.getAbsoluteUri());		
	}

}
