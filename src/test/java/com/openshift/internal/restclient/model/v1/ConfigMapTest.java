/*******************************************************************************
 * Copyright (c) 2015 Red Hat, Inc. Distributed under license by Red Hat, Inc.
 * All rights reserved. This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Red Hat, Inc.
 ******************************************************************************/
package com.openshift.internal.restclient.model.v1;

import com.openshift.internal.restclient.model.ConfigMap;
import com.openshift.internal.restclient.model.properties.ResourcePropertiesRegistry;
import com.openshift.restclient.IClient;
import com.openshift.restclient.ResourceKind;
import com.openshift.restclient.model.IConfigMap;
import com.openshift.restclient.utils.Samples;
import org.jboss.dmr.ModelNode;
import org.junit.Before;
import org.junit.Test;

import java.util.Collections;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

/**
 * @author Ulf Lilleengen
 */
public class ConfigMapTest {

	private static final String VERSION = "v1";
	private IConfigMap configMap;
	
	@Before
	public void setUp(){
		IClient client = mock(IClient.class);
		ModelNode node = ModelNode.fromJSONString(Samples.V1_CONFIG_MAP.getContentAsString());
		configMap = new ConfigMap(node, client, ResourcePropertiesRegistry.getInstance().get(VERSION, ResourceKind.CONFIG_MAP));
	}
	
	@Test
	public void testGetData() {
		assertEquals(Collections.singletonMap("key1", "config1"), configMap.getData());
	}
}
