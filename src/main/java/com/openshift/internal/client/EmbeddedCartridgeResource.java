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
package com.openshift.internal.client;

import java.util.regex.Pattern;

import com.openshift.client.IApplication;
import com.openshift.client.OpenShiftException;
import com.openshift.client.cartridge.EmbeddableCartridge;
import com.openshift.client.cartridge.IEmbeddableCartridge;
import com.openshift.client.cartridge.IEmbeddedCartridge;
import com.openshift.internal.client.response.CartridgeResourceDTO;
import com.openshift.internal.client.response.ResourceProperties;
import com.openshift.internal.client.response.ResourceProperty;

/**
 * A cartridge that is embedded into an application. The cartridge is added when
 * the application resource is loaded. The cartridge resource is only loaded
 * from backend when detail informations are needed (see #getDisplayName,
 * {@link #getDescription()}.
 * 
 * @author André Dietisheim
 */
public class EmbeddedCartridgeResource extends AbstractOpenShiftResource implements IEmbeddedCartridge {

	private static final Pattern NAME_URL_PATTERN = Pattern.compile("url", Pattern.CASE_INSENSITIVE);
	
	private static final String LINK_DELETE_CARTRIDGE = "DELETE";

	private final String name;
	private String displayName;
	private String description;
	private final CartridgeType type;
	private final ApplicationResource application;
	private ResourceProperties properties;

	protected EmbeddedCartridgeResource(final CartridgeResourceDTO dto, final ApplicationResource application) {
		super(application.getService(), dto.getLinks(), dto.getMessages());
		this.name = dto.getName();
		this.type = CartridgeType.EMBEDDED;
		this.displayName = dto.getDisplayName();
		this.description = dto.getDescription();
		this.properties = dto.getProperties();
		this.application = application;
	}

	protected void update(CartridgeResourceDTO dto) {
		this.description = dto.getDescription();
		this.displayName = dto.getDisplayName();
		this.properties = dto.getProperties();
		setLinks(dto.getLinks());
	}

	public String getName() {
		return name;
	}

	public String getDisplayName() {
		// only available in resource, not in embedded block within application
		if (!isResourceLoaded()) {
			refresh();
		}
		return displayName;
	}

	public String getDescription() {
		// only available in resource, not in embedded block within application
		if (!isResourceLoaded()) {
			refresh();
		}
		return description;
	}

	protected CartridgeType getType() {
		return type;
	}

	public String getUrl() throws OpenShiftException {
		for (ResourceProperty property : properties.getAll()) {
			if (NAME_URL_PATTERN.matcher(property.getName()).find()) {
				return property.getValue();
			}
		}
		return null;
	}

	public IApplication getApplication() {
		return application;
	}

	/**
	 * Refreshes the content of this embedded cartridge. Causes all embedded
	 * cartridges of the same application to get updated.
	 * 
	 * @see #update(CartridgeResourceDTO)
	 * @see ApplicationResource#refreshEmbeddedCartridges()
	 */
	@Override
	public void refresh() throws OpenShiftException {
		// tell application to refresh all embedded cartridges
		application.refreshEmbeddedCartridges(); 
	}

	public void destroy() throws OpenShiftException {
		if (!isResourceLoaded()) {
			application.refreshEmbeddedCartridges();
		}
		new DeleteCartridgeRequest().execute();
		application.removeEmbeddedCartridge(this);
	}

	protected boolean isResourceLoaded() {
		return areLinksLoaded();
	}

	@Override
	public ResourceProperties getProperties() {
		return properties;
	}

	private class DeleteCartridgeRequest extends ServiceRequest {

		protected DeleteCartridgeRequest() {
			super(LINK_DELETE_CARTRIDGE);
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	/**
	 * TODO: implement fully correct #equals and #hashcode. The current
	 * implementation only ensures that {@link EmbeddedCartridgeResource} may be
	 * compared to {@link EmbeddableCartridge}.
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(IEmbeddableCartridge.class.isAssignableFrom(obj.getClass())))
			return false;
		IEmbeddableCartridge other = (IEmbeddableCartridge) obj;
		if (name == null) {
			if (other.getName() != null)
				return false;
		} else if (!name.equals(other.getName()))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "EmbeddedCartridgeResource [" +
				"name=" + name
				+ ", type=" + type
				+ ", application=" + application.getName()
				+ "]";
	}

}