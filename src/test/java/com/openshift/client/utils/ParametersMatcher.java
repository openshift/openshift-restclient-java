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
package com.openshift.client.utils;

import static org.mockito.Matchers.argThat;

import java.util.Arrays;

import org.mockito.ArgumentMatcher;
import org.mockito.internal.matchers.VarargMatcher;

import com.openshift.internal.client.httpclient.request.Parameter;

/**
 * Custom Mockito Matcher that verifies that the given RequestParameters matches
 * the expected ones.
 * 
 * @author Andre Dietisheim
 * 
 */
public class ParametersMatcher extends ArgumentMatcher<Parameter[]> implements VarargMatcher {

	private static final long serialVersionUID = 1L;

	private final Parameter[] expected;

	private ParametersMatcher(final Parameter... expected) {
		this.expected = expected;
	}

	@Override
	public boolean matches(Object argument) {
		if (!(argument instanceof Parameter[])) {
			return false;
		}
		Parameter[] parameters = (Parameter[]) argument;
		if (expected.length != parameters.length) {
			return false;
		}

		for (Parameter parameter : parameters) {
			if (!isExpected(parameter)) {
				return false;
			}
		}
		return true;
	}

	private boolean isExpected(Parameter parameter) {
		for (Parameter expectedParameter : expected) {
			if (expectedParameter.equals(parameter)) {
				return true;
			}
		}
		return false;
	}

	public static Parameter[] eq(Parameter[] expected) {
		return argThat(new ParametersMatcher(expected));
	}

	@Override
	public String toString() {
		return "RequestParametersMatcher ["
				+ "expected=" + Arrays.toString(expected) 
				+ "]";
	}

}