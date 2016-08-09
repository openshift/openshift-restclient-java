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

import java.util.Map;

import org.jboss.dmr.ModelNode;

import com.openshift.internal.util.JBossDmrExtentions;
import com.openshift.restclient.IClient;
import com.openshift.restclient.model.IEvent;
import com.openshift.restclient.model.IObjectReference;

/**
 * @author Jeff Cantrill
 */
public class KubernetesEvent extends KubernetesResource implements IEvent {

	public KubernetesEvent(ModelNode node, IClient client, Map<String, String[]> propertyKeys) {
		super(node, client, propertyKeys);
	}

	@Override
	public String getReason() {
		return asString("reason");
	}

	@Override
	public String getMessage() {
		return asString("message");
	}

	@Override
	public IObjectReference getInvolvedObject() {
		return new ObjectReference(get("involvedObject"));
	}

	@Override
	public String getFirstSeenTimestamp() {
		return asString("firstTimestamp");
	}

	@Override
	public String getLastSeenTimestamp() {
		return asString("lastTimestamp");
	}

	@Override
	public int getCount() {
		return asInt("count");
	}

	@Override
	public String getType() {
		return asString("type");
	}

	@Override
	public IEventSource getEventSource() {
		return new EventSource(get("source"), this.getPropertyKeys());
	}

	private static class EventSource extends ModelNodeAdapter implements IEventSource{

		protected EventSource(ModelNode node, Map<String, String[]> propertyKeys) {
			super(node, propertyKeys);
		}

		@Override
		public String getComponent() {
			return JBossDmrExtentions.asString(getNode(), getPropertyKeys(), "component");
		}

		@Override
		public String getHost() {
			return JBossDmrExtentions.asString(getNode(), getPropertyKeys(), "host");
		}
		
	}
}
