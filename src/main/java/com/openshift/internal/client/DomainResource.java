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
package com.openshift.internal.client;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.openshift.client.ApplicationScale;
import com.openshift.client.IApplication;
import com.openshift.client.IDomain;
import com.openshift.client.IGearProfile;
import com.openshift.client.IUser;
import com.openshift.client.Message;
import com.openshift.client.OpenShiftException;
import com.openshift.client.cartridge.IStandaloneCartridge;
import com.openshift.internal.client.response.ApplicationResourceDTO;
import com.openshift.internal.client.response.DomainResourceDTO;
import com.openshift.internal.client.response.Link;
import com.openshift.internal.client.response.LinkParameter;
import com.openshift.internal.client.utils.Assert;
import com.openshift.internal.client.utils.CollectionUtils;
import com.openshift.internal.client.utils.IOpenShiftJsonConstants;

/**
 * @author André Dietisheim
 */
public class DomainResource extends AbstractOpenShiftResource implements IDomain {

	private static final String LINK_GET = "GET";
	private static final String LINK_LIST_APPLICATIONS = "LIST_APPLICATIONS";
	private static final String LINK_ADD_APPLICATION = "ADD_APPLICATION";
	private static final String LINK_UPDATE = "UPDATE";
	private static final String LINK_DELETE = "DELETE";
	private String id;
	private String suffix;
	/** root node in the business domain. */
	private final APIResource connectionResource;
	/** Applications for the domain. */
	// TODO: replace by a map indexed by application names ?
	private List<IApplication> applications = null;

	protected DomainResource(final String namespace, final String suffix, final Map<String, Link> links,
			final Map<String, Message> messages,
			final APIResource api) {
		super(api.getService(), links, messages);
		this.id = namespace;
		this.suffix = suffix;
		this.connectionResource = api;
	}

	protected DomainResource(DomainResourceDTO domainDTO, final APIResource api) {
		this(domainDTO.getId(), domainDTO.getSuffix(), domainDTO.getLinks(), domainDTO.getMessages(), api);
	}

	public String getId() {
		return id;
	}

	public String getSuffix() {
		return suffix;
	}

	public void rename(String id) throws OpenShiftException {
		Assert.notNull(id);

		DomainResourceDTO domainDTO = new UpdateDomainRequest().execute(id);
		this.id = domainDTO.getId();
		this.suffix = domainDTO.getSuffix();
		this.getLinks().clear();
		this.getLinks().putAll(domainDTO.getLinks());
	}

	public IUser getUser() throws OpenShiftException {
		return connectionResource.getUser();
	}

	public boolean waitForAccessible(long timeout) throws OpenShiftException {
		throw new UnsupportedOperationException();
		//TODO: implement
	}

	public IApplication createApplication(final String name, final IStandaloneCartridge cartridge)
			throws OpenShiftException {
		return createApplication(name, cartridge, (String) null);
	}

	public IApplication createApplication(final String name, final IStandaloneCartridge cartridge,
			final ApplicationScale scale) throws OpenShiftException {
		return createApplication(name, cartridge, scale, null, null);
	}

	public IApplication createApplication(final String name, final IStandaloneCartridge cartridge, String initialGitUrl)
			throws OpenShiftException {
		return createApplication(name, cartridge, null, null, initialGitUrl);
	}

	public IApplication createApplication(final String name, final IStandaloneCartridge cartridge,
			final ApplicationScale scale, String initialGitUrl) throws OpenShiftException {
		return createApplication(name, cartridge, scale, null, initialGitUrl);
	}

	public IApplication createApplication(final String name, final IStandaloneCartridge cartridge,
			final IGearProfile gearProfile) throws OpenShiftException {
		return createApplication(name, cartridge, null, gearProfile);
	}

	public IApplication createApplication(final String name, final IStandaloneCartridge cartridge,
			final IGearProfile gearProfile, String initialGitUrl) throws OpenShiftException {
		return createApplication(name, cartridge, null, gearProfile, initialGitUrl);
	}

	public IApplication createApplication(final String name, final IStandaloneCartridge cartridge,
			final ApplicationScale scale, final IGearProfile gearProfile) throws OpenShiftException {
		return createApplication(name, cartridge, scale, gearProfile, null);
	}

