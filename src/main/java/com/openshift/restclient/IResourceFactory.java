/*******************************************************************************
 * Copyright (c) 2015 Red Hat, Inc. Distributed under license by Red Hat, Inc.
 * All rights reserved. This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: Red Hat, Inc.
 ******************************************************************************/
package com.openshift.restclient;

import com.openshift.restclient.api.ITypeFactory;
import com.openshift.restclient.model.IResource;

import java.io.InputStream;
import java.util.List;

/**
 * Factory class for creating resources from a
 * response string
 *
 * @author Jeff Cantrill
 */
public interface IResourceFactory extends ITypeFactory {

    /**
     * Create a list of resources of the given kind
     * from a response string
     *
     * @param json
     * @param kind
     * @return
     * @throws ResourceFactoryException if it is unable to create resources
     */
    List<IResource> createList(String json, String kind);

    /**
     * Create a resource from a response string
     *
     * @param response
     * @return
     * @throws ResourceFactoryException if it is unable to create resources
     */
    <T extends IResource> T create(String response);

    /**
     * Create a resource from a response string
     *
     * @param input Read the given input stream which assumes the input
     *              is parsable JSON representing a valid resource
     * @return
     * @throws ResourceFactoryException if it is unable to create resources
     */
    <T extends IResource> T create(InputStream input);

    /**
     * Create(or stub) a resource for a given version and kind
     *
     * @param version the version of the resource
     * @param kind    the kind of the resource
     * @return the actual resource
     */
    <T extends IResource> T create(String version, String kind);

    /**
     * Create(or stub) a resource for a given version and kind and name
     *
     * @param version the version of the resource
     * @param kind    the kind of the resource
     * @param name    the name of the resource
     * @return the resource created
     */
    <T extends IResource> T create(String version, String kind, String name);

    /**
     * Stub out the given resource kind using a version determined by the factory
     *
     * @param kind the kind of the resource
     * @param name the name of the resource
     * @return the stubbed resource
     */
    <T extends IResource> T stub(String kind, String name);

    /**
     * Stub out the given resource kind using a version determined by the factory
     *
     * @param kind the kind of the resource
     * @param name the name of the resource
     * @return the stubbed resource
     */
    <T extends IResource> T stub(String kind, String name, String namespace);

    /**
     * The client given to resources when they are created
     *
     * @param client
     */
    void setClient(IClient client);

    /**
     * @return a resource kind factory to be used to obtain descriptors
     */
    ResourceKindRegistry getResourceKindRegistry();
}
