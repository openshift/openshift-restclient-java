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
	ApplicationIntegrationTest.class,
	ApplicationLogReaderIntegrationTest.class,
	CartridgesIntegrationTest.class,
	DomainIntegrationTest.class,
	ListCartridgesIntegrationTest.class,
	UserInfoIntegrationTest.class,
	UserIntegrationTest.class
})
/**
 * @author André Dietisheim
 */
public class OpenShiftIntegrationTestSuite {

}
