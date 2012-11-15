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
import java.text.MessageFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.openshift.client.IEmbeddableCartridge;
import com.openshift.client.IOpenShiftConnection;
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
public class EmbeddableCartridgeNameConstraint extends AbstractEmbeddableCartridgeConstraint {

	private final String nameConstraint;

	public EmbeddableCartridgeNameConstraint(final String nameConstraint) {
		Assert.isTrue(nameConstraint != null);
		this.nameConstraint = nameConstraint;
	}

	protected boolean matches(IEmbeddableCartridge cartridge) {
		return cartridge.getName().startsWith(nameConstraint);
	}

	@Override
	protected String createNoMatchErrorMessage(IOpenShiftConnection connection) {
		return MessageFormat.format(
				"No embeddable cartridge that matches the name constraint {0} is available at {1}",
				nameConstraint, connection.getServer());
	}

	@Override
	public List<IEmbeddableCartridge> getEmbeddableCartridges(IOpenShiftConnection connection) {
		List<IEmbeddableCartridge> matchingCartridges = super.getEmbeddableCartridges(connection);
		return Collections.<IEmbeddableCartridge> singletonList(getLatest(matchingCartridges));
	}

	protected IEmbeddableCartridge getLatest(List<IEmbeddableCartridge> matchingCartridges) {
		if (matchingCartridges.size() == 1) {
			return matchingCartridges.get(0);
		}

		Collections.sort(matchingCartridges, new Comparator<IEmbeddableCartridge>() {

			@Override
			public int compare(IEmbeddableCartridge thisCartridge, IEmbeddableCartridge thatCartridge) {
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
		if (!(obj instanceof EmbeddableCartridgeNameConstraint)) {
			return false;
		}
		EmbeddableCartridgeNameConstraint other = (EmbeddableCartridgeNameConstraint) obj;
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
