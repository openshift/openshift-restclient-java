package com.openshift.kube;

import org.jboss.dmr.ModelNode;

import com.openshift.internal.kube.OpenshiftResource;
import com.openshift.kube.images.ImageUri;

public class ImageRepository extends OpenshiftResource {

	public ImageRepository(){
		this(new ModelNode(), null);
	}
	
	public ImageRepository(ModelNode node, Client client) {
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
