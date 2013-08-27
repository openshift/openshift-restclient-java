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
package com.openshift.internal.client;

import java.io.IOException;
import java.io.OutputStream;

import com.openshift.internal.client.httpclient.IMediaType;

/**
 * A parameter wrapper to pass param key/values to the service when executing a
 * remote operation from a link.
 * 
 * @author Xavier Coulon
 * @author Andre Dietisheim
 * 
 */
public class RequestParameter {

	private final String name;
	private final Object value;

	public RequestParameter(final String name, final Object value) {
		this.name = name;
		this.value = value;
	}

	public String getName() {
		return name;
	}

	public Object getValue() {
		return value;
	}
	
	public void writeTo(OutputStream out, IMediaType mediaType) throws IOException {
		mediaType.write(name, String.valueOf(value), out);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((value == null) ? 0 : value.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		RequestParameter other = (RequestParameter) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (value == null) {
			if (other.value != null)
				return false;
		} else if (!value.equals(other.value))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "RequestParameter ["
				+ "name=" + name 
				+ ", value=" + value 
				+ "]";
	}

}
