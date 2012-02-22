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
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import com.openshift.express.client.ApplicationLogReader;
import com.openshift.express.client.IApplication;
import com.openshift.express.client.ICartridge;
import com.openshift.express.client.IDomain;
import com.openshift.express.client.IEmbeddableCartridge;
import com.openshift.express.client.IOpenShiftService;
import com.openshift.express.client.IUser;
import com.openshift.express.client.OpenShiftException;
import com.openshift.express.internal.client.utils.Assert;

/**
 * @author André Dietisheim
 */
public class Application extends UserInfoAware implements IApplication {

	private static final String GIT_URI_PATTERN = "ssh://{0}@{1}-{2}.{3}/~/git/{1}.git/";
	private static final String APPLICATION_URL_PATTERN = "https://{0}-{1}.{2}/";
	private static final String DEFAULT_LOGREADER = "defaultLogReader";

	protected String name;
	protected ICartridge cartridge;
	private List<IEmbeddableCartridge> embeddedCartridges;
	protected IOpenShiftService service;
	private HashMap<String, ApplicationLogReader> logReaders = new HashMap<String, ApplicationLogReader>();
	private String healthCheckPath;
	private ApplicationInfo applicationInfo;
	private String creationLog;
	private String uuid;

	public Application(String name, String uuid, String creationLog, String healthCheckPath, ICartridge cartridge,
			InternalUser user, IOpenShiftService service) {
		this(name, uuid, creationLog, healthCheckPath, cartridge, new ArrayList<IEmbeddableCartridge>(), null, user,
				service);
	}

	public Application(String name, String uuid, ICartridge cartridge, ApplicationInfo applicationInfo,
			InternalUser user, IOpenShiftService service) {
		this(name, uuid, null, null, cartridge, null, applicationInfo, user, service);
	}

	protected Application(String name, String uuid, String creationLog, String healthCheckPath, ICartridge cartridge,
			List<IEmbeddableCartridge> embeddedCartridges, ApplicationInfo applicationInfo, InternalUser user,
			IOpenShiftService service) {
		super(user);
		this.name = name;
		this.uuid = uuid;
		this.healthCheckPath = healthCheckPath;
		this.creationLog = creationLog;
		this.cartridge = cartridge;
		this.embeddedCartridges = embeddedCartridges;
		this.applicationInfo = applicationInfo;
		this.service = service;
	}

	public String getName() {
		return name;
	}

	public String getUUID() throws OpenShiftException {
		return uuid;
	}

	public ICartridge getCartridge() {
		return cartridge;
	}

	public Date getCreationTime() throws OpenShiftException {
		return getApplicationInfo().getCreationTime();
	}

	public String getCreationLog() {
		return creationLog;
	}

	public void destroy() throws OpenShiftException {
		getInternalUser().destroy(this);
	}

	public void start() throws OpenShiftException {
		service.startApplication(name, cartridge, getInternalUser());
	}

	public void restart() throws OpenShiftException {
		service.restartApplication(name, cartridge, getInternalUser());
	}

	public void stop() throws OpenShiftException {
		service.stopApplication(name, cartridge, getInternalUser());
	}

	public ApplicationLogReader getLogReader() throws OpenShiftException {
		ApplicationLogReader logReader = null;
		if (logReaders.get(DEFAULT_LOGREADER) == null) {
			logReader = new ApplicationLogReader(this, getInternalUser(), service);
			logReaders.put(DEFAULT_LOGREADER, logReader);
		}
		return logReader;
	}

	public ApplicationLogReader getLogReader(String logFile) throws OpenShiftException {
		ApplicationLogReader logReader = null;
		if (logReaders.get(logFile) == null) {
			logReader = new ApplicationLogReader(this, getInternalUser(), service, logFile);
			logReaders.put(logFile, logReader);
		}
		return logReader;
	}

	public String getGitUri() throws OpenShiftException {
		IDomain domain = getInternalUser().getDomain();
		if (domain == null) {
			return null;
		}
		return MessageFormat
				.format(GIT_URI_PATTERN, getUUID(), getName(), domain.getNamespace(), domain.getRhcDomain());
	}

