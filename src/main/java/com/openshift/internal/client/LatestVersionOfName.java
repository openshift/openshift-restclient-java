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
package com.openshift.internal.client;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.openshift.client.IEmbeddableCartridge;
import com.openshift.internal.client.utils.Assert;

/**
 * A constraint that shall match available embeddable cartridges by name. Among
 * several matching ones, the one with the highest version is chosen.
 * 
 * @author Andre Dietisheim
 * 
 * @see IEmbeddableCartridge for cartridges that have already been added and
 *      configured to an application.
 */
public class LatestVersionOfName extends AbstractCartridgeConstraint {

	private final String nameConstraint;

	public LatestVersionOfName(final String name) {
		Assert.isTrue(name != null);
		this.nameConstraint = name;
	}
	
	@Override
	public <C extends IEmbeddableCartridge> Collection<C> getMatching(Collection<C> cartridges) {
		List<C> matchingCartridges = new ArrayList<C>(super.getMatching(cartridges));
		return Collections.singletonList(getLatest(matchingCartridges));
	}

	@Override
	protected <C extends IEmbeddableCartridge> boolean matches(C cartridge) {
		return cartridge.getName().startsWith(nameConstraint);
	}

	protected <C extends IEmbeddableCartridge> C getLatest(List<C> matchingCartridges) {
		if (matchingCartridges.size() == 1) {
			return matchingCartridges.get(0);
		}

		Collections.sort(matchingCartridges, new Comparator<C>() {

			@Override
			public int compare(C thisCartridge, C thatCartridge) {
				VersionedName thisName = new VersionedName(thisCartridge.getName());
				VersionedName thatName = new VersionedName(thatCartridge.getName());
				return thisName.compareTo(thatName);
			}
		});
		return matchingCartridges.get(matchingCartridges.size() - 1);
	}

	protected class VersionedName implements Comparable<VersionedName> {

		private Pattern versionPattern = Pattern.compile("([^-]+)-([0-9a-zA-Z]+)\\.{0,1}([0-9a-zA-Z]*)");
		private Collator collator = Collator.getInstance();

		private String name;
		private String major;
		private String minor;

		protected VersionedName(String name) {
			Matcher matcher = versionPattern.matcher(name);
			if (!matcher.matches()) {
				this.name = name;
			}

			this.name = matcher.group(1);
			if (matcher.groupCount() >= 2) {
				this.major = matcher.group(2);
				if (matcher.groupCount() >= 3) {
					this.minor = matcher.group(3);
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
		if (!(obj instanceof LatestVersionOfName)) {
			return false;
		}
		LatestVersionOfName other = (LatestVersionOfName) obj;
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
