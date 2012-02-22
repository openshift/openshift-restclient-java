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

import java.util.List;

import org.jboss.dmr.ModelNode;

import com.openshift.express.internal.client.utils.IOpenShiftJsonConstants;

/**
 * @author André Dietisheim
 */
public abstract class AbstractListCartridgesResponseUnmarshaller<CARTRIDGE> extends AbstractOpenShiftJsonResponseUnmarshaller<List<CARTRIDGE>> {

	protected List<CARTRIDGE> createCartridgeList(ModelNode responseNode, List<CARTRIDGE> cartridges) {
		ModelNode dataNode = responseNode.get(IOpenShiftJsonConstants.PROPERTY_DATA);
		if (dataNode == null) {
			return cartridges;
		}
		ModelNode cartridgesNode = dataNode.get(IOpenShiftJsonConstants.PROPERTY_CARTS);
		if (cartridgesNode == null) {
			return cartridges;
		}
		for (ModelNode cartridgeNode : cartridgesNode.asList()) {
			cartridges.add(createCartridge(cartridgeNode));
		}
		return cartridges;
	}

	protected abstract CARTRIDGE createCartridge(ModelNode cartridgeNode);
}
