/******************************************************************************* 
 * Copyright (c) 2012 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/
package com.openshift.client.utils;

import org.junit.rules.Verifier;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.Statement;

/**
 * @author Xavier Coulon
 *
 */
public class StateVerifier extends Verifier {

	/* (non-Javadoc)
	 * @see org.junit.rules.Verifier#apply(org.junit.runners.model.Statement, org.junit.runners.model.FrameworkMethod, java.lang.Object)
	 */
	@Override
	public Statement apply(Statement base, FrameworkMethod method, Object target) {
		// TODO Auto-generated method stub
		return super.apply(base, method, target);
	}

}
