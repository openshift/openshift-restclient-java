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

import java.util.Date;

/**
 * @author André Dietisheim
 */
public interface IApplication {

	public String getName();
	
	public String getUUID() throws OpenShiftException;

	public ICartridge getCartridge();

	public String getEmbedded() throws OpenShiftException;

	public Date getCreationTime() throws OpenShiftException;

	public void destroy() throws OpenShiftException;

	public void start() throws OpenShiftException;

	public void restart() throws OpenShiftException;

	public void stop() throws OpenShiftException;

	public ApplicationLogReader getLogReader() throws OpenShiftException;

	public String getGitUri() throws OpenShiftException;

	public String getApplicationUrl() throws OpenShiftException;

}