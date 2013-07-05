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

import com.openshift.internal.client.Field;

/**
 * @author Andre Dietisheim
 */
public interface IField {
	
	public static final IField DEFAULT = new Field(null);
	public static final IField RESULT = new Field("result");
	public static final IField APPINFO = new Field("appinfo");

	public String getValue();
}
