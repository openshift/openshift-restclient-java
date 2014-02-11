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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.openshift.client.IDomain;
import com.openshift.client.IHttpClient;
import com.openshift.client.IOpenShiftConnection;
import com.openshift.client.IUser;
import com.openshift.client.OpenShiftException;
import com.openshift.client.cartridge.EmbeddableCartridge;
import com.openshift.client.cartridge.IEmbeddableCartridge;
import com.openshift.client.cartridge.IStandaloneCartridge;
import com.openshift.client.cartridge.StandaloneCartridge;
import com.openshift.internal.client.httpclient.request.Parameter;
import com.openshift.internal.client.httpclient.request.StringParameter;
import com.openshift.internal.client.response.CartridgeResourceDTO;
import com.openshift.internal.client.response.DomainResourceDTO;
import com.openshift.internal.client.response.Link;
import com.openshift.internal.client.response.UserResourceDTO;
import com.openshift.internal.client.utils.Assert;
import com.openshift.internal.client.utils.CollectionUtils;
import com.openshift.internal.client.utils.IOpenShiftJsonConstants;

/**
 * @author Andre Dietisheim
 * @author Xavier Coulon
 */
public class APIResource extends AbstractOpenShiftResource implements IOpenShiftConnection {
	
	private static final String SYSPROPERTY_PROXY_PORT = "proxyPort";
	private static final String SYSPROPERTY_PROXY_HOST = "proxyHost";
	private static final String SYSPROPERTY_PROXY_SET = "proxySet";

	private final String login;
	private final String password;
	private List<IDomain> domains;
	private UserResource user;
	//TODO: implement switch that allows to turn ssl checks on/off 
	private boolean doSSLChecks = false;
	private final List<IStandaloneCartridge> standaloneCartridgeNames = new ArrayList<IStandaloneCartridge>();
	private final List<IEmbeddableCartridge> embeddedCartridgeNames = new ArrayList<IEmbeddableCartridge>();
	private final ExecutorService executorService;
	
	protected APIResource(final String login, final String password, final IRestService service,
			final Map<String, Link> links) {
		super(service, links, null);
		this.login = login;
		this.password = password;
		this.executorService = Executors.newFixedThreadPool(10);
	}

	protected final String getLogin() {
		return login;
	}

	protected final String getPassword() {
		return password;
	}
	
	public String getServer() {
		return getService().getPlatformUrl();
	}
	
	public void setEnableSSLCertChecks(boolean doSSLChecks) {
		this.doSSLChecks = doSSLChecks;
	}

    @Deprecated
	public void setProxySet(boolean proxySet) {
		if (proxySet) {
			System.setProperty(SYSPROPERTY_PROXY_SET, "true");
		} else {
			System.setProperty(SYSPROPERTY_PROXY_SET, "false");
		}
	}

    @Deprecated
	public void setProxyHost(String proxyHost) {
		System.setProperty(SYSPROPERTY_PROXY_HOST, proxyHost);
	}

    @Deprecated
	public void setProxyPort(String proxyPort) {
		Assert.notNull(proxyPort);

		System.setProperty(SYSPROPERTY_PROXY_PORT, proxyPort);
	}

	public IUser getUser() throws OpenShiftException {
		if (user == null) {
			this.user = new UserResource(this, new GetUserRequest().execute(), this.password);
		}
		return this.user;
	}

	public List<IDomain> getDomains() throws OpenShiftException {
		if (domains == null) {
			this.domains = loadDomains();
		}
		return CollectionUtils.toUnmodifiableCopy(this.domains);
	}

	private List<IDomain> loadDomains() throws OpenShiftException {
		List<IDomain> domains = new ArrayList<IDomain>();
		for (DomainResourceDTO domainDTO : new ListDomainsRequest().execute()) {
			domains.add(new DomainResource(domainDTO, this));
		}
		return domains;
	}

	public IDomain getDomain(String id) throws OpenShiftException {
		Assert.notNull(id);

		for (IDomain domain : getDomains()) {
			if (domain.getId().equals(id)) {
				return domain;
			}
		}
		return null;
	}

	public IDomain getDefaultDomain() {
		final List<IDomain> domains = getDomains();
		if (domains.size() > 0) {
			return domains.get(0);
		}
		return null;
	}

