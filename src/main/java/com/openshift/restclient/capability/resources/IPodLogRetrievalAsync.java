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
package com.openshift.restclient.capability.resources;

import com.openshift.restclient.capability.ICapability;
import com.openshift.restclient.capability.IStoppable;
import org.apache.commons.lang.StringUtils;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Retrieve logs in an async call
 * @author jeff.cantrill
 *
 */
public interface IPodLogRetrievalAsync extends ICapability{

	/**
	 * Start retrieving logs using the given listener and options
	 * @param listener
	 * @return A Handle to allow termination of log streaming
	 */
	IStoppable start(IPodLogListener listener);

	/**
	 * Start retrieving logs using the given listener and options
	 * @param listener
	 * @param options  options for retrieving logs
	 * @return A Handle to allow termination of log streaming
	 */
	IStoppable start(IPodLogListener listener, Options options);
	
	
	/**
	 * A callback for log messages
	 * @author jeff.cantrill
	 *
	 */
	interface IPodLogListener{
		
		/**
		 * Callback received on initial connection
		 */
		void onOpen();
		
		/**
		 * A log message
		 * @param message
		 */
		void onMessage(String message);
		
		/**
		 * Callback received when the connection
		 * to the pod is terminated from the server-side
		 * @param code       a valid http response code
		 * @param reason     a reason for termination, may be null
		 */
		void onClose(int code, String reason);

		/**
		 * Callback received when the web socket connection
		 * fails
		 * @param e the exception which occurred
		 */
		void onFailure(IOException e);

	}
	
	/**
	 * Options for retrieving logs using a 
	 * fluent builder style
	 * 
	 * @author jeff.cantrill
	 *
	 */
	public static class Options{
		
		private static final String CONTAINER = "container";
		private static final String FOLLOW = "follow";
		private boolean follow = false;
		private String container = null;
		private Map<String, String> options = new HashMap<>();

		/**
		 * The container from which to retrieve logs
		 * @param container
		 * @return
		 */
		public Options container(String container) {
			this.container = container;
			return this;
		}
		
		/**
		 * follow the logs, defaults to false
		 * @return
		 */
		public Options follow() {
			return follow(true);
		}

		/**
		 * follow the logs
		 * @param value
		 * @return
		 */
		public Options follow(boolean value) {
			this.follow = value;
			return this;
		}
		
		/**
		 * Add an option that is not explicitly 
		 * defined.  These will override any
		 * explicit options if there are collisions
		 * 
		 * @param name
		 * @param value
		 * @return
		 */
		public Options parameter(String name, String value) {
			options.put(name, value);
			return this;
		}
		
		/**
		 * The collective options
		 * @return a map of all the options
		 */
		public Map<String, String> getMap(){
			if(!options.containsKey(FOLLOW) && follow) {
				options.put(FOLLOW, "true");
			}
			if(!options.containsKey(CONTAINER) && StringUtils.isNotBlank(container)) {
				options.put(CONTAINER, container);
			}
			return Collections.unmodifiableMap(options);
		}
	}
}
