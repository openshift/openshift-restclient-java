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
package com.openshift.restclient;

import static com.openshift.internal.restclient.IntegrationTestHelper.cleanUpResource;
import static org.junit.Assert.*;

import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.junit.Test;
import org.junit.After;
import org.junit.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.openshift.internal.restclient.IntegrationTestHelper;
import com.openshift.restclient.IClient;
import com.openshift.restclient.IOpenShiftWatchListener;
import com.openshift.restclient.IOpenShiftWatchListener.ChangeType;
import com.openshift.restclient.ResourceKind;
import com.openshift.restclient.model.IResource;
import com.openshift.restclient.model.IService;

public class ClientWatchIntegrationTest {

	private static final Logger LOG = LoggerFactory.getLogger(ClientWatchIntegrationTest.class);

	private IntegrationTestHelper helper = new IntegrationTestHelper();
	private IClient client;
	private IResource project;

	private ExecutorService service;
	private boolean isError;

	@Before
	public void setup() {
		service = Executors.newSingleThreadScheduledExecutor();
		client = helper.createClientForBasicAuth();
		IResource projRequest = client.getResourceFactory().stub(ResourceKind.PROJECT_REQUEST, helper.generateNamespace());
		project = client.create(projRequest);
	}
	
	@After
	public void teardown() {
		cleanUpResource(client, project);
		service.shutdownNow();
	}
	
	@SuppressWarnings("rawtypes")
	@Test(timeout=30000)
	public void test() throws Exception{
		List results = new ArrayList();
		CountDownLatch latch = new CountDownLatch(2);
		IOpenShiftWatchListener listener = new IOpenShiftWatchListener() {

			@SuppressWarnings("unchecked")
			@Override
			public void received(IResource resource, ChangeType change) {
				results.add(change);
			}
			
			@Override
			public void connected(List<IResource> resources) {
				latch.countDown();
			}

			@Override
			public void disconnected() {
				latch.countDown();
			}

			@Override
			public void error(Throwable err) {
				latch.countDown();
				isError = true;
				LOG.error("",err);
			}
		};
		
		IWatcher watcher = null;
		try {
			watcher = client.watch(Arrays.asList(ResourceKind.SERVICE, ResourceKind.POD), project.getName(), listener);
			latch.await();
			assertFalse("Expected connection without error",isError);
			IService service = client.getResourceFactory().stub(ResourceKind.SERVICE,"hello-world", project.getName());
			service.addPort(8080,8080);
			service = client.create(service);
			service.addLabel("foo", "bar");
			service = client.update(service);
			client.delete(service);
			assertArrayEquals(new ChangeType[] {ChangeType.ADDED, ChangeType.MODIFIED, ChangeType.DELETED}, results.toArray());
			assertEquals(0, latch.getCount());
		}finally {
			if(watcher != null) {
				watcher.stop();
			}
		}
	}

}
