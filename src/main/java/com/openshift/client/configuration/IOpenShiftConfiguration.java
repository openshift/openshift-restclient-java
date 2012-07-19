/*******************************************************************************
 * Copyright (c) 2011 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package com.openshift.client.configuration;

import java.util.Properties;

/**
 * @author André Dietisheim
 */
public interface IOpenShiftConfiguration {

	public abstract String getRhlogin();

	public abstract void setRhlogin(String rhlogin);

	public abstract String getLibraServer();

	public abstract void setLibraServer(String libraServer);

	public abstract String getLibraDomain();

	public abstract void setLibraDomain(String libraDomain);

	public Properties getProperties();
}