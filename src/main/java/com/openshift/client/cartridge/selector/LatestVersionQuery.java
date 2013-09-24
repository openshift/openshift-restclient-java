/******************************************************************************* 
 * Copyright (c) 2012 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/
package com.openshift.client.cartridge.selector;

import java.text.Collator;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.openshift.client.IApplication;
import com.openshift.client.cartridge.ICartridge;
import com.openshift.client.cartridge.IEmbeddableCartridge;
import com.openshift.client.cartridge.IStandaloneCartridge;
import com.openshift.internal.client.cartridge.AbstractCartridgeQuery;
import com.openshift.internal.client.utils.Assert;

/**
 * A constraint that shall match available embeddable and standalone cartridges by name. Among
 * several matching ones, the one with the highest version is chosen.
 * 
 * @author Andre Dietisheim
 * 
 * @see IEmbeddableCartridge for cartridges that have already been added and
 *      configured to an application.
 */
public class LatestVersionQuery extends AbstractCartridgeQuery {

	private final String nameConstraint;

	public LatestVersionQuery(final String name) {
		Assert.isTrue(name != null);
		this.nameConstraint = name;
	}

	public String getNameConstraint() {
		return nameConstraint;
	}

	@Override
	public <C extends ICartridge> Collection<C> getAll(Collection<C> cartridges) {
		return Collections.singleton(getLatest(super.getAll(cartridges)));
	}

	@Override
	public <C extends ICartridge> C get(Collection<C> cartridges) {
		return getLatest(super.getAll(cartridges));
	}

	public <C extends ICartridge> boolean matches(C cartridge) {
		String name = cartridge.getName();
		int delimiterIndex = name.lastIndexOf(ICartridge.NAME_VERSION_DELIMITER);
		if (delimiterIndex == -1) {
			return false;
		}
		return nameConstraint.equals(name.substring(0, delimiterIndex));
	}

	protected <C extends ICartridge> C getLatest(Collection<C> matchingCartridges) {
		Iterator<C> it = matchingCartridges.iterator();
		if (!it.hasNext()) {
			return null;
		}
		C latest = it.next();
		while (it.hasNext()) {
			C cartridge = it.next();
			VersionedName latestName = new VersionedName(latest.getName());
			VersionedName cartridgeName = new VersionedName(cartridge.getName());
			switch (latestName.compareTo(cartridgeName)) {
			case 0:
			case 1:
				break;
			case -1:
				latest = cartridge;
				break;
			}
		}
		return latest;
	}

	protected class VersionedName implements Comparable<VersionedName> {

		private Pattern versionPattern = Pattern.compile(
				"(([^" + ICartridge.NAME_VERSION_DELIMITER + " ]+" + ICartridge.NAME_VERSION_DELIMITER + ")*([^"
						+ ICartridge.NAME_VERSION_DELIMITER + "]+))" + ICartridge.NAME_VERSION_DELIMITER
						+ "([0-9a-zA-Z]+)\\.{0,1}([0-9a-zA-Z]*)");
		private Collator collator = Collator.getInstance();

		private String name;
		private String major;
		private String minor;

		protected VersionedName(String name) {
			Matcher matcher = versionPattern.matcher(name);
			if (!matcher.matches()) {
				this.name = name;
				return;
			}

			this.name = matcher.group(1);
			if (matcher.groupCount() >= 4) {
				this.major = matcher.group(4);
				if (matcher.groupCount() >= 5) {
					this.minor = matcher.group(5);
				}
			}
		}

		public String getName() {
			return name;
		}

		public boolean hasVersion() {
			return major != null;
		}

		public String getMajor() {
			return major;
		}

		public String getMinor() {
			return minor;
		}

		@Override
		public int compareTo(VersionedName other) {
			int result = collator.compare(getName(), other.getName());
			if (result != 0) {
				return result;
			}

			result = collator.compare(major, other.getMajor());
			if (result != 0) {
				return result;
			}

			return collator.compare(minor, other.getMinor());
		}
	}
	
	public Collection<IStandaloneCartridge> allStandaloneCartridges(IApplication application) {
		Assert.notNull(application);
		return getConnection(application).getStandaloneCartridges();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((nameConstraint == null) ? 0 : nameConstraint.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof LatestVersionQuery)) {
			return false;
		}
		LatestVersionQuery other = (LatestVersionQuery) obj;
		if (nameConstraint == null) {
			if (other.nameConstraint != null) {
				return false;
			}
		} else if (!nameConstraint.equals(other.nameConstraint)) {
			return false;
		}
		return true;
	}
}
