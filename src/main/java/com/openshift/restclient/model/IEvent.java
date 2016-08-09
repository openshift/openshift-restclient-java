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

import com.openshift.restclient.model.IResource;

/**
 * @author Jeff Cantrill
 */
public interface IEvent extends IResource {
	
	/**
	 * The reason for the event
	 * @return
	 */
	String getReason();
	
	/**
	 * The additional message associated with the event
	 * @return
	 */
	String getMessage();
	
	/**
	 * A reference to the Object that was involved in
	 * this event
	 * 
	 * @return
	 */
	IObjectReference getInvolvedObject();
	
	/**
	 * The first time this event was recorded
	 * @return
	 */
	String getFirstSeenTimestamp();
	
	/**
	 * The last time this event was recorded
	 * @return
	 */
	String getLastSeenTimestamp();
	
	/**
	 * The number of times this event has occured
	 * @return
	 */
	int getCount();
	
	/**
	 * The type of this event (e.g. Normal, Warning)
	 * @return
	 */
	String getType();
	
	/**
	 * Optional information of the component reporting this event
	 * @return
	 */
	IEventSource getEventSource();
	
	/**
	 * Event source information
	 * @author jeff.cantrill
	 *
	 */
	static interface IEventSource{
		
		/**
		 * The component from which this event was generated
		 * @return
		 */
		String getComponent();
		
		/**
		 * The host name on which this event was generated
		 * @return
		 */
		String getHost();
	}
}
