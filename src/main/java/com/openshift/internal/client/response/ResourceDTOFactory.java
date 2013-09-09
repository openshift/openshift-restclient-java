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
package com.openshift.internal.client.response;

import static com.openshift.internal.client.utils.IOpenShiftJsonConstants.PROPERTY_ALIASES;
import static com.openshift.internal.client.utils.IOpenShiftJsonConstants.PROPERTY_APP_URL;
import static com.openshift.internal.client.utils.IOpenShiftJsonConstants.PROPERTY_CONSUMED_GEARS;
import static com.openshift.internal.client.utils.IOpenShiftJsonConstants.PROPERTY_CREATION_TIME;
import static com.openshift.internal.client.utils.IOpenShiftJsonConstants.PROPERTY_DATA;
import static com.openshift.internal.client.utils.IOpenShiftJsonConstants.PROPERTY_DESCRIPTION;
import static com.openshift.internal.client.utils.IOpenShiftJsonConstants.PROPERTY_DISPLAY_NAME;
import static com.openshift.internal.client.utils.IOpenShiftJsonConstants.PROPERTY_DOMAIN;
import static com.openshift.internal.client.utils.IOpenShiftJsonConstants.PROPERTY_DOMAIN_ID;
import static com.openshift.internal.client.utils.IOpenShiftJsonConstants.PROPERTY_EMBEDDED;
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
import static com.openshift.internal.client.utils.IOpenShiftJsonConstants.PROPERTY_OPTIONAL_PARAMS;
import static com.openshift.internal.client.utils.IOpenShiftJsonConstants.PROPERTY_PROPERTIES;
import static com.openshift.internal.client.utils.IOpenShiftJsonConstants.PROPERTY_REL;
import static com.openshift.internal.client.utils.IOpenShiftJsonConstants.PROPERTY_REQUIRED_PARAMS;
import static com.openshift.internal.client.utils.IOpenShiftJsonConstants.PROPERTY_SCALABLE;
import static com.openshift.internal.client.utils.IOpenShiftJsonConstants.PROPERTY_SUFFIX;
import static com.openshift.internal.client.utils.IOpenShiftJsonConstants.PROPERTY_TYPE;
import static com.openshift.internal.client.utils.IOpenShiftJsonConstants.PROPERTY_UUID;
import static com.openshift.internal.client.utils.IOpenShiftJsonConstants.PROPERTY_VALID_OPTIONS;

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
import com.openshift.client.GearState;
import com.openshift.client.IField;
import com.openshift.client.IGear;
import com.openshift.client.IGearProfile;
import com.openshift.client.Message;
import com.openshift.client.Messages;
import com.openshift.client.OpenShiftException;
import com.openshift.client.OpenShiftRequestException;
import com.openshift.internal.client.CartridgeType;
import com.openshift.internal.client.Gear;
import com.openshift.internal.client.GearProfile;
import com.openshift.internal.client.utils.IOpenShiftJsonConstants;
import com.openshift.internal.client.utils.StringUtils;

/**
 * A factory for creating DTO objects.
 * 
 * @author Xavier Coulon
 */
public class ResourceDTOFactory {

	private static final Logger LOGGER = LoggerFactory.getLogger(ResourceDTOFactory.class);

