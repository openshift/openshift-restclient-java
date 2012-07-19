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
package com.openshift.internal.client;

import com.openshift.client.IEmbeddableCartridge;

/**
 * An interface that designate a cartridge that can be embedded into an
 * application.
 * 
 * @author Xavier Coulon
 * 
 * @see IEmbeddableCartridge for cartridges that have already been added and
 *      configured to an application.
 */
public class EmbeddableCartridge implements IEmbeddableCartridge {

	private final String name;

	public EmbeddableCartridge(final String name) {
		this.name = name;
	}

	public String getName() {
		return this.name;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
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
		if (name == null) {
			if (other.getName() != null) {
				return false;
			}
		} else if (!name.equals(other.getName())) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "EmbeddableCartridge [" +
				"name=" + name +
				"]";
	}

}
