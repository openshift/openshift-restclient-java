/*******************************************************************************
 * Copyright (c) 2016 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package com.openshift.internal.restclient.model.image;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.lang.StringUtils;
import org.jboss.dmr.ModelNode;

import com.openshift.internal.restclient.model.KubernetesResource;
import com.openshift.internal.restclient.model.Status;
import com.openshift.restclient.IClient;
import com.openshift.restclient.images.DockerImageURI;
import com.openshift.restclient.model.IStatus;
import com.openshift.restclient.model.image.IImageStreamImport;

/**
 * 
 * @author jeff.cantrill
 *
 */
public class ImageStreamImport extends KubernetesResource implements IImageStreamImport {

	private static final String FROM_KIND = "from.kind";
	private static final String IMAGE_DOCKER_IMAGE_REFERENCE = "image.dockerImageReference";
	private static final String SPEC_IMAGES = "spec.images";
	private static final String SPEC_IMPORT = "spec.import";
	private static final String STATUS = "status";
	private static final String STATUS_IMAGES = "status.images";
	private static final String TAG = "tag";

	public ImageStreamImport(ModelNode node, IClient client, Map<String, String[]> overrideProperties) {
		super(node, client, overrideProperties);
	}

	@Override
	public void setImport(boolean importTags) {
		set(SPEC_IMPORT, importTags);
	}

	@Override
	public boolean isImport() {
		return asBoolean(SPEC_IMPORT);
	}

	@Override
	public void addImage(String fromKind, DockerImageURI imageUri) {
		ModelNode image = new ModelNode();
		set(image, FROM_KIND, fromKind);
		set(image, "from.name", imageUri.getAbsoluteUri());
		get(SPEC_IMAGES).add(image);
	}

	@Override
	public Collection<IStatus> getImageStatus() {
		Collection<IStatus> status = new ArrayList<>();
		ModelNode images = get(STATUS_IMAGES);
		if(images.isDefined()) {
			images.asList()
				.stream()
				.filter(n->get(n,STATUS).isDefined())
				.forEach(n->status.add(new Status(get(n,STATUS), getClient(), getPropertyKeys())));
		}
		return status;
	}

	@Override
	public String getImageJsonFor(DockerImageURI uri) {
		String prefix = uri.getUriWithoutTag();
		String tag = uri.getTag();
		ModelNode images = get(STATUS_IMAGES);
		if(images.isDefined()) {
			Optional<ModelNode> node = images.asList()
				.stream()
				.filter(n->asString(n, IMAGE_DOCKER_IMAGE_REFERENCE).startsWith(prefix) && tag.equals(asString(n,TAG)))
				.findFirst();
			if(node.isPresent()) {
				return node.get().toJSONString(true);
			}
		}		
		return null;
	}

}
