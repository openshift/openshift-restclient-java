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
package org.jboss.tools.openshift.express.client;

/**
 * @author André Dietisheim
 */
public interface ISSHPublicKey {

	/**
	 * Returns the content of the public key (key content without ssh-rsa
	 * identifier nor comment) of the ssh key
	 * 
	 * @return the content of the public key (without signature, without
	 *         comment)
	 * @throws OpenShiftException
	 */
	public String getPublicKey() throws OpenShiftException ;
	
}
