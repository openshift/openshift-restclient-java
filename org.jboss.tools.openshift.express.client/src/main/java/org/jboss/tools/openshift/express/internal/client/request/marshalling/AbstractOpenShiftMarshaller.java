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
package org.jboss.tools.openshift.express.internal.client.request.marshalling;

import org.jboss.tools.openshift.express.internal.client.request.IOpenShiftRequest;


/**
 * @author Andre Dietisheim
 *
 * @param <REQUEST>
 */
public abstract class AbstractOpenShiftMarshaller<REQUEST extends IOpenShiftRequest> implements IOpenShiftMarshaller<REQUEST> {

	@Override
	public String marshall(REQUEST object) {
		StringBuilder builder = new StringBuilder();
		append(builder);
		return builder.toString();
	}

	protected abstract void append(StringBuilder builder);

}
