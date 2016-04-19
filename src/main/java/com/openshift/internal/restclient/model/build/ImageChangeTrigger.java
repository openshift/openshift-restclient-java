/*******************************************************************************
 * Copyright (c) 2015 Red Hat, Inc. Distributed under license by Red Hat, Inc.
 * All rights reserved. This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Red Hat, Inc.
 ******************************************************************************/
package com.openshift.internal.restclient.model.build;

import static org.apache.commons.lang.StringUtils.isNotBlank;
import com.openshift.restclient.images.DockerImageURI;
import com.openshift.restclient.model.build.BuildTriggerType;
import com.openshift.restclient.model.build.IImageChangeTrigger;

/**
 * @author Jeff Cantrill
 */
public class ImageChangeTrigger implements IImageChangeTrigger {

	private String tag;
	private DockerImageURI image;
	private DockerImageURI from;
	private final String type;

	public ImageChangeTrigger(String image, String from, String tag) {
		this(BuildTriggerType.IMAGE_CHANGE, image, from, tag);
	}
	
	public ImageChangeTrigger(String type, String image, String from, String tag) {
		this.type = type;
		this.tag = tag;
		this.image = isNotBlank(image) ? new DockerImageURI(image) : null;
		this.from = isNotBlank(from) ? new DockerImageURI(from) : null;
	}

	@Override
	public String getType() {
		return type;
	}

	@Override
	public DockerImageURI getImage() {
		return image;
	}

	@Override
	public DockerImageURI getFrom() {
		return from;
	}

	@Override
	public String getTag() {
		return this.tag;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((from == null) ? 0 : from.hashCode());
		result = prime * result + ((image == null) ? 0 : image.hashCode());
		result = prime * result + ((tag == null) ? 0 : tag.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ImageChangeTrigger other = (ImageChangeTrigger) obj;
		if (from == null) {
			if (other.from != null)
				return false;
		} else if (!from.equals(other.from))
			return false;
		if (image == null) {
			if (other.image != null)
				return false;
		} else if (!image.equals(other.image))
			return false;
		if (tag == null) {
			if (other.tag != null)
				return false;
		} else if (!tag.equals(other.tag))
			return false;
		return true;
	}
	
}
