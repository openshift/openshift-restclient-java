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

import static com.openshift.internal.util.JBossDmrExtentions.*;
import java.util.Map;

import org.jboss.dmr.ModelNode;

import com.openshift.internal.restclient.model.ModelNodeAdapter;
import com.openshift.internal.restclient.model.ObjectReference;
import com.openshift.internal.restclient.model.properties.ResourcePropertyKeys;
import com.openshift.restclient.model.IObjectReference;
import com.openshift.restclient.model.image.ITagReference;

public class TagReference extends ModelNodeAdapter implements ITagReference, ResourcePropertyKeys {

	private static final String TAG_ANNOTATIONS = "annotations";

	public TagReference(ModelNode node, Map<String, String[]> propertyKeys) {
		super(node, propertyKeys);
	}

	@Override
	public boolean isAnnotatedWith(String key) {
		return getAnnotations().containsKey(key);
	}

	@Override
	public String getAnnotation(String key) {
		return getAnnotations().get(key);
	}

	@Override
	public void setAnnotation(String key, String value) {
		if(value == null) return;
		ModelNode annotations = get(getNode(), getPropertyKeys(), TAG_ANNOTATIONS);
		annotations.get(key).set(value);
	}

	@Override
	public Map<String, String> getAnnotations() {
		return asMap(getNode(), getPropertyKeys(), TAG_ANNOTATIONS);
	}

	@Override
	public String getName() {
		return asString(getNode(),getPropertyKeys(), NAME);
	}

	@Override
	public IObjectReference getFrom() {
		return new ObjectReference(get(getNode(), getPropertyKeys(), "from"));
	}

}
