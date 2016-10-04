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
package com.openshift.internal.restclient.api.capabilities;

import com.openshift.internal.restclient.DefaultClient;
import com.openshift.internal.restclient.TypeMapperFixture;
import com.openshift.internal.restclient.capability.resources.PodLogRetrievalAsync;
import com.openshift.restclient.IApiTypeMapper;
import com.openshift.restclient.api.capabilities.IPodExec;
import com.openshift.restclient.model.IPod;
import com.openshift.restclient.model.MocksFactory;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class PodExecTest extends TypeMapperFixture {

	private DefaultClient client;
	@Mock
	private IApiTypeMapper mapper;
	private PodLogRetrievalAsync capability;
	private IPod pod;
	private PodExec.ExecOutputListenerAdapter adapter;

	@Mock
	private IPodExec.IPodExecOutputListener listener;

	@Before
	public void setUp() throws Exception {
		super.setUp();
		client = new DefaultClient(null, getHttpClient(), null, getApiTypeMapper(), null);
		pod = new MocksFactory().mock(IPod.class);
		capability = new PodLogRetrievalAsync(pod, client);
		adapter = new PodExec.ExecOutputListenerAdapter(null, listener);
	}

	@Test
	public void testIsSupported() {
		assertTrue("Exp. capability to be supported because the pod endpoint exists", capability.isSupported());
	}

	@Test
	public void testIsNotSupportedWhenEndpointDoesNotExist() {
		when(pod.getApiVersion()).thenReturn("somenoneexitentversion");
		assertFalse("Exp. capability to not be supported because the pod endpoint does not exist exists", capability.isSupported());
	}

	@Test
	public void testAdapterCallsListenerCycle() throws Exception {
		adapter.onOpen(null, null);
		adapter.onOpen(null, null);
		verify(listener).onOpen();


		adapter.deliver(PodExec.CHANNEL_STDOUT, "ImStdOut");
		adapter.deliver(PodExec.CHANNEL_STDERR, "ImStdErr");
		adapter.deliver(PodExec.CHANNEL_EXECERR, "ImExecErr");

		verify(listener).onStdOut("ImStdOut");
		verify(listener).onStdErr("ImStdErr");
		verify(listener).onExecErr("ImExecErr");

		adapter.onClose(1986, "the reason");
		adapter.onClose(1986, "the reason");
		verify(listener).onClose(1986, "the reason");
	}

	@Test
	public void testExecOptions() throws Exception {
		IPodExec.Options options = new IPodExec.Options();
		assertEquals( 0, options.getMap().size() );

		options.stdErr( false );
		options.stdOut( false );
		options.container( "test" );

		assertEquals( 3, options.getMap().size() );

		assertEquals( "false", options.getMap().get(IPodExec.Options.STDERR) );
		assertEquals( "false", options.getMap().get(IPodExec.Options.STDOUT) );
		assertEquals( "test", options.getMap().get(IPodExec.Options.CONTAINER) );

		options.parameter( IPodExec.Options.STDERR, "true" );
		options.parameter( IPodExec.Options.STDOUT, "true" );
		options.parameter( IPodExec.Options.CONTAINER, "override" );

		// Re-set these options to ensure they do not override parameter API
		options.stdErr( false );
		options.stdOut( false );
		options.container( "test" );

		assertEquals( "true", options.getMap().get(IPodExec.Options.STDERR) );
		assertEquals( "true", options.getMap().get(IPodExec.Options.STDOUT) );
		assertEquals( "override", options.getMap().get(IPodExec.Options.CONTAINER) );

	}

}