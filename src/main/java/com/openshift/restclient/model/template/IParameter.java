/*******************************************************************************
 * Copyright (c) 2015 Red Hat, Inc. Distributed under license by Red Hat, Inc.
 * All rights reserved. This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Red Hat, Inc.
 ******************************************************************************/

package com.openshift.restclient.model.template;

/**
 * A parameter for a template
 * 
 */
public interface IParameter extends Cloneable {

    /**
     * Returns the name of the parameter that is substituted in the template
     * 
     */
    String getName();

    /**
     * Returns the description
     * 
     */
    String getDescription();

    /**
     * Returns the value to use
     * 
     */
    String getValue();

    /**
     * Sets the value to use
     * 
     */
    void setValue(String value);

    /**
     * Returns the generator name which will use the value from 'getFrom' if set
     * 
     */
    String getGeneratorName();

    /**
     * Returns the input to the generator
     * 
     */
    String getFrom();

    /**
     * Returns true if parameter is required; false otherwise
     * 
     */
    boolean isRequired();

    IParameter clone();

}
