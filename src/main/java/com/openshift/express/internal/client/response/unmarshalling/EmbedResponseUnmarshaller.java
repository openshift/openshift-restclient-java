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
package com.openshift.express.internal.client.response.unmarshalling;

import org.jboss.dmr.ModelNode;

import com.openshift.express.client.IEmbeddableCartridge;
import com.openshift.express.client.OpenShiftException;
import com.openshift.express.internal.client.utils.IOpenShiftJsonConstants;

/**
 * @author André Dietisheim
 */
public class EmbedResponseUnmarshaller extends AbstractOpenShiftJsonResponseUnmarshaller<IEmbeddableCartridge> {

	private IEmbeddableCartridge embeddedCartridge;
	
	public EmbedResponseUnmarshaller(IEmbeddableCartridge embeddedCartridge) {
		this.embeddedCartridge = embeddedCartridge;
	}

	protected IEmbeddableCartridge createOpenShiftObject(ModelNode node) throws OpenShiftException {
		embeddedCartridge.setCreationLog(getString(IOpenShiftJsonConstants.PROPERTY_RESULT, node));
		return embeddedCartridge;
	}
}
