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
import com.openshift.client.IGearProfile;
import com.openshift.client.Messages;

/**
 * The Class ApplicationDTO.
 *
 * @author Xavier Coulon
 */
public class ApplicationResourceDTO extends BaseResourceDTO {

	/** the application's framework (or main cartridge). */
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
	
	/** the application's url. */
	private final String applicationUrl;
	
	/** the ssh url. */
	private final String sshUrl;
	
	/** the application's git repository url. */
	private final String gitUrl;
	
	/** the url for the git repo with the initial code for this application. */
	private final String initialGitUrl;

	private Map<String, CartridgeResourceDTO> cartridgesByName;

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
	ApplicationResourceDTO(final String framework, final String domainId, final String creationTime,
			final String name, final IGearProfile gearProfile, final ApplicationScale scale, final String uuid,
			final String applicationUrl, final String sshUrl, final String gitUrl, final String initialGitUrl, final List<String> aliases,
			final Map<String, CartridgeResourceDTO> cartridgeByName, final Map<String, Link> links, Messages messages) {
	super(links, messages);
		this.framework = framework;
		this.domainId = domainId;
		this.creationTime = creationTime;
		this.name = name;
		this.uuid = uuid;
		this.gearProfile = gearProfile;
		this.scale = scale;
		this.applicationUrl = applicationUrl;
		this.sshUrl = sshUrl;
		this.gitUrl = gitUrl;
		this.initialGitUrl = initialGitUrl;
		this.aliases = aliases;
		this.cartridgesByName = cartridgeByName;
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

	/**
	 * Gets the uuid.
	 *
	 * @return the uuid
	 */
	public final String getUuid() {
		return uuid;
	}

	/**
	 * @return the url at which this application may be reached
	 */
	public final String getApplicationUrl() {
		return applicationUrl;
	}
	
	/**
	 * @return the url to use to connect with ssh
	 */
	public String getSshUrl() {
		return sshUrl;
	}


	/**
	 * @return the url at which this applications git repo may be reached at
	 */
	public final String getGitUrl() {
		return gitUrl;
	}

	/**
	 * @return the initial git repo url for the initial code of this application
	 */
	public final String getInitialGitUrl() {
		return initialGitUrl;
	}

	/**
	 * @return the aliases
	 */
	public List<String> getAliases() {
		return aliases;
	}

	public Map<String, CartridgeResourceDTO> getCartridges() {
		return cartridgesByName;
	}
	
	@Override
	public String toString() {
		return "ApplicationResourceDTO ["
				+ "name=" + name
				+ ", framework=" + framework
				+ "]";
	}


}
