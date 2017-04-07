/*******************************************************************************
 * Copyright (c) 2015 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package com.openshift.internal.restclient.model.build;

import static com.openshift.internal.util.JBossDmrExtentions.asString;
import static com.openshift.internal.util.JBossDmrExtentions.set;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;

import org.jboss.dmr.ModelNode;

import com.openshift.internal.restclient.model.EnvironmentVariable;
import com.openshift.internal.restclient.model.ModelNodeAdapter;
import com.openshift.internal.restclient.model.properties.ResourcePropertyKeys;
import com.openshift.internal.util.JBossDmrExtentions;
import com.openshift.restclient.model.IEnvironmentVariable;
import com.openshift.restclient.model.build.BuildStrategyType;
import com.openshift.restclient.model.build.IJenkinsPipelineStrategy;

/**
 * @author Andre Dietisheim
 */
public class JenkinsPipelineStrategy extends ModelNodeAdapter implements IJenkinsPipelineStrategy, ResourcePropertyKeys {

	public JenkinsPipelineStrategy(ModelNode node, Map<String, String []> propertyKeys) {
		super(node, propertyKeys);
		set(node, propertyKeys, TYPE, BuildStrategyType.JENKINS_PIPELINE);
	}

	@Override
	public String getType() {
		return asString(getNode(), getPropertyKeys(), TYPE);
	}

	@Override
	public void setJenkinsfilePath(String filePath) {
		set(getNode(),getPropertyKeys(), JENKINS_FILE_PATH, filePath);
	}

	@Override
	public String getJenkinsfilePath() {
		return asString(getNode(), getPropertyKeys(), JENKINS_FILE_PATH);
	}

	@Override
	public void setJenkinsfile(String file) {
		set(getNode(),getPropertyKeys(), JENKINS_FILE, file);
	}

	@Override
	public String getJenkinsfile() {
		return asString(getNode(), getPropertyKeys(), JENKINS_FILE);
	}
	
	@Override
	public Collection<IEnvironmentVariable> getEnvVars() {
		String [] path = JBossDmrExtentions.getPath(getPropertyKeys(), ENV);
		ModelNode envNode = getNode().get(path);
		if (envNode.isDefined()) {
			return envNode.asList()
					.stream()
					.map(n -> new EnvironmentVariable(n, getPropertyKeys()))
					.collect(Collectors.toList());
		}
		return Collections.emptyList();
	}

	@Override
	public void setEnvVars(Collection<IEnvironmentVariable> envVars) {
		if (envVars == null) {
			return;
		}
		String [] path = JBossDmrExtentions.getPath(getPropertyKeys(), ENV);
		ModelNode envNode = getNode().get(path);
		envNode.clear();
		envVars.forEach(v->envNode.add(ModelNode.fromJSONString(v.toJson())));
	}

}
