/*******************************************************************************
 * Copyright (c) 2015 Red Hat, Inc. Distributed under license by Red Hat, Inc.
 * All rights reserved. This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Red Hat, Inc.
 ******************************************************************************/
package com.openshift.internal.restclient.model.properties;

/**
 * Keys used to determine where a given property is for a given resource
 * 
 * @author Jeff Cantrill
 */
public interface ResourcePropertyKeys {
	
	static final String APIVERSION  = "apiVersion";
	static final String KIND = "kind";

	static final String ANNOTATIONS = "metadata.annotations";
	static final String CREATION_TIMESTAMP = "metadata.creationTimestamp";
	static final String LABELS = "metadata.labels";
	static final String METADATA = "metadata";
	static final String METADATA_NAME = "metadata.name";
	static final String METADATA_RESOURCE_VERSION = "metadata.resourceVersion";
	static final String NAMESPACE = "metadata.namespace";

	static final String FROM = "from";
	static final String NAME = "name";
	static final String OBJECTS = "objects";
	static final String PORTS = "ports";
	static final String PROTOCOL = "protocol";
	static final String RESOURCE_VERSION = "resourceVersion";
	static final String VALUE = "value";
	static final String TYPE = "type";
}
