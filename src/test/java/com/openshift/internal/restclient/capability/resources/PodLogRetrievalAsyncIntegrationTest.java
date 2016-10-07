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
package com.openshift.internal.restclient.capability.resources;

import static org.junit.Assert.assertNotNull;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.openshift.internal.restclient.DefaultClient;
import com.openshift.internal.restclient.IntegrationTestHelper;
import com.openshift.restclient.ResourceKind;
import com.openshift.restclient.capability.CapabilityVisitor;
import com.openshift.restclient.capability.IStoppable;
import com.openshift.restclient.capability.resources.IPodLogRetrievalAsync;
import com.openshift.restclient.capability.resources.IPodLogRetrievalAsync.IPodLogListener;
import com.openshift.restclient.capability.resources.IPodLogRetrievalAsync.Options;
import com.openshift.restclient.model.IPod;
import com.openshift.restclient.model.IResource;

/**
 * 
 * @author Jeff Cantrill
 *
 */
public class PodLogRetrievalAsyncIntegrationTest {
	
	private static final Logger LOG = LoggerFactory.getLogger(PodLogRetrievalAsyncIntegrationTest.class);
	
	private IntegrationTestHelper helper = new IntegrationTestHelper();
	private CountDownLatch latch;


	@Test
	public void testAsyncLogRetrieval() throws Exception {
		latch = new CountDownLatch(2);
		DefaultClient client = (DefaultClient) helper.createClientForBasicAuth();
		List<IResource> pods = client.list(ResourceKind.POD, "default");
		IPod pod = (IPod) pods.stream().filter(p->p.getName().startsWith("docker-registry")).findFirst().orElse(null);
		assertNotNull("Need a pod to continue the test. Expected to find the registry", pod);
		
		final String container = pod.getContainers().iterator().next().getName();

		IStoppable stop = pod.accept(new CapabilityVisitor<IPodLogRetrievalAsync, IStoppable>() {

			@Override
			public IStoppable visit(IPodLogRetrievalAsync capability) {
				return capability.start(new IPodLogListener() {
					
					@Override
					public void onOpen() {
						LOG.debug("onOpen");
						latch.countDown();
					}
					
					@Override
					public void onMessage(String message) {
						LOG.debug(message);
					}
					
					@Override
					public void onClose(int code, String reason) {
						LOG.debug("onClose code:{} reason:{}", code, reason);
						latch.countDown();
					}
				}, new Options()
						.follow()
						.container(container));
			}
		}, null);
		assertNotNull("Exp. to support the capability", stop);
		latch.await(10, TimeUnit.SECONDS);
		stop.stop();
		latch.await(5, TimeUnit.SECONDS);
	}

}
