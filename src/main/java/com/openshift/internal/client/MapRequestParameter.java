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
import java.util.LinkedHashMap;
import java.util.Map;

import com.openshift.internal.client.httpclient.IMediaType;

/**
 * A parameter that's used to pass a collection of named parameters to the service when
 * executing a remote operation from a link.
 * 
 * @author Andre Dietisheim
 * 
 */
public class MapRequestParameter extends RequestParameter {

	private Map<String, String> values = new LinkedHashMap<String, String>();
	
	public MapRequestParameter(final String name, NamedValue... namedValues) {
		super(name, null);
		addValues(namedValues);
	}

	private void addValues(NamedValue... namedValues) {
		for (NamedValue namedValue : namedValues) {
			values.put(namedValue.name, namedValue.value);
		}
	}
	
	public MapRequestParameter(final String name, Map<String, String> namedValues) {
		super(name, null);
		this.values.putAll(namedValues);
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
		MapRequestParameter other = (MapRequestParameter) obj;
		if (values == null) {
			if (other.values != null)
				return false;
		} else if (!values.equals(other.values))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "MapRequestParameter ["
				+ "values=" + values 
				+ "]";
	}
	
	public static class NamedValue {

		private String name;
		private String value;

		public NamedValue(String name, String value) {
			this.name = name;
			this.value = value;
		}
	}

}