	public String getApplicationUrl() throws OpenShiftException {
		IDomain domain = getInternalUser().getDomain();
		if (domain == null) {
			return null;
		}
		return MessageFormat.format(APPLICATION_URL_PATTERN, name, domain.getNamespace(), domain.getRhcDomain());
	}

	public String getHealthCheckUrl() throws OpenShiftException {
		return getApplicationUrl() + '/' + healthCheckPath;
	}

	public void addEmbbedCartridge(IEmbeddableCartridge embeddedCartridge) throws OpenShiftException {
		service.addEmbeddedCartridge(getName(), embeddedCartridge, getInternalUser());
		Assert.isTrue(embeddedCartridge instanceof EmbeddableCartridge);
		((EmbeddableCartridge) embeddedCartridge).setApplication(this);
		this.embeddedCartridges.add(embeddedCartridge);
	}

	public void addEmbbedCartridges(List<IEmbeddableCartridge> embeddedCartridges) throws OpenShiftException {
		for (IEmbeddableCartridge cartridge : embeddedCartridges) {
			// TODO: catch exceptions when removing cartridges, contine removing
			// and report the exceptions that occurred<
			addEmbbedCartridge(cartridge);
		}
	}

	public void removeEmbbedCartridge(IEmbeddableCartridge embeddedCartridge) throws OpenShiftException {
		if (!hasEmbeddedCartridge(embeddedCartridge.getName())) {
			throw new OpenShiftException("There's no cartridge \"{0}\" embedded to the application \"{1}\"",
					cartridge.getName(), getName());
		}
		service.removeEmbeddedCartridge(getName(), embeddedCartridge, getInternalUser());
		embeddedCartridges.remove(embeddedCartridge);
	}

	public void removeEmbbedCartridges(List<IEmbeddableCartridge> embeddedCartridges) throws OpenShiftException {
		for (IEmbeddableCartridge cartridge : embeddedCartridges) {
			// TODO: catch exceptions when removing cartridges, contine removing
			// and report the exceptions that occurred<
			removeEmbbedCartridge(cartridge);
		}
	}

	public List<IEmbeddableCartridge> getEmbeddedCartridges() throws OpenShiftException {
		if (embeddedCartridges == null) {
			this.embeddedCartridges = new ArrayList<IEmbeddableCartridge>();
			for (EmbeddableCartridgeInfo cartridgeInfo : getApplicationInfo().getEmbeddedCartridges()) {
				embeddedCartridges.add(new EmbeddableCartridge(cartridgeInfo.getName(), this));
			}
		}
		return embeddedCartridges;
	}

	public boolean hasEmbeddedCartridge(String cartridgeName) throws OpenShiftException {
		return getEmbeddedCartridge(cartridgeName) != null;
	}

	public IEmbeddableCartridge getEmbeddedCartridge(String cartridgeName) throws OpenShiftException {
		IEmbeddableCartridge embeddedCartridge = null;
		for (IEmbeddableCartridge cartridge : getEmbeddedCartridges()) {
			if (cartridgeName.equals(cartridge.getName())) {
				embeddedCartridge = cartridge;
				break;
			}
		}
		return embeddedCartridge;
	}

	protected IOpenShiftService getService() {
		return service;
	}

	protected ApplicationInfo getApplicationInfo() throws OpenShiftException {
		if (applicationInfo == null) {
			this.applicationInfo = getUserInfo().getApplicationInfoByName(getName());
			if (applicationInfo == null) {
				throw new OpenShiftException("Could not find info for application {0}", getName());
			}
		}
		return applicationInfo;
	}

	public boolean waitForAccessible(long timeout) throws OpenShiftException {
		if (healthCheckPath == null) {
			return true;
		}
		return service.waitForApplication(getHealthCheckUrl(), timeout);
	}

	public IUser getUser() {
		return getInternalUser();
	}
	
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	public boolean equals(Object object) {
		if (this == object)
			return true;
		if (object == null)
			return false;
		if (getClass() != object.getClass())
			return false;
		Application other = (Application) object;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}

	public String toString() {
		return name;
	}
}
