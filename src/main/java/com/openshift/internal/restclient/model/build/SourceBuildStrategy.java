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

import static com.openshift.internal.util.JBossDmrExtentions.asBoolean;
import static com.openshift.internal.util.JBossDmrExtentions.asString;
import static com.openshift.internal.util.JBossDmrExtentions.set;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.jboss.dmr.ModelNode;
import org.jboss.dmr.ModelType;

import com.openshift.internal.restclient.model.EnvironmentVariable;
import com.openshift.internal.restclient.model.ModelNodeAdapter;
import com.openshift.internal.restclient.model.properties.ResourcePropertyKeys;
import com.openshift.internal.util.JBossDmrExtentions;
import com.openshift.restclient.images.DockerImageURI;
import com.openshift.restclient.model.IEnvironmentVariable;
import com.openshift.restclient.model.build.BuildStrategyType;
import com.openshift.restclient.model.build.ISourceBuildStrategy;

public class SourceBuildStrategy extends ModelNodeAdapter implements ISourceBuildStrategy, ResourcePropertyKeys {

    public static final String FROM_IMAGE = "sourceStrategy.from.name";
    public static final String FROM_KIND = "sourceStrategy.from.kind";
    public static final String FROM_NAMESPACE = "sourceStrategy.from.namespace";
    public static final String SCRIPTS = "sourceStrategy.scripts";
    public static final String INCREMENTAL = "sourceStrategy.incremental";
    public static final String ENV = "sourceStrategy.env";

    public SourceBuildStrategy(ModelNode node, Map<String, String[]> propertyKeys) {
        super(node, propertyKeys);
        set(node, propertyKeys, TYPE, BuildStrategyType.SOURCE);
    }

    @Override
    public String getType() {
        return asString(getNode(), getPropertyKeys(), TYPE);
    }

    @Override
    public String getFromNamespace() {
        return asString(getNode(), getPropertyKeys(), FROM_NAMESPACE);
    }

    @Override
    public void setFromNamespace(String namespace) {
        set(getNode(), getPropertyKeys(), FROM_NAMESPACE, namespace);
    }

    @Override
    public String getFromKind() {
        return asString(getNode(), getPropertyKeys(), FROM_KIND);
    }

    @Override
    public void setFromKind(String kind) {
        set(getNode(), getPropertyKeys(), FROM_KIND, kind);
    }

    @Override
    public DockerImageURI getImage() {
        return new DockerImageURI(asString(getNode(), getPropertyKeys(), FROM_IMAGE));
    }

    @Override
    public void setImage(DockerImageURI image) {
        set(getNode(), getPropertyKeys(), FROM_IMAGE, image.toString());
    }

    @Override
    public String getScriptsLocation() {
        return asString(getNode(), getPropertyKeys(), SCRIPTS);
    }

    @Override
    public void setScriptsLocation(String location) {
        set(getNode(), getPropertyKeys(), SCRIPTS, location);
    }

    @Override
    public Collection<IEnvironmentVariable> getEnvVars() {
        String[] path = JBossDmrExtentions.getPath(getPropertyKeys(), ENV);
        ModelNode envNode = getNode().get(path);
        if (envNode.isDefined()) {
            return envNode.asList().stream().map(n -> new EnvironmentVariable(n, getPropertyKeys()))
                    .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    @Override
    public void setEnvVars(Collection<IEnvironmentVariable> envVars) {
        if (envVars == null) {
            return;
        }
        String[] path = JBossDmrExtentions.getPath(getPropertyKeys(), ENV);
        ModelNode envNode = getNode().get(path);
        envNode.clear();
        envVars.forEach(v -> envNode.add(ModelNode.fromJSONString(v.toJson())));
    }

    @Override
    public Map<String, String> getEnvironmentVariables() {
        String[] path = JBossDmrExtentions.getPath(getPropertyKeys(), ENV);
        ModelNode env = getNode().get(path);
        Map<String, String> values = new HashMap<>();
        if (env.getType() == ModelType.LIST) {
            for (ModelNode value : env.asList()) {
                values.put(value.get(NAME).asString(), value.get(VALUE).asString());
            }
        }
        return values;
    }

    @Override
    public void setEnvironmentVariables(Map<String, String> envVars) {
        String[] path = JBossDmrExtentions.getPath(getPropertyKeys(), ENV);
        ModelNode env = getNode().get(path);
        env.clear();
        for (Entry<String, String> entry : envVars.entrySet()) {
            ModelNode var = new ModelNode();
            var.get(NAME).set(entry.getKey());
            var.get(VALUE).set(entry.getValue());
            env.add(var);
        }
    }

    @Override
    public boolean incremental() {
        return asBoolean(getNode(), getPropertyKeys(), INCREMENTAL);
    }

    @Override
    public void setIncremental(boolean isIncremental) {
        set(getNode(), getPropertyKeys(), INCREMENTAL, isIncremental);
    }

}