	public IDomain createDomain(String id) throws OpenShiftException {
		Assert.notNull(id);

		if (hasDomain(id)) {
			throw new OpenShiftException("Domain {0} already exists", id);
		}

		final DomainResourceDTO domainDTO = new AddDomainRequest().execute(id);
		final IDomain domain = new DomainResource(domainDTO, this);
		this.domains.add(domain);
		return domain;
	}

	public IDomain showDomain(String id) throws OpenShiftException {
		Assert.notNull(id);

		final DomainResourceDTO domainDTO = new ShowDomainRequest().execute(id);
		final IDomain domain = new DomainResource(domainDTO, this);
		// TODO: implement caching
		return domain;
	}

	public List<IStandaloneCartridge> getStandaloneCartridges() throws OpenShiftException {
		if (standaloneCartridgeNames.isEmpty()) {
			retrieveCartridges();
		}
		return standaloneCartridgeNames;
	}

	public List<IEmbeddableCartridge> getEmbeddableCartridges() throws OpenShiftException {
		if (embeddedCartridgeNames.isEmpty()) {
			retrieveCartridges();
		}
		return CollectionUtils.toUnmodifiableCopy(embeddedCartridgeNames);
	}

	private void retrieveCartridges() throws OpenShiftException {
		final Map<String, CartridgeResourceDTO> cartridgeDTOsByName = new GetCartridgesRequest().execute();
		for (CartridgeResourceDTO cartridgeDTO : cartridgeDTOsByName.values()) {
			// TODO replace by enum (standalone, embedded)
			switch (cartridgeDTO.getType()) {
			case STANDALONE:
				this.standaloneCartridgeNames.add(
						new StandaloneCartridge(
								cartridgeDTO.getName(), cartridgeDTO.getDisplayName(), cartridgeDTO.getDescription()));
				break;
			case EMBEDDED:
				this.embeddedCartridgeNames.add(
						new EmbeddableCartridge(
								cartridgeDTO.getName(), cartridgeDTO.getDisplayName(), cartridgeDTO.getDescription()));
				break;
			default:
			}
		}
	}
	
	@Override
	public void refresh() throws OpenShiftException {
		this.domains = null;
	}

	/**
	 * Called after a domain has been destroyed
	 * 
	 * @param domain
	 *            the domain to remove from the API's domains list.
	 */
	protected void removeDomain(final IDomain domain) {
		this.domains.remove(domain);
	}

	protected boolean hasDomain(String name) throws OpenShiftException {
		return getDomain(name) != null;
	}

	public ExecutorService getExecutorService() {
		return executorService;
	}
	
	public void disconnect() {
		standaloneCartridgeNames.clear();
		embeddedCartridgeNames.clear();
		domains = null;
		executorService.shutdownNow();
	}
	
	private class AddDomainRequest extends ServiceRequest {

		private AddDomainRequest() throws OpenShiftException {
			super("ADD_DOMAIN");
		}

		private DomainResourceDTO execute(String namespace) throws OpenShiftException {
			return execute(new StringParameter(IOpenShiftJsonConstants.PROPERTY_ID, namespace));
		}
	}

	private class ListDomainsRequest extends ServiceRequest {

		private ListDomainsRequest() throws OpenShiftException {
			super("LIST_DOMAINS");
		}

		protected List<DomainResourceDTO> execute() throws OpenShiftException {
			return super.execute();
		}
	}

	private class GetUserRequest extends ServiceRequest {

		private GetUserRequest() throws OpenShiftException {
			super("GET_USER");
		}

		protected UserResourceDTO execute() throws OpenShiftException {
			return super.execute();
		}
	}

	private class GetCartridgesRequest extends ServiceRequest {

		private GetCartridgesRequest() throws OpenShiftException {
			super("LIST_CARTRIDGES");
		}

		protected Map<String, CartridgeResourceDTO> execute() throws OpenShiftException {
			return super.execute();
		}
	}

	private class ShowDomainRequest extends ServiceRequest {

		private ShowDomainRequest() throws OpenShiftException {
			super("SHOW_DOMAIN");
		}

		protected DomainResourceDTO execute(String id) throws OpenShiftException {
			List<Parameter> urlPathParameter = new Parameters().add("name", id).toList();
			return super.execute(IHttpClient.NO_TIMEOUT, 
					urlPathParameter,  // url path parameter
					Collections.<Parameter>emptyList()); // request body parameter
		}
	}
}
