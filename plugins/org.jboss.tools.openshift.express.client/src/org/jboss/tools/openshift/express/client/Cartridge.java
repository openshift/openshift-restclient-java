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
 * A cartridge that is available on the openshift server. This class is no enum
 * since we dont know all available types and they may change at any time.
 * 
 * @author André Dietisheim
 */
public class Cartridge implements ICartridge {

	private String name;

	public Cartridge(String name) {
		this.name = name;
	}

	@Override
	public String getName() {
		return name;
	}

	public static ICartridge valueOf(String name) {
		if (JBOSSAS_7.getName().equals(name)) {
			return JBOSSAS_7;
		}
		return null;
	}
}
