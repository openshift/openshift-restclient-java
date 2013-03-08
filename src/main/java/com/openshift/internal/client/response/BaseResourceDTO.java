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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The Class BaseResourceDTO.
 * 
 * @param <T>
 *            the generic type
 */
public abstract class BaseResourceDTO {

	/** the indexed map of links to perform operations from this resource. */
	private final Map<String, Link> links;
	private final List<Message> creationLog;

	protected BaseResourceDTO() {
		this(new HashMap<String, Link>(), null);
	}

	/**
	 * Instantiates a new base resource dto.
	 * 
	 * @param links
	 *            the links
	 */
	protected BaseResourceDTO(final Map<String, Link> links, List<Message> creationLog) {
		this.links = links;
		this.creationLog = creationLog;
	}

	/**
	 * Gets the links.
	 * 
	 * @return all the links
	 */
	public final Map<String, Link> getLinks() {
		return links;
	}

	/**
	 * Gets the link.
	 * 
	 * @param name
	 *            the name of the link to look for.
	 * @return the named link
	 */
	public final Link getLink(String name) {
		return links.get(name);
	}

	/**
	 * Returns the creationLog that were reported when this resource was created.
	 * May be <code>null</code> if retrieved in a session in which we did not
	 * create this resource.
	 * 
	 * @return the creationLog that were reported when this resource was created
	 */
	public List<Message> getCreationLog() {
		return creationLog;
	}

	public boolean hasCreationLog() {
		return creationLog != null
				&& creationLog.size() > 0;
	}
}
