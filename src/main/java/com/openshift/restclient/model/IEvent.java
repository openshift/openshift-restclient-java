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

package com.openshift.restclient.model;

public interface IEvent extends IResource {

    /**
     * The reason for the event
     * 
     */
    String getReason();

    /**
     * The additional message associated with the event
     * 
     */
    String getMessage();

    /**
     * A reference to the Object that was involved in this event
     * 
     */
    IObjectReference getInvolvedObject();

    /**
     * The first time this event was recorded
     * 
     */
    String getFirstSeenTimestamp();

    /**
     * The last time this event was recorded
     * 
     */
    String getLastSeenTimestamp();

    /**
     * The number of times this event has occured
     * 
     */
    int getCount();

    /**
     * The type of this event (e.g. Normal, Warning)
     * 
     */
    String getType();

    /**
     * Optional information of the component reporting this event
     * 
     */
    IEventSource getEventSource();

    /**
     * Event source information
     * 
     * @author jeff.cantrill
     *
     */
    static interface IEventSource {

        /**
         * The component from which this event was generated
         * 
         */
        String getComponent();

        /**
         * The host name on which this event was generated
         * 
         */
        String getHost();
    }
}
