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
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.openshift.client.ICartridge;
import com.openshift.client.IDomain;
import com.openshift.client.IEmbeddableCartridge;
import com.openshift.client.IOpenShiftConnection;
import com.openshift.client.IUser;
import com.openshift.client.OpenShiftException;
import com.openshift.internal.client.response.CartridgeResourceDTO;
import com.openshift.internal.client.response.DomainResourceDTO;
import com.openshift.internal.client.response.Link;
import com.openshift.internal.client.response.UserResourceDTO;
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
	private final List<ICartridge> standaloneCartridgeNames = new ArrayList<ICartridge>();
	private final List<IEmbeddableCartridge> embeddedCartridgeNames = new ArrayList<IEmbeddableCartridge>();
	private final ExecutorService executorService;
	
	protected APIResource(final String login, final String password, final IRestService service,
			final Map<String, Link> links) {
		super(service, links, null);
		this.login = login;
		this.password = password;
		this.executorService = Executors.newFixedThreadPool(10);
	}

	/**
	 * @return the login
	 */
	protected final String getLogin() {
		return login;
	}

	protected final String getPassword() {
		return password;
	}
	
	public void setEnableSSLCertChecks(boolean doSSLChecks) {
		this.doSSLChecks = doSSLChecks;
	}

	public void setProxySet(boolean proxySet) {
		if (proxySet) {
			System.setProperty(SYSPROPERTY_PROXY_SET, "true");
		} else {
			System.setProperty(SYSPROPERTY_PROXY_SET, "false");
		}
	}

	public void setProxyHost(String proxyHost) {
		System.setProperty(SYSPROPERTY_PROXY_HOST, proxyHost);
	}

	public void setProxyPort(String proxyPort) {
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
		if (hasDomain(id)) {
			throw new OpenShiftException("Domain {0} already exists", id);
		}

		final DomainResourceDTO domainDTO = new AddDomainRequest().execute(id);
		final IDomain domain = new DomainResource(domainDTO, this);
		this.domains.add(domain);
		return domain;
	}

	public List<ICartridge> getStandaloneCartridges() throws OpenShiftException {
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
		final List<CartridgeResourceDTO> cartridgeDTOs = new GetCartridgesRequest().execute();
		for (CartridgeResourceDTO cartridgeDTO : cartridgeDTOs) {
			// TODO replace by enum (standalone, embedded)
			switch (cartridgeDTO.getType()) {
			case STANDALONE:
				this.standaloneCartridgeNames.add(new Cartridge(cartridgeDTO.getName()));
				break;
			case EMBEDDED:
				this.embeddedCartridgeNames.add(new EmbeddableCartridge(cartridgeDTO.getName()));
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

		public AddDomainRequest() throws OpenShiftException {
			super("ADD_DOMAIN");
		}

		public DomainResourceDTO execute(String namespace) throws OpenShiftException {
			return execute(new ServiceParameter(IOpenShiftJsonConstants.PROPERTY_ID, namespace));
		}
	}

	private class ListDomainsRequest extends ServiceRequest {

		public ListDomainsRequest() throws OpenShiftException {
			super("LIST_DOMAINS");
		}

		public List<DomainResourceDTO> execute() throws OpenShiftException {
			return super.execute();
		}
	}

	private class GetUserRequest extends ServiceRequest {

		public GetUserRequest() throws OpenShiftException {
			super("GET_USER");
		}

		public UserResourceDTO execute() throws OpenShiftException {
			return super.execute();
		}
	}

	private class GetCartridgesRequest extends ServiceRequest {

		public GetCartridgesRequest() throws OpenShiftException {
			super("LIST_CARTRIDGES");
		}

		public List<CartridgeResourceDTO> execute() throws OpenShiftException {
			return super.execute();
		}
	}
}
