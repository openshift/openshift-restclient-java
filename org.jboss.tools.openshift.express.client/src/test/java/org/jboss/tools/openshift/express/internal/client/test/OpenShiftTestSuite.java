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
package org.jboss.tools.openshift.express.internal.client.test;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;


@RunWith(Suite.class)
@Suite.SuiteClasses({
	UserConfigurationTest.class,
	ApplicationTest.class,
	ApplicationLogReaderTest.class,
	ListCartridgesTest.class,
	DomainTest.class,
	UserInfoTest.class,
	UserTest.class,
	CartridgeTest.class,
	SSHKeyTest.class
})
/**
 * @author André Dietisheim
 */
public class OpenShiftTestSuite {

}
