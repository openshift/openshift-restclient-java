/*******************************************************************************
 * Copyright (c) 2015 Red Hat, Inc. Distributed under license by Red Hat, Inc.
 * All rights reserved. This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Red Hat, Inc.
 ******************************************************************************/
package com.openshift3.internal.client.capability.server;

import java.util.Collection;

import org.jboss.dmr.ModelNode;

import com.openshift3.client.IClient;
import com.openshift3.client.ResourceKind;
import com.openshift3.client.capability.server.ITemplateProcessing;
import com.openshift3.client.model.IConfig;
import com.openshift3.client.model.IResource;
import com.openshift3.client.model.template.ITemplate;
import com.openshift3.internal.client.model.KubernetesResource;

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

	@Override
	public IConfig process(ITemplate template, String namespace) {
		return client.<IConfig>create(new TemplateConfigAdapter(template, namespace));
	}
	
	protected static class TemplateConfigAdapter extends KubernetesResource implements IConfig{
		
		private ITemplate template;
		private String namespace;

		public TemplateConfigAdapter(ITemplate template, String namespace){
			super(new ModelNode(), null, null);
			this.template = template;
			this.namespace = namespace;
		}
		@Override
		public String getNamespace(){
			return namespace;
		}
		
		@Override
		public ResourceKind getKind() {
			return ResourceKind.TemplateConfig;
		}

		@Override
		public String toString() {
			return template.toString();
		}

		@Override
		public Collection<IResource> getItems() {
			return template.getItems();
		}
		
	}
}
