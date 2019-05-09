/*******************************************************************************
 * Copyright (c) 2019 Red Hat, Inc. Distributed under license by Red Hat, Inc.
 * All rights reserved. This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Red Hat, Inc.
 ******************************************************************************/

package com.openshift.internal.restclient;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import org.jboss.dmr.ModelNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.openshift.restclient.IClient;
import com.openshift.restclient.api.models.ITypeMeta;
import com.openshift.restclient.model.IResource;

/**
 * Registry for types implemented by custom classes
 *
 */
public class TypeRegistry {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(TypeRegistry.class);
    
    private static final String RESOURCE_NAME = "k8stypes.properties";
    
    private static TypeRegistry instance;
    
    public static final TypeRegistry getInstance() {
        if (instance == null) {
            instance = new TypeRegistry();
        }
        return instance;
    }
    
    private Map<String, Class<?>> registeredTypes = new HashMap<>();
    
    private TypeRegistry() {
        load();
    }
    
    private void load() {
        try {
            Enumeration<URL> urls = TypeRegistry.class.getClassLoader().getResources(RESOURCE_NAME);
            while (urls.hasMoreElements()) {
                URL url = urls.nextElement();
                try (InputStream is = url.openStream()) {
                    load(is);
                } catch (IOException e) {
                    LOGGER.error("Can't load resource from " + url, e);
                }
            }
        } catch (IOException e) {
            LOGGER.error("Can't load resources from " + RESOURCE_NAME, e);
        }
    }
    
    private void load(InputStream stream) throws IOException {
        Properties p = new Properties();
        p.load(stream);
        for (Entry<Object, Object> entry : p.entrySet()) {
            try {
                String className = (String) entry.getKey();
                Class<?> clazz = Class.forName(className);
                if (check(clazz)) {
                    String types = (String) entry.getValue();
                    for (String type : types.split(",")) {
                        registeredTypes.put(type, clazz);
                    }
                }
            } catch (ClassNotFoundException e) {
                LOGGER.warn("Can't load class", e);
            }
        }
    }
    
    private boolean check(Class<?> clazz) {
        boolean valid = false;
        if (IResource.class.isAssignableFrom(clazz)) {
            try {
                clazz.getConstructor(ModelNode.class, IClient.class, Map.class);
                valid = true;
            } catch (NoSuchMethodException | SecurityException e) {
                LOGGER.error(e.getLocalizedMessage(), e);
            }
        } else if (ITypeMeta.class.isAssignableFrom(clazz)) {
            try {
                clazz.getConstructor(ModelNode.class, Map.class);
                valid = true;
            } catch (NoSuchMethodException | SecurityException e) {
                LOGGER.error(e.getLocalizedMessage(), e);
            }
        }
        return valid;
    }
    
    public Class<?> getRegisteredType(String kind) {
        return registeredTypes.get(kind);
    }
}
