/******************************************************************************* 
 * Copyright (c) 2016 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/
package com.openshift.restclient.api.models;

/**
 * A resource that can describe itself by
 * the kind it is
 * @author jeff.cantrill
 *
 */
public interface IKindable {

	/**
	 * The kind of the resource as would be found
	 * at kind 
	 * @return
	 */
	String getKind();
}
