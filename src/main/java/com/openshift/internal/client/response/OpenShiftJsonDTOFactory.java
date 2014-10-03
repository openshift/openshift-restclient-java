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
package com.openshift.internal.client.response;

import static com.openshift.internal.client.utils.IOpenShiftJsonConstants.PROPERTY_ALIASES;
import static com.openshift.internal.client.utils.IOpenShiftJsonConstants.PROPERTY_APP_URL;
import static com.openshift.internal.client.utils.IOpenShiftJsonConstants.PROPERTY_CARTRIDGES;
import static com.openshift.internal.client.utils.IOpenShiftJsonConstants.PROPERTY_CONSUMED_GEARS;
import static com.openshift.internal.client.utils.IOpenShiftJsonConstants.PROPERTY_CREATION_TIME;
import static com.openshift.internal.client.utils.IOpenShiftJsonConstants.PROPERTY_DATA;
import static com.openshift.internal.client.utils.IOpenShiftJsonConstants.PROPERTY_DEPLOYMENT_TYPE;
import static com.openshift.internal.client.utils.IOpenShiftJsonConstants.PROPERTY_DESCRIPTION;
import static com.openshift.internal.client.utils.IOpenShiftJsonConstants.PROPERTY_DISPLAY_NAME;
import static com.openshift.internal.client.utils.IOpenShiftJsonConstants.PROPERTY_DOMAIN_ID;
import static com.openshift.internal.client.utils.IOpenShiftJsonConstants.PROPERTY_EXPIRES_IN;
import static com.openshift.internal.client.utils.IOpenShiftJsonConstants.PROPERTY_FRAMEWORK;
import static com.openshift.internal.client.utils.IOpenShiftJsonConstants.PROPERTY_GEARS;
import static com.openshift.internal.client.utils.IOpenShiftJsonConstants.PROPERTY_GEAR_PROFILE;
import static com.openshift.internal.client.utils.IOpenShiftJsonConstants.PROPERTY_GEAR_STATE;
import static com.openshift.internal.client.utils.IOpenShiftJsonConstants.PROPERTY_GIT_URL;
import static com.openshift.internal.client.utils.IOpenShiftJsonConstants.PROPERTY_HREF;
import static com.openshift.internal.client.utils.IOpenShiftJsonConstants.PROPERTY_ID;
import static com.openshift.internal.client.utils.IOpenShiftJsonConstants.PROPERTY_INITIAL_GIT_URL;
import static com.openshift.internal.client.utils.IOpenShiftJsonConstants.PROPERTY_LINKS;
import static com.openshift.internal.client.utils.IOpenShiftJsonConstants.PROPERTY_LOGIN;
import static com.openshift.internal.client.utils.IOpenShiftJsonConstants.PROPERTY_MAX_GEARS;
import static com.openshift.internal.client.utils.IOpenShiftJsonConstants.PROPERTY_METHOD;
import static com.openshift.internal.client.utils.IOpenShiftJsonConstants.PROPERTY_NAME;
import static com.openshift.internal.client.utils.IOpenShiftJsonConstants.PROPERTY_NOTE;
import static com.openshift.internal.client.utils.IOpenShiftJsonConstants.PROPERTY_OBSOLETE;
import static com.openshift.internal.client.utils.IOpenShiftJsonConstants.PROPERTY_OPTIONAL_PARAMS;
import static com.openshift.internal.client.utils.IOpenShiftJsonConstants.PROPERTY_PROPERTIES;
import static com.openshift.internal.client.utils.IOpenShiftJsonConstants.PROPERTY_REL;
import static com.openshift.internal.client.utils.IOpenShiftJsonConstants.PROPERTY_REQUIRED_PARAMS;
import static com.openshift.internal.client.utils.IOpenShiftJsonConstants.PROPERTY_SCALABLE;
import static com.openshift.internal.client.utils.IOpenShiftJsonConstants.PROPERTY_SCOPES;
import static com.openshift.internal.client.utils.IOpenShiftJsonConstants.PROPERTY_SSH_URL;
import static com.openshift.internal.client.utils.IOpenShiftJsonConstants.PROPERTY_SUFFIX;
import static com.openshift.internal.client.utils.IOpenShiftJsonConstants.PROPERTY_TOKEN;
import static com.openshift.internal.client.utils.IOpenShiftJsonConstants.PROPERTY_TYPE;
import static com.openshift.internal.client.utils.IOpenShiftJsonConstants.PROPERTY_URL;
import static com.openshift.internal.client.utils.IOpenShiftJsonConstants.PROPERTY_UUID;
import static com.openshift.internal.client.utils.IOpenShiftJsonConstants.PROPERTY_VALID_OPTIONS;
import static com.openshift.internal.client.utils.IOpenShiftJsonConstants.PROPERTY_VALUE;
import static com.openshift.internal.client.utils.IOpenShiftJsonConstants.PROPERTY_NOTE;
import static com.openshift.internal.client.utils.IOpenShiftJsonConstants.PROPERTY_SCOPES;
import static com.openshift.internal.client.utils.IOpenShiftJsonConstants.PROPERTY_TOKEN;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.jboss.dmr.ModelNode;
import org.jboss.dmr.ModelType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.openshift.client.ApplicationScale;
import com.openshift.client.HttpMethod;
import com.openshift.client.IGearProfile;
import com.openshift.client.Messages;
import com.openshift.client.OpenShiftException;
import com.openshift.client.OpenShiftRequestException;
import com.openshift.internal.client.GearProfile;
import com.openshift.internal.client.utils.IOpenShiftJsonConstants;
import com.openshift.internal.client.utils.StringUtils;

