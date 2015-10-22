/*******************************************************************************
 * Copyright (c) 2015 Red Hat, Inc. Distributed under license by Red Hat, Inc.
 * All rights reserved. This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Red Hat, Inc.
 ******************************************************************************/
package com.openshift.internal.restclient.capability.server;

import java.util.Collection;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.jboss.dmr.ModelNode;

import com.openshift.internal.restclient.OpenShiftAPIVersion;
import com.openshift.internal.restclient.model.KubernetesResource;
import com.openshift.restclient.IClient;
import com.openshift.restclient.ResourceKind;
import com.openshift.restclient.capability.server.ITemplateProcessing;
import com.openshift.restclient.model.IResource;
import com.openshift.restclient.model.template.IParameter;
import com.openshift.restclient.model.template.ITemplate;

/**
 * @author Jeff Cantrill
 */
public class ServerTemplateProcessing implements ITemplateProcessing {

	private IClient client;

	public ServerTemplateProcessing(IClient client){
		this.client = client;
	}
	
	@Override
	public boolean isSupported() {
		return true;
	}

	@Override
	public String getName() {
		return this.getClass().getSimpleName();
	}

	@SuppressWarnings("unchecked")
	@Override
	public ITemplate process(ITemplate template, String namespace) {
		return client.create(new TemplateConfigAdapter(template, namespace));
	}
	
	protected static class TemplateConfigAdapter extends KubernetesResource implements ITemplate {
		
		private ITemplate template;
		private String namespace;

		public TemplateConfigAdapter(ITemplate template, String namespace){
			super(new ModelNode(), null, null);
			this.template = template;
			this.namespace = namespace;
		}

		@Override
		public String getName() {
			return template.getName();
		}

		@Override
		public String getNamespace(){
			return namespace;
		}
		
		@Override
		public String getKind() {
			return ResourceKind.PROCESSED_TEMPLATES;
		}

		@Override
		public String toString() {
			return template.toString();
		}

		@Override
		public Collection<IResource> getItems() {
			return template.getItems();
		}

		@Override
		public Map<String, IParameter> getParameters() {
			return template.getParameters();
		}

		@Override
		public void updateParameterValues(Collection<IParameter> parameters) {
			template.updateParameterValues(parameters);
		}
		
		

		@Override
		public void updateParameter(String key, String value) {
			template.updateParameter(key, value);
		}

		@Override
		public Map<String, String> getObjectLabels() {
			return template.getObjectLabels();
		}

		@Override
		public void addObjectLabel(String key, String value) {
			template.addObjectLabel(key, value);
		}

		@Override
		public boolean isMatching(String text) {
			if (StringUtils.isEmpty(text)) {
				return true;
			}
			if (text.equals(getNamespace())) {
				return true;
			}
			if (template == null) {
				return false;
			}
			
			return template.isMatching(text);
		}
		
	}
}
