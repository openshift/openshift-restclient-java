package com.openshift.restclient.model;

import java.util.Map;

/**
 * Represents a ConfigMap resource that holds configuration properties.
 *
 * @author Ulf Lilleengen
 */
public interface IConfigMap extends IResource {
    /**
     * Return the configuration data map.
     *
     * @return a map of config keys to config values.
     */
    Map<String, String> getData();
}
