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
package com.openshift.express.client;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;


/**
 * A cartridge that is available on the openshift server. This class is no enum
 * since we dont know all available types and they may change at any time.
 * 
 * @author André Dietisheim
 */
public class Cartridge implements ICartridge {
	
	protected static final String JBOSS = "jboss";
	protected static final String RUBY = "ruby";
	protected static final String PYTHON = "python";
	protected static final String PHP = "php";
	protected static final String PERL = "perl";
	protected static final String NODEJS = "nodejs";
	protected static final String JENKINS = "jenkins";
	protected static final String HAPROXY = "haproxy";
	protected static final String RAW = "raw";

	protected IOpenShiftService service;
	protected IUser user;
	protected String name;
	
	public Cartridge(IOpenShiftService service, IUser user) {
		this.service = service;
		this.user = user;
	}

	public Cartridge(String name) {
		this.name = name;
	}

	protected void setName(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	
	public String getLogLocation() {
		return "/";
	}

	public static ICartridge valueOf(String name) {
		if (name.contains(JBOSS))
			return new JBossCartridge(name);
		else return new Cartridge(name);
	}

	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Cartridge other = (Cartridge) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}

	public String toString() {
		return "Cartridge [name=" + name + "]";
	}
	
	protected String getCartridgeName(String cartridgeType) throws OpenShiftException {
		List<ICartridge> cartridges = service.getCartridges(user);
		
		Iterator<ICartridge> i = cartridges.iterator();
		while (i.hasNext()){
			ICartridge cartridge = i.next();
			if (cartridge.getName().contains(cartridgeType))
				return cartridge.getName();
		}
		
		throw new OpenShiftException("No cartridge found for type " + cartridgeType);
	}

}