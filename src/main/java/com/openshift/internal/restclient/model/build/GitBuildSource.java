/*******************************************************************************
 * Copyright (c) 2015 Red Hat, Inc. Distributed under license by Red Hat, Inc.
 * All rights reserved. This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Red Hat, Inc.
 ******************************************************************************/
package com.openshift.internal.restclient.model.build;

import com.openshift.restclient.model.build.BuildSourceType;
import com.openshift.restclient.model.build.IGitBuildSource;

/**
 * @author Jeff Cantrill
 */
public class GitBuildSource implements IGitBuildSource {

	private String ref;
	private String uri;
	private String contextDir;

	public GitBuildSource(String uri, String ref, String contextDir){
		this.ref = ref;
		this.uri = uri;
		this.contextDir = contextDir;
	}
	
	@Override
	public String getType() {
		return BuildSourceType.GIT;
	}

	@Override
	public String getURI() {
		return uri;
	}
	
	public void setURI(String uri) {
		this.uri = uri;
	}

	@Override
	public String getRef() {
		return ref;
	}
	
	public void setRef(String ref) {
		this.ref = ref;
	}

	@Override
	public String getContextDir() {
		return contextDir;
	}
	
	public void setContextDir(String contextDir) {
		this.contextDir = contextDir;
	}

}
