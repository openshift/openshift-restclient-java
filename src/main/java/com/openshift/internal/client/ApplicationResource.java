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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.datatype.DatatypeConfigurationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.openshift.client.ApplicationScale;
import com.openshift.client.IApplication;
import com.openshift.client.IApplicationPortForwarding;
import com.openshift.client.IDomain;
import com.openshift.client.IEnvironmentVariable;
import com.openshift.client.IGearGroup;
import com.openshift.client.IGearProfile;
import com.openshift.client.IOpenShiftConnection;
import com.openshift.client.Messages;
import com.openshift.client.OpenShiftException;
import com.openshift.client.OpenShiftSSHOperationException;
import com.openshift.client.cartridge.ICartridge;
import com.openshift.client.cartridge.IEmbeddableCartridge;
import com.openshift.client.cartridge.IEmbeddedCartridge;
import com.openshift.client.cartridge.IStandaloneCartridge;
import com.openshift.client.cartridge.StandaloneCartridge;
import com.openshift.client.utils.HostUtils;
import com.openshift.client.utils.RFC822DateUtils;
import com.openshift.internal.client.httpclient.request.StringParameter;
import com.openshift.internal.client.response.ApplicationResourceDTO;
import com.openshift.internal.client.response.CartridgeResourceDTO;
import com.openshift.internal.client.response.EnvironmentVariableResourceDTO;
import com.openshift.internal.client.response.GearGroupResourceDTO;
import com.openshift.internal.client.response.Link;
import com.openshift.internal.client.ssh.ApplicationPortForwarding;
import com.openshift.internal.client.utils.Assert;
import com.openshift.internal.client.utils.IOpenShiftJsonConstants;
import com.openshift.internal.client.utils.StringUtils;

/**
 * The ApplicationResource object is an implementation of com.openshift.client.IApplication, and provides 
 * a runtime model for the real application that resides on the OpenShift platform being accessed.
 * 
 * @author André Dietisheim
 * @author Syed Iqbal
 * @author Martes G Wigglesworth
 */
public class ApplicationResource extends AbstractOpenShiftResource implements IApplication {

	private static final long APPLICATION_WAIT_RETRY_DELAY = 2 * 1024;
	private static final Logger LOGGER = LoggerFactory.getLogger(ApplicationResource.class);
	
	private static final String LINK_DELETE_APPLICATION = "DELETE";
	private static final String LINK_START_APPLICATION = "START";
	private static final String LINK_STOP_APPLICATION = "STOP";
	private static final String LINK_FORCE_STOP_APPLICATION = "FORCE_STOP";
	private static final String LINK_RESTART_APPLICATION = "RESTART";
	private static final String LINK_SCALE_UP = "SCALE_UP";
	private static final String LINK_SCALE_DOWN = "SCALE_DOWN";
	private static final String LINK_ADD_ALIAS = "ADD_ALIAS";
	private static final String LINK_REMOVE_ALIAS = "REMOVE_ALIAS";
	private static final String LINK_ADD_CARTRIDGE = "ADD_CARTRIDGE";
	private static final String LINK_LIST_CARTRIDGES = "LIST_CARTRIDGES";
	private static final String LINK_GET_GEAR_GROUPS = "GET_GEAR_GROUPS";
    private static final String LINK_LIST_ENVIRONMENT_VARIABLES = "LIST_ENVIRONMENT_VARIABLES";
    private static final String LINK_SET_UNSET_ENVIRONMENT_VARIABLES = "SET_UNSET_ENVIRONMENT_VARIABLES";
    private static final Pattern REGEX_FORWARDED_PORT = Pattern.compile("([^ ]+) -> ([^:]+):(\\d+)");
	
	/** The (unique) uuid of this application. */
	private String uuid;

	/** The name of this application. */
	private String name;

	/** The time at which this application was created. */
	private Date creationTime;

	/** The cartridge (application type/framework) of this application. */
	private IStandaloneCartridge cartridge;

	/** The scalability enablement. */
	private ApplicationScale scale;

	/** The application gear profile. */
	private IGearProfile gearProfile;

	/** The domain this application belongs to. */
	private final DomainResource domain;

	/** The url of this application. */
	private String applicationUrl;

	/** The url to use to connect with ssh.*/
	private String sshUrl;
	
	/** The url at which the git repo of this application may be reached. */
	private String gitUrl;

	/** the git url for the initial code and configuration for the application */
	private String initialGitUrl;
	
	/** The aliases of this application. */
	private List<String> aliases;

	/**
	 * Map of configured embedded cartridges. 
	 */
	private Map<String, EmbeddedCartridgeResource> embeddedCartridgesByName = new LinkedHashMap<String, EmbeddedCartridgeResource>();

