/*******************************************************************************
 * Copyright (c) 2015 Red Hat, Inc. Distributed under license by Red Hat, Inc.
 * All rights reserved. This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Red Hat, Inc.
 ******************************************************************************/
package com.openshift.restclient.model.build;

/**
 * The build strategies supported
 * by OpenShift
 */

// TODO: evalute switching to a class/constants since enums are not extendable
public enum BuildStrategyType {
	Docker,
	STI,
	Custom
}
