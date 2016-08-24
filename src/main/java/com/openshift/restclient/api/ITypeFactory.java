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
package com.openshift.restclient.api;

import java.util.Optional;

import com.openshift.restclient.ResourceFactoryException;

/**
 * A factory that is able of producing
 * types from a response 
 * @author jeff.cantrill
 *
 */
public interface ITypeFactory {

	/**
	 * Create a resource from a response string
	 * @param response
	 * @return
	 * @throws ResourceFactoryException  if it is unable to create resources
	 */
	Object createInstanceFrom(String response);
	
	/**
	 * Stub out the given resource kind using a version determined by the factory
	 * @param kind - Required. For arg types it may be in the form of apigroup/version.kind
	 * @param name - The name of the kind which may only be significant for instances that
	 *               can be persisted by the server (e.g. Service)
	 * @param namespace - The namespace of the kind which may only be significant for instance
	 *                    that can be persisted
	 * @return
	 */
	Object stubKind(String kind, Optional<String> name, Optional<String> namespace);
}
