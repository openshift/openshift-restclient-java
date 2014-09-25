/******************************************************************************* 
 * Copyright (c) 2012-2014 Red Hat, Inc. 
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.openshift.client.IAuthorization;
import com.openshift.client.IDomain;
import com.openshift.client.IHttpClient;
import com.openshift.client.IOpenShiftConnection;
import com.openshift.client.IQuickstart;
import com.openshift.client.IUser;
import com.openshift.client.OpenShiftException;
import com.openshift.client.cartridge.EmbeddableCartridge;
import com.openshift.client.cartridge.ICartridge;
import com.openshift.client.cartridge.IEmbeddableCartridge;
import com.openshift.client.cartridge.IStandaloneCartridge;
import com.openshift.client.cartridge.StandaloneCartridge;
import com.openshift.internal.client.httpclient.request.Parameter;
import com.openshift.internal.client.httpclient.request.StringParameter;
import com.openshift.internal.client.response.AuthorizationResourceDTO;
import com.openshift.internal.client.response.CartridgeResourceDTO;
import com.openshift.internal.client.response.DomainResourceDTO;
import com.openshift.internal.client.response.Link;
import com.openshift.internal.client.response.QuickstartDTO;
import com.openshift.internal.client.response.QuickstartJsonDTOFactory;
import com.openshift.internal.client.response.UserResourceDTO;
import com.openshift.internal.client.utils.Assert;
import com.openshift.internal.client.utils.CollectionUtils;
import com.openshift.internal.client.utils.IOpenShiftJsonConstants;

/**
 * @author Andre Dietisheim
 * @author Xavier Coulon
 * @author Sean Kavanagh
 */
public class APIResource extends AbstractOpenShiftResource implements IOpenShiftConnection {

	private static final String SYSPROPERTY_PROXY_PORT = "proxyPort";
	private static final String SYSPROPERTY_PROXY_HOST = "proxyHost";
	private static final String SYSPROPERTY_PROXY_SET = "proxySet";

	private final String login;
	private final String password;
	// TODO: dont rely on a single token, we could have several authorizations
	// existing on the server
	private final String token;
	private UserResource user;
	private AuthorizationResource authorization;
	// TODO: implement switch that allows to turn ssl checks on/off
	private boolean doSSLChecks = false;
	private List<IDomain> domains;
	private List<IStandaloneCartridge> standaloneCartridges;
	private List<IEmbeddableCartridge> embeddableCartridges;
	private Map<String, IQuickstart> quickstartsByName;
	private final ExecutorService executorService;

	protected APIResource(final String token, final IRestService service,
			final Map<String, Link> links) {
		super(service, links, null);
		this.login = null;
		this.password = null;
		this.token = token;
		this.executorService = Executors.newFixedThreadPool(10);
	}

	protected APIResource(final String login, final String password, final String token, final IRestService service,
			final Map<String, Link> links) {
		super(service, links, null);
		this.login = login;
		this.password = password;
		this.token = token;
		this.executorService = Executors.newFixedThreadPool(10);
	}

	protected final String getLogin() {
		return login;
	}

	protected final String getPassword() {
		return password;
	}

	@Override
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

	@Override
	public IUser getUser() throws OpenShiftException {
		if (user == null) {
			this.user = new UserResource(this, new GetUserRequest().execute(), this.password);
		}
		return this.user;
	}

	public IAuthorization createAuthorization(String note, String scopes) throws OpenShiftException {
		if (authorization != null) {
			authorization.destroy();
		}
		return this.authorization = createAuthorization(note, scopes, null);
	}

	protected AuthorizationResource createAuthorization(String note, String scopes, Integer expiresIn)
			throws OpenShiftException {
		Parameters parameters = new Parameters()
				.add(IOpenShiftJsonConstants.PROPERTY_NOTE, note)
				.add(IOpenShiftJsonConstants.PROPERTY_SCOPES, scopes)
				.add(IOpenShiftJsonConstants.PROPERTY_EXPIRES_IN,
						expiresIn == null ? null : Integer.toString(expiresIn));
		return new AuthorizationResource(this,
				new AddAuthorizationRequest().execute(parameters.toArray()));
	}

	protected void removeAuthorization() {
		this.authorization = null;
	}

	protected AuthorizationResource getOrCreateAuthorization(String token) {
		if (token == null) {
			return createAuthorization(null, IOpenShiftJsonConstants.PROPERTY_SESSION, IAuthorization.NO_EXPIRES_IN);
		} else {
			return new AuthorizationResource(
					this, new ShowAuthorizationRequest().execute(token));
		}
	}

	public IAuthorization getAuthorization() throws OpenShiftException {
		if (authorization == null) {
			// TODO: if the given token is expired we get an exception here
			this.authorization = getOrCreateAuthorization(token);
		}
		return this.authorization;
	}

	@Override
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

	@Override
	public List<IStandaloneCartridge> getStandaloneCartridges() throws OpenShiftException {
		return getStandaloneCartridges(false);
	}

	@Override
	public List<IStandaloneCartridge> getStandaloneCartridges(boolean includeObsolete) throws OpenShiftException {
		return CollectionUtils.toUnmodifiableCopy(filterObsolete(includeObsolete, getOrLoadStandaloneCartridges()));
	}

