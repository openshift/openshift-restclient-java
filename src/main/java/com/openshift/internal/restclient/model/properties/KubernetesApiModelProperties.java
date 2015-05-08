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
	static final Map<String, String []> V1BETA3_KUBERNETES_MAP = new HashMap<String, String []>(){{
		put(ANNOTATIONS, new String [] {"metadata", "annotations"});
		put(APIVERSION, new String [] {"apiVersion"});
		put(CREATION_TIMESTAMP, new String []  {"metadata","creationTimestamp"});
		put(LABELS, new String []  {"metadata","labels"});
		put(NAME , new String []  {"metadata","name"});
		put(NAMESPACE, new String []  {"metadata","namespace"});
		
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
	@Deprecated
	static final Map<String, String []> V1BETA1_KUBERNETES_MAP = new HashMap<String, String []>(){
	{
		put(ANNOTATIONS, new String [] {"annotations"});
		put(APIVERSION, new String [] {"apiVersion"});
		put(CREATION_TIMESTAMP, new String []  {"creationTimestamp"});
		put(LABELS, new String []  {"labels"});
		put(NAME , new String []  {"id"});
		put(NAMESPACE, new String []  {"namespace"});
		
		put(REPLICATION_CONTROLLER_REPLICA_COUNT, new String [] {"desiredState", "replicas"});
		put(REPLICATION_CONTROLLER_REPLICA_SELECTOR, new String [] {"desiredState", "replicaSelector"});
		put(REPLICATION_CONTROLLER_CONTAINERS, new String [] {"desiredState", "podTemplate","desiredState","manifest","containers"});
		put(REPLICATION_CONTROLLER_CURRENT_REPLICA_COUNT, new String [] {"currentState", "replicas"});
		
		put(POD_IP, new String[]{"currentState","podIP"});
		put(POD_HOST, new String[]{"currentState","host"});
		put(POD_STATUS, new String[]{"currentState","status"});
		put(POD_CONTAINERS, new String[]{"desiredState","manifest","containers"});
		put(SERVICE_CONTAINER_PORT, new String [] {"containerPort"});
		put(SERVICE_PORT, new String [] {"port"});
		put(SERVICE_SELECTOR, new String [] {"selector"});
		put(SERVICE_PORTALIP, new String [] {"portalIP"});
		put(STATUS_MESSAGE, new String [] {"message"});
		put(STATUS_CODE, new String [] {"code"});
		put(STATUS_STATUS, new String [] {"status"});
	}};
}