	public IApplication createApplication(final String name, final IStandaloneCartridge cartridge,
			final ApplicationScale scale, final IGearProfile gearProfile, String initialGitUrl)
			throws OpenShiftException {
		if (name == null) {
			throw new OpenShiftException("Application name is mandatory but none was given.");
		}
		// this would trigger lazy loading list of available applications.
		// this is needed anyhow since we're adding the new app to the list of
		// available apps
		if (hasApplicationByName(name)) {
			throw new OpenShiftException("Application with name \"{0}\" already exists.", name);
		}

		ApplicationResourceDTO applicationDTO =
				new CreateApplicationRequest().execute(name, cartridge, scale, gearProfile, initialGitUrl);
		IApplication application = new ApplicationResource(applicationDTO, cartridge, this);

		getOrLoadApplications().add(application);
		return application;
	}

	public boolean hasApplicationByName(String name) throws OpenShiftException {
		return getApplicationByName(name) != null;
	}

	public IApplication getApplicationByName(String name) throws OpenShiftException {
		Assert.notNull(name);
		return getApplicationByName(name, getApplications());
	}

	private IApplication getApplicationByName(String name, Collection<IApplication> applications) throws OpenShiftException {
		Assert.notNull(name);

		IApplication matchingApplication = null;
		for (IApplication application : applications) {
			if (application.getName().equalsIgnoreCase(name)) {
				matchingApplication = application;
				break;
			}
		}
		return matchingApplication;
	}

	public List<IApplication> getApplicationsByCartridge(IStandaloneCartridge cartridge) throws OpenShiftException {
		List<IApplication> matchingApplications = new ArrayList<IApplication>();
		for (IApplication application : getApplications()) {
			if (cartridge.equals(application.getCartridge())) {
				matchingApplications.add(application);
			}
		}
		return matchingApplications;
	}

	public boolean hasApplicationByCartridge(IStandaloneCartridge cartridge) throws OpenShiftException {
		return getApplicationsByCartridge(cartridge).size() > 0;
	}

	public void destroy() throws OpenShiftException {
		destroy(false);
	}

	public void destroy(boolean force) throws OpenShiftException {
		new DeleteDomainRequest().execute(force);
		connectionResource.removeDomain(this);
	}

	protected List<IApplication> getOrLoadApplications() throws OpenShiftException {
		if (applications == null) {
			this.applications = loadApplications();
		}
		return applications;
	}
	
	public List<IApplication> getApplications() throws OpenShiftException {
		return CollectionUtils.toUnmodifiableCopy(getOrLoadApplications());
	}

	/**
	 * @throws OpenShiftException
	 */
	private List<IApplication> loadApplications() throws OpenShiftException {
		List<IApplication> apps = new ArrayList<IApplication>();
		List<ApplicationResourceDTO> applicationDTOs = new ListApplicationsRequest().execute();
		for (ApplicationResourceDTO applicationDTO : applicationDTOs) {
			final IStandaloneCartridge cartridge = new StandaloneCartridge(applicationDTO.getFramework());
			final IApplication application =
					new ApplicationResource(applicationDTO, cartridge, this);
			apps.add(application);
		}
		return apps;
	}

	protected void removeApplication(IApplication application) {
		// TODO: can this collection be a null ?
		this.applications.remove(application);
	}

	public List<String> getAvailableCartridgeNames() throws OpenShiftException {
		final List<String> cartridges = new ArrayList<String>();
		for (LinkParameter param : getLink(LINK_ADD_APPLICATION).getRequiredParams()) {
			// TODO: extract "cartridge" to constant
			if (param.getName().equals("cartridge")) {
				for (String option : param.getValidOptions()) {
					cartridges.add(option);
				}
			}
		}
		return cartridges;
	}

