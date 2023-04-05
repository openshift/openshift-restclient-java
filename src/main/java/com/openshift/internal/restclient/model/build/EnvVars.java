package com.openshift.internal.restclient.model.build;

import com.openshift.internal.restclient.model.EnvironmentVariable;
import com.openshift.restclient.model.IEnvironmentVariable;
import org.jboss.dmr.ModelNode;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;

public class EnvVars {
    public Collection<IEnvironmentVariable> getEnvVars(String[] path, ModelNode node, Map<String, String[]> propertyKeys){
        ModelNode envNode = node.get(path);
        if (envNode.isDefined()) {
            return envNode.asList().stream().map(n -> new EnvironmentVariable(n, propertyKeys))
                    .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }
}