	protected List<IStandaloneCartridge> getOrLoadStandaloneCartridges() throws OpenShiftException {
		if (standaloneCartridges == null) {
			loadCartridges();
		}
		return standaloneCartridges;
	}

	@Override
	public List<IEmbeddableCartridge> getEmbeddableCartridges() throws OpenShiftException {
		return getEmbeddableCartridges(false);
	}
	
	@Override
	public List<IEmbeddableCartridge> getEmbeddableCartridges(boolean includeObsolete) throws OpenShiftException {
		return CollectionUtils.toUnmodifiableCopy(filterObsolete(includeObsolete, getOrLoadEmbeddableCartridges()));
	}

	protected List<IEmbeddableCartridge> getOrLoadEmbeddableCartridges() throws OpenShiftException {
		if (embeddableCartridges == null) {
			loadCartridges();
		}
		return embeddableCartridges;
	}

	protected <C extends ICartridge> List<C> filterObsolete(boolean includeObsolete, List<C> allCartridges) {
		if (includeObsolete) {
			return allCartridges;
		}

		List<C> filteredList = new ArrayList<C>(allCartridges.size());
		for (C cartridge : allCartridges) {
			if (!cartridge.isObsolete()) {
				filteredList.add(cartridge);
			}
		}
		return filteredList;
	}
	
	@Override
	public List<ICartridge> getCartridges() {
		return getCartridges(false);
	}

	
	@Override
	public List<ICartridge> getCartridges(boolean includeObsolete) {
		List<IEmbeddableCartridge> embeddableCartridges = getOrLoadEmbeddableCartridges();
		List<IStandaloneCartridge> standaloneCartridges = getOrLoadStandaloneCartridges();
		List<ICartridge> cartridges = 
				new ArrayList<ICartridge>(embeddableCartridges.size() + standaloneCartridges.size());
		
		cartridges.addAll(filterObsolete(includeObsolete, embeddableCartridges));
		cartridges.addAll(filterObsolete(includeObsolete, standaloneCartridges));
		return cartridges;
	}

	private void loadCartridges() throws OpenShiftException {
		final Map<String, CartridgeResourceDTO> cartridgeDTOsByName = new GetCartridgesRequest().execute();
		this.standaloneCartridges = new ArrayList<IStandaloneCartridge>();
		this.embeddableCartridges = new ArrayList<IEmbeddableCartridge>();
		for (CartridgeResourceDTO cartridgeDTO : cartridgeDTOsByName.values()) {
			addCartridge(cartridgeDTO, standaloneCartridges, embeddableCartridges);
		}
	}

	private void addCartridge(CartridgeResourceDTO dto, List<IStandaloneCartridge> standaloneCartridges,
			List<IEmbeddableCartridge> embeddableCartridges) {
		switch (dto.getType()) {
		case STANDALONE:
			standaloneCartridges.add(
					new StandaloneCartridge(dto.getName(), dto.getDisplayName(), dto.getDescription(), dto.getObsolete()));
			break;
		case EMBEDDED:
			embeddableCartridges.add(
					new EmbeddableCartridge(dto.getName(), dto.getDisplayName(), dto.getDescription(), dto.getObsolete()));
			break;
		case UNDEFINED:
			break;
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

	public List<IQuickstart> getQuickstarts() {
		if (quickstartsByName == null) {
			this.quickstartsByName = loadQuickstarts();
		}
		return CollectionUtils.toUnmodifiableCopy(this.quickstartsByName.values());
	}

	private Map<String, IQuickstart> loadQuickstarts() throws OpenShiftException {
		Map<String, IQuickstart> quickstarts = new HashMap<String, IQuickstart>();
		for (QuickstartDTO quickstartDTO : new ListQuickstartsRequest().execute()) {
			quickstarts.put(quickstartDTO.getName(), new Quickstart(quickstartDTO, this));
		}
		return quickstarts;
	}

	public ExecutorService getExecutorService() {
		return executorService;
	}

	public void disconnect() {
		standaloneCartridges = null;
		embeddableCartridges = null;
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
					urlPathParameter, // url path parameter
					Collections.<Parameter> emptyList()); // request body
															// parameter
		}
	}

	private class ListQuickstartsRequest extends ServiceRequest {

		private ListQuickstartsRequest() throws OpenShiftException {
			super("LIST_QUICKSTARTS");
		}

		protected List<QuickstartDTO> execute() throws OpenShiftException {
			return super.execute(IHttpClient.NO_TIMEOUT, new QuickstartJsonDTOFactory(),
					Collections.<Parameter> emptyList(), Collections.<Parameter> emptyList());
		}
	}

	private class AddAuthorizationRequest extends ServiceRequest {

		private AddAuthorizationRequest() throws OpenShiftException {
			super("ADD_AUTHORIZATION");
		}

		protected AuthorizationResourceDTO execute(Parameter... parameters) throws OpenShiftException {
			return super.execute(parameters);
		}
	}

	private class ShowAuthorizationRequest extends ServiceRequest {

		private ShowAuthorizationRequest() throws OpenShiftException {
			super("SHOW_AUTHORIZATION");
		}

		protected AuthorizationResourceDTO execute(String id) throws OpenShiftException {
			List<Parameter> urlPathParameter = new Parameters().add("id", id).toList();
			return (AuthorizationResourceDTO) super.execute(IHttpClient.NO_TIMEOUT, urlPathParameter,
					Collections.<Parameter> emptyList());
		}
	}
}
