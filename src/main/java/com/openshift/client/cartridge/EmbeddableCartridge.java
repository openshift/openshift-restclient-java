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
package com.openshift.client.cartridge;

/**
 * An cartridge that may be embedded (added) into an application. Add-on
 * cartridge is an equivalent name for embedded cartridge.
 * 
 * @author Xavier Coulon
 * 
 * @see IEmbeddableCartridge for cartridges that have already been added and
 *      configured to an application.
 */
public class EmbeddableCartridge extends BaseCartridge implements IEmbeddableCartridge {

	public EmbeddableCartridge(final String name) {
		super(name);
	}

	public EmbeddableCartridge(final String name, String version) {
		super(name, version);
	}

	public EmbeddableCartridge(final String name, String displayName, String description) {
		super(name, displayName, description);
	}

	/**
	 * @see java.lang.Object#equals(java.lang.Object) equals support comparison
	 *      between EmbeddedCartridges and EmbeddableCartridges (ie, removed
	 *      'class' comparison from generated equals() implementation)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof IEmbeddableCartridge)) {
			return false;
		}
		IEmbeddableCartridge other = (IEmbeddableCartridge) obj;
		if (getName() == null) {
			if (other.getName() != null) {
				return false;
			}
		} else if (!getName().equals(other.getName())) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "EmbeddableCartridge [" +
				"name=" + getName() +
				"]";
	}

}