/**
 * A factory for creating ResourceDTO objects.
 * 
 * @author Xavier Coulon
 * @author Andre Dietisheim
 * @author Sean Kavanagh
 */
public class OpenShiftJsonDTOFactory extends AbstractJsonDTOFactory {

	private final Logger LOGGER = LoggerFactory.getLogger(OpenShiftJsonDTOFactory.class);

	@Override
	protected Object createData(EnumDataType dataType, Messages messages, ModelNode dataNode) {
		switch (dataType) {
		case user:
			return createUser(dataNode);
		case keys:
			return createKeys(dataNode);
		case key:
			return createKey(dataNode, messages);
		case links:
			return createLinks(dataNode);
		case domains:
			return createDomains(dataNode);
		case domain:
			return createDomain(dataNode, messages);
		case applications:
			return createApplications(dataNode);
		case application:
			return createApplication(dataNode, messages);
		case authorization:
			return createAuthorization(dataNode, messages);
		case gear_groups:
			return createGearGroups(dataNode);
		case cartridges:
			return createCartridges(dataNode);
		case cartridge:
			return createCartridge(dataNode, messages);
		case environment_variables:
			return createEnvironmentVariables(dataNode);
		case environment_variable:
			return createEnvironmentVariable(dataNode, messages);

		default:
			return null;
		}
	}

	/**
	 * Creates a new ResourceDTO object.
	 * 
	 * @param userNode
	 *            the root node
	 * @return the user resource dto
	 * @throws OpenShiftException
	 */
	private UserResourceDTO createUser(ModelNode userNode) throws OpenShiftException {
		if (!userNode.isDefined()) {
			return null;
		}
        	final String id = getAsString(userNode, PROPERTY_ID);
		final String rhlogin = getAsString(userNode, PROPERTY_LOGIN);
		final int maxGears = getAsInteger(userNode, PROPERTY_MAX_GEARS);
		final int consumedGears = getAsInteger(userNode, PROPERTY_CONSUMED_GEARS);
		final Map<String, Link> links = createLinks(userNode.get(PROPERTY_LINKS));
		return new UserResourceDTO(id, rhlogin, maxGears, consumedGears, links);
	}

    /**
     * Creates a new ResourceDTO object.
     *
     * @param dataNode
     *            the root node
     * @return the list< key resource dt o>
     * @throws OpenShiftException
     *             the open shift exception
     */
    private AuthorizationResourceDTO createAuthorization(ModelNode dataNode, Messages messages) throws OpenShiftException {

        final String id = getAsString(dataNode, PROPERTY_ID);
        final String note = getAsString(dataNode, PROPERTY_NOTE);
        final String scopes = getAsString(dataNode, PROPERTY_SCOPES);
        final String token = getAsString(dataNode, PROPERTY_TOKEN);
        final int expiresIn = getAsInteger(dataNode, PROPERTY_EXPIRES_IN);
        final Map<String, Link> links = createLinks(dataNode.get(PROPERTY_LINKS));
        return new AuthorizationResourceDTO(id, note, scopes, token, expiresIn, links, messages);
     }

