/*******************************************************************************
 * Copyright (c) 2015 Red Hat, Inc. Distributed under license by Red Hat, Inc.
 * All rights reserved. This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Red Hat, Inc.
 ******************************************************************************/
package com.openshift.restclient.model.build;

import com.openshift.restclient.images.DockerImageURI;

/**
 * @author Jeff Cantrill
 */
public interface IImageChangeTrigger extends IBuildTrigger {
	
	
	DockerImageURI getImage();
	
	/**
	 * Returns the name of the docker image repo to watch
	 * @return
	 */
	DockerImageURI getFrom();
	
	/**
	 * Returns the tag to watch in the image repository
	 * @return
	 */
	String getTag();
	
}