	/**
	 * SSH Fowardable ports for the current application.
	 */
	private List<IApplicationPortForwarding> ports = null;

	/**
	 * SSH Session used to perform port-forwarding and other ssh-based
	 * operations.
	 */
	private Session session;
	
	private Collection<IGearGroup> gearGroups;
	/**
	 * The environment variables for this application
	 */
	private Map<String, IEnvironmentVariable> environmentVariablesMap;


	protected ApplicationResource(ApplicationResourceDTO dto, DomainResource domain) {
		this(dto.getName(), dto.getUuid(), dto.getCreationTime(), dto.getMessages(), dto.getApplicationUrl(),
				dto.getSshUrl(), dto.getGitUrl(), dto.getInitialGitUrl(), dto.getGearProfile(), dto.getApplicationScale(), 
				dto.getAliases(), dto.getCartridges(), dto.getLinks(), domain);
	}

	/**
	 * Instantiates a new application.
	 * 
	 * @param name
	 *            the name
	 * @param uuid
	 *            the uuid
	 * @param creationTime
	 *            the creation time
	 * @param messages
	 *            the creation log
	 * @param applicationUrl
	 *            the application url
	 * @param gitUrl
	 *            the git url
	 * @param sshUrl
	 *            the ssh url
	 * @param cartridge
	 *            the cartridge (type/framework)
	 * @param aliases
	 *            the aliases
	 * @param links
	 *            the links
	 * @param domain
	 *            the domain this application belongs to
	 * @throws DatatypeConfigurationException
	 */
	protected ApplicationResource(final String name, final String uuid, final String creationTime,
			final Messages messages, final String applicationUrl, final String sshUrl, final String gitUrl, 
			final String initialGitUrl, final IGearProfile gearProfile, final ApplicationScale scale, final List<String> aliases,
			final Map<String, CartridgeResourceDTO> cartridgesByName, final Map<String, Link> links,
			final DomainResource domain) {
		super(domain.getService(), links, messages);
		this.name = name;
		this.uuid = uuid;
		this.creationTime = RFC822DateUtils.safeGetDate(creationTime);
		this.scale = scale;
		this.gearProfile = gearProfile;
		this.applicationUrl = applicationUrl;
		this.sshUrl = sshUrl;
		this.gitUrl = gitUrl;
		this.initialGitUrl = initialGitUrl;
		this.domain = domain;
		this.aliases = aliases;
		updateCartridges(cartridgesByName);
		environmentVariablesMap = new HashMap<String, IEnvironmentVariable>();
	}

	public String getName() {
		return name;
	}

	@Override
	public ApplicationScale getApplicationScale() {
		return scale;
	}

	@Override
	public IGearProfile getGearProfile() {
		return gearProfile;
	}

	public String getUUID() {
		return uuid;
	}

	public IStandaloneCartridge getCartridge() {
		return cartridge;
	}

	/**
	 * Returns the main (standalone) cartrige or one of the embedded cartridges
	 * whose name matches the given param.
	 * @param cartridgeName the name of the cartridge to look for.
	 * @return the cartridge or null if none has this name.
	 */
	protected ICartridge getCartridge(String cartridgeName) {
		if(cartridgeName == null) {
			return null;
		}
		if(this.cartridge != null && cartridgeName.equals(this.cartridge.getName())) {
			return this.cartridge;
		}
		return getEmbeddedCartridge(cartridgeName);
	}


	public Date getCreationTime() {
		return creationTime;
	}

	public IDomain getDomain() {
		return this.domain;
	}

	public void destroy() throws OpenShiftException {
		new DeleteApplicationRequest().execute();
		domain.removeApplication(this);
	}

	public void start() throws OpenShiftException {
		new StartApplicationRequest().execute();
	}

	public void restart() throws OpenShiftException {
		new RestartApplicationRequest().execute();
	}

	public void stop() throws OpenShiftException {
		stop(false);
	}

	public void stop(boolean force) throws OpenShiftException {
		if (force) {
			new ForceStopApplicationRequest().execute();
		} else {
			new StopApplicationRequest().execute();
		}
	}

	public void getDescriptor() {
		throw new UnsupportedOperationException();
	}

	public void scaleDown() throws OpenShiftException {
		new ScaleDownRequest().execute();
	}

	public void scaleUp() throws OpenShiftException {
		new ScaleUpRequest().execute();
	}

	public void addAlias(String alias) throws OpenShiftException {
		Assert.notNull(alias);

		ApplicationResourceDTO applicationDTO = new AddAliasRequest().execute(alias);
		updateAliases(applicationDTO);

	}

	private void updateAliases(ApplicationResourceDTO applicationDTO) {
		Assert.notNull(applicationDTO);

		this.aliases.clear();
		this.aliases.addAll(applicationDTO.getAliases());
	}

