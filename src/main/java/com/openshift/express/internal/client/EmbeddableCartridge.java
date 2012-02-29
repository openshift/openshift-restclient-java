/******************************************************************************* 
 * Copyright (c) 2011 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/
package com.openshift.express.internal.client;

import java.text.MessageFormat;
import java.util.Iterator;
import java.util.List;

import com.openshift.express.client.Cartridge;
import com.openshift.express.client.ICartridge;
import com.openshift.express.client.IEmbeddableCartridge;
import com.openshift.express.client.IOpenShiftService;
import com.openshift.express.client.IUser;
import com.openshift.express.client.OpenShiftException;

/**
 * A cartridge that may be embedded into an application. This class is no enum
 * since we dont know all available types and they may change at any time.
 * 
 * @author André Dietisheim
 */
public class EmbeddableCartridge extends Cartridge implements IEmbeddableCartridge {
	
	protected static final String JENKINS_CLIENT = "jenkins-client";
	protected static final String MYSQL = "mysql";
	protected static final String PHPMYADMIN = "phpmyadmin";

	private String creationLog;
	private String url;
	private Application application;
	
	public EmbeddableCartridge(IOpenShiftService service, IUser user) {
		super(service, user);
	}
	
	public EmbeddableCartridge(IOpenShiftService service, IUser user, Application application) {
		super(service, user);
		this.application = application;
	}
	
	public EmbeddableCartridge(IOpenShiftService service, IUser user, String url) {
		super(service, user);
		this.url = url;
	}
	
	public EmbeddableCartridge(IOpenShiftService service, IUser user, String url, Application application) {
		super(service, user);
		this.url = url;
		this.application = application;
	}


	public EmbeddableCartridge(String name) {
		this(name, (Application) null);
	}

	public EmbeddableCartridge(String name, Application application) {
		super(name);
		this.application = application;
	}

	public EmbeddableCartridge(String name, String url) {
		this(name, url, null);
	}

	public EmbeddableCartridge(String name, String url, Application application) {
		this(name, application);
		this.url = url;
	}

	public String getUrl() throws OpenShiftException {
		if (url == null) {
			EmbeddableCartridgeInfo cartridgeInfo = getApplicationInfo().getEmbeddedCartridge(getName());
			update(cartridgeInfo);
		}
		return url;
	}

	protected void update(EmbeddableCartridgeInfo cartridgeInfo) throws OpenShiftException {
		setName(cartridgeInfo.getName());
		this.url = cartridgeInfo.getUrl();
	}
	
	public void setCreationLog(String creationLog) {
		this.creationLog = creationLog;
	}

	public String getCreationLog() {
		return creationLog;
	}
	
	protected ApplicationInfo getApplicationInfo() throws OpenShiftException {
		if (application == null) {
			throw new OpenShiftException(
					MessageFormat.format("Could not get application info for cartridge {0}, no application was set to it", getName()));
		}
		return application.getApplicationInfo();
	}
	
	protected void setApplication(Application application) {
		this.application = application;
	}
	
	protected String getCartridgeName(String cartridgeType) throws OpenShiftException {
		List<IEmbeddableCartridge> cartridges = service.getEmbeddableCartridges(user);
		
		Iterator<IEmbeddableCartridge> i = cartridges.iterator();
		while (i.hasNext()){
			IEmbeddableCartridge cartridge = i.next();
			if (cartridge.getName().contains(cartridgeType))
				return cartridge.getName();
		}
		
		throw new OpenShiftException("No cartridge found for type " + cartridgeType);
	}

}