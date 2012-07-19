/******************************************************************************* 
 * Copyright (c) 2012 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/
package com.openshift.internal.client.httpclient;

import java.io.UnsupportedEncodingException;
import java.util.Map;

/**
 * @author Andre Dietisheim
 */
public interface IMediaType {

	public String getType();
	
	public String encodeParameters(Map<String, Object> parameters) throws UnsupportedEncodingException;
	
}
