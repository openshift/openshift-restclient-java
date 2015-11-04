/*******************************************************************************
 * Copyright (c) 2015 Red Hat, Inc. Distributed under license by Red Hat, Inc.
 * All rights reserved. This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Red Hat, Inc.
 ******************************************************************************/
package com.openshift.restclient.model.limit;

/**
 * @author HyunsooKim1112
 */
public interface ILimit {
	
	public String getType();
	
	public void setType(String type);
		
	public String getMaxMemory();
	
	public void setMaxMemeory(String maxMemory);
	
	public String getMinMemory();
	
	public void setMinMemeory(String minMemory);
	
	public String getMaxCpu();
	
	public void setMaxCpu(String maxCpu);
	
	public String getMinCpu();
	
	public void setMinCpu(String minCpu);
	
	public String getDefaultMemory();
	
	public void setDefaultMemeory(String defaultMemory);
	
	public String getDefaultCpu();
	
	public void setDefaultCpu(String defaultCpu);
	
}