	public List<String> getAliases() {
		return Collections.unmodifiableList(this.aliases);
	}

	public boolean hasAlias(String name) {
		Assert.notNull(name);

		return aliases.contains(name);
	}

	public void removeAlias(String alias) throws OpenShiftException {
		Assert.notNull(alias);

		ApplicationResourceDTO applicationDTO = new RemoveAliasRequest().execute(alias);
		updateAliases(applicationDTO);
	}

	public String getGitUrl() {
		return gitUrl;
	}

	public String getInitialGitUrl() {
		return initialGitUrl;
	}

	/**
	 * @return the sshUrl
	 */
	public String getSshUrl() {
		return sshUrl;
	}

	public String getApplicationUrl() {
		return applicationUrl;
	}

	/**
	 * Adds the given embedded cartridge to this application.
	 * 
	 * @param cartridge
	 *            the embeddable cartridge that shall be added to this
	 *            application
	 */
	public IEmbeddedCartridge addEmbeddableCartridge(IEmbeddableCartridge cartridge) throws OpenShiftException {
		Assert.notNull(cartridge);

		final CartridgeResourceDTO embeddedCartridgeDTO =
				new AddEmbeddedCartridgeRequest().execute(cartridge);
		final EmbeddedCartridgeResource embeddedCartridge = new EmbeddedCartridgeResource(embeddedCartridgeDTO, this);
		this.embeddedCartridgesByName.put(embeddedCartridge.getName(), embeddedCartridge);
		return embeddedCartridge;
	}

	public List<IEmbeddedCartridge> addEmbeddableCartridges(Collection<IEmbeddableCartridge> cartridges)
			throws OpenShiftException {
		Assert.notNull(cartridges);

		final List<IEmbeddedCartridge> addedCartridge = new ArrayList<IEmbeddedCartridge>();
		for (IEmbeddableCartridge cartridge : cartridges) {
			// TODO: catch exceptions when removing cartridges, contine removing
			// and report the exceptions that occurred
			addedCartridge.add(addEmbeddableCartridge(cartridge));
		}
		return addedCartridge;
	}

	/**
	 * "callback" from the embeddedCartridge once it has been destroyed.
	 * 
	 * @param embeddedCartridge
	 * @throws OpenShiftException
	 */
	protected void removeEmbeddedCartridge(IEmbeddedCartridge embeddedCartridge) throws OpenShiftException {
		Assert.notNull(embeddedCartridge);

		this.embeddedCartridgesByName.remove(embeddedCartridge.getName());
	}

	/**
	 * Queries the backend to list the embedded cartridges and adds the new ones
	 * & update the ones that are already present
	 * 
	 * @throws OpenShiftException
	 */
	protected void refreshEmbeddedCartridges() throws OpenShiftException {
		// load collection if necessary
		Map<String, CartridgeResourceDTO> cartridgeDTOByName = new ListCartridgesRequest().execute();
		updateCartridges(cartridgeDTOByName);
		removeCartridges(cartridgeDTOByName);
	}

	private void updateCartridges(Map<String, CartridgeResourceDTO> cartridgeDTOByName) {
		for (CartridgeResourceDTO cartridgeDTO : cartridgeDTOByName.values()) {
			switch(cartridgeDTO.getType()) {
			case STANDALONE:
				createStandaloneCartrdige(cartridgeDTO);
				break;
			case EMBEDDED:
				addOrUpdateEmbeddedCartridge(cartridgeDTO.getName(), cartridgeDTO);
			}
		}
	}

	private void createStandaloneCartrdige(CartridgeResourceDTO cartridgeDTO) {
		this.cartridge = new StandaloneCartridge(
				cartridgeDTO.getName(), 
				cartridgeDTO.getUrl(), 
				cartridgeDTO.getDisplayName(),
				cartridgeDTO.getDescription());
	}

	private void addOrUpdateEmbeddedCartridge(String name, CartridgeResourceDTO cartridgeDTO) {
		EmbeddedCartridgeResource embeddedCartridge = embeddedCartridgesByName.get(name);
		if (embeddedCartridge != null) {
			embeddedCartridge.update(cartridgeDTO);
		} else {
			embeddedCartridgesByName.put(name, new EmbeddedCartridgeResource(cartridgeDTO, this));
		}
	}

	private void removeCartridges(Map<String, CartridgeResourceDTO> cartridgeDTOsByName) {
		List<EmbeddedCartridgeResource> cartridges = new ArrayList<EmbeddedCartridgeResource>(embeddedCartridgesByName.values());
		for (EmbeddedCartridgeResource cartridge : cartridges) {
			String name = cartridge.getName();
			if (!cartridgeDTOsByName.containsKey(name)) {
				// not present in updated collection
				embeddedCartridgesByName.remove(name);
			}
		}
	}
	
