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

import java.net.URL;

import com.openshift.client.IApplication;
import com.openshift.client.OpenShiftException;
import com.openshift.client.cartridge.EmbeddableCartridge;
import com.openshift.client.cartridge.IEmbeddableCartridge;
import com.openshift.client.cartridge.IEmbeddedCartridge;
import com.openshift.internal.client.response.CartridgeResourceDTO;
import com.openshift.internal.client.response.CartridgeResourceProperties;

/**
 * A cartridge that is embedded into an application. 
 * 
 * @author André Dietisheim
 */
public class EmbeddedCartridgeResource extends AbstractOpenShiftResource implements IEmbeddedCartridge {

	private static final String LINK_DELETE_CARTRIDGE = "DELETE";

	private final String name;
	private String displayName;
	private String description;
	private final CartridgeType type;
	private URL url;
	private final ApplicationResource application;
	private CartridgeResourceProperties properties;

	protected EmbeddedCartridgeResource(final CartridgeResourceDTO dto, final ApplicationResource application) {
		super(application.getService(), dto.getLinks(), dto.getMessages());
		this.name = dto.getName();
		this.displayName = dto.getDisplayName();
		this.description = dto.getDescription();
		this.type = CartridgeType.EMBEDDED;
		this.url = dto.getUrl();
		this.properties = dto.getProperties();
		this.application = application;
	}

	public String getName() {
		return name;
	}

	public String getDisplayName() {
		return displayName;
	}

	public String getDescription() {
		return description;
	}

	protected CartridgeType getType() {
		return type;
	}

	public URL getUrl() {
		return url;
	}
	
	public boolean isDownloadable() {
		return url != null;
	}
	
	public IApplication getApplication() {
		return application;
	}

	protected void update(CartridgeResourceDTO dto) {
		this.description = dto.getDescription();
		this.displayName = dto.getDisplayName();
		this.url = dto.getUrl();
		this.properties = dto.getProperties();
		setLinks(dto.getLinks());
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
		new DeleteCartridgeRequest().execute();
		application.removeEmbeddedCartridge(this);
	}

	@Override
	public CartridgeResourceProperties getProperties() {
		return properties;
	}

	private class DeleteCartridgeRequest extends ServiceRequest {

		private DeleteCartridgeRequest() {
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
				+ "url=" + url
				+ ", displayName=" + displayName
				+ ", description=" + description
				+ ", type=" + type
				+ ", application=" + application.getName()
				+ "]";
	}

}