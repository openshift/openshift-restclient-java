/******************************************************************************* 
 * Copyright (c) 2013 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/
package com.openshift.internal.client.httpclient.request;

/**
 * @author Andre Dietisheim
 */
public class StringParameter extends Parameter {

	public StringParameter(final String name, final String value) {
		super(name, new StringValue(value));
	}

	@Override
	public String toString() {
		return "StringParameter ["
				+ "name=" + getName()
				+ ", value=" + getValue()
				+ "]";
	}


}
