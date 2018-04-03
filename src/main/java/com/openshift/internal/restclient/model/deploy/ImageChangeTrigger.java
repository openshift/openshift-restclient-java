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
package com.openshift.internal.restclient.model.deploy;

import com.openshift.restclient.PredefinedResourceKind;
import com.openshift.restclient.images.DockerImageURI;
import com.openshift.restclient.model.deploy.IDeploymentImageChangeTrigger;
import org.apache.commons.lang.StringUtils;
import org.jboss.dmr.ModelNode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;

import static com.openshift.internal.util.JBossDmrExtentions.*;

public class ImageChangeTrigger extends DeploymentTrigger implements IDeploymentImageChangeTrigger {

    private static final String DEPLOYMENTCONFIG_TRIGGER_IMAGECHANGE_AUTO = "imageChangeParams.automatic";
    private static final String DEPLOYMENTCONFIG_TRIGGER_CONTAINERS = "imageChangeParams.containerNames";
    private static final String DEPLOYMENTCONFIG_TRIGGER_FROM = "imageChangeParams.from.name";
    private static final String DEPLOYMENTCONFIG_TRIGGER_FROM_KIND = "imageChangeParams.from.kind";
    private static final String FROM_NAMESPACE = "imageChangeParams.from.namespace";

    public ImageChangeTrigger(ModelNode node, Map<String, String[]> propertyKeys) {
        super(node, propertyKeys);
    }

    @Override
    public DockerImageURI getFrom() {
        return new DockerImageURI(asString(getNode(), getPropertyKeys(), DEPLOYMENTCONFIG_TRIGGER_FROM));
    }

    @Override
    public void setFrom(DockerImageURI fromImage) {
        if (StringUtils.isBlank(asString(getNode(), getPropertyKeys(), DEPLOYMENTCONFIG_TRIGGER_FROM_KIND))) {
            setKind(PredefinedResourceKind.IMAGE_STREAM_TAG.getIdentifier());
        }
        set(getNode(), getPropertyKeys(), DEPLOYMENTCONFIG_TRIGGER_FROM, fromImage.getAbsoluteUri());
    }

    @Override
    public void setNamespace(String namespace) {
        set(getNode(), getPropertyKeys(), FROM_NAMESPACE, namespace);
    }

    @Override
    public String getNamespace() {
        return asString(getNode(), getPropertyKeys(), FROM_NAMESPACE);
    }

    @Override
    public void setKind(String kind) {
        set(getNode(), getPropertyKeys(), DEPLOYMENTCONFIG_TRIGGER_FROM_KIND, kind);
    }

    @Override
    public String getKind() {
        return asString(getNode(), getPropertyKeys(), DEPLOYMENTCONFIG_TRIGGER_FROM_KIND);
    }

    @Override
    public boolean isAutomatic() {
        return asBoolean(getNode(), getPropertyKeys(), DEPLOYMENTCONFIG_TRIGGER_IMAGECHANGE_AUTO);
    }

    @Override
    public void setAutomatic(boolean auto) {
        set(getNode(), getPropertyKeys(), DEPLOYMENTCONFIG_TRIGGER_IMAGECHANGE_AUTO, auto);
    }

    @Override
    public Collection<String> getContainerNames() {
        Collection<String> containers = new ArrayList<>();
        ModelNode containerNode = get(getNode(), getPropertyKeys(), DEPLOYMENTCONFIG_TRIGGER_CONTAINERS);
        if (containerNode.isDefined()) {
            for (ModelNode node : containerNode.asList()) {
                containers.add(node.asString());
            }
        }
        return containers;
    }

    @Override
    public void setContainerNames(Collection<String> names) {
        ModelNode containerNode = get(getNode(), getPropertyKeys(), DEPLOYMENTCONFIG_TRIGGER_CONTAINERS);
        containerNode.clear();
        for (String name : names) {
            containerNode.add(name);
        }
    }

    @Override
    public void setContainerName(String name) {
        setContainerNames(Arrays.asList(name));
    }


}
