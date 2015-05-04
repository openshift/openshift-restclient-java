/*******************************************************************************
 * Copyright (c) 2015 Red Hat, Inc. Distributed under license by Red Hat, Inc.
 * All rights reserved. This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Red Hat, Inc.
 ******************************************************************************/
package com.openshift.internal.restclient.model.template;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jboss.dmr.ModelNode;

import com.openshift.internal.restclient.KubernetesAPIVersion;
import com.openshift.internal.restclient.model.KubernetesResource;
import com.openshift.internal.restclient.model.properties.ResourcePropertiesRegistry;
import com.openshift.internal.util.JBossDmrExtentions;
import com.openshift.restclient.IClient;
import com.openshift.restclient.IResourceFactory;
import com.openshift.restclient.model.IResource;
import com.openshift.restclient.model.template.IParameter;
import com.openshift.restclient.model.template.ITemplate;

/**
 * @author Jeff Cantrill
 */
public class Template extends KubernetesResource implements ITemplate{

	public Template(ModelNode node, IClient client, Map<String, String []> propertyKeys) {
		super(node, client, propertyKeys);
	}
	
	/**
	 * Template is an OS kind that compiles to kubernetes v1beta1 structure
	 * for labels.
	 * TODO: remove method when we move to v1beta3
	 */
	@Override
	public Map<String, String> getLabels() {
		final String version = getNode().get("apiVersion").asString();
		if(KubernetesAPIVersion.valueOf(version).equals(KubernetesAPIVersion.v1beta1)) {
			return JBossDmrExtentions.asMap(getNode(), ResourcePropertiesRegistry.V1BETA1_KUBERNETES_MAP, LABELS);
		}
		return super.getLabels();
	}


	@Override
	public Map<String, IParameter> getParameters() {
		Collection<ModelNode> nodes = get(TEMPLATE_PARAMETERS).asList();
		Map<String, IParameter> params = new HashMap<String, IParameter>(nodes.size());
		for (ModelNode node : nodes) {
			Parameter p = new Parameter(node);
			params.put(p.getName(), p);
		}
		return params;
	}
	
	@Override
	public Collection<IResource> getItems() {
		Collection<ModelNode> nodes = get(TEMPLATE_ITEMS).asList();
		List<IResource> resources = new ArrayList<IResource>(nodes.size());
		IResourceFactory factory = getClient().getResourceFactory();
		if(factory != null){
			for (ModelNode node : nodes) {
				resources.add(factory.create(node.toJSONString(true)));
			}
		}
		return resources;
	}

	@Override
	public void updateParameterValues(Collection<IParameter> parameters) {
		Map<String, IParameter> actuals = getParameters();
		for (IParameter param : parameters) {
			if(actuals.containsKey(param.getName())) {
				actuals.get(param.getName()).setValue(param.getValue());
			}
		}
	}

}
