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
package com.openshift.internal.client;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.openshift.client.IQuickstart;
import com.openshift.client.cartridge.query.ICartridgeQuery;
import com.openshift.internal.client.response.QuickstartDTO;

/**
 * A quickstart that is available on the OpenShift platform.
 * 
 * @author André Dietisheim
 */
public class Quickstart implements IQuickstart {

	private final String id;
	private final String href;
	private final String name;
	private final String summary;
	private final List<AlternativeCartridges> alternativeCartridges;
	private final String website;
	private final List<String> tags;
	private final String language;
	private final String initialGitUrl;
	private final String provider;

	Quickstart(QuickstartDTO dto, APIResource api) {
		this.id = dto.getId();
		this.href = dto.getHref();
		this.name = dto.getName();
		this.summary = dto.getSummary();
		this.alternativeCartridges = createAlternativeCartridges(dto.getCartridges(), api);
		this.website = dto.getWebsite();
		this.tags = dto.getTags();
		this.language = dto.getLanguage();
		this.initialGitUrl = dto.getInitialGitUrl();
		this.provider = dto.getProvider();
	}

	private List<AlternativeCartridges> createAlternativeCartridges(List<ICartridgeQuery> cartridges, APIResource api) {
		List<AlternativeCartridges> alternativeCartridges = new ArrayList<AlternativeCartridges>();
		if (cartridges != null) {
			for (ICartridgeQuery query : cartridges) {
				alternativeCartridges.add(new AlternativeCartridges(query, api));
			}
		}
		return alternativeCartridges;
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

	public String getSummary() {
		return summary;
	}

	@Override
	public List<AlternativeCartridges> getSuitableCartridges() {
		return Collections.unmodifiableList(alternativeCartridges);
	}

	@Override
	public String getWebsite() {
		return website;
	}

	@Override
	public List<String> getTags() {
		return tags;
	}

	@Override
	public String getLanguage() {
		return language;
	}

	@Override
	public String getInitialGitUrl() {
		return initialGitUrl;
	}

	@Override
	public String getProvider() {
		return provider;
	}

	@Override
	public String toString() {
		return "Quickstart ["
				+ "id=" + id
				+ ", href=" + href
				+ ", name=" + name
				+ ", summary=" + summary
				+ ", cartridges=" + alternativeCartridges
				+ ", website=" + website
				+ ", tags=" + tags
				+ ", language=" + language
				+ ", initialGitUrl=" + initialGitUrl
				+ ", provider=" + provider
				+ "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((alternativeCartridges == null) ? 0 : alternativeCartridges.hashCode());
		result = prime * result + ((href == null) ? 0 : href.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((initialGitUrl == null) ? 0 : initialGitUrl.hashCode());
		result = prime * result + ((language == null) ? 0 : language.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((provider == null) ? 0 : provider.hashCode());
		result = prime * result + ((summary == null) ? 0 : summary.hashCode());
		result = prime * result + ((tags == null) ? 0 : tags.hashCode());
		result = prime * result + ((website == null) ? 0 : website.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Quickstart other = (Quickstart) obj;
		if (alternativeCartridges == null) {
			if (other.alternativeCartridges != null)
				return false;
		} else if (!alternativeCartridges.equals(other.alternativeCartridges))
			return false;
		if (href == null) {
			if (other.href != null)
				return false;
		} else if (!href.equals(other.href))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (initialGitUrl == null) {
			if (other.initialGitUrl != null)
				return false;
		} else if (!initialGitUrl.equals(other.initialGitUrl))
			return false;
		if (language == null) {
			if (other.language != null)
				return false;
		} else if (!language.equals(other.language))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (provider == null) {
			if (other.provider != null)
				return false;
		} else if (!provider.equals(other.provider))
			return false;
		if (summary == null) {
			if (other.summary != null)
				return false;
		} else if (!summary.equals(other.summary))
			return false;
		if (tags == null) {
			if (other.tags != null)
				return false;
		} else if (!tags.equals(other.tags))
			return false;
		if (website == null) {
			if (other.website != null)
				return false;
		} else if (!website.equals(other.website))
			return false;
		return true;
	}
}