	public List<IEmbeddedCartridge> getEmbeddedCartridges() throws OpenShiftException {
		return Collections.unmodifiableList(new ArrayList<IEmbeddedCartridge>(this.embeddedCartridgesByName.values()));
	}

	public boolean hasEmbeddedCartridge(String cartridgeName) throws OpenShiftException {
		Assert.notNull(cartridgeName);

		return getEmbeddedCartridge(cartridgeName) != null;
	}

	public boolean hasEmbeddedCartridge(IEmbeddableCartridge cartridge) throws OpenShiftException {
		return getEmbeddedCartridge(cartridge) != null;
	}

	public IEmbeddedCartridge getEmbeddedCartridge(IEmbeddableCartridge cartridge) throws OpenShiftException {
		Assert.notNull(cartridge);

		for (IEmbeddedCartridge embeddedCartridge : getEmbeddedCartridges()) {
			if (cartridge.equals(embeddedCartridge)) {
				return embeddedCartridge;
			}
		}
		return null;
	}

	public IEmbeddedCartridge getEmbeddedCartridge(String cartridgeName) throws OpenShiftException {
		Assert.notNull(cartridgeName);

		for (IEmbeddedCartridge embeddedCartridge : getEmbeddedCartridges()) {
			if (cartridgeName.equals(embeddedCartridge.getName())) {
				return embeddedCartridge;
			}
		}
		return null;
	}
		
	public void removeEmbeddedCartridge(IEmbeddableCartridge cartridge) throws OpenShiftException {
		Assert.notNull(cartridge);

		IEmbeddedCartridge embeddedCartridge = getEmbeddedCartridge(cartridge);
		if (embeddedCartridge != null) {
			embeddedCartridge.destroy();
		}
	}

	public void removeEmbeddedCartridges(Collection<IEmbeddableCartridge> cartridges) throws OpenShiftException {
		Assert.notNull(cartridges);

		for(IEmbeddableCartridge cartridge : cartridges) {
			// TODO: catch exceptions when removing cartridges, contine removing
			// and report the exceptions that occurred
			removeEmbeddedCartridge(cartridge);
		}
	}

	public Collection<IGearGroup> getGearGroups() throws OpenShiftException {
		// this collection is not cached so we always have the latest info 
		// about the gear groups consumed by this application.
		loadGearGroups();
		return gearGroups;
	}

	private Collection<IGearGroup> loadGearGroups() throws OpenShiftException {
		List<IGearGroup> gearGroups = new ArrayList<IGearGroup>();
		Collection<GearGroupResourceDTO> dtos = new GetGearGroupsRequest().execute(); 
		for(GearGroupResourceDTO dto : dtos) {
			gearGroups.add(new GearGroupResource(dto, this, getService()));
		}
		
		return this.gearGroups = gearGroups;
	}
	
	public boolean waitForAccessible(long timeout) throws OpenShiftException {
		try {
			return waitForResolved(timeout, System.currentTimeMillis());
		} catch (InterruptedException e) {
			return false;
		}
	}

	public Future<Boolean> waitForAccessibleAsync(final long timeout) throws OpenShiftException {
		IOpenShiftConnection connection = getConnection();
		return connection.getExecutorService().submit(new Callable<Boolean>() {

			public Boolean call() throws Exception {
				return waitForAccessible(timeout);
			}
		});
	}
	
	protected IOpenShiftConnection getConnection() {
		return getDomain().getUser().getConnection();
	}

	private boolean waitForResolved(long timeout, long startTime) throws OpenShiftException, InterruptedException {
		try {
			while (!canResolv(applicationUrl)
					&& !isTimeouted(timeout, startTime)) {
				Thread.sleep(APPLICATION_WAIT_RETRY_DELAY);
			}
			return canResolv(applicationUrl);
		} catch (MalformedURLException e) {
			throw new OpenShiftException(e,
					"Could not wait for application {0} to become accessible, it has an invalid URL \"{1}\": {2}",
					name, applicationUrl, e.getMessage());
		}
	}

	protected boolean canResolv(String url) throws MalformedURLException {
		return HostUtils.canResolv(url);
	}
	
	private boolean isTimeouted(long timeout, long startTime) {
		return !(System.currentTimeMillis() < (startTime + timeout));
	}

	@Override
	public void refresh() throws OpenShiftException {
		refreshEmbeddedCartridges();
		if (this.gearGroups != null) {
			this.gearGroups = loadGearGroups();
		}
		if (this.ports != null) {
			this.ports = loadPorts();
		}
		updateEnvironmentVariables();
	}

	public void setSSHSession(final Session session) {
		this.session = session;
	}

