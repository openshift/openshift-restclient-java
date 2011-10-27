/******************************************************************************* 
 * Copyright (c) 2011 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/ 
package org.jboss.tools.openshift.express.internal.client.response;


/**
 * @author André Dietisheim
 */
public class OpenShiftResponse<OPENSHIFTOBJECT> {

	private boolean debug;
	private String messages;
	private String result;
	private OPENSHIFTOBJECT openshiftObject;
	private int exitCode;

	public OpenShiftResponse(boolean debug, String messages, String result, OPENSHIFTOBJECT openshiftObject, int exitCode) {
		this.debug = debug;
		this.messages = messages;
		this.result = result;
		this.openshiftObject = openshiftObject;
		this.exitCode = exitCode;
	}

	public boolean isDebug() {
		return debug;
	}

	public String getMessages() {
		return messages;
	}

	public String getResult() {
		return result;
	}

	public OPENSHIFTOBJECT getOpenShiftObject() {
		return openshiftObject;
	}

	public int getExitCode() {
		return exitCode;
	}
}
