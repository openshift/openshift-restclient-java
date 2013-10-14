/******************************************************************************* 
 * Copyright (c) 2007 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/ 
package com.openshift.internal.client;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import com.openshift.internal.client.httpclient.HttpClientTest;
import com.openshift.internal.client.httpclient.request.FormUrlEncodedMediaTypeTest;
import com.openshift.internal.client.httpclient.request.JsonMediaTypeTest;
import com.openshift.internal.client.response.OpenShiftJsonDTOFactoryTest;

@RunWith(Suite.class)
@Suite.SuiteClasses({
	ConfigurationTest.class,
	HttpClientTest.class,
	RestServicePropertiesTest.class,
	RestServiceTest.class,
	OpenShiftJsonDTOFactoryTest.class,
	DomainResourceTest.class,
	ApplicationResourceTest.class,
	APIResourceTest.class,
	StandaloneCartridgeTest.class,
	EmbeddableCartridgeTest.class,
	EmbeddedCartridgeResourceTest.class,
	QueryTest.class,
	UserTest.class,
	SSHKeyTest.class,
	GearGroupsResourceTest.class,
	GearTest.class,
	GearTest.class,
	OpenShiftExceptionTest.class,
	FormUrlEncodedMediaTypeTest.class,
	JsonMediaTypeTest.class,
	EnvironmentVariableResourceTest.class
})

/**
 * @author André Dietisheim
 */
public class OpenShiftTestSuite {

}
