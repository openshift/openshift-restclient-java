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



import java.io.BufferedInputStream;

import org.junit.Before;
import org.junit.Test;

import com.openshift.internal.restclient.IntegrationTestHelper;
import com.openshift.internal.restclient.authorization.AuthorizationClient;
import com.openshift.restclient.IClient;
import com.openshift.restclient.ResourceKind;
import com.openshift.restclient.authorization.BasicAuthorizationStrategy;
import com.openshift.restclient.authorization.IAuthorizationClient;
import com.openshift.restclient.authorization.IAuthorizationContext;
import com.openshift.restclient.authorization.TokenAuthorizationStrategy;
import com.openshift.restclient.capability.CapabilityVisitor;
import com.openshift.restclient.capability.IBinaryCapability;
import com.openshift.restclient.capability.resources.IPodLogRetrieval;
import com.openshift.restclient.model.IPod;

/**
 * 
 * @author Jeff Cantrill
 *
 */
public class OpenshiftBinaryRSyncRetrievalIntegrationTest {

	private IntegrationTestHelper helper = new IntegrationTestHelper();
	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void testLogRetrieval() {
		System.setProperty(IBinaryCapability.OPENSHIFT_BINARY_LOCATION, helper.getOpenShiftLocation());
		IClient client = helper.createClient();
		client.setAuthorizationStrategy(new BasicAuthorizationStrategy("admin", "admin", ""));
		IAuthorizationClient authClient = new AuthorizationClient(client);
		IAuthorizationContext context = authClient.getContext(client.getBaseURL().toString());
		client.setAuthorizationStrategy(new TokenAuthorizationStrategy(context.getToken()));
		client.get(ResourceKind.POD, "hello-openshift", "openshift-dev");
		IPod pod = client.get(ResourceKind.POD, "hello-openshift", "openshift-dev");

		pod.accept(new CapabilityVisitor<IPodLogRetrieval, Object>() {

			@Override
			public Object visit(IPodLogRetrieval cap) {
				try {
					BufferedInputStream os = new BufferedInputStream(cap.getLogs(true));
					int c;
					while((c = os.read()) != -1) {
						System.out.print((char)c);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				return null;
			}

		}, new Object());
	}
}
