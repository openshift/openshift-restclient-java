/******************************************************************************* 
 * Copyright (c) 2011 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/ 
package com.openshift.internal.client.httpclient.request;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.openshift.client.OpenShiftException;


/**
 * @author Andre Dietisheim
 */
public class ParameterValueMap extends ParameterValue<Map<String, Parameter>> {

	public ParameterValueMap(List<Parameter> parameters) {
		this();
		addAll(parameters);
	}

	public ParameterValueMap(Parameter... parameters) {
		this();
		addAll(Arrays.asList(parameters));
	}

	public ParameterValueMap() {
		super(new LinkedHashMap<String, Parameter>());
	}

	public ParameterValueMap addAll(List<? extends Parameter> parameters) {
		for (Parameter parameter : parameters) {
			add(parameter);
		}
		return this;
	}
	
	public ParameterValueMap add(String name, String value) {
		add(new StringParameter(name, value));
		return this;
	}
	
	public ParameterValueMap add(Parameter parameter) {
		if (getValue().put(parameter.getName(), parameter) != null) {
			throw new OpenShiftException(
					"Duplicate parameter found. There's already a parameter named {0}", parameter.getName());
		}
		return this;
	}

	public Parameter getParamater(String name) {
		return getValue().get(name);
	}
	
	public boolean isEmpty() {
		Map<String, Parameter> values = getValue(); 
		return values == null
				|| values.isEmpty();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		@SuppressWarnings("rawtypes")
		ParameterValue other = (ParameterValue) obj;
		if (getValue() == null) {
			if (other.getValue() != null)
				return false;
		} else if (!getValue().equals(other.getValue()))
			return false;
		return true;
	}
	
	@Override
	public String toString() {
		return "ParameterValueMap ["
				+ "values=" + getValue() + "]";
	}


}
