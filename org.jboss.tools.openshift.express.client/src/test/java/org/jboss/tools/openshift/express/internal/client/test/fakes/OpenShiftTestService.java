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
package org.jboss.tools.openshift.express.internal.client.test.fakes;

import org.jboss.tools.openshift.express.client.OpenShiftService;
import org.jboss.tools.openshift.express.internal.client.test.IOpenShiftTestService;

public class OpenShiftTestService extends OpenShiftService implements IOpenShiftTestService {

	public OpenShiftTestService() {
		super(STAGING_BASE_URL);
	}
}