	/**
	 * Creates a new ResourceDTO object.
	 * 
	 * @param dataNode
	 *            the root node
	 * @return the list< key resource dt o>
	 * @throws OpenShiftException
	 *             the open shift exception
	 */
	private List<KeyResourceDTO> createKeys(ModelNode dataNode) throws OpenShiftException {
		final List<KeyResourceDTO> keys = new ArrayList<KeyResourceDTO>();
		// temporarily supporting single and multiple values for 'keys' node
		for (ModelNode keyNode : dataNode.asList()) {
			if (keyNode.getType() == ModelType.OBJECT) {
				KeyResourceDTO dto = createKey(keyNode, null);
				if (dto != null) {
					keys.add(dto);
				}
			}
		}
		return keys;
	}

	/**
	 * Creates a new ResourceDTO object.
	 * 
	 * @param keyNode
	 *            the key node
	 * @return the key resource dto
	 * @throws OpenShiftException
	 */
	private KeyResourceDTO createKey(ModelNode keyNode, Messages messages) throws OpenShiftException {
		if (!keyNode.isDefined()) {
			return null;
		}
		final String name = getAsString(keyNode, IOpenShiftJsonConstants.PROPERTY_NAME);
		final String type = getAsString(keyNode, IOpenShiftJsonConstants.PROPERTY_TYPE);
		final String content = getAsString(keyNode, IOpenShiftJsonConstants.PROPERTY_CONTENT);
		final Map<String, Link> links = createLinks(keyNode.get(PROPERTY_LINKS));
		return new KeyResourceDTO(name, type, content, links, messages);
	}

	/**
	 * Creates a new set of indexed links.
	 * 
	 * @param linksNode
	 *            the root node
	 * @return the list< domain dt o>
	 * @throws OpenShiftException
	 *             the open shift exception
	 */
	private Map<String, Link> createLinks(final ModelNode linksNode) throws OpenShiftException {
		if (linksNode.has(PROPERTY_DATA)) {
			// loop inside 'data' node
			return createLinks(linksNode.get(PROPERTY_DATA));
		}
		Map<String, Link> links = new HashMap<String, Link>();
		if (linksNode.isDefined()) {
			for (ModelNode linkNode : linksNode.asList()) {
				final String linkName = linkNode.asProperty().getName();
				final ModelNode valueNode = linkNode.asProperty().getValue();
				if (valueNode.isDefined()) {
					Link link = createLink(valueNode);
					if(link != null){
						links.put(linkName, link);
					}
				}
			}
		}
		return links;
	}

	private Link createLink(final ModelNode valueNode) {
		final String method = valueNode.get(PROPERTY_METHOD).asString();
		if(!HttpMethod.hasValue(method)){
			return null;
		}
		final String rel = getAsString(valueNode, PROPERTY_REL);
		final String href = valueNode.get(PROPERTY_HREF).asString();
		final List<LinkParameter> requiredParams =
				createLinkParameters(valueNode.get(PROPERTY_REQUIRED_PARAMS));
		final List<LinkParameter> optionalParams =
				createLinkParameters(valueNode.get(PROPERTY_OPTIONAL_PARAMS));
		return new Link(rel, href, method, requiredParams, optionalParams);
	}

	/**
	 * Creates a new DTO object.
	 * 
	 * @param dataNode
	 *            the root node
	 * @return the list< domain dt o>
	 * @throws OpenShiftException
	 *             the open shift exception
	 */
	private List<DomainResourceDTO> createDomains(final ModelNode dataNode) throws OpenShiftException {
		final List<DomainResourceDTO> domainDtos = new ArrayList<DomainResourceDTO>();
		// temporarily supporting absence of 'data' node in the 'domain'
		// FIXME: simplify once openshift response is fixed
		for (ModelNode domainNode : dataNode.asList()) {
			if (domainNode.getType() == ModelType.OBJECT) {
				DomainResourceDTO dto = createDomain(domainNode, null);
				if (dto != null) {
					domainDtos.add(dto);
				}
			}
		}

		return domainDtos;
	}