	/**
	 * Gets the.
	 * 
	 * @param content
	 *            the content
	 * @return the response
	 * @throws OpenShiftException
	 *             the open shift exception
	 */
	public static RestResponse get(final String content) throws OpenShiftException {
		// in case the server answers with 'no-content'
		if (StringUtils.isEmpty(content)) {
			return null;
		}
		LOGGER.trace("Unmarshalling response\n{}", content);
		final ModelNode rootNode = getModelNode(content);
		final String type = rootNode.get(IOpenShiftJsonConstants.PROPERTY_TYPE).asString();
		final String status = rootNode.get(IOpenShiftJsonConstants.PROPERTY_STATUS).asString();
		final Messages messages = createMessages(rootNode.get(IOpenShiftJsonConstants.PROPERTY_MESSAGES));

		final EnumDataType dataType = EnumDataType.safeValueOf(type);
		// the response is after an error, only the messages are relevant
		

		if (dataType == null) {
			return new RestResponse(status, messages, null, null);
		}
		
		
		switch (dataType) {
		case user:
			return new RestResponse(status, messages, createUser(rootNode), dataType);
		case keys:
			return new RestResponse(status, messages, createKeys(rootNode), dataType);
		case key:
			return new RestResponse(status, messages, createKey(rootNode, messages), dataType);
		case links:
			return new RestResponse(status, messages, createLinks(rootNode), dataType);
		case domains:
			return new RestResponse(status, messages, createDomains(rootNode), dataType);
		case domain:
			return new RestResponse(status, messages, createDomain(rootNode, messages), dataType);
		case applications:
			return new RestResponse(status, messages, createApplications(rootNode), dataType);
		case application:
			return new RestResponse(status, messages, createApplication(rootNode, messages), dataType);
		case gear_groups:
			return new RestResponse(status, messages, createGearGroups(rootNode), dataType);
		case cartridges:
			return new RestResponse(status, messages, createCartridges(rootNode), dataType);
		case cartridge:
			return new RestResponse(status, messages, createCartridge(rootNode, messages), dataType);
		default:
			return null;
		}
	}

	/**
	 * Creates a new ResourceDTO object.
	 * 
	 * @param messagesNode
	 *            the messages node
	 * @return the list< string>
	 */
	private static Messages createMessages(ModelNode messagesNode) {
		Map<IField, List<Message>> messagesByField = new LinkedHashMap<IField, List<Message>>();
		if (messagesNode.getType() == ModelType.LIST) {
			for (ModelNode messageNode : messagesNode.asList()) {
				Message message = createMessage(messageNode);
				List<Message> messages = (List<Message>) messagesByField.get(message.getField());
				if (messages == null) {
					messages = new ArrayList<Message>();
				}
				messages.add(message);
				messagesByField.put(message.getField(), messages);
			}
		}
		return new Messages(messagesByField);
	}

	
	
	private static Message createMessage(ModelNode messageNode) {
		String text = getString(messageNode.get(IOpenShiftJsonConstants.PROPERTY_TEXT));
		String field = getString(messageNode.get(IOpenShiftJsonConstants.PROPERTY_FIELD));
		int exitCode = getInt(messageNode.get(IOpenShiftJsonConstants.PROPERTY_EXIT_CODE));
		String severity = getString(messageNode.get(IOpenShiftJsonConstants.PROPERTY_SEVERITY));
		return new Message(text, field, severity, exitCode);
	}

	private static int getInt(ModelNode messageNode) {
		if (messageNode == null
				|| !messageNode.isDefined()) {
			return -1;
		}
		return messageNode.asInt();
	}

	private static String getString(ModelNode node) {
		if (node == null
				|| !node.isDefined()) {
			return null;
		}
		return node.asString();
	}

	/**
	 * Gets the model node.
	 * 
	 * @param content
	 *            the content
	 * @return the model node
	 * @throws OpenShiftException
	 *             the open shift exception
	 */
	private static ModelNode getModelNode(final String content) throws OpenShiftException {
		if (content == null) {
			throw new OpenShiftException("Could not unmarshall response: no content.");
		}
		final ModelNode node = ModelNode.fromJSONString(content);
		if (!node.isDefined()) {
			throw new OpenShiftException("Could not unmarshall response: erroneous content.");
		}

		return node;
	}

