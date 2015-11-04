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
package com.openshift.internal.restclient.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.jboss.dmr.ModelNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.openshift.internal.restclient.http.UrlConnectionHttpClient;
import com.openshift.internal.restclient.model.limit.Limit;
import com.openshift.restclient.IClient;
import com.openshift.restclient.model.ILimitRange;
import com.openshift.restclient.model.limit.ILimit;
import com.openshift.restclient.model.limit.LimitType;

/**
 * @author HyunsooKim1112
 */
public class LimitRange extends KubernetesResource implements ILimitRange {

	private static final Logger LOGGER = LoggerFactory.getLogger(UrlConnectionHttpClient.class);
	
	private static final String LIMIT_RANGE_LIMITS = "spec.limits";
	private static final String LIMIT_RANGE_MAX_MEMORY = "max.memory";
	private static final String LIMIT_RANGE_MAX_CPU = "max.cpu";
	private static final String LIMIT_RANGE_MIN_MEMORY = "min.memory";
	private static final String LIMIT_RANGE_MIN_CPU = "min.cpu";
	private static final String LIMIT_RANGE_DEFAULT_MEMORY = "default.memory";
	private static final String LIMIT_RANGE_DEFAULT_CPU = "default.cpu";

	public LimitRange(ModelNode node, IClient client,
			Map<String, String[]> propertyKeys) {
		super(node, client, propertyKeys);
		// TODO Auto-generated constructor stub
	}

	@Override
	public List<ILimit> getLimits() {
		List<ILimit> limits = new ArrayList<ILimit>();
		List<ModelNode> list = get(LIMIT_RANGE_LIMITS).asList();
		for (ModelNode node : list) {
			String type = node.get("type").asString();
			switch (type) {
			case LimitType.POD:
				limits.add(new Limit(LimitType.POD, asString(node, LIMIT_RANGE_MAX_CPU),
						asString(node, LIMIT_RANGE_MIN_CPU), asString(node,	LIMIT_RANGE_MAX_MEMORY),
						asString(node, LIMIT_RANGE_MIN_MEMORY), "", ""));
				break;
			case LimitType.CONTAINER:
				limits.add(new Limit(LimitType.CONTAINER, asString(node, LIMIT_RANGE_MAX_CPU),
						asString(node, LIMIT_RANGE_MIN_CPU), asString(node,	LIMIT_RANGE_MAX_MEMORY),
						asString(node, LIMIT_RANGE_MIN_MEMORY), asString(node, LIMIT_RANGE_DEFAULT_CPU),
						asString(node, LIMIT_RANGE_DEFAULT_MEMORY)));
				break;
			default:
			}
		}
		return limits;
	}

	@Override
	public void setLimits(List<ILimit> iLimits) {
		ModelNode list = get(LIMIT_RANGE_LIMITS);
		List<ModelNode> limitRageList = list.asList();
		list.clear();
		
		for (ILimit limit : iLimits) {
			for (ModelNode node : limitRageList) {
				String type = node.get("type").asString();
						
				if (limit.getType().equals(type)) {
					switch (type) {
					case LimitType.POD:
						node.get(getPath(LIMIT_RANGE_MAX_CPU)).set(limit.getMaxCpu());
						node.get(getPath(LIMIT_RANGE_MIN_CPU)).set(limit.getMinCpu());
						node.get(getPath(LIMIT_RANGE_MAX_MEMORY)).set(limit.getMaxMemory());
						node.get(getPath(LIMIT_RANGE_MIN_MEMORY)).set(limit.getMinMemory());
						break;
					case LimitType.CONTAINER:
						node.get(getPath(LIMIT_RANGE_MAX_CPU)).set(limit.getMaxCpu());
						node.get(getPath(LIMIT_RANGE_MIN_CPU)).set(limit.getMinCpu());
						node.get(getPath(LIMIT_RANGE_DEFAULT_CPU)).set(limit.getDefaultCpu());
						node.get(getPath(LIMIT_RANGE_MAX_MEMORY)).set(limit.getMaxMemory());
						node.get(getPath(LIMIT_RANGE_MAX_MEMORY)).set(limit.getMaxMemory());
						node.get(getPath(LIMIT_RANGE_DEFAULT_MEMORY)).set(limit.getDefaultMemory());
						break;
					default:
					}
					
					list.add(node);
				}
			}
		}
		
	}

}