	/**
	 * Creates a new DTO object.
	 * 
	 * @param domainNode
	 *            the domain node
	 * @return the domain dto
	 * @throws OpenShiftException
	 */
	private DomainResourceDTO createDomain(final ModelNode domainNode, Messages messages)
			throws OpenShiftException {
		if (!domainNode.isDefined()
				|| domainNode.getType() != ModelType.OBJECT) {
			return null;
		}
		final String namespace = getAsString(domainNode, PROPERTY_ID);
		final String suffix = getAsString(domainNode, PROPERTY_SUFFIX);
		final Map<String, Link> links = createLinks(domainNode.get(PROPERTY_LINKS));
		return new DomainResourceDTO(namespace, suffix, links, messages);
	}

	/**
	 * Creates a new DTO object.
	 * 
	 * @param dataNode
	 *            the domain node
	 * @return the list< application dt o>
	 * @throws OpenShiftException
	 */
	private List<ApplicationResourceDTO> createApplications(final ModelNode dataNode)
			throws OpenShiftException {
		final List<ApplicationResourceDTO> applicationDTOs = new ArrayList<ApplicationResourceDTO>();
		for (ModelNode applicationNode : dataNode.asList()) {
			ApplicationResourceDTO dto = createApplication(applicationNode, null);
			if (dto != null) {
				applicationDTOs.add(dto);
			}
		}
		return applicationDTOs;
	}

	/**
	 * Creates a new ApplicationResourceDTO.
	 * 
	 * @param appNode
	 *            the application node
	 * @return the application resource dto
	 * @throws OpenShiftException
	 */
	private ApplicationResourceDTO createApplication(ModelNode appNode, Messages messages)
			throws OpenShiftException {
		if (!appNode.isDefined()) {
			return null;
		}
		final String framework = getAsString(appNode, PROPERTY_FRAMEWORK);
		final String creationTime = getAsString(appNode, PROPERTY_CREATION_TIME);
		final String name = getAsString(appNode, PROPERTY_NAME);
		final String uuid = getAsString(appNode, PROPERTY_UUID);
		final ApplicationScale scalable = ApplicationScale.safeValueOf(getAsString(appNode, PROPERTY_SCALABLE));
		final IGearProfile gearProfile = createGearProfile(appNode);
		final String applicationUrl = getAsString(appNode, PROPERTY_APP_URL);
		final String sshUrl = getAsString(appNode, PROPERTY_SSH_URL);
		final String gitUrl = getAsString(appNode, PROPERTY_GIT_URL);
		final String initialGitUrl = getAsString(appNode, PROPERTY_INITIAL_GIT_URL);
		final String deploymentType = getAsString(appNode, PROPERTY_DEPLOYMENT_TYPE);
		final String domainId = getAsString(appNode, PROPERTY_DOMAIN_ID);
		final Map<String, Link> links = createLinks(appNode.get(PROPERTY_LINKS));
		final List<String> aliases = createAliases(appNode.get(PROPERTY_ALIASES));
		final Map<String, CartridgeResourceDTO> cartridges = createCartridges(appNode.get(PROPERTY_CARTRIDGES));

		return new ApplicationResourceDTO(
				framework,
				domainId,
				creationTime,
				name,
				gearProfile,
				scalable,
				uuid,
				applicationUrl,
				sshUrl,
				gitUrl,
				initialGitUrl,
				deploymentType,
				aliases,
				cartridges,
				links,
				messages);
	}

	private GearProfile createGearProfile(ModelNode appNode) {
		String gearProfileName = getAsString(appNode, PROPERTY_GEAR_PROFILE);
		if (gearProfileName == null) {
			return null;
		}
		return new GearProfile(gearProfileName);
	}

	private Collection<GearGroupResourceDTO> createGearGroups(ModelNode dataNode) {
		Collection<GearGroupResourceDTO> gearGroupDTOs = new ArrayList<GearGroupResourceDTO>();
		for (ModelNode gearGroupNode : dataNode.asList()) {
			GearGroupResourceDTO dto = createGearGroupResourceDTO(gearGroupNode);
			if (dto != null) {
				gearGroupDTOs.add(dto );
			}
		}

		return gearGroupDTOs;
	}

	private GearGroupResourceDTO createGearGroupResourceDTO(ModelNode gearGroupNode) {
		if (!gearGroupNode.isDefined()) {
			return null;
		}
		final String uuid = getAsString(gearGroupNode, PROPERTY_UUID);
		final String name = getAsString(gearGroupNode, PROPERTY_NAME);
		final Collection<GearResourceDTO> gears = createGears(gearGroupNode.get(PROPERTY_GEARS));
		final Map<String, CartridgeResourceDTO> cartridges = createCartridges(gearGroupNode.get(PROPERTY_CARTRIDGES));
		return new GearGroupResourceDTO(uuid, name, gears, cartridges);
	}

