/*******************************************************************************
 * Copyright (c) 2015 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package com.openshift.internal.restclient;

import static org.mockito.Mockito.mock;

import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.openshift.internal.restclient.model.Project;
import com.openshift.restclient.IClient;
import com.openshift.restclient.ResourceKind;
import com.openshift.restclient.capability.CapabilityVisitor;
import com.openshift.restclient.capability.ICapability;
import com.openshift.restclient.model.IProject;

/**
 * @author jeff.cantrill
 */
public class ResourceFactoryTest {


	/*
	 * Validate the implementation classes implemented the expected constructor
	 */
	@Test
	public void testV1Beta3Implementations() {
		List<ResourceKind> v1beta3Exlusions = Arrays.asList(new ResourceKind [] {
				ResourceKind.Config, 
				ResourceKind.ProcessedTemplates, 
				ResourceKind.TemplateConfig
		});
		ResourceFactory factory = new ResourceFactory(mock(IClient.class));
		final String version = OpenShiftAPIVersion.v1beta3.toString();
		for (ResourceKind kind : ResourceKind.values()) {
			if(!v1beta3Exlusions.contains(kind)) {
				factory.create(version, kind);
			}
		}
	}

	@Test
	public void testSupportsCapability() throws Exception {
		ResourceFactory factory = new ResourceFactory(mock(IClient.class));
		factory.registerCapabilities(ResourceKind.Project, Test2Capabillity.class);

		IProject project = factory.create(OpenShiftAPIVersion.v1beta3.toString(), ResourceKind.Project);
		Assert.assertTrue(project.supports(ITestCapability.class));
	}

	@Test
	public void testSupportsCapabilityFalse() throws Exception {
		ResourceFactory factory = new ResourceFactory(mock(IClient.class));

		IProject project = factory.create(OpenShiftAPIVersion.v1beta3.toString(), ResourceKind.Project);
		Assert.assertFalse(project.supports(ITestCapability.class));
	}

	@Test
	public void testGetCapability() throws Exception {
		ResourceFactory factory = new ResourceFactory(mock(IClient.class));
		factory.registerCapabilities(ResourceKind.Project, Test2Capabillity.class);

		IProject project = factory.create(OpenShiftAPIVersion.v1beta3.toString(), ResourceKind.Project);
		Assert.assertEquals("B", project.getCapability(ITestCapability.class).getValue());
	}

	@Test
	public void testFirstSupportedCapability() throws Exception {
		ResourceFactory factory = new ResourceFactory(mock(IClient.class));
		factory.registerCapabilities(ResourceKind.Project, Test1Capabillity.class, Test2Capabillity.class, Test3Capabillity.class);

		IProject project = factory.create(OpenShiftAPIVersion.v1beta3.toString(), ResourceKind.Project);
		Assert.assertEquals("B", project.getCapability(ITestCapability.class).getValue());
	}

	@Test
	public void testVisitCapability() throws Exception {
		ResourceFactory factory = new ResourceFactory(mock(IClient.class));
		factory.registerCapabilities(ResourceKind.Project, Test2Capabillity.class);

		IProject project = factory.create(OpenShiftAPIVersion.v1beta3.toString(), ResourceKind.Project);
		String value = project.accept(new CapabilityVisitor<ITestCapability, String>() {
			@Override
			public String visit(ITestCapability capability) {
				return capability.getValue();
			}
		}, null);
		Assert.assertEquals("B", value);
	}

	@Test
	public void testVisitCapabilityDefault() throws Exception {
		ResourceFactory factory = new ResourceFactory(mock(IClient.class));

		IProject project = factory.create(OpenShiftAPIVersion.v1beta3.toString(), ResourceKind.Project);
		String value = project.accept(new CapabilityVisitor<ITestCapability, String>() {
			@Override
			public String visit(ITestCapability capability) {
				return capability.getValue();
			}
		}, "C");
		Assert.assertEquals("C", value);
	}

	public interface ITestCapability extends ICapability {
		String getValue();
	}

	public static class Test1Capabillity implements ITestCapability {

		private IProject project;

		public Test1Capabillity(Project project, IClient client) {
			this.project = project;
		}

		@Override
		public boolean isSupported() {
			return false;
		}

		public String getValue() {
			return "A";
		}

		@Override
		public String getName() {
			return this.getClass().getName();
		}
	}

	public static class Test2Capabillity extends Test1Capabillity {

		public Test2Capabillity(Project project, IClient client) {
			super(project, client);
		}

		@Override
		public boolean isSupported() {
			return true;
		}

		public String getValue() {
			return "B";
		}
	}

	public static class Test3Capabillity extends Test1Capabillity {

		public Test3Capabillity(Project project, IClient client) {
			super(project, client);
		}

		@Override
		public boolean isSupported() {
			return true;
		}

		public String getValue() {
			return "C";
		}
	}
}