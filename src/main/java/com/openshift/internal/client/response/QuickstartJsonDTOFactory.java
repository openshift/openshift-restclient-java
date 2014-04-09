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

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jboss.dmr.ModelNode;
import org.jboss.dmr.ModelType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.openshift.client.Messages;
import com.openshift.client.cartridge.query.CartridgeNameRegexQuery;
import com.openshift.client.cartridge.query.ICartridgeQuery;
import com.openshift.internal.client.utils.IOpenShiftJsonConstants;
import com.openshift.internal.client.utils.StringUtils;

/**
 * A factory for creating DTO objects.
 * 
 * @author Andre Dietisheim
 */
public class QuickstartJsonDTOFactory extends AbstractJsonDTOFactory {

	private static final Pattern CARTRIDGE_NAME_QUERY_PATTERN = Pattern.compile("\\*?([^|*]+)\\*?(\\|)?");

	private final Logger LOGGER = LoggerFactory.getLogger(OpenShiftJsonDTOFactory.class);
	
	@Override
	protected Object createData(EnumDataType dataType, Messages messages, ModelNode dataNode) {
		List<QuickstartDTO> quickstarts = new ArrayList<QuickstartDTO>();
		if (dataNode.getType() == ModelType.LIST) {
			for (ModelNode quickstartContainerNode : dataNode.asList()) {
				QuickstartDTO dto = createQuickstart(quickstartContainerNode.get(IOpenShiftJsonConstants.PROPERTY_QUICKSTART));
				if (dto == null) {
					continue;
				}
				quickstarts.add(dto);
			}
		}
		return quickstarts;
	}

	private QuickstartDTO createQuickstart(ModelNode quickstartNode) {
		if (!isDefined(quickstartNode)) {
			return null;
		}
		
		final String id = getAsString(quickstartNode, IOpenShiftJsonConstants.PROPERTY_ID);
		final String href = getAsString(quickstartNode, IOpenShiftJsonConstants.PROPERTY_HREF);
		final String name = getAsString(quickstartNode, IOpenShiftJsonConstants.PROPERTY_NAME);
		final String updated = getAsString(quickstartNode, IOpenShiftJsonConstants.PROPERTY_UPDATED);
		final String summary = getAsString(quickstartNode, IOpenShiftJsonConstants.PROPERTY_SUMMARY);
		final List<ICartridgeQuery> cartridgeQueries = createCartridgeQueries(quickstartNode.get(IOpenShiftJsonConstants.PROPERTY_CARTRIDGES));
		final String website = getAsString(quickstartNode, IOpenShiftJsonConstants.PROPERTY_WEBSITE);
		final List<String> tags = createTags(quickstartNode.get(IOpenShiftJsonConstants.PROPERTY_TAGS));
		final String language = getAsString(quickstartNode, IOpenShiftJsonConstants.PROPERTY_LANGUAGE);
		final String initialGitUrl = getAsString(quickstartNode, IOpenShiftJsonConstants.PROPERTY_INITIAL_GIT_URL);
		final String provider = getAsString(quickstartNode, IOpenShiftJsonConstants.PROPERTY_PROVIDER);
		return new QuickstartDTO(id, href, name, updated, summary, cartridgeQueries, website, tags, language, initialGitUrl, provider);
	}
	
	protected List<ICartridgeQuery> createCartridgeQueries(ModelNode cartridgesNode) {
		if (!isDefined(cartridgesNode)
				|| cartridgesNode.getType() != ModelType.STRING) {
			return null;
		}
		
		String cartridgesSpecs = cartridgesNode.asString();
		try {
			ModelNode cartridgesSpecsNode =
					ModelNode.fromJSONString(StringUtils.decodeQuotationMarks(cartridgesSpecs));
			// json array
			return createCartridgeQueriesFromJson(cartridgesSpecsNode);
		} catch(IllegalArgumentException e) {
			// comma delimited list 
			return createCartridgeQueriesFromCommaDelimitedList(cartridgesSpecs);
		}
	}
	

