/*******************************************************************************
 * Copyright (c) 2015 Red Hat, Inc. Distributed under license by Red Hat, Inc.
 * All rights reserved. This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Red Hat, Inc.
 ******************************************************************************/
package com.openshift.internal.restclient.model.limit;

import com.openshift.restclient.model.limit.ILimit;

/**
 * @author HyunsooKim1112
 */
public class Limit implements ILimit {

	private String type;
	private String maxCpu;
	private String minCpu;
	private String defaultCpu;
	private String defaultMemory;
	private String maxMemory;
	private String minMemory;

	public Limit(String limitType, String maxCpu, String minCpu, String maxMemory, String minMemory, String defaultCpu, String defaultMemeory) {
		this.type = limitType;
		this.maxCpu = maxCpu;
		this.minCpu = minCpu;
		this.maxMemory = maxMemory;
		this.minMemory = minMemory;
		this.defaultCpu = defaultCpu;
		this.defaultMemory = defaultMemeory;
	}

	@Override
	public String getType() {

		return this.type;
	}

	@Override
	public void setType(String type) {
		this.type = type;
		
	}

	@Override
	public String getMaxMemory() {
		return this.maxMemory;
	}

	@Override
	public void setMaxMemeory(String maxMemory) {
		this.maxMemory = maxMemory;
		
	}

	@Override
	public String getMinMemory() {

		return this.minMemory;
	}

	@Override
	public void setMinMemeory(String minMemory) {
		this.minMemory = minMemory;
		
	}

	@Override
	public String getMaxCpu() {

		return this.maxCpu;
	}

	@Override
	public void setMaxCpu(String maxCpu) {
		this.maxCpu = maxCpu;
		
	}

	@Override
	public String getMinCpu() {

		return this.minCpu;
	}

	@Override
	public void setMinCpu(String minCpu) {
		this.minCpu = minCpu;
		
	}

	@Override
	public String getDefaultMemory() {

		return this.defaultMemory;
	}

	@Override
	public void setDefaultMemeory(String defaultMemory) {
		this.defaultMemory = defaultMemory;
		
	}

	@Override
	public String getDefaultCpu() {

		return this.defaultCpu;
	}

	@Override
	public void setDefaultCpu(String defaultCpu) {
		this.defaultCpu = defaultCpu;
		
	}

}