	private Collection<GearResourceDTO> createGears(ModelNode gearsNode) {
		List<GearResourceDTO> gears = new ArrayList<GearResourceDTO>();
		for (ModelNode gearNode : gearsNode.asList()) {
			gears.add(
					new GearResourceDTO(
							getAsString(gearNode, PROPERTY_ID),
							getAsString(gearNode, PROPERTY_GEAR_STATE),
							getAsString(gearNode, PROPERTY_SSH_URL)));
		}
		return gears;
	}

	/**
	 * Creates a new CartridgeResourceDTO for a given root node.
	 * 
	 * @param cartridgesNode
	 *            the root node
	 * @return the list< cartridge resource dto>
	 * @throws OpenShiftException
	 */
	private Map<String, CartridgeResourceDTO> createCartridges(ModelNode cartridgesNode) throws OpenShiftException {
		final Map<String, CartridgeResourceDTO> cartridgesByName = new LinkedHashMap<String, CartridgeResourceDTO>();
		if (cartridgesNode.isDefined()
				&& cartridgesNode.getType() == ModelType.LIST) {
			for (ModelNode cartridgeNode : cartridgesNode.asList()) {
				CartridgeResourceDTO cartridgeResourceDTO = createCartridge(cartridgeNode, null);
				cartridgesByName.put(cartridgeResourceDTO.getName(), cartridgeResourceDTO);
			}
		}
		return cartridgesByName;
	}

	/**
	 * Creates a new CartridgeResourceDTO object for a given cartridge node and
	 * messages.
	 * 
	 * @param cartridgeNode
	 *            the cartridge node
	 * @return the cartridge resource dto
	 * @throws OpenShiftException
	 */
	private CartridgeResourceDTO createCartridge(ModelNode cartridgeNode, Messages messages)
			throws OpenShiftException {
		final String name = getAsString(cartridgeNode, PROPERTY_NAME);
		final String displayName = getAsString(cartridgeNode, PROPERTY_DISPLAY_NAME);
		final String description = getAsString(cartridgeNode, PROPERTY_DESCRIPTION);
		final String type = getAsString(cartridgeNode, PROPERTY_TYPE);
		final URL url = createUrl(getAsString(cartridgeNode, PROPERTY_URL), name);
		final boolean obsolete = getAsBoolean(cartridgeNode, PROPERTY_OBSOLETE);
		final CartridgeResourceProperties properties = createProperties(cartridgeNode.get(PROPERTY_PROPERTIES));
		final Map<String, Link> links = createLinks(cartridgeNode.get(PROPERTY_LINKS));
		return new CartridgeResourceDTO(name, displayName, description, type, url, obsolete, properties, links, messages);
	}

	private URL createUrl(String url, String name) {
		try {
			if (url == null) {
				return null;
			}
			return new URL(url);
		} catch (MalformedURLException e) {
			LOGGER.warn("Url {} in server response for cartridge {} is not a valid URL.", url, name);
			return null;
		}
	}

	/**
	 * Creates a new ResourceDTO object.
	 * 
	 * @param aliasNodeList
	 *            the alias node list
	 * @return the list< string>
	 */
	private List<String> createAliases(ModelNode aliasNodesList) {
		final List<String> aliases = new ArrayList<String>();
		switch (aliasNodesList.getType()) {
		case OBJECT:
		case LIST:
			for (ModelNode aliasNode : aliasNodesList.asList()) {
				aliases.add(aliasNode.asString());
			}
			break;
		default:
			aliases.add(aliasNodesList.asString());
		}
		return aliases;
	}

	/**
	 * Creates a new DTO object.
	 * 
	 * @param linkParamNodes
	 *            the link param nodes
	 * @return the list< link param>
	 * @throws OpenShiftRequestException
	 */
	private List<LinkParameter> createLinkParameters(ModelNode linkParamNodes)
			throws OpenShiftRequestException {
		List<LinkParameter> linkParams = new ArrayList<LinkParameter>();
		if (linkParamNodes.isDefined()) {
			for (ModelNode linkParamNode : linkParamNodes.asList()) {
				linkParams.add(createLinkParameter(linkParamNode));
			}
		}
		return linkParams;
	}

