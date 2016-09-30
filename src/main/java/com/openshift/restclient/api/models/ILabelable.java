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
package com.openshift.restclient.api.models;

import java.util.Map;

/**
 * A resource that can be labeled
 * @author jeff.cantrill
 *
 */
public interface ILabelable {
	
	/**
	 * Retrieves the labels associated with the resource
	 * @return
	 */
	Map<String, String> getLabels();
	
	/**
	 * Add or update a label;
	 * @param key
	 * @param value
	 */
	void addLabel(String key, String value);

}
