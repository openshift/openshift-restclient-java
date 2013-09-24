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

import static org.fest.assertions.Assertions.assertThat;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.openshift.client.OpenShiftException;

/**
 * @author Andre Dietisheim
 */
public class FormUrlEncodedMediaTypeTest {

	private FormUrlEncodedMediaType formUrlEncoder;
	private ByteArrayOutputStream out;

	@Before
	public void setUp() throws IOException {
		this.formUrlEncoder = new FormUrlEncodedMediaType();
		this.out = new ByteArrayOutputStream();
	}

	@After
	public void tearDown() {
	}

	@Test
	public void shouldEncodeSingleParameter() throws IOException {
		// pre-condition
		// operation
		formUrlEncoder.writeTo(
				new ParameterValueMap()
					.add(new StringParameter("name", "adietish"))
				, out);
		// verification
		assertThat(out.toString()).isEqualTo("name=adietish");
	}

	@Test
	public void shouldNotStart2ndWriteWithAmpersand() throws IOException {
		// pre-condition
		formUrlEncoder.writeTo(
				new ParameterValueMap()
					.add(new StringParameter("name", "adietish"))
				, out);
		assertThat(out.toString()).isEqualTo("name=adietish");

		// operation
		out = new ByteArrayOutputStream();
		formUrlEncoder.writeTo(
				new ParameterValueMap()
					.add(new StringParameter("name", "adietish"))
				, out);
		// verification
		assertThat(out.toString().startsWith("&")).isFalse();;
	}

	@Test
	public void shouldEncode3ParametersWithAmpersand() throws IOException {
		// pre-condition
		// operation
		formUrlEncoder.writeTo(
				new ParameterValueMap()
						.add(new StringParameter("name", "adietish"))
						.add(new StringParameter("company", "redhat"))
						.add(new StringParameter("paas", "OpenShift"))
				, out);
		// verification
		assertThat(out.toString()).isEqualTo("name=adietish&company=redhat&paas=OpenShift");
	}

	@Test(expected = OpenShiftException.class)
	public void shouldNotAllowIdenticalNames() throws IOException {
		// pre-condition
		// operation
		formUrlEncoder.writeTo(
				new ParameterValueMap()
						.add(new StringParameter("name", "adietish"))
						.add(new StringParameter("name", "redhat"))
				, out);
	}

	@Test
	public void shouldEncodeNestedMap() throws IOException {
		// pre-condition
		// operation
		formUrlEncoder.writeTo(
				new ParameterValueMap()
						.add(new Parameter("honkabear",
								new ParameterValueMap()
										.add(new StringParameter("name", "adietish"))
										.add(new StringParameter("company", "redhat"))))
				, out);
		// verification
		assertThat(out.toString()).isEqualTo("honkabear[name]=adietish&honkabear[company]=redhat");
	}

	@Test
	public void shouldEncodeMultipleNestedMaps() throws IOException {
		// pre-condition
		// operation
		formUrlEncoder.writeTo(
				new ParameterValueMap().add(
						new Parameter("thirst",
								new ParameterValueArray()
										.add(new ParameterValueMap()
												.add(new StringParameter("name", "adietish"))
												.add(new StringParameter("company", "redhat")))
										.add(new ParameterValueMap()
												.add(new StringParameter("name", "xcoulon"))
												.add(new StringParameter("company", "redhat")))))
				, out);
		// verification
		assertThat(out.toString()).isEqualTo(
				"thirst[][name]=adietish&thirst[][company]=redhat&thirst[][name]=xcoulon&thirst[][company]=redhat");
	}
}
