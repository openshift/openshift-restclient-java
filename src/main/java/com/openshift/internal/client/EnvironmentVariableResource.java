/******************************************************************************* 
 * Copyright (c) 2013 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/
package com.openshift.internal.client;

import java.util.Map;

import com.openshift.client.IApplication;
import com.openshift.client.IEnvironmentVariable;
import com.openshift.client.Messages;
import com.openshift.client.OpenShiftException;
import com.openshift.internal.client.response.EnvironmentVariableResourceDTO;
import com.openshift.internal.client.response.Link;
import com.openshift.internal.client.utils.IOpenShiftJsonConstants;

/**
 * @author Syed Iqbal
 */
public class EnvironmentVariableResource extends AbstractOpenShiftResource implements IEnvironmentVariable {

	private static final String LINK_GET = "GET";
	private static final String LINK_UPDATE = "UPDATE";
	private static final String LINK_DELETE = "DELETE";

	/** The name of the environment variable */
	private String name;

	/** The value of the environment variable */
	private String value;

	/** The application this environment variable belongs to */
	private ApplicationResource application;

	protected EnvironmentVariableResource(EnvironmentVariableResourceDTO dto, ApplicationResource application) {
		this(dto.getName(), dto.getValue(), dto.getMessages(), dto.getLinks(), application);
	}

	protected EnvironmentVariableResource(final String name, final String value, final Messages messages,
			final Map<String, Link> links, final ApplicationResource application) {
		super(application.getService(), links, messages);
		this.name = name;
		this.value = value;
		this.application = application;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public String getValue() {
		return value;
	}

	@Override
	public void update(String newValue) throws OpenShiftException {
		if (newValue == null) {
			throw new OpenShiftException("Value for environment variable \"{0}\" not given.", name);
		}
		EnvironmentVariableResourceDTO environmentVariableResourceDTO = 
				new UpdateEnvironmentVariableRequest().execute(newValue);
		updateEnvironmentVariable(environmentVariableResourceDTO);
		/*
		 * This should be done in the IApplication, to break up this dependency
		 * on the entity, i.e. IEnvironmentVariable, on something that is 
		 * outside of itself, such as the implementation of IApplication.
		 * @author Martes G Wigglesworth
		 */
		application.updateEnvironmentVariables();
		
		
	}

	private void updateEnvironmentVariable(EnvironmentVariableResourceDTO dto) {
		this.name = dto.getName();
		this.value = dto.getValue();
		this.getLinks().clear();
		this.getLinks().putAll(dto.getLinks());
	}

	@Override
	public void destroy() throws OpenShiftException {
		new DeleteEnvironmentVariableRequest().execute();
		
	}

	@Override
	public void refresh() throws OpenShiftException {
		EnvironmentVariableResourceDTO environmentVariableResourceDTO = new GetEnvironmentVariableRequest().execute();
		updateEnvironmentVariable(environmentVariableResourceDTO);
	}

	private class UpdateEnvironmentVariableRequest extends ServiceRequest {
		protected UpdateEnvironmentVariableRequest() {
			super(LINK_UPDATE);
		}

		protected EnvironmentVariableResourceDTO execute(String value) throws OpenShiftException {
			Parameters parameters = new Parameters()
					.add(IOpenShiftJsonConstants.PROPERTY_VALUE, value);
			return super.execute(parameters.toArray());
		}
	}

	private class GetEnvironmentVariableRequest extends ServiceRequest {
		protected GetEnvironmentVariableRequest() {
			super(LINK_GET);
		}
	}

	private class DeleteEnvironmentVariableRequest extends ServiceRequest {
		protected DeleteEnvironmentVariableRequest() {
			super(LINK_DELETE);
		}
	}

	@Override
	public IApplication getApplication() {
		return application;
	}

	public String toString(){
		return new String(
				"Name:"+this.name+",Value:"+value
				);
	}
}
