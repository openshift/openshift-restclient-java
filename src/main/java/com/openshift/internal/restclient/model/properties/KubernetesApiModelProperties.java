/*******************************************************************************
 * Copyright (c) 2015 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package com.openshift.internal.restclient.model.properties;

import java.util.HashMap;
import java.util.Map;

/**
 * @author jeff.cantrill
 */
public interface KubernetesApiModelProperties extends ResourcePropertyKeys{
	
	@SuppressWarnings("serial")
	static final Map<String, String []> V1_KUBERNETES_MAP = new HashMap<String, String []>(){{
		put(ANNOTATIONS, new String [] {"metadata", "annotations"});
		put(APIVERSION, new String [] {"apiVersion"});
		put(CREATION_TIMESTAMP, new String []  {"metadata","creationTimestamp"});
		put(KIND, new String[] { "kind" });
		put(LABELS, new String []  {"metadata","labels"});
		put(NAME , new String []  {"metadata","name"});
		put(NAMESPACE, new String []  {"metadata","namespace"});
		put(PORTS, new String []  {"ports"});

		put(PORTS_CONTAINER_PORT, new String []  {"containerPort"});
		put(PORTS_PROTOCOL, new String []  {"protocol"});
		put(PORTS_NAME, new String []  {"name"});
		

		put(POD_IP, new String[]{"status","podIP"});
		put(POD_HOST, new String[]{"status","hostIP"});
		put(POD_STATUS, new String[]{"status","phase"});
		put(POD_CONTAINERS, new String[]{"spec","containers"});
		
		put(REPLICATION_CONTROLLER_REPLICA_COUNT, new String [] {"spec", "replicas"});
		put(REPLICATION_CONTROLLER_REPLICA_SELECTOR, new String [] {"spec", "selector"});
		put(REPLICATION_CONTROLLER_CONTAINERS, new String [] {"spec", "template","spec","containers"});
		put(REPLICATION_CONTROLLER_CURRENT_REPLICA_COUNT, new String [] {"status", "replicas"});
			
		put(SERVICE_CONTAINER_PORT, new String [] {"containerPort"});
		put(SERVICE_PORT, new String [] {"spec","ports"});
		put(SERVICE_SELECTOR, new String [] {"spec","selector"});
		put(SERVICE_PORTALIP, new String [] {"spec","portalIP"});
		put(STATUS_MESSAGE, new String [] {"message"});
		put(STATUS_CODE, new String [] {"code"});
		put(STATUS_STATUS, new String [] {"status"});
		
		put(STATUS_MESSAGE, new String [] {"message"});
		put(STATUS_CODE, new String [] {"code"});
		put(STATUS_STATUS, new String [] {"status"});
	}};

	@SuppressWarnings("serial")
	static final Map<String, String []> V1BETA3_KUBERNETES_MAP = new HashMap<String, String []>(){{
		put(ANNOTATIONS, new String [] {"metadata", "annotations"});
		put(APIVERSION, new String [] {"apiVersion"});
		put(CREATION_TIMESTAMP, new String []  {"metadata","creationTimestamp"});
		put(KIND, new String[] { "kind" });
		put(LABELS, new String []  {"metadata","labels"});
		put(NAME , new String []  {"metadata","name"});
		put(NAMESPACE, new String []  {"metadata","namespace"});
		put(PORTS, new String []  {"ports"});

		put(PORTS_CONTAINER_PORT, new String []  {"containerPort"});
		put(PORTS_PROTOCOL, new String []  {"protocol"});
		put(PORTS_NAME, new String []  {"name"});
		
		put(POD_IP, new String[]{"status","podIP"});
		put(POD_HOST, new String[]{"status","hostIP"});
		put(POD_STATUS, new String[]{"status","phase"});
		put(POD_CONTAINERS, new String[]{"spec","containers"});
		
		put(REPLICATION_CONTROLLER_REPLICA_COUNT, new String [] {"spec", "replicas"});
		put(REPLICATION_CONTROLLER_REPLICA_SELECTOR, new String [] {"spec", "selector"});
		put(REPLICATION_CONTROLLER_CONTAINERS, new String [] {"spec", "template","spec","containers"});
		put(REPLICATION_CONTROLLER_CURRENT_REPLICA_COUNT, new String [] {"status", "replicas"});
		
		put(SERVICE_CONTAINER_PORT, new String [] {"containerPort"});
		put(SERVICE_PORT, new String [] {"spec","ports"});
		put(SERVICE_SELECTOR, new String [] {"spec","selector"});
		put(SERVICE_PORTALIP, new String [] {"spec","portalIP"});
		put(STATUS_MESSAGE, new String [] {"message"});
		put(STATUS_CODE, new String [] {"code"});
		put(STATUS_STATUS, new String [] {"status"});
		
		put(STATUS_MESSAGE, new String [] {"message"});
		put(STATUS_CODE, new String [] {"code"});
		put(STATUS_STATUS, new String [] {"status"});
	}};
	
}
