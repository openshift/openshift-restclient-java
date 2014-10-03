/*******************************************************************************
 * Copyright (c) 2011-2014 Red Hat, Inc.
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

import com.openshift.client.configuration.AbstractOpenshiftConfiguration.ConfigurationOptions;

/**
 * @author André Dietisheim
 * @author Corey Daley
 */
public interface IOpenShiftConfiguration {

	public String getRhlogin();

	public void setRhlogin(String rhlogin);

	public String getLibraServer();

	public void setLibraServer(String libraServer);

	public String getLibraDomain();

	public Integer getTimeout();

	public void setLibraDomain(String libraDomain);

	public ConfigurationOptions getDisableBadSSLCiphers();
	
	public void setDisableBadSSLCiphers(ConfigurationOptions option);
	
	public Properties getProperties();
}