	private List<ICartridgeQuery> createCartridgeQueriesFromJson(ModelNode cartridgesNode) {
		if (!isDefined(cartridgesNode)) {
			return null;
		}
		
		List<ICartridgeQuery> queries = new ArrayList<ICartridgeQuery>();
		for (ModelNode cartridgeSpec : cartridgesNode.asList()) {
			ICartridgeQuery query = createCartridgeQueryFromJson(cartridgeSpec);
			if (query != null) {
				queries.add(query);
			}
		}
		return queries;
	}
	
	private ICartridgeQuery createCartridgeQueryFromJson(ModelNode cartridgeNode) {
		if (!isDefined(cartridgeNode)) {
			return null;
		}
		if(ModelType.STRING == cartridgeNode.getType()) {
			// string spec
			return createCartridgeQuery(cartridgeNode.asString());
		} else if (ModelType.OBJECT == cartridgeNode.getType()) {
			// json object spec
			if (cartridgeNode.has(IOpenShiftJsonConstants.PROPERTY_NAME)) {
				return new NamedCartridgeSpec(cartridgeNode.get(IOpenShiftJsonConstants.PROPERTY_NAME).asString());
			} else if (cartridgeNode.has(IOpenShiftJsonConstants.PROPERTY_URL)) {
				String url = getAsString(cartridgeNode, IOpenShiftJsonConstants.PROPERTY_URL);
				try {
					return new DownloadableCartridgeSpec(url);
				} catch (MalformedURLException e) {
					LOGGER.error("Could not create downloadable cartridge spec for Url {}", e);
				}
			}
		}
		return null;
	}
	
	private List<ICartridgeQuery> createCartridgeQueriesFromCommaDelimitedList(String cartridgeSpecs) {
		List<ICartridgeQuery> queries = new ArrayList<ICartridgeQuery>();
//		Matcher matcher = CARTRIDGE_ITEMS_REGEX.matcher(cartridgeSpecs);
//		if(matcher.matches()) {
//			if (matcher.groupCount() >= 1) {
//				queries.add(createCartridgeQuery(matcher.group(1)));
//			}
//			for (int i = 3; i <=  matcher.groupCount(); i++) {
//				if (!StringUtils.isEmpty(matcher.group(i))) {
//					queries.add(createCartridgeQuery(matcher.group(i)));
//				}
//			}
//		}
		if (!StringUtils.isEmpty(cartridgeSpecs)) {
			for (String cartridgeSpec : cartridgeSpecs.split(",")) {
				queries.add(createCartridgeQuery(cartridgeSpec.trim()));
			}
		}
		return queries;
	}

	private ICartridgeQuery createCartridgeQuery(String cartridgeSpec) {
		try {
			return new DownloadableCartridgeSpec(new URL(cartridgeSpec));
		} catch(MalformedURLException e) {
			return new CartridgeNameRegexQuery(createNamePattern(cartridgeSpec));
		}
			
	}
	
	private String createNamePattern(String namePattern) {
		Matcher matcher = CARTRIDGE_NAME_QUERY_PATTERN.matcher(namePattern);
		StringBuilder builder = new StringBuilder();
		while (matcher.find()) {
			// name pattern is considered a substring match
			builder.append(".*");
			if (matcher.groupCount() >= 2
					&& !StringUtils.isEmpty(matcher.group(1))) {
				builder.append(matcher.group(1));
			}
			builder.append(".*");
			if (matcher.groupCount() >= 2
					&& !StringUtils.isEmpty(matcher.group(2))) {
				builder.append(matcher.group(2));
			}
		}
		return builder.toString();
	}
	
	protected List<String> createTags(ModelNode tagsNode) {
		List<String> tags = new ArrayList<String>();
		if (isDefined(tagsNode)) {
			String[] parts = tagsNode.asString().split("\\, ");
			for (int i = 0; parts.length > i; i++) {
				tags.add(parts[i]);
			}
		}
		return tags;
	}
}
