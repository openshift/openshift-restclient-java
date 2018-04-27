/******************************************************************************* 
 * Copyright (c) 2016 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/

package com.openshift.internal.restclient.apis;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.lang.StringUtils;
import org.jboss.dmr.ModelNode;

import com.openshift.internal.restclient.api.models.TypeMeta;
import com.openshift.internal.restclient.apis.autoscaling.models.Scale;
import com.openshift.internal.restclient.model.deploy.DeploymentRequest;
import com.openshift.internal.restclient.model.properties.ResourcePropertiesRegistry;
import com.openshift.internal.restclient.model.properties.ResourcePropertyKeys;
import com.openshift.internal.util.JBossDmrExtentions;
import com.openshift.restclient.OpenShiftException;
import com.openshift.restclient.ResourceFactoryException;
import com.openshift.restclient.UnsupportedVersionException;
import com.openshift.restclient.api.ITypeFactory;
import com.openshift.restclient.api.models.INameSetable;
import com.openshift.restclient.api.models.INamespaceSetable;
import com.openshift.restclient.api.models.ITypeMeta;

public class TypeMetaFactory implements ITypeFactory, ResourcePropertyKeys {

    private static final String DELIMITER = ".";
    private static final Map<String, Class<? extends ITypeMeta>> IMPL_MAP = new HashMap<>();

    static {
        IMPL_MAP.put("Scale", Scale.class);
        // its own factory?
        IMPL_MAP.put("DeploymentRequest", DeploymentRequest.class);
    }

    @Override
    public Object stubKind(String kind, Optional<String> name, Optional<String> namespace) {
        if (StringUtils.isEmpty(kind)) {
            throw new OpenShiftException("Unable to stub a kind when the kind passed in is empty");
        }
        try {
            String version = "";
            if (kind.contains(DELIMITER)) {
                int delimeter = kind.indexOf(DELIMITER);
                version = StringUtils.left(kind, delimeter);
                kind = StringUtils.right(kind, kind.length() - delimeter - DELIMITER.length());
            }
            Map<String, String[]> properyKeyMap = ResourcePropertiesRegistry.getInstance().get(version, kind);
            ModelNode node = new ModelNode();
            JBossDmrExtentions.set(node, properyKeyMap, APIVERSION, version);
            JBossDmrExtentions.set(node, properyKeyMap, KIND, kind);

            ITypeMeta instance = null;
            if (IMPL_MAP.containsKey(kind)) {
                Constructor<? extends ITypeMeta> constructor = IMPL_MAP.get(kind).getConstructor(ModelNode.class,
                        Map.class);
                instance = constructor.newInstance(node, properyKeyMap);
            } else {
                instance = new TypeMeta(node, properyKeyMap);
            }

            if (name.isPresent() && instance instanceof INameSetable) {
                ((INameSetable) instance).setName(name.get());
            }
            if (namespace.isPresent() && instance instanceof INamespaceSetable) {
                ((INamespaceSetable) instance).setNamespace(namespace.get());
            }

            return instance;
        } catch (UnsupportedVersionException e) {
            throw e;
        } catch (Exception e) {
            throw new ResourceFactoryException(e, "Unable to stub instance from %s", kind);
        }
    }

    @Override
    public Object createInstanceFrom(String response) {
        try {
            ModelNode node = ModelNode.fromJSONString(response);
            String version = node.get(APIVERSION).asString();
            String kind = node.get(KIND).asString();

            Map<String, String[]> properyKeyMap = ResourcePropertiesRegistry.getInstance().get(version, kind);
            if (IMPL_MAP.containsKey(kind)) {
                Constructor<? extends ITypeMeta> constructor = IMPL_MAP.get(kind).getConstructor(ModelNode.class,
                        Map.class);
                return constructor.newInstance(node, properyKeyMap);
            }
            return new TypeMeta(node, properyKeyMap);

        } catch (UnsupportedVersionException e) {
            throw e;
        } catch (Exception e) {
            throw new ResourceFactoryException(e, "Unable to create from %s", response);
        }
    }

}
