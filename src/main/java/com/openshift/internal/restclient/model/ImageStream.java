/*******************************************************************************
 * Copyright (c) 2015 Red Hat, Inc. Distributed under license by Red Hat, Inc.
 * All rights reserved. This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Red Hat, Inc.
 ******************************************************************************/
package com.openshift.internal.restclient.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.jboss.dmr.ModelNode;
import org.jboss.dmr.ModelType;

import com.openshift.internal.restclient.model.image.TagReference;
import com.openshift.restclient.IClient;
import com.openshift.restclient.images.DockerImageURI;
import com.openshift.restclient.model.IImageStream;
import com.openshift.restclient.model.image.ITagReference;

/**
 * @author Jeff Cantrill
 */
public class ImageStream extends KubernetesResource implements IImageStream {

	private static final String DOCKER_IMAGE_REPO = "status.dockerImageRepository";
	private static final String SPEC_TAGS = "spec.tags";
	private static final String STATUS_TAGS = "status.tags";
	private static final String TAG = "tag";
	private static final String ITEMS = "items";
	private static final String IMAGE = "image";
	private Map<String, String[]> propertyKeys;

	public ImageStream(){
		this(new ModelNode(), null, null);
	}
	
	public ImageStream(ModelNode node, IClient client, Map<String, String []> propertyKeys) {
		super(node, client, propertyKeys);
		this.propertyKeys = propertyKeys;
	}

	@Override
	public void setDockerImageRepository(DockerImageURI uri) {
		set(DOCKER_IMAGE_REPO, uri.getAbsoluteUri());		
	}

	@Override
	public DockerImageURI getDockerImageRepository() {
		return new DockerImageURI(asString(DOCKER_IMAGE_REPO));
	}

	
	@Override
	public Collection<String> getTagNames() {
		ModelNode node = get(STATUS_TAGS);
		if(!node.isDefined()) return new ArrayList<>();
		return node.asList().stream().map(n->asString(n,TAG)).collect(Collectors.toList());
	}

	@Override
	public Collection<ITagReference> getTags() {
		ModelNode node = get(SPEC_TAGS);
		if(!node.isDefined()) return new ArrayList<>();
		return node.asList().stream().map(n->new TagReference(n, propertyKeys)).collect(Collectors.toList());
	}

	@Override
	public void setTag(String newTag, String fromTag) {
		ModelNode tags = get(SPEC_TAGS);
		ModelNode tag = new ModelNode();
		tag.get("name").set(newTag);
		ModelNode from = new ModelNode();
		from.get("kind").set("ImageStreamTag");
		from.get("name").set(fromTag);
		tag.get("from").set(from);
		tags.add(tag);
	}
	
	protected String getImageId(List<ModelNode> itemWrappers) {
		for (ModelNode itemWrapper : itemWrappers) {
			ModelNode image = itemWrapper.get(IMAGE);
			if (image != null) {
				return image.asString();
			}
		}
		return null;
	}

	@Override
	public String getImageId(String tagName) {
		String imageId = null;
		ModelNode tags = get(STATUS_TAGS);
		if (tags.getType() != ModelType.LIST || tagName == null)
			return null;
		
		List<ModelNode> tagWrappers = tags.asList();
		for (ModelNode tagWrapper : tagWrappers) {
			ModelNode tag = tagWrapper.get(TAG);
			ModelNode items = tagWrapper.get(ITEMS);
			if (tag.asString().equals(tagName) && items.getType() == ModelType.LIST) {
				imageId = getImageId(items.asList());
				break;
			}
		}
		return imageId;
	}
		
}

