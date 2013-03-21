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

import java.util.List;
import java.util.Map;

import com.openshift.client.ApplicationScale;
import com.openshift.client.IGearGroup;
import com.openshift.client.IGearProfile;

/**
 * The Class ApplicationDTO.
 *
 * @author Xavier Coulon
 */
public class ApplicationResourceDTO extends BaseResourceDTO {

	/** the application's framework. */
	private final String framework;
	
	/** the application's domainId. */
	private final String domainId;
	
	/** the application's creation time. */
	private final String creationTime;
	
	/** the application's name. */
	private final String name;
	
	/** the application's UUID. */
	private final String uuid;
	
	/** The scalability enablement. */
	private final ApplicationScale scale;

	/** The application gear profile. */
	private final IGearProfile gearProfile;

	/** the application's aliases. */
	private final List<String> aliases;
	
	/** the application's URL. */
	private final String applicationUrl;
	
	/** the application's git repository URL. */
	private final String gitUrl;
	
	private Map<String, String> embeddedCartridgeInfos;
	
	private List<IGearGroup> gearGroups;

	/**
	 * Instantiates a new application dto.
	 *
	 * @param framework the framework
	 * @param domainId the domain id
	 * @param creationTime the creation time
	 * @param name the name
	 * @param uuid the uuid
	 * @param links the links
	 */
	public ApplicationResourceDTO(final String framework, final String domainId, final String creationTime,
			final String name, final IGearProfile gearProfile, final ApplicationScale scale, final String uuid,
			final String applicationUrl, final String gitUrl, final List<String> aliases,
			final Map<String, String> embeddedCartridgeInfos, final Map<String, Link> links, List<Message> creationLog) {
	super(links, creationLog);
		this.framework = framework;
		this.domainId = domainId;
		this.creationTime = creationTime;
		this.name = name;
		this.uuid = uuid;
		this.gearProfile = gearProfile;
		this.scale = scale;
		this.applicationUrl = applicationUrl;
		this.gitUrl = gitUrl;
		this.aliases = aliases;
		this.embeddedCartridgeInfos = embeddedCartridgeInfos;
	}

	/**
	 * Gets the framework.
	 *
	 * @return the framework
	 */
	public final String getFramework() {
		return framework;
	}

	/**
	 * Gets the domain id.
	 *
	 * @return the domainId
	 */
	public final String getDomainId() {
		return domainId;
	}

	/**
	 * Gets the creation time.
	 *
	 * @return the creationTime
	 */
	public final String getCreationTime() {
		return creationTime;
	}

	/**
	 * Gets the name.
	 *
	 * @return the name
	 */
	public final String getName() {
		return name;
	}

	/**
	 * @return the scalable
	 */
	public final ApplicationScale getApplicationScale() {
		return scale;
	}

	/**
	 * @return the gearProfile
	 */
	public final IGearProfile getGearProfile() {
		return gearProfile;
	}
	
	public final List<IGearGroup> getGearGroups() {
		return gearGroups;
	}

	/**
	 * Gets the uuid.
	 *
	 * @return the uuid
	 */
	public final String getUuid() {
		return uuid;
	}

	/**
	 * @return the applicationUrl
	 */
	public final String getApplicationUrl() {
		return applicationUrl;
	}

	/**
	 * @return the gitUrl
	 */
	public final String getGitUrl() {
		return gitUrl;
	}

	/**
	 * @return the aliases
	 */
	public List<String> getAliases() {
		return aliases;
	}

	public Map<String, String> getEmbeddedCartridgeInfos() {
		return embeddedCartridgeInfos;
	}
	
	@Override
	public String toString() {
		return "ApplicationResourceDTO [name=" + name + ", framework=" + framework + "]";
	}

}
