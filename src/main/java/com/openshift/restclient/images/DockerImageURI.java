/*******************************************************************************
 * Copyright (c) 2015 Red Hat, Inc. Distributed under license by Red Hat, Inc.
 * All rights reserved. This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Red Hat, Inc.
 ******************************************************************************/
package com.openshift.restclient.images;

/**
 * ImageUri is an immutable representation of a full image tag in accordance with
 * with Docker conventions [REGISTRYHOST/][USERNAME/]NAME[:TAG]
 * 
 * @author Jeff Cantrill
 */
public class DockerImageURI {
	
	public static final String LATEST = "latest";
	private String registryHost;
	private String userName;
	private String name;
	private String tag;
	
	public DockerImageURI(String registryHost, String userName, String name){
		this(registryHost, userName, name, LATEST);
	}
	public DockerImageURI(String registryHost, String userName, String name, String imageTag){
		this.registryHost = registryHost;
		this.userName = userName;
		this.name = name;
		this.tag = imageTag;
	}

	public DockerImageURI(String tag){
		if(tag != null) {
			String[] segments = tag.split("/");
			switch (segments.length) {
			case 3:
				registryHost = segments[0];
				userName = segments[1];
				setNameAndTag(segments[2]);
				break;
			case 2:
				userName = segments[0];
				setNameAndTag(segments[1]);
				break;
			default:
				setNameAndTag(segments[0]);
				break;
			}
		}
	}

	private void setNameAndTag(String nameAndTag){
		String [] nameTag = nameAndTag.split(":");
		if(nameTag.length == 2){
			name = nameTag[0];
			tag = nameTag[1];
		}
		else{
			name =nameTag[0];
			tag = LATEST;
		}
	}
	
	@Override
	public String toString() {
		return getAbsoluteUri();
	}

	public String getName() {
		return this.name;
	}

	public String getTag() {
		return this.tag;
	}

	public String getUserName() {
		return this.userName;
	}

	public String getRepositoryHost() {
		return this.registryHost;
	}

	public String getAbsoluteUri() {
		return buildUri(registryHost, userName, name, tag);
	}

	public String getBaseUri() {
		return buildUri(null, userName, name, tag);
	}

	public String getUriWithoutTag() {
		return buildUri(registryHost, userName, name, null);
	}
	
	public String getUriWithoutHost() {
		return buildUri(null, userName, name, tag);
	}

	public String getNameAndTag() {
		return buildUri(null, null, name, tag);
	}
	
	private String buildUri(String host, String user, String name, String tag){
		StringBuilder b = new StringBuilder();
		if(host != null) b.append(host).append("/");
		if(user != null) b.append(user).append("/");
		b.append(name);
		if(tag != null)b.append(":").append(tag);
		return b.toString();
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result
				+ ((registryHost == null) ? 0 : registryHost.hashCode());
		result = prime * result + ((tag == null) ? 0 : tag.hashCode());
		result = prime * result
				+ ((userName == null) ? 0 : userName.hashCode());
		return result;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		DockerImageURI other = (DockerImageURI) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (registryHost == null) {
			if (other.registryHost != null)
				return false;
		} else if (!registryHost.equals(other.registryHost))
			return false;
		if (tag == null) {
			if (other.tag != null)
				return false;
		} else if (!tag.equals(other.tag))
			return false;
		if (userName == null) {
			if (other.userName != null)
				return false;
		} else if (!userName.equals(other.userName))
			return false;
		return true;
	}
	
	
}
