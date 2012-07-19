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

import java.util.Collections;
import java.util.List;

import com.openshift.client.IApplication;
import com.openshift.client.IApplicationGear;
import com.openshift.client.IApplicationGearComponent;
import com.openshift.client.OpenShiftException;
import com.openshift.internal.client.response.GearResourceDTO;

/**
 * @author Xavier Coulon
 *
 */
public class ApplicationGearResource extends AbstractOpenShiftResource implements IApplicationGear {

	/** The gear's uuid. */
	private final String uuid;
	
	/** The gear's git url. */
	private final String gitUrl;
	
	/** the gear's components. */
	private final List<IApplicationGearComponent> components;

	/** the enclosing applicationResource. */
	private final ApplicationResource applicationResource;
	
	protected ApplicationGearResource(final String uuid, final String gitUrl, final List<IApplicationGearComponent> components, final ApplicationResource applicationResource) {
		super(applicationResource.getService());
		this.uuid = uuid;
		this.gitUrl = gitUrl;
		this.components = components;
		this.applicationResource = applicationResource;
	}

	protected ApplicationGearResource(GearResourceDTO gearDTO, List<IApplicationGearComponent> components,
			ApplicationResource applicationResource) {
		this(gearDTO.getUuid(), gearDTO.getGitUrl(), components, applicationResource);
	}

	/**
	 * @return the uuid
	 */
	public final String getUuid() {
		return uuid;
	}

	/**
	 * @return the url at which the git repo of this gear may be reached
	 */
	public final String getGitUrl() {
		return gitUrl;
	}

	/**
	 * @return the components
	 */
	public final List<IApplicationGearComponent> getComponents() {
		return Collections.unmodifiableList(components);
	}

	/**
	 * @return the application
	 */
	public final IApplication getApplication() {
		return applicationResource;
	}
	
	@Override
	public void refresh() throws OpenShiftException {
	}

}
