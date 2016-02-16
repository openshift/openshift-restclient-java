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
package com.openshift.internal.restclient.model;

import static com.openshift.internal.util.JBossDmrExtentions.*;

import java.util.Map;

import org.jboss.dmr.ModelNode;

import com.openshift.internal.restclient.model.properties.ResourcePropertyKeys;
import com.openshift.restclient.model.IConfigMapKeySelector;
import com.openshift.restclient.model.IEnvironmentVariable;
import com.openshift.restclient.model.IObjectFieldSelector;
import com.openshift.restclient.model.ISecretKeySelector;

public class EnvironmentVariable extends ModelNodeAdapter implements IEnvironmentVariable, ResourcePropertyKeys {

	public EnvironmentVariable(ModelNode node, Map<String, String[]> propertyKeys) {
		super(node, propertyKeys);
	}

	@Override
	public String getName() {
		return  asString(getNode(), getPropertyKeys(), NAME);
	}

	@Override
	public String getValue() {
		return  asString(getNode(), getPropertyKeys(), VALUE);
	}
	
	@Override
	public IEnvVarSource getValueFrom() {
		if(getNode().hasDefined("fieldRef")) {
			return new IObjectFieldSelector(){
				@Override
				public String getApiVersion() {
					return asString(getNode(), getPropertyKeys(), "fieldRef.apiVersion");
				}

				@Override
				public String getFieldPath() {
					return asString(getNode(), getPropertyKeys(), "fieldRef.fieldPath");
				} 
				
			};
		}else if(getNode().hasDefined("configMapKeyRef")) {
			return new IConfigMapKeySelector() {

				@Override
				public String getName() {
					return asString(getNode(), getPropertyKeys(), "configMapKeyRef.name");
				}

				@Override
				public String getKey() {
					return asString(getNode(), getPropertyKeys(), "configMapKeyRef.key");
				}
			};
			
		}else if(getNode().hasDefined("secretKeyRef")) {
			return new ISecretKeySelector() {
				
				@Override
				public String getName() {
					return asString(getNode(), getPropertyKeys(), "secretKeyRef.name");
				}
				
				@Override
				public String getKey() {
					return asString(getNode(), getPropertyKeys(), "secretKeyRef.key");
				}
			};
		}
		return null;
	}
	
}
