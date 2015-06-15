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

	public GitBuildSource(String uri, String ref){
		this.ref = ref;
		this.uri = uri;
	}
	
	@Override
	public String getType() {
		return BuildSourceType.GIT;
	}

	@Override
	public String getURI() {
		return uri;
	}

	@Override
	public String getRef() {
		return ref;
	}

}
