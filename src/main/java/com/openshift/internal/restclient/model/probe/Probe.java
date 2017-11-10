/*******************************************************************************
 * Copyright (c) 2017 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package com.openshift.internal.restclient.model.probe;

import static com.openshift.internal.util.JBossDmrExtentions.asInt;
import static com.openshift.internal.util.JBossDmrExtentions.set;

import java.util.HashMap;

import org.jboss.dmr.ModelNode;

import com.openshift.internal.restclient.model.ModelNodeAdapter;
import com.openshift.restclient.model.probe.IProbe;

/**
 * @author Andre Dietisheim
 */
public class Probe extends ModelNodeAdapter implements IProbe {
	
	private static final String INITIAL_DELAY_SECONDS = "initialDelaySeconds";
	private static final String TIMEOUT_SECONDS = "timeoutSeconds";
	private static final String PERIOD_SECONDS = "periodSeconds";
	private static final String SUCCESS_THRESHOLD = "successThreshold";
	private static final String FAILURE_THRESHOLD = "failureThreshold";

	public Probe(ModelNode node) {
		super(node, new HashMap<String, String []>());
	}

	@Override
	public void setInitialDelaySeconds(int delay) {
		set(getNode(), getPropertyKeys(), INITIAL_DELAY_SECONDS, delay);
	}

	@Override
	public int getInitialDelaySeconds() {
		return asInt(getNode(), getPropertyKeys(), INITIAL_DELAY_SECONDS);
	}

	@Override
	public void setPeriodSeconds(int period) {
		set(getNode(), getPropertyKeys(), PERIOD_SECONDS, period);
	}

	@Override
	public int getPeriodSeconds() {
		return asInt(getNode(), getPropertyKeys(), PERIOD_SECONDS);
	}

	@Override
	public void setSuccessThreshold(int threshold) {
		set(getNode(), getPropertyKeys(), SUCCESS_THRESHOLD, threshold);
	}

	@Override
	public int getSuccessThreshold() {
		return asInt(getNode(), getPropertyKeys(), SUCCESS_THRESHOLD);
	}
	
	@Override
	public void setFailureThreshold(int failureThreshold) {
		set(getNode(), getPropertyKeys(), FAILURE_THRESHOLD, failureThreshold);
	}
	
	@Override
	public int getFailureThreshold() {
		return asInt(getNode(), getPropertyKeys(), FAILURE_THRESHOLD);
	}
	
	
	@Override
	public void setTimeoutSeconds(int timeout) {
		set(getNode(), getPropertyKeys(), TIMEOUT_SECONDS, timeout);
	}

	@Override
	public int getTimeoutSeconds() {
		return asInt(getNode(), getPropertyKeys(), TIMEOUT_SECONDS);
	}
}
