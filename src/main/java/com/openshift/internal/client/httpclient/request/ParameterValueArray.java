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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Andre Dietisheim
 */
public class ParameterValueArray extends ParameterValue<List<ParameterValue<?>>> {

	public ParameterValueArray() {
		this(new ArrayList<ParameterValue<?>>());
	}

	public ParameterValueArray(List<ParameterValue<?>> values) {
		super(values);
	}

	public ParameterValueArray add(String name, String value) {
		return add(new StringParameter(name, value));
	}

	public ParameterValueArray add(ParameterValue<?> value) {
		getValue().add(value);
		return this;
	}

	public ParameterValueArray addAll(List<? extends ParameterValue<?>> values) {
		getValue().addAll(values);
		return this;
	}

	@Override
	public String toString() {
		return "ParameterValueArray ["
				+ "values=" + Arrays.toString(getValue().toArray()) + "]";
	}

}
