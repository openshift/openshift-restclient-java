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
package com.openshift.client.cartridge;

import java.net.URL;

import com.openshift.internal.client.CartridgeType;

/**
 * @author André Dietisheim
 */
public interface ICartridge {

	public static final char NAME_VERSION_DELIMITER = '-';

	/**
	 * Returns the name of this cartridge
	 * 
	 * @return the name
	 */
	public abstract String getName();

	/**
	 * Returns a (human readable, nice) display name for this cartridge
	 * 
	 * @return the display name
	 */
	public String getDisplayName();
	
	/**
	 * Returns a description for this cartridge
	 * 
	 * @return the description
	 */
	public String getDescription();
	

	/**
	 * Returns <code>true</code> if this is a downloadable cartridge whose code
	 * may be downloaded at the url returned by {@link #getUrl()} (cartridge
	 * code will get downloaded upon creation).
	 * Examples:
	 * <ul>
	 * <li>go standalone cartridge (https://github.com/smarterclayton/openshift-go-cart)</li>
	 * <li>redis embedded cartridge (https://github.com/smarterclayton/openshift-redis-cart)</li>
	 * <li>foreman embedded cartridge (https://github.com/ncdc/openshift-foreman-cartridge)</li>
	 * </ul>
	 * 
	 * @return <code>true</code> if this is a downloadable cartridge
	 * 
	 * @see #getUrl()
	 */
	public boolean isDownloadable();
	
	/**
	 * Returns the url at which the code for this cartridge may get downloaded.
	 * Returns <code>null</null> if this is not a downloadable cartridge.
	 * 
	 * @return the url if downloadable cartridge or null
	 * 
	 * @see #isDownloadable()
	 */
	public URL getUrl();
	
	public CartridgeType getType();

}