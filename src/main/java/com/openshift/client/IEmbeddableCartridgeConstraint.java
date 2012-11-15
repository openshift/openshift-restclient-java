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
package com.openshift.client;

import java.util.List;

import com.openshift.internal.client.EmbeddableCartridge;

/**
 * @author Andre Dietisheim
 * 
 */
public interface IEmbeddableCartridgeConstraint {

	public static final IEmbeddableCartridge MYSQL = new EmbeddableCartridge("mysql");

	public static final IEmbeddableCartridge PHPMYADMIN = new EmbeddableCartridge("phpmyadmin");

	public static final IEmbeddableCartridge POSTGRESQL = new EmbeddableCartridge("postgresql");

	public static final IEmbeddableCartridge MONGODB = new EmbeddableCartridge("mongodb");

	public static final IEmbeddableCartridge ROCKMONGO = new EmbeddableCartridge("rockmongo");

	public static final IEmbeddableCartridge _10GEN_MMS_AGENT = new EmbeddableCartridge("10gen-mms-agent");

	public static final IEmbeddableCartridge JENKINS = new EmbeddableCartridge("jenkins-client");

	public static final IEmbeddableCartridge METRICS = new EmbeddableCartridge("metrics");

	/**
	 * Returns the cartridge that matches this constraint.
	 * 
	 * @param connection
	 *            the connection to use when retrieving the available embeddable
	 *            cartridges
	 * @return the embeddable cartridge that matches this constraint
	 */
	public List<IEmbeddableCartridge> getEmbeddableCartridges(IOpenShiftConnection connection);
}
