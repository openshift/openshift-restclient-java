/******************************************************************************* 
 * Copyright (c) 2014 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/
package com.openshift.client;

import java.util.List;

import com.openshift.client.cartridge.ICartridge;
import com.openshift.internal.client.AlternativeCartridges;

/**
 * @author André Dietisheim
 */
public interface IQuickstart {

	/**
	 * Returns the name.
	 * 
	 * @return
	 */
	public String getName();

	/**
	 * Returns the id. 
	 * 
	 * @return
	 */
	public String getId();

	/**
	 * Returns the href which is where you can find additional infos for this OpenShift quickstart. 
	 * 
	 * @return
	 */
	public String getHref();

	/**
	 * Returns the summary that describes this quickstart. 
	 * 
	 * @return
	 */
	public String getSummary();

	/**
	 * Returns the website for this quickstart. 
	 * 
	 * @return
	 */
	public String getWebsite();


	/**
	 * Returns the tags for this quickstart.
	 * @return
	 */
	public List<String> getTags();
	
	/**
	 * Returns the programming language that this quickstart is written in. 
	 * 
	 * @return
	 */
	public String getLanguage();
	
	/**
	 * Returns the list of suitable alternatives that a user may choose from for
	 * this quickstart.
	 * 
	 * @return
	 */
	public List<AlternativeCartridges> getSuitableCartridges();

	/**
	 * Returns the alternatives to a given cartridge.
	 * 
	 * @param cartridge
	 * @return
	 */
	public List<ICartridge> getAlternativesFor(ICartridge cartridge);
	
	/**
	 * Returns the url of the git repository that this quickstart is available at. 
	 * 
	 * @return
	 */
	public String getInitialGitUrl();

	/**
	 * Returns the provider for this quickstart. 
	 * 
	 * @return
	 */
	public String getProvider();

}