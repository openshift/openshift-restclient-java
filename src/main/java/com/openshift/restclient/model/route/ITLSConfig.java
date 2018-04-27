/*******************************************************************************
 * Copyright (c) 2015 Red Hat, Inc. Distributed under license by Red Hat, Inc.
 * All rights reserved. This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Red Hat, Inc.
 ******************************************************************************/

package com.openshift.restclient.model.route;

/**
 * TLS configuration for routes.
 * 
 */
public interface ITLSConfig {

    /**
     * Returns the termination type. If termination type is not set, any termination
     * config will be ignored.
     * 
     * @return Termination type.
     */
    String getTerminationType();

    /**
     * Sets the termination type to this config. Termination indicates termination
     * type. If termination type is not set, any termination config will be ignored.
     * 
     * @param type
     *            termination type
     */
    void setTerminationType(String type);

    /**
     * Retrieves the certificate contents.
     * 
     * @return Certificate contents.
     */
    String getCertificate();

    /**
     * Sets the certificate contents.
     * 
     * @param certificate
     *            certificate contents
     */
    void setCertificate(String certificate);

    /**
     * Retrieves the key file contents.
     * 
     * @return Key file contents.
     */
    String getKey();

    /**
     * Sets the key file contents.
     * 
     * @param key
     *            key file contents
     */
    void setKey(String key);

    /**
     * Retrieves the certification authority certificate contents.
     * 
     * @return CA certificate contents.
     */
    String getCACertificate();

    /**
     * Sets the certification authority certificate contents.
     * 
     * @param caCertificate
     *            CA certificate contents
     */
    void setCACertificate(String caCertificate);

    /**
     * DestinationCACertificate provides the contents of the CA certificate of the
     * final destination. When using reencrypt termination this file should be
     * provided in order to have routers use it for health checks on the secure
     * connection.
     * 
     * @return Contents of CA certificate of the final destination.
     */
    String getDestinationCertificate();

    /**
     * DestinationCACertificate provides the contents of the CA certificate of the
     * final destination. When using reencrypt termination this file should be
     * provided in order to have routers use it for health checks on the secure
     * connection.
     * 
     * @param destinationCertificate
     *            contents of CA certificate of the final destination
     */
    void setDestinationCertificate(String destinationCertificate);

    /**
     * Retrieves InsecureEdgeTerminationPolicy
     *
     * @return InsecureEdgeTerminationPolicy
     */
    String getInsecureEdgeTerminationPolicy();

    /**
     * Sets insecureEdgeTerminationPolicy
     *
     * @param insecureEdgeTerminationPolicy
     *            insecureEdgeTerminationPolicy
     */
    void setInsecureEdgeTerminationPolicy(String insecureEdgeTerminationPolicy);
}