	public Session getSSHSession() {
		return this.session;
	}

	public boolean hasSSHSession() {
		return this.session != null && this.session.isConnected();
	}

	public boolean isPortFowardingStarted() throws OpenShiftSSHOperationException {
		try {
			return this.session != null && this.session.isConnected() && this.session.getPortForwardingL().length > 0;
		} catch (JSchException e) {
			throw new OpenShiftSSHOperationException(e,
					"Unable to verify if port-forwarding has been started for application \"{0}\"", this.getName());
		}
	}

	public List<IApplicationPortForwarding> refreshForwardablePorts() throws OpenShiftSSHOperationException {
		this.ports = loadPorts();
		return this.ports;
	}

	public List<IApplicationPortForwarding> getForwardablePorts() throws OpenShiftSSHOperationException {
		if (ports == null) {
			this.ports = loadPorts();
		}
		return ports;
	}

	@Override
	public List<String> getEnvironmentProperties() throws OpenShiftSSHOperationException {
		List<String> openshiftProps = new ArrayList<String>();
		List<String> allEnvProps = sshExecCmd("set", SshStreams.INPUT);
		for (String line : allEnvProps) {
			openshiftProps.add(line);
		}
		return openshiftProps;
	}
	
	@Override
	public Map<String, IEnvironmentVariable> getEnvironmentVariables() throws OpenShiftException {
		return Collections.unmodifiableMap(new LinkedHashMap<String, IEnvironmentVariable>(getOrLoadEnvironmentVariables()));
	}

	
  protected Map<String, IEnvironmentVariable> getOrLoadEnvironmentVariables() throws OpenShiftException {
	if(environmentVariablesMap.isEmpty())
	   environmentVariablesMap = loadEnvironmentVariables();		
	return environmentVariablesMap;
	}
	
	private Map<String, IEnvironmentVariable> loadEnvironmentVariables() throws OpenShiftException {
		List<EnvironmentVariableResourceDTO> environmentVariableDTOs = new ListEnvironmentVariablesRequest().execute();
		if (environmentVariableDTOs == null) {
			return new LinkedHashMap<String, IEnvironmentVariable>();
		}

		for (EnvironmentVariableResourceDTO environmentVariableResourceDTO : environmentVariableDTOs) {
			final IEnvironmentVariable environmentVariable = 
					new EnvironmentVariableResource(environmentVariableResourceDTO, this);
			
			environmentVariablesMap.put(environmentVariable.getName(),environmentVariable);
			
		}
		return environmentVariablesMap;
	}

	@Override
	public IEnvironmentVariable addEnvironmentVariable(String name, String value) throws OpenShiftException {
		if (name == null) {
			throw new OpenShiftException("Environment variable name is mandatory but none was given.");
		}
		if (value == null) {
			throw new OpenShiftException("Value for environment variable \"{0}\" not given.", name);
		}
		if (hasEnvironmentVariable(name)) {
			throw new OpenShiftException("Environment variable with name \"{0}\" already exists.", name);
		}		
		
		EnvironmentVariableResourceDTO environmentVariableResourceDTO =
				new AddEnvironmentVariableRequest().execute(name, value);		
		IEnvironmentVariable environmentVariable = new EnvironmentVariableResource(environmentVariableResourceDTO, this);
		
		environmentVariablesMap.put(environmentVariable.getName(), environmentVariable);
		
		return environmentVariable;
	}

	@Override
	public Map<String, IEnvironmentVariable> addEnvironmentVariables(Map<String, String> environmentVariables)
			throws OpenShiftException {
	  
	  Map<String,String>variablesCandidateMap = new HashMap<String,String>();
	  for(String varCandidateName:environmentVariables.keySet()){
	    IEnvironmentVariable tempVar = environmentVariablesMap.get(varCandidateName);
	    if(tempVar != null)
	    {  if(tempVar.getValue() == environmentVariables.get(varCandidateName))
	        variablesCandidateMap.put(varCandidateName,environmentVariables.get(varCandidateName));
	    }
	    else
	        variablesCandidateMap.put(varCandidateName, environmentVariables.get(varCandidateName));
	  }
	  List<EnvironmentVariableResourceDTO> environmentVariableResourceDTOs = new AddEnvironmentVariablesRequest()
				.execute(variablesCandidateMap);
		
		for (EnvironmentVariableResourceDTO dto : environmentVariableResourceDTOs) {
			IEnvironmentVariable environmentVariable = new EnvironmentVariableResource(dto, this);
			environmentVariablesMap.put(environmentVariable.getName(), environmentVariable);
		}
		
		return environmentVariablesMap;
	}
    /*
     * (non-Javadoc)
     * @see com.openshift.client.IApplication#removeEnvironmentVariable(java.lang.String)
     */
	@Override
	public void removeEnvironmentVariable(String targetName) {
		removeEnvironmentVariable(getEnvironmentVariable(targetName));		
	}
	