	/**
	 * Creates a new link parameter for the given link parameter node.
	 * 
	 * @param linkParamNode
	 *            the model node that contains the link parameters
	 * @return the link parameter
	 * @throws OpenShiftRequestException
	 */
	private LinkParameter createLinkParameter(ModelNode linkParamNode) throws OpenShiftRequestException {
		final String description = linkParamNode.get(IOpenShiftJsonConstants.PROPERTY_DESCRIPTION).asString();
		final String type = linkParamNode.get(IOpenShiftJsonConstants.PROPERTY_TYPE).asString();
		final String defaultValue = linkParamNode.get(IOpenShiftJsonConstants.PROPERTY_DEFAULT_VALUE).asString();
		final String name = linkParamNode.get(IOpenShiftJsonConstants.PROPERTY_NAME).asString();
		return new LinkParameter(name, type, defaultValue, description, createValidOptions(linkParamNode));
	}

	/**
	 * Gets the valid options.
	 * 
	 * @param linkParamNode
	 *            the link param node
	 * @return the valid options
	 */
	private List<String> createValidOptions(ModelNode linkParamNode) {
		final List<String> validOptions = new ArrayList<String>();
		final ModelNode validOptionsNode = linkParamNode.get(PROPERTY_VALID_OPTIONS);
		if (validOptionsNode.isDefined()) {
			switch (validOptionsNode.getType()) {
			case STRING: // if there's only one value, it is not serialized as a
							// list, but just a string
				validOptions.add(validOptionsNode.asString());
				break;
			case LIST:
				for (ModelNode validOptionNode : validOptionsNode.asList()) {
					validOptions.add(validOptionNode.asString());
				}
				break;
			default:
				break;
			}
		}
		return validOptions;
	}

	/**
	 * Creates ResourceProperties for a given propertiesNode
	 * <p>
	 * ex.
	 * 
	 * <pre>
	 * "properties":[
	 *       {
	 *          "name":"connection_url",
	 *          "type":"cart_data",
	 *          "description":"Application metrics URL",
	 *          "value":"https://eap6-foobarz.rhcloud.com/metrics/"
	 *       },
	 * </pre>
	 * 
	 * @param propertiesNode
	 * @return
	 */
	private CartridgeResourceProperties createProperties(ModelNode propertiesNode) {
		if (propertiesNode == null
				|| !propertiesNode.isDefined()) {
			return null;
		}

		CartridgeResourceProperties properties = new CartridgeResourceProperties();
		for (ModelNode propertyNode : propertiesNode.asList()) {
			CartridgeResourceProperty property = createProperty(propertyNode);
			String name = property.getName();
			if (StringUtils.isEmpty(name)) {
				continue;
			}
			properties.add(name, property);
		}
		return properties;
	}

	private CartridgeResourceProperty createProperty(ModelNode propertyNode) {
		String name = getAsString(propertyNode, IOpenShiftJsonConstants.PROPERTY_NAME);
		String description = getAsString(propertyNode, IOpenShiftJsonConstants.PROPERTY_DESCRIPTION);
		String type = getAsString(propertyNode, IOpenShiftJsonConstants.PROPERTY_TYPE);
		String value = getAsString(propertyNode, IOpenShiftJsonConstants.PROPERTY_VALUE);
		return new CartridgeResourceProperty(name, type, description, value);
	}

	private List<EnvironmentVariableResourceDTO> createEnvironmentVariables(ModelNode dataNode)
			throws OpenShiftException {
		final List<EnvironmentVariableResourceDTO> environmentVariables = new ArrayList<EnvironmentVariableResourceDTO>();
		for (ModelNode environmentVariableNode : dataNode.asList()) {
			EnvironmentVariableResourceDTO dto = createEnvironmentVariable(environmentVariableNode, null);
			if (dto != null) {
				environmentVariables.add(dto);
			}
		}
		return environmentVariables;
	}

	private EnvironmentVariableResourceDTO createEnvironmentVariable(ModelNode environmentVariableNode,
			Messages messages) throws OpenShiftException {
		if (!environmentVariableNode.isDefined()) {
			return null;
		}
		final String name = getAsString(environmentVariableNode, PROPERTY_NAME);
		final String value = getAsString(environmentVariableNode, PROPERTY_VALUE);
		final Map<String, Link> links = createLinks(environmentVariableNode.get(PROPERTY_LINKS));
		return new EnvironmentVariableResourceDTO(name, value, links, messages);
	}
}
