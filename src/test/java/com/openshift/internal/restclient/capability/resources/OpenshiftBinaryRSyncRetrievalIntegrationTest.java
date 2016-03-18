package com.openshift.internal.restclient.capability.resources;
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

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

import static org.fest.assertions.Assertions.*;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.openshift.internal.restclient.IntegrationTestHelper;
import com.openshift.internal.restclient.ResourceFactory;
import com.openshift.internal.restclient.authorization.AuthorizationClient;
import com.openshift.restclient.ClientBuilder;
import com.openshift.restclient.IClient;
import com.openshift.restclient.NoopSSLCertificateCallback;
import com.openshift.restclient.ResourceKind;
import com.openshift.restclient.authorization.BasicAuthorizationStrategy;
import com.openshift.restclient.authorization.IAuthorizationClient;
import com.openshift.restclient.authorization.IAuthorizationContext;
import com.openshift.restclient.authorization.TokenAuthorizationStrategy;
import com.openshift.restclient.capability.CapabilityVisitor;
import com.openshift.restclient.capability.IBinaryCapability;
import com.openshift.restclient.capability.resources.IRSyncable;
import com.openshift.restclient.capability.resources.IRSyncable.LocalPeer;
import com.openshift.restclient.capability.resources.IRSyncable.PodPeer;
import com.openshift.restclient.model.IPod;
import com.openshift.restclient.model.IService;

/**
 * 
 * @author Xavier Coulon
 *
 */
public class OpenshiftBinaryRSyncRetrievalIntegrationTest {

	/** The usual Logger.*/
	private static final Logger LOGGER = LoggerFactory.getLogger(OpenshiftBinaryRSyncRetrievalIntegrationTest.class);
	
	private IntegrationTestHelper helper = new IntegrationTestHelper();

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void testRSyncLogRetrieval() throws IOException {
		// given
		System.setProperty(IBinaryCapability.OPENSHIFT_BINARY_LOCATION, helper.getOpenShiftLocation());
		final IClient client = new ClientBuilder(helper.getServerUrl()).resourceFactory(new ResourceFactory(null))
				.sslCertificateCallback(new NoopSSLCertificateCallback()).build();
		client.setAuthorizationStrategy(new BasicAuthorizationStrategy("test-admin", "test-admin", ""));
		final IAuthorizationClient authClient = new AuthorizationClient(client);
		final IAuthorizationContext context = authClient.getContext(client.getBaseURL().toString());
		client.setAuthorizationStrategy(new TokenAuthorizationStrategy(context.getToken()));
		// retrieve the first pod in the 'eap-app' service
		final IService service = client.get(ResourceKind.SERVICE, "nodejs-example", "int-test");
		final List<IPod> pods = service.getPods();
		assertThat(pods).isNotEmpty();
		final IPod pod = pods.get(0);
		final String localDir = "/Users/xcoulon/git/nodejs-ex";
		final String targetDir = "/tmp";
		// when
		// create a dummy file to be sure there will be something to rsync
		final String fileName = File.createTempFile("test", ".txt", new File(localDir)).getName();
		// run the rsync and collect the logs
		final List<String> logs = new ArrayList<>(100);
		pod.accept(new CapabilityVisitor<IRSyncable, Object>() {

			@Override
			public Object visit(IRSyncable cap) {
				try {
					System.out.println("**** RSync Logs ****");
					final BufferedReader reader = new BufferedReader(new InputStreamReader(
							cap.sync(new LocalPeer(localDir), new PodPeer(targetDir, pod))));
					String line;
					while((line = reader.readLine()) != null) {
						System.out.println(line);
						logs.add(line);
					}
					// wait until end of 'rsync'
					cap.await();
				} catch (Exception e) {
					e.printStackTrace();
				}
				return null;
			}

		}, new Object());
		// then
		// verify that the logs contain a message about the dummy file
		assertThat(logs).isNotEmpty();
		assertThat(logs.stream().anyMatch(line -> line.contains(fileName))).isTrue();
	}
}
