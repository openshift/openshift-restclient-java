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

import java.util.ArrayList;
import java.util.List;

import org.jboss.dmr.ModelNode;

import com.openshift.express.client.Cartridge;
import com.openshift.express.client.ICartridge;

/**
 * @author André Dietisheim
 */
public class ListCartridgesResponseUnmarshaller extends AbstractListCartridgesResponseUnmarshaller<ICartridge> {

	protected List<ICartridge> createOpenShiftObject(ModelNode responseNode) {
		return createCartridgeList(responseNode, new ArrayList<ICartridge>());
	}

	protected Cartridge createCartridge(ModelNode cartridgeNode) {
		String name = cartridgeNode.asString();
		return new Cartridge(name);
	}
}
