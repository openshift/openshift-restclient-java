package com.openshift.internal.restclient.model;

import java.util.Map;

import org.jboss.dmr.ModelNode;

import com.openshift.restclient.IClient;
import com.openshift.restclient.model.IConfigMap;

/**
 * @author Ulf Lilleengen
 */
public class ConfigMap extends KubernetesResource implements IConfigMap {
    private static final String CONFIGMAP_DATA = "data";

    public ConfigMap(ModelNode node, IClient client, Map<String, String []> propertyKeys) {
        super(node, client, propertyKeys);
    }

    @Override
    public Map<String, String> getData() {
        return asMap(CONFIGMAP_DATA);
    }
}