	/* (non-Javadoc)
	 * @see com.openshift.client.IApplication#removeEnvironmentVariable(com.openshift.client.IEnvironmentVariable)
	 */
	@Override
	public void removeEnvironmentVariable(IEnvironmentVariable environmentVariable){      
      if(getEnvironmentVariable(environmentVariable.getName()) == null)
        throw new OpenShiftException("IEnvironmentVariable with supplied name does not exist.");
      environmentVariable.destroy();
      environmentVariablesMap.remove(environmentVariable.getName());
     
    }
	
	
	/*
	 * (non-Javadoc)
	 * @see com.openshift.client.IApplication#hasEnvironmentVariable(java.lang.String)
	 */
    @Override
	public boolean hasEnvironmentVariable(String name) throws OpenShiftException {
		if (StringUtils.isEmpty(name)) {
			throw new OpenShiftException("Environment variable name is mandatory but none was given.");
		}
		return getEnvironmentVariable(name) != null;

	}
    
	protected void updateEnvironmentVariables() throws OpenShiftException {
		if (!canGetEnvironmentVariables()) 
			return;
		else
		{
		  environmentVariablesMap.clear();
		  environmentVariablesMap = loadEnvironmentVariables();
		}

	}
    
	/*
	 * (non-Javadoc)
	 * @see com.openshift.client.IApplication#getEnvironmentVariable(java.lang.String)
	 */
	@Override
	public IEnvironmentVariable getEnvironmentVariable(String name) {
		return getEnvironmentVariables().get(name);
	}
    
	@Override
	public boolean canGetEnvironmentVariables() {
		try {
			return getLink(LINK_LIST_ENVIRONMENT_VARIABLES) != null;
		} catch (OpenShiftException e) {
			return false;
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.openshift.client.IApplication#canUpdateEnvironmentVariables()
	 */
	@Override
	public boolean canUpdateEnvironmentVariables() {
		try {
			return getLink(LINK_SET_UNSET_ENVIRONMENT_VARIABLES) != null;
		} catch (OpenShiftException e) {
			return false;
		}
	}    
  
	/**
	 * List all forwardable ports for a given application.
	 * 
	 * @param application
	 * @return the forwardable ports in an unmodifiable collection
	 * @throws JSchException
	 * @throws OpenShiftSSHOperationException
	 */
	private List<IApplicationPortForwarding> loadPorts() throws OpenShiftSSHOperationException {
		this.ports = new ArrayList<IApplicationPortForwarding>();
		List<String> lines = sshExecCmd("rhc-list-ports", SshStreams.EXT_INPUT);
		for (String line : lines) {
			ApplicationPortForwarding port = extractForwardablePortFrom(line);
			if (port != null) {
				ports.add(port);
			}
		}
		return ports;
	}

	/**
	 * 
	 * @param command
	 * @return
	 * @throws OpenShiftSSHOperationException
	 */
	protected List<String> sshExecCmd(final String command, final SshStreams sshStream)
			throws OpenShiftSSHOperationException {
		final Session session = getSSHSession();
		if (session == null) {
			throw new OpenShiftSSHOperationException("No SSH session available for application ''{0}''", this.getName());
		}
		Channel channel = null;
		BufferedReader reader = null;
		try {
			session.openChannel("exec");
			channel = session.openChannel("exec");
			((ChannelExec) channel).setCommand(command);
			channel.connect();
			return sshStream.getLines(channel);
		} catch (JSchException e) {
			throw new OpenShiftSSHOperationException(e, "Failed to list forwardable ports for application \"{0}\"",
					this.getName());
		} catch (IOException e) {
			throw new OpenShiftSSHOperationException(e, "Failed to list forwardable ports for application \"{0}\"",
					this.getName());
		} finally {

			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
					LOGGER.error("Failed to close SSH error stream reader", e);
				}
			}

			if (channel != null && channel.isConnected()) {
				channel.disconnect();
			}
		}
	}

	/**
	 * Extract the named forwardable port from the 'rhc-list-ports' command
	 * result line, with the following format:
	 * <code>java -> 127.10.187.1:4447</code>.
	 * 
	 * @param portValue
	 * @return the forwardable port.
	 */
	private ApplicationPortForwarding extractForwardablePortFrom(final String portValue) {
		Matcher matcher = REGEX_FORWARDED_PORT.matcher(portValue);
		if (!matcher.find()
				|| matcher.groupCount() != 3) {
			return null;
		}
		try {
			final String name = matcher.group(1);
			final String host = matcher.group(2);
			final int remotePort = Integer.parseInt(matcher.group(3));
			return new ApplicationPortForwarding(this, name, host, remotePort);
		} catch(NumberFormatException e) {
			throw new OpenShiftSSHOperationException(e,
					"Couild not determine forwarded port in application {0}", getName());
		}
	}

