/*******************************************************************************
 * Copyright (c) 2015 Red Hat, Inc. Distributed under license by Red Hat, Inc.
 * All rights reserved. This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Red Hat, Inc.
 ******************************************************************************/
package com.openshift.restclient;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * OpenShift Context. 
 * 
 * @author Fred Bricon
 * 
 * @since 3.1.0
 */
public class OpenShiftContext {

	private Map<String, Object> context = new ConcurrentHashMap<>();
	
	private static ThreadLocal<OpenShiftContext> threadLocalBinary = new ThreadLocal<OpenShiftContext>() {
		protected OpenShiftContext initialValue() {
			return new OpenShiftContext();
		};
	};
	
	private OpenShiftContext() {
	}
	
	/**
	 * Returns the {@link OpenShiftContext} instance for the current {@link Thread}. 
	 * 
	 * @return an {@link OpenShiftContext}
	 */
	public static OpenShiftContext get() {
		return threadLocalBinary.get();
	}
	
	public void put(String key, Object value) {
		context.put(key, value);
	}

	@SuppressWarnings("unchecked")
	public <T> T get(String key) {
		T value = (T) context.get(key);
		return value;
	}
	
	public void remove(String key) {
		context.remove(key);
	}
	
	public void clear() {
		context.clear();
	}
	
		
	
}
