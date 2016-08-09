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
package com.openshift.restclient.model;

import static org.mockito.Mockito.when;

import java.lang.reflect.Field;

import org.mockito.Mockito;

import com.openshift.internal.restclient.OpenShiftAPIVersion;
import com.openshift.internal.restclient.ResourceFactory;
import com.openshift.restclient.IClient;
import com.openshift.restclient.IResourceFactory;
import com.openshift.restclient.utils.Samples;

/**
 * A mock factory to return fixtures of api models.
 * 
 * @author jeff.cantrill
 *
 */
public class MocksFactory {
	
	private IClient client;
	private IResourceFactory factory;

	public MocksFactory() {
		this(Mockito.mock(IClient.class));
	}
	
	public MocksFactory(IClient client) {
		this.client = client;
		this.factory = new ResourceFactory(client);
	}
	
	public IClient getClient() {
		return client;
	}
	
	public <T extends IResource> T mock(Class<T> klass, String namespace, String name) {
		T mock = mock(klass);
		when(mock.getNamespace()).thenReturn(namespace);
		when(mock.getName()).thenReturn(name);
		return mock;
	}
	
	/**
	 * Mock the given kind based on the class
	 * @param klass
	 * @return a mocked instance with mocked name, version, and namespace
	 */
	public <T extends IResource> T mock(Class<T> klass) {
		final String version = OpenShiftAPIVersion.v1.toString();
		T mock = Mockito.mock(klass);
		when(mock.getName()).thenReturn("a" + klass.getSimpleName());
		when(mock.getApiVersion()).thenReturn(version);
		when(mock.getNamespace()).thenReturn("aNamespace");
		when(mock.getKind()).thenReturn(klass.getSimpleName().substring(1));
		return mock;
	}
	
	/**
	 * Stub a given kind based on the JSON files defined in {@link Samples}
	 * @param kind
	 * @return a stubbed resource
	 */
	public <T extends IResource> T stub(Class<T> kind) {
		String name = kind.getSimpleName();
		if(name.startsWith("I")) {
			name = name.substring(1);
		}
		return stub(name);
	}
	
	/**
	 * Stub a given kind based on the JSON files defined in {@link Samples}
	 * @param kind
	 * @return a stubbed resource
	 */
	public <T extends IResource> T stub(String kind) {
		final String version = OpenShiftAPIVersion.v1.toString();
		String stub = String.format("%s_%s", version, splitCamelCase(kind, "_")).toUpperCase();
		try {
			Field field = Samples.class.getField(stub);
			Samples sample = (Samples) field.get(null);
			return factory.create(sample.getContentAsString());
		} catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
			throw new RuntimeException(String.format("Sample not found for kind %s"), e);
		}
	}
	
	/**
	 * Split the camelcase string and delimit with the given delimiter
	 * @param value     the camelcase string
	 * @param delimiter the delimiter for the resulting string
	 * @return
	 */
	private String splitCamelCase(String value, String delimiter) {
	   return value.replaceAll(
	      String.format("%s|%s|%s",
	         "(?<=[A-Z])(?=[A-Z][a-z])",
	         "(?<=[^A-Z])(?=[A-Z])",
	         "(?<=[A-Za-z])(?=[^A-Za-z])"
	      ),
	      delimiter
	   );
	}
}
