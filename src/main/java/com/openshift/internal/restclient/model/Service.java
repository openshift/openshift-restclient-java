/*******************************************************************************
 * Copyright (c) 2015 Red Hat, Inc. Distributed under license by Red Hat, Inc.
 * All rights reserved. This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Red Hat, Inc.
 ******************************************************************************/
package com.openshift.internal.restclient.model;

import static com.openshift.internal.restclient.capability.CapabilityInitializer.initializeCapabilities;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.jboss.dmr.ModelNode;

import com.openshift.restclient.IClient;
import com.openshift.restclient.ResourceKind;
import com.openshift.restclient.model.IPod;
import com.openshift.restclient.model.IService;
import com.openshift.restclient.model.IServicePort;

/**
 * @author Jeff Cantrill
 */
public class Service extends KubernetesResource implements IService {


	private static final String SERVICE_SELECTOR = "spec.selector";
	private static final String SERVICE_PORT = "spec.ports";

	public Service(ModelNode node, IClient client, Map<String, String []> propertyKeys) {
		super(node, client, propertyKeys);
		initializeCapabilities(getModifiableCapabilities(), this, getClient());
	}
	
	@Override
	public void setPort(int port){
		IServicePort lowestPort = getLowestPort();
		if(lowestPort == null) {
			lowestPort = addPort(0,0);
		}
		lowestPort.setPort(port);
	}
	
	public IServicePort addPort(int port, int targetPort) {
		return addPort(port, targetPort, null);
	}
	
	public IServicePort addPort(int port, int targetPort, String name) {
        ServicePort servicePort = new ServicePort(get(SERVICE_PORT).add());
        if(port > 0) servicePort.setPort(port);
        if(targetPort >0) servicePort.setTargetPort(targetPort);
        if(StringUtils.isNotEmpty(name)) servicePort.setName(name);
        return servicePort;
    }
	
	@Override
	public int getPort(){
		IServicePort port = getLowestPort();
		return port != null ? port.getPort() : 0;
	}
	
	private IServicePort getLowestPort() {
		List<IServicePort> ports = getPorts();
		return ports.size() == 0 ? null : ports.get(0);
	}
	@Override
	public List<IServicePort> getPorts() {
		return getPorts(false);
	}
	
	private List<IServicePort> getPorts(boolean modifiable) {
		List<IServicePort> ports = new ArrayList<>();
		if(!get(SERVICE_PORT).isDefined()) return ports;
		for (ModelNode node : get(SERVICE_PORT).asList()) {
			ports.add(new ServicePort(node));
		}
		Collections.sort(ports, new Comparator<IServicePort>() {
			@Override
			public int compare(IServicePort first, IServicePort second) {
				Integer port0 = first.getPort();
				Integer port1 = second.getPort();
				return port0.compareTo(port1);
			}
		});	
		return modifiable ? ports : Collections.unmodifiableList(ports);
	}

	@Override
	public void setPorts(List<IServicePort> ports) {
		ModelNode portspec = get(SERVICE_PORT).clear();
		for (IServicePort port : ports) {
			new ServicePort(portspec.add(), port);
		}
	}

	@Override
	public Map<String, String> getSelector(){
		return asMap(SERVICE_SELECTOR);
	}
	
	@Override
	public void setSelector(Map<String, String> selector) {
		ModelNode node = new ModelNode();
		for (Map.Entry<String, String> entry : selector.entrySet()) {
			node.get(entry.getKey()).set(entry.getValue());
		}
		get(SERVICE_SELECTOR).set(node);
	}
	
	
	@Override
	public void setSelector(String key, String value) {
		get(SERVICE_SELECTOR).get(key).set(value);
	}

	@Override
	public void setTargetPort(int port) {
		IServicePort portspec = getLowestPort();
		if(portspec == null) {
			portspec = addPort(0,0);
		}
		portspec.setTargetPort(port);
	}
	
	@Override
	public String getTargetPort() {
		IServicePort port = getLowestPort();
		return port != null ? port.getTargetPort() : "0";
	}

	@Override @Deprecated
	public String getPortalIP() {
		String tmp = asString("spec.portalIP");
		if (StringUtils.isBlank(tmp)) {
		    tmp = getClusterIP();
		}
		return tmp;
	}
	
	@Override
	public String getClusterIP() {
	    return asString("spec.clusterIP");
	}

	@Override
	public List<IPod> getPods() {
		if(getClient() == null) return new ArrayList<IPod>();
		return getClient().list(ResourceKind.POD, getNamespace(), getSelector());
	}

	@Override
	public String getType() { return asString("spec.type"); }

	@Override
	public void setType(String type) { set("spec.type", type); }
}
