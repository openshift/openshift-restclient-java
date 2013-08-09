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
package com.openshift.internal.client;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.openshift.internal.client.httpclient.IMediaType;

/**
 * A parameter that's used to pass an array parameter to the service when
 * executing a remote operation from a link.
 * 
 * @author Andre Dietisheim
 * 
 */
public class ArrayRequestParameter extends RequestParameter {

	private List<String> values = new ArrayList<String>();
	
	public ArrayRequestParameter(final String name, String... values) {
		this(name, Arrays.asList(values));
	}
	
	public ArrayRequestParameter(final String name, List<String> values) {
		super(name, null);
		this.values.addAll(values);
	}
	
	@Override
	public Object getValue() {
		return values;
	}

	@Override
	public void writeTo(OutputStream out, IMediaType mediaType) throws IOException {
		mediaType.write(getName(), values, out);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((values == null) ? 0 : values.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		ArrayRequestParameter other = (ArrayRequestParameter) obj;
		if (values == null) {
			if (other.values != null)
				return false;
		} else if (!values.equals(other.values))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "ArrayRequestParameter ["
				+ "values=" + Arrays.toString(values.toArray(new String[values.size()])) 
				+ "]";
	}
}