	public List<IGearProfile> getAvailableGearProfiles() throws OpenShiftException {
		final List<IGearProfile> gearSizes = new ArrayList<IGearProfile>();
		for (LinkParameter param : getLink(LINK_ADD_APPLICATION).getOptionalParams()) {
			if (param.getName().equals(IOpenShiftJsonConstants.PROPERTY_GEAR_PROFILE)) {
				for (String option : param.getValidOptions()) {
					gearSizes.add(new GearProfile(option));
				}
			}
		}
		return gearSizes;
	}
	
	
	public void refresh() throws OpenShiftException {
		final DomainResourceDTO domainResourceDTO =  new GetDomainRequest().execute();
		this.id = domainResourceDTO.getId();
		this.suffix = domainResourceDTO.getSuffix();
		if(this.applications != null) {
			this.applications = loadApplications();
		}
		
	}

	@Override
	public String toString() {
		return "Domain ["
				+ "id=" + id + ", "
				+ "suffix=" + suffix
				+ "]";
	}

	private class GetDomainRequest extends ServiceRequest {
		public GetDomainRequest() throws OpenShiftException {
			super(LINK_GET);
		}

		protected DomainResourceDTO execute() throws OpenShiftException {
			return (DomainResourceDTO)(super.execute());
		}
		
		
	}
	
	private class ListApplicationsRequest extends ServiceRequest {

		public ListApplicationsRequest() throws OpenShiftException {
			super(LINK_LIST_APPLICATIONS);
		}

	}

	private class CreateApplicationRequest extends ServiceRequest {

		public CreateApplicationRequest() throws OpenShiftException {
			super(LINK_ADD_APPLICATION);
		}

		public ApplicationResourceDTO execute(final String name, final IStandaloneCartridge cartridge,
				final ApplicationScale scale, final IGearProfile gearProfile, final String initialGitUrl) throws OpenShiftException {
			if (cartridge == null) {
				throw new OpenShiftException("Application cartridge is mandatory but was not given.");
			} 
			
			List<ServiceParameter> parameters = new ArrayList<ServiceParameter>();
			addStringParameter(IOpenShiftJsonConstants.PROPERTY_NAME, name, parameters);
			addCartridgeParameter(cartridge, parameters);
			addScaleParameter(scale, parameters);
			addGearProfileParameter(gearProfile, parameters);
			addStringParameter(IOpenShiftJsonConstants.PROPERTY_INITIAL_GIT_URL, initialGitUrl, parameters);
			
			return super.execute((ServiceParameter[]) parameters.toArray(new ServiceParameter[parameters.size()]));
		}

		private List<ServiceParameter> addCartridgeParameter(IStandaloneCartridge cartridge, List<ServiceParameter> parameters) {
			if (cartridge == null) {
				return parameters;
			}
			parameters.add(new ServiceParameter(IOpenShiftJsonConstants.PROPERTY_CARTRIDGE, cartridge.getName()));
			return parameters;
		}
		
		private List<ServiceParameter> addScaleParameter(ApplicationScale scale, List<ServiceParameter> parameters) {
			if (scale == null) {
				return parameters;
			}
			parameters.add(new ServiceParameter(IOpenShiftJsonConstants.PROPERTY_SCALE, scale.getValue()));
			return parameters;
		}

		private List<ServiceParameter> addGearProfileParameter(IGearProfile gearProfile, List<ServiceParameter> parameters) {
			if (gearProfile == null) {
				return parameters;
			}
			parameters.add(new ServiceParameter(IOpenShiftJsonConstants.PROPERTY_GEAR_PROFILE, gearProfile.getName()));
			return parameters;
		}

		private List<ServiceParameter> addStringParameter(String parameterName, String value, List<ServiceParameter> parameters) {
			if (value == null) {
				return parameters;
			}
			parameters.add(new ServiceParameter(parameterName, value));
			return parameters;
		}

	}

	private class UpdateDomainRequest extends ServiceRequest {

		public UpdateDomainRequest() throws OpenShiftException {
			super(LINK_UPDATE);
		}

		public DomainResourceDTO execute(String namespace) throws OpenShiftException {
			return super.execute(new ServiceParameter(IOpenShiftJsonConstants.PROPERTY_ID, namespace));
		}
	}

	private class DeleteDomainRequest extends ServiceRequest {
		public DeleteDomainRequest() throws OpenShiftException {
			super(LINK_DELETE);
		}

		public void execute(boolean force) throws OpenShiftException {
			super.execute(new ServiceParameter(IOpenShiftJsonConstants.PROPERTY_FORCE, force));
		}
	}

}
