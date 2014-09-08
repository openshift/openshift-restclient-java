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
package com.openshift.internal.client;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
	APIResourceIntegrationTest.class,
	LatestVersionQueryIntegrationTest.class,
	SSHKeyIntegrationTest.class,
	ApplicationSSHSessionIntegrationTest.class,
	UserResourceIntegrationTest.class,
	DomainResourceIntegrationTest.class,
	ApplicationResourceIntegrationTest.class,
	StandaloneCartridgesIntegrationTest.class,
	EmbeddedCartridgeResourceIntegrationTest.class,
	EnvironmentVariableResourceIntegrationTest.class,
	AuthorizationIntegrationTest.class
})
/**
 * @author André Dietisheim
 */
public class OpenShiftIntegrationTestSuite {

}
