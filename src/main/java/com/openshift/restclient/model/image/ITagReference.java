/*******************************************************************************
 * Copyright (c) 2016 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package com.openshift.restclient.model.image;

import com.openshift.restclient.model.Annotatable;
import com.openshift.restclient.model.IObjectReference;

public interface ITagReference extends Annotatable {
	
	/**
	 * Returns the identifier for this reference
	 * @return
	 */
	String getName();
	
	/**
	 * if specified, a reference to another image that this tag should point to. 
	 * Valid values are ImageStreamTag, ImageStreamImage, and DockerImage.
	 * @return
	 */
	IObjectReference getFrom();
	
	String toJson();
}