	public List<IApplicationPortForwarding> startPortForwarding() throws OpenShiftSSHOperationException {
		if (!hasSSHSession()) {
			throw new OpenShiftSSHOperationException(
					"SSH session for application \"{0}\" is closed or null. Cannot start port forwarding",
					getName());
		}
		for (IApplicationPortForwarding port : ports) {
			try {
				port.start(session);
			} catch (OpenShiftSSHOperationException oss) {
				/*
				 * ignore for now
				 * FIXME: should store this error on the forward to let user 
				 * know why it could not start/stop
				 */
			}
		}
		return ports;
	}

	public List<IApplicationPortForwarding> stopPortForwarding() throws OpenShiftSSHOperationException {
		for (IApplicationPortForwarding port : ports) {
			try {
				port.stop(session);
			} catch (OpenShiftSSHOperationException oss) {
				/* ignore for now
				 *  should store this error on the forward to let user know why
				 *  it could not start/stop
				 */
			}
		}
		// make sure port forwarding is stopped by closing session...
		session.disconnect();
		return ports;
	}

	protected void update(ApplicationResourceDTO dto) {
		this.name = dto.getName();
		this.uuid = dto.getUuid();
		this.creationTime = RFC822DateUtils.safeGetDate(dto.getCreationTime());
		this.scale = dto.getApplicationScale();
		this.gearProfile = dto.getGearProfile();
		this.applicationUrl = dto.getApplicationUrl();
		this.sshUrl = dto.getSshUrl();
		this.gitUrl = dto.getGitUrl();
		this.initialGitUrl = dto.getInitialGitUrl();
		this.aliases = dto.getAliases();
		updateCartridges(dto.getCartridges());
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((uuid == null) ? 0 : uuid.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object object) {
		if (this == object)
			return true;
		if (object == null)
			return false;
		if (getClass() != object.getClass())
			return false;
		ApplicationResource other = (ApplicationResource) object;
		if (uuid == null) {
			if (other.uuid != null)
				return false;
		} else if (!uuid.equals(other.uuid))
			return false;
		return true;
	}


	@Override
	public String toString() {
		return "ApplicationResource ["
				+ "uuid=" + uuid
				+ ", name=" + name
				+ ", creationTime=" + creationTime
				+ ", cartridge=" + cartridge
				+ ", scale=" + scale
				+ ", gearProfile=" + gearProfile
				+ ", domain=" + domain
				+ ", applicationUrl=" + applicationUrl
				+ ", gitUrl=" + gitUrl
				+ ", initialGitUrl=" + initialGitUrl
				+ ", aliases=" + aliases
				+ ", gearGroups=" + gearGroups
				+ "]";
	}

	protected enum SshStreams {
		EXT_INPUT {
			protected InputStream getInputStream(Channel channel) throws IOException {
				return channel.getExtInputStream(); 
			}

		}, INPUT {
			protected InputStream getInputStream(Channel channel) throws IOException {
				return channel.getInputStream(); 
			}
		};
		
		public List<String> getLines(Channel channel) throws IOException {
			BufferedReader reader = new BufferedReader(new InputStreamReader(getInputStream(channel)));
			List<String> lines = new ArrayList<String>();
			String line = null;
			// Read File Line By Line
			while ((line = reader.readLine()) != null) {
				lines.add(line);
			}
			return lines;
		}
		
		protected abstract InputStream getInputStream(Channel channel) throws IOException;

	}
	
	private class DeleteApplicationRequest extends ServiceRequest {

		private DeleteApplicationRequest() {
			super(LINK_DELETE_APPLICATION);
		}
	}

	private class StartApplicationRequest extends ServiceRequest {

		private StartApplicationRequest() {
			super(LINK_START_APPLICATION);
		}

		protected <DTO> DTO execute() throws OpenShiftException {
			return super.execute(
					new StringParameter(IOpenShiftJsonConstants.PROPERTY_EVENT, IOpenShiftJsonConstants.VALUE_START));
		}
	}

	private class StopApplicationRequest extends ServiceRequest {

		private StopApplicationRequest() {
			super(LINK_STOP_APPLICATION);
		}

		protected <DTO> DTO execute() throws OpenShiftException {
			return super.execute(
					new StringParameter(IOpenShiftJsonConstants.PROPERTY_EVENT, IOpenShiftJsonConstants.VALUE_STOP));
		}
	}

	private class ForceStopApplicationRequest extends ServiceRequest {

		private ForceStopApplicationRequest() {
			super(LINK_FORCE_STOP_APPLICATION);
		}

		protected <DTO> DTO execute() throws OpenShiftException {
			return super.execute(
					new StringParameter(IOpenShiftJsonConstants.PROPERTY_EVENT, IOpenShiftJsonConstants.VALUE_FORCESTOP));
		}
	}

	private class RestartApplicationRequest extends ServiceRequest {

		private RestartApplicationRequest() {
			super(LINK_RESTART_APPLICATION);
		}

		protected <DTO> DTO execute() throws OpenShiftException {
			return super.execute(new StringParameter(IOpenShiftJsonConstants.PROPERTY_EVENT,
					IOpenShiftJsonConstants.VALUE_RESTART));
		}
	}

	private class ScaleUpRequest extends ServiceRequest {

		private ScaleUpRequest() {
			super(LINK_SCALE_UP);
		}

		protected <DTO> DTO execute() throws OpenShiftException {
			return super.execute(new StringParameter(IOpenShiftJsonConstants.PROPERTY_EVENT,
					IOpenShiftJsonConstants.VALUE_SCALE_UP));
		}
	}

	private class ScaleDownRequest extends ServiceRequest {

		private ScaleDownRequest() {
			super(LINK_SCALE_DOWN);
		}

		protected <DTO> DTO execute() throws OpenShiftException {
			return super.execute(new StringParameter(IOpenShiftJsonConstants.PROPERTY_EVENT,
					IOpenShiftJsonConstants.VALUE_SCALE_DOWN));
		}
	}

	private class AddAliasRequest extends ServiceRequest {

		private AddAliasRequest() {
			super(LINK_ADD_ALIAS);
		}

		protected <DTO> DTO execute(String alias) throws OpenShiftException {
			return super.execute(
					new StringParameter(IOpenShiftJsonConstants.PROPERTY_EVENT, IOpenShiftJsonConstants.VALUE_ADD_ALIAS), 
					new StringParameter(IOpenShiftJsonConstants.PROPERTY_ALIAS, alias));
		}
	}

	private class RemoveAliasRequest extends ServiceRequest {

		private RemoveAliasRequest() {
			super(LINK_REMOVE_ALIAS);
		}

		protected <DTO> DTO execute(String alias) throws OpenShiftException {
			return super.execute(
					new StringParameter(IOpenShiftJsonConstants.PROPERTY_EVENT, IOpenShiftJsonConstants.VALUE_REMOVE_ALIAS), 
					new StringParameter(IOpenShiftJsonConstants.PROPERTY_ALIAS, alias));
		}
	}

	private class AddEmbeddedCartridgeRequest extends ServiceRequest {

		private AddEmbeddedCartridgeRequest() {
			super(LINK_ADD_CARTRIDGE);
		}

		protected <DTO> DTO execute(IEmbeddableCartridge embeddable) throws OpenShiftException {
			return super.execute(new Parameters().addCartridge(embeddable).toArray());
		}
	}

	private class ListCartridgesRequest extends ServiceRequest {

		private ListCartridgesRequest() {
			super(LINK_LIST_CARTRIDGES);
		}

		protected Map<String, CartridgeResourceDTO> execute() throws OpenShiftException {
			return super.execute();
		}
	}
	
	private class GetGearGroupsRequest extends ServiceRequest {

		private GetGearGroupsRequest() {
			super(LINK_GET_GEAR_GROUPS);
		}
	}
	
	private class ListEnvironmentVariablesRequest extends ServiceRequest {
		protected ListEnvironmentVariablesRequest() {
			super(LINK_LIST_ENVIRONMENT_VARIABLES);
		}
	}

	private class AddEnvironmentVariableRequest extends ServiceRequest {
		protected AddEnvironmentVariableRequest() {
			super(LINK_SET_UNSET_ENVIRONMENT_VARIABLES);
		}

		protected EnvironmentVariableResourceDTO execute(String name, String value) throws OpenShiftException {
			Parameters parameters = new Parameters()
					.add(IOpenShiftJsonConstants.PROPERTY_NAME, name)
					.add(IOpenShiftJsonConstants.PROPERTY_VALUE, value);
			return super.execute(parameters.toArray());
		}
	}
	
	private class AddEnvironmentVariablesRequest extends ServiceRequest {
		protected AddEnvironmentVariablesRequest() {
			super(LINK_SET_UNSET_ENVIRONMENT_VARIABLES);
		}

		protected List<EnvironmentVariableResourceDTO> execute(Map<String, String> environmentVariables)
				throws OpenShiftException {
			Parameters parameters = new Parameters()
					.addEnvironmentVariables(environmentVariables);
			return super.execute(parameters.toArray());
		}
	}

 }