	/**
	 * Creates a new ResourceDTO object.
	 * 
	 * @param userNode
	 *            the root node
	 * @return the user resource dto
	 * @throws OpenShiftException
	 */
	private static UserResourceDTO createUser(ModelNode userNode) throws OpenShiftException {
		if (userNode.has(PROPERTY_DATA)) {
			// loop inside 'data' node
			return createUser(userNode.get(PROPERTY_DATA));
		}
		final String rhlogin = getAsString(userNode, PROPERTY_LOGIN);
		final int maxGears = getAsInteger(userNode, PROPERTY_MAX_GEARS);
		final int consumedGears = getAsInteger(userNode, PROPERTY_CONSUMED_GEARS);
		final Map<String, Link> links = createLinks(userNode.get(PROPERTY_LINKS));
		return new UserResourceDTO(rhlogin, maxGears, consumedGears, links);
	}

	/**
	 * Creates a new ResourceDTO object.
	 * 
	 * @param rootNode
	 *            the root node
	 * @return the list< key resource dt o>
	 * @throws OpenShiftException
	 *             the open shift exception
	 */
	private static List<KeyResourceDTO> createKeys(ModelNode rootNode) throws OpenShiftException {
		final List<KeyResourceDTO> keys = new ArrayList<KeyResourceDTO>();
		// temporarily supporting single and multiple values for 'keys' node
		if (rootNode.has(PROPERTY_DATA)) {
			for (ModelNode dataNode : rootNode.get(PROPERTY_DATA).asList()) {
				if (dataNode.getType() == ModelType.OBJECT) {
					keys.add(createKey(dataNode, null));
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
	private static KeyResourceDTO createKey(ModelNode keyNode, Messages messages) throws OpenShiftException {
		if (keyNode.has(PROPERTY_DATA)) {
			// loop inside 'data' node
			return createKey(keyNode.get(PROPERTY_DATA), messages);
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
	private static Map<String, Link> createLinks(final ModelNode linksNode) throws OpenShiftException {
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
					links.put(linkName, createLink(valueNode));
				}
			}
		}
		return links;
	}

	private static Link createLink(final ModelNode valueNode) {
		final String rel = getAsString(valueNode, PROPERTY_REL);
		final String href = valueNode.get(PROPERTY_HREF).asString();
		final String method = valueNode.get(PROPERTY_METHOD).asString();
		final List<LinkParameter> requiredParams = 
				createLinkParameters(valueNode.get(PROPERTY_REQUIRED_PARAMS));
		final List<LinkParameter> optionalParams = 
				createLinkParameters(valueNode.get(PROPERTY_OPTIONAL_PARAMS));
		return new Link(rel, href, method, requiredParams, optionalParams);
	}

	/**
	 * Creates a new DTO object.
	 * 
	 * @param rootNode
	 *            the root node
	 * @return the list< domain dt o>
	 * @throws OpenShiftException
	 *             the open shift exception
	 */
	private static List<DomainResourceDTO> createDomains(final ModelNode rootNode) throws OpenShiftException {
		final List<DomainResourceDTO> domains = new ArrayList<DomainResourceDTO>();
		// temporarily supporting absence of 'data' node in the 'domain'
		// response message
		// FIXME: simplify once openshift response is fixed
		if (rootNode.has(PROPERTY_DATA)) {
			for (ModelNode dataNode : rootNode.get(PROPERTY_DATA).asList()) {
				if (dataNode.getType() == ModelType.OBJECT) {
					domains.add(createDomain(dataNode, null));
				} else {
					throw new OpenShiftException("Unexpected node type: {0}", dataNode.getType());
				}
			}
		} else {
			final ModelNode domainNode = rootNode.get(PROPERTY_DOMAIN);
			if (domainNode.isDefined()
					&& domainNode.getType() == ModelType.OBJECT) {
				domains.add(createDomain(domainNode, null));
			} else {
				throw new OpenShiftException("Unexpected node type: {0}", domainNode.getType());
			}
		}

		return domains;
	}

	/**
	 * Creates a new DTO object.
	 * 
	 * @param domainNode
	 *            the domain node
	 * @return the domain dto
	 * @throws OpenShiftException
	 */
	private static DomainResourceDTO createDomain(final ModelNode domainNode, Messages messages)
			throws OpenShiftException {
		if (domainNode.has(PROPERTY_DATA)) {
			// recurse into "data" node
			return createDomain(domainNode.get(PROPERTY_DATA), messages);
		}
		final String namespace = getAsString(domainNode, PROPERTY_ID);
		final String suffix = getAsString(domainNode, PROPERTY_SUFFIX);
		final Map<String, Link> links = createLinks(domainNode.get(PROPERTY_LINKS));
		return new DomainResourceDTO(namespace, suffix, links, messages);
	}

	/**
	 * Creates a new DTO object.
	 * 
	 * @param rootNode
	 *            the domain node
	 * @return the list< application dt o>
	 * @throws OpenShiftException
	 */
	private static List<ApplicationResourceDTO> createApplications(final ModelNode rootNode)
			throws OpenShiftException {
		final List<ApplicationResourceDTO> applicationDTOs = new ArrayList<ApplicationResourceDTO>();
		if (rootNode.has(PROPERTY_DATA)) {
			for (ModelNode applicationNode : rootNode.get(PROPERTY_DATA).asList()) {
				applicationDTOs.add(createApplication(applicationNode, null));
			}
		}
		return applicationDTOs;
	}

	/**
	 * Creates a new DTO object.
	 * 
	 * @param appNode
	 *            the app node
	 * @return the application dto
	 * @throws OpenShiftException
	 */
	private static ApplicationResourceDTO createApplication(ModelNode appNode, Messages messages)
			throws OpenShiftException {
		if (appNode.has(PROPERTY_DATA)) {
			// recurse into 'data' node
			return createApplication(appNode.get(PROPERTY_DATA), messages);
		}
		final String framework = getAsString(appNode, PROPERTY_FRAMEWORK);
		final String creationTime = getAsString(appNode, PROPERTY_CREATION_TIME);
		final String name = getAsString(appNode, PROPERTY_NAME);
		final String uuid = getAsString(appNode, PROPERTY_UUID);
		final ApplicationScale scalable = ApplicationScale.safeValueOf(getAsString(appNode, PROPERTY_SCALABLE));
		final IGearProfile gearProfile = createGearProfile(appNode);
		final String applicationUrl = getAsString(appNode, PROPERTY_APP_URL);
		final String gitUrl = getAsString(appNode, PROPERTY_GIT_URL);
		final String initialGitUrl = getAsString(appNode, PROPERTY_INITIAL_GIT_URL);
		final String domainId = getAsString(appNode, PROPERTY_DOMAIN_ID);
		final Map<String, Link> links = createLinks(appNode.get(PROPERTY_LINKS));
		final List<String> aliases = createAliases(appNode.get(PROPERTY_ALIASES));
		final List<CartridgeResourceDTO> embeddedCartridges = createEmbeddedCartridges(appNode.get(PROPERTY_EMBEDDED));
		
		return new ApplicationResourceDTO(
				framework, 
				domainId, 
				creationTime, 
				name, 
				gearProfile, 
				scalable, 
				uuid, 
				applicationUrl, 
				gitUrl, 
				initialGitUrl,
				aliases, 
				embeddedCartridges, 
				links, 
				messages);
	}

	private static GearProfile createGearProfile(ModelNode appNode) {
		String gearProfileName = getAsString(appNode, PROPERTY_GEAR_PROFILE);
		if (gearProfileName == null) {
			return null;
		}
		return new GearProfile(gearProfileName);
	}

	private static Collection<GearGroupResourceDTO> createGearGroups(ModelNode dataNode) {
		Collection<GearGroupResourceDTO> gearGroupDTOs = new ArrayList<GearGroupResourceDTO>();
		for(ModelNode gearGroupNode : dataNode.get(PROPERTY_DATA).asList()) {
			gearGroupDTOs.add(createGearGroupResourceDTO(gearGroupNode));
		}
		
		return gearGroupDTOs;
	}

	private static GearGroupResourceDTO createGearGroupResourceDTO(ModelNode gearGroupNode) {
		String uuid = getAsString(gearGroupNode, PROPERTY_UUID);
		String name = getAsString(gearGroupNode, PROPERTY_NAME);
		Collection<IGear> gears = createGears(gearGroupNode.get(PROPERTY_GEARS));
		return new GearGroupResourceDTO(uuid, name, gears);
	}
	
	private static Collection<IGear> createGears(ModelNode gearsNode) {
		List<IGear> gears = new ArrayList<IGear>();
		for (ModelNode gearNode : gearsNode.asList()) {
			gears.add(
					new Gear(
							getAsString(gearNode, PROPERTY_ID),
							GearState.safeValueOf(getAsString(gearNode, PROPERTY_GEAR_STATE))));
		}
		return gears;
	}
	
	/**
	 * Creates a new CartridgeResourceDTO for a given root node.
	 * 
	 * @param rootNode
	 *            the root node
	 * @return the list< cartridge resource dt o>
	 * @throws OpenShiftException
	 */
	private static Map<String, CartridgeResourceDTO> createCartridges(ModelNode rootNode) throws OpenShiftException {
		final Map<String, CartridgeResourceDTO> cartridgesByName = new LinkedHashMap<String, CartridgeResourceDTO>();
		if (rootNode.has(PROPERTY_DATA)) {
			for (ModelNode cartridgeNode : rootNode.get(PROPERTY_DATA).asList()) {
				CartridgeResourceDTO cartridgeResourceDTO = createCartridge(cartridgeNode, null);
				cartridgesByName.put(cartridgeResourceDTO.getName(), cartridgeResourceDTO);
			}
		}
		return cartridgesByName;
	}

	/** 
	 * Creates a map of cartridge resource dtos for a given embedded node (within an application)
	 * 
	 * @param embeddedNode the "embedded" node in the application response
	 * @return the map of cartridges (by name) for the given node
	 * @throws OpenShiftException
	 */
	private static List<CartridgeResourceDTO> createEmbeddedCartridges(ModelNode embeddedNode)
			throws OpenShiftException {
		List<CartridgeResourceDTO> embeddedCartridges = new ArrayList<CartridgeResourceDTO>();
		for (String name : embeddedNode.keys()) {
			ModelNode cartridgeNode = embeddedNode.get(name);
			CartridgeResourceDTO cartridgeDTO = createEmbeddedCartridge(name, cartridgeNode);
			embeddedCartridges.add(cartridgeDTO);
		}
		return embeddedCartridges;
	}

	private static CartridgeResourceDTO createEmbeddedCartridge(String name, ModelNode embeddedCartridgeNode) {
		ResourceProperties properties = createEmbeddedProperties(embeddedCartridgeNode);
		return new CartridgeResourceDTO(name, CartridgeType.EMBEDDED, properties);
	}

	/**
	 * Creates ResourceProperties for a given embedded cartridge node
	 * <p>
	 * ex.
	 * 
	 * <pre>
	 * "embedded":{
	 *       "metrics-0.1":{
	 *          "connection_url":"https://eap6-foobarz.rhcloud.com/metrics/",
	 *          "info":"Connection URL: https://eap6-foobarz.rhcloud.com/metrics/"
	 *       }
	 * </pre>
	 * 
	 * @param embeddedCartridgeNode
	 * @return
	 */
	private static ResourceProperties createEmbeddedProperties(ModelNode embeddedCartridgeNode) {
		ResourceProperties properties = new ResourceProperties();
		if (embeddedCartridgeNode == null
				|| !embeddedCartridgeNode.isDefined()) {
 			return properties;
		}
		for(String propertyName : embeddedCartridgeNode.keys()) {
			String value = getAsString(embeddedCartridgeNode, propertyName);
			properties.add(propertyName, new ResourceProperty(propertyName, value));
		}
		return properties;
	}

	/**
	 * Creates a new CartridgeResourceDTO object for a given cartridge node and messages.
	 * 
	 * @param cartridgeNode
	 *            the cartridge node
	 * @return the cartridge resource dto
	 * @throws OpenShiftException
	 */
	private static CartridgeResourceDTO createCartridge(ModelNode cartridgeNode, Messages messages)
			throws OpenShiftException {
		if (cartridgeNode.has(PROPERTY_DATA)) {
			// recurse into 'data' node
			return createCartridge(cartridgeNode.get(PROPERTY_DATA), messages);
		}
		
		final String name = getAsString(cartridgeNode, PROPERTY_NAME);
		final String displayName = getAsString(cartridgeNode, PROPERTY_DISPLAY_NAME);
		final String description = getAsString(cartridgeNode, PROPERTY_DESCRIPTION);
		final String type = getAsString(cartridgeNode, PROPERTY_TYPE);
		final ResourceProperties properties = createProperties(cartridgeNode.get(PROPERTY_PROPERTIES));
		final Map<String, Link> links = createLinks(cartridgeNode.get(PROPERTY_LINKS));
		return new CartridgeResourceDTO(name, displayName, description, type, properties, links, messages);
	}

	/**
	 * Creates a new ResourceDTO object.
	 * 
	 * @param aliasNodeList
	 *            the alias node list
	 * @return the list< string>
	 */
	private static List<String> createAliases(ModelNode aliasNodesList) {
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
	private static List<LinkParameter> createLinkParameters(ModelNode linkParamNodes)
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
	private static LinkParameter createLinkParameter(ModelNode linkParamNode) throws OpenShiftRequestException {
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
	private static List<String> createValidOptions(ModelNode linkParamNode) {
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
	 * Returns the property identified by the given name in the given model
	 * node, or null if the named property is undefined.
	 * 
	 * @param node
	 *            the model node
	 * @param propertyName
	 *            the name of the property
	 * @return the property as a String
	 */
	private static String getAsString(final ModelNode node, String propertyName) {
		final ModelNode propertyNode = node.get(propertyName);
		return propertyNode.isDefined() ? propertyNode.asString() : null;
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
	private static ResourceProperties createProperties(ModelNode propertiesNode) {
		if (propertiesNode == null
				|| !propertiesNode.isDefined()) {
			return null;
		}
		
		ResourceProperties properties = new ResourceProperties();
		for(ModelNode propertyNode : propertiesNode.asList()) {
			ResourceProperty property = createProperty(propertyNode);
			String name = property.getName();
			if (StringUtils.isEmpty(name)) {
				continue;
			}
			properties.add(name, property);
		}
		return properties;
	}

	private static ResourceProperty createProperty(ModelNode propertyNode) {
		String name = getAsString(propertyNode, IOpenShiftJsonConstants.PROPERTY_NAME);
		String description = getAsString(propertyNode, IOpenShiftJsonConstants.PROPERTY_DESCRIPTION);
		String type = getAsString(propertyNode, IOpenShiftJsonConstants.PROPERTY_TYPE);
		String value = getAsString(propertyNode, IOpenShiftJsonConstants.PROPERTY_VALUE);
		return new ResourceProperty(name, type, description, value);
	}
	
	/**
	 * Returns the property identified by the given name in the given model node, or null if the named property is
	 * undefined.
	 * 
	 * @param node
	 *            the model node
	 * @param propertyName
	 *            the name of the property
	 * @return the property as a String
	 */
	private static Boolean getAsBoolean(final ModelNode node, String propertyName) {
		final ModelNode propertyNode = node.get(propertyName);
		return propertyNode.isDefined() ? propertyNode.asBoolean() : Boolean.FALSE;
	}
	
	/**
	 * Returns the property identified by the given name in the given model node, or null if the named property is
	 * undefined.
	 * 
	 * @param node
	 *            the model node
	 * @param propertyName
	 *            the name of the property
	 * @return the property as an Integer
	 */
	private static int getAsInteger(final ModelNode node, String propertyName) {
		final ModelNode propertyNode = node.get(propertyName);
		return propertyNode.isDefined() ? propertyNode.asInt() : 0;
	}
}
