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

import com.openshift.internal.client.GearProfile;

/**
 * @author Andre Dietisheim
 */
public interface IGearProfile {

	public static final IGearProfile JUMBO = new GearProfile("jumbo");
	public static final IGearProfile EXLARGE = new GearProfile("exlarge");
	public static final IGearProfile LARGE = new GearProfile("large");
	public static final IGearProfile MEDIUM = new GearProfile("medium");
	public static final IGearProfile MICRO = new GearProfile("micro");
	public static final IGearProfile SMALL = new GearProfile("small");
	
	public String getName();
	
}
