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
package com.openshift.client;

import com.openshift.internal.client.Severity;

/**
 * @author Andre Dietisheim
 */
public interface ISeverity {
	
	public static final ISeverity INFO = new Severity("info");
	public static final ISeverity DEBUG = new Severity("debug");
	public static final ISeverity ERROR = new Severity("error");
	public static final ISeverity RESULT = new Severity("result");

	public String getValue();
}
