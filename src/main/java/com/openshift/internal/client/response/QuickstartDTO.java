/******************************************************************************* 
 * Copyright (c) 2014 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/
package com.openshift.internal.client.response;

import java.util.List;

import com.openshift.client.cartridge.query.ICartridgeQuery;

/**
 * @author Andre Dietisheim
 */
public class QuickstartDTO {

	private final String id;
	private final String href;
	private final String name;
	private final String updated;
	private final String summary;
	private final List<ICartridgeQuery> cartridgeQueries;
	private final String website;
	private final List<String> tags;
	private final String language;
	private final String initialGitUrl;
	private final String provider;
	
	QuickstartDTO(String id, String href, String name, String updated, String summary, List<ICartridgeQuery> cartridgeQueries,
			String website, List<String> tags, String language, String initialGitUrl, String provider) {
		this.id = id;
		this.href = href;
		this.name = name;
		this.updated = updated;
		this.summary = summary;
		this.cartridgeQueries = cartridgeQueries;
		this.website = website;
		this.tags = tags;
		this.language = language;
		this.initialGitUrl = initialGitUrl;
		this.provider = provider;
	}

	public String getId() {
		return id;
	}

	public String getHref() {
		return href;
	}

	public String getName() {
		return name;
	}

	public String getUpdated() {
		return updated;
	}

	public String getSummary() {
		return summary;
	}

	public List<ICartridgeQuery> getCartridges() {
		return cartridgeQueries;
	}

	public String getWebsite() {
		return website;
	}

	public List<String> getTags() {
		return tags;
	}

	public String getLanguage() {
		return language;
	}

	public String getInitialGitUrl() {
		return initialGitUrl;
	}

	public String getProvider() {
		return provider;
	}
}
