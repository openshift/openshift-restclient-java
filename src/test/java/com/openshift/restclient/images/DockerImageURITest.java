/*******************************************************************************
 * Copyright (c) 2015 Red Hat, Inc. Distributed under license by Red Hat, Inc.
 * All rights reserved. This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Red Hat, Inc.
 ******************************************************************************/
package com.openshift.restclient.images;

import static org.junit.Assert.*;

import org.junit.Test;

import com.openshift.restclient.images.DockerImageURI;

/**
 * @author Jeff Cantrill
 */
public class DockerImageURITest {
	
	private static final String NAME = "foo";
	private static final String TAG = "bar";
	private static final String USERNAME = "openshift";
	private static final String REPO_HOST = "127.0.0.1";
	
	private static final String NAME_TAG = String.format("%s:%s", NAME, TAG);
	private static final String USER_NAME_TAG = String.format("%s/%s:%s", USERNAME, NAME, TAG);
	private static final String REPO_USER_NAME_TAG = String.format("%s/%s/%s:%s", REPO_HOST, USERNAME, NAME, TAG);;;
	
	@Test
	public void testGetUriWithoutHost(){
		assertEquals("Exp. to get the uri without tag", String.format("%s/%s:%s", USERNAME, NAME, TAG), new DockerImageURI(REPO_USER_NAME_TAG).getUriWithoutHost());
	}
	@Test
	public void testGetUriWithoutTag(){
		assertEquals("Exp. to get the uri without tag", String.format("%s/%s/%s", REPO_HOST, USERNAME, NAME), new DockerImageURI(REPO_USER_NAME_TAG).getUriWithoutTag());
	}
	@Test
	public void testGetBaseUri(){
		assertEquals("Exp. to get the uri without repo", USER_NAME_TAG, new DockerImageURI(REPO_USER_NAME_TAG).getBaseUri());
	}
	@Test
	public void testGetAbsoluteUri() {
		assertEquals("Exp. to get the full uri", REPO_USER_NAME_TAG, new DockerImageURI(REPO_USER_NAME_TAG).getAbsoluteUri());
	}

	@Test
	public void testGetAbsoluteUriWithoutRepo() {
		assertEquals("Exp. to get the full uri without rep", USER_NAME_TAG, new DockerImageURI(USER_NAME_TAG).getAbsoluteUri());
	}

	@Test
	public void testGetAbsoluteUriWithoutUserName() {
		assertEquals("Exp. to get the full uri without user name", NAME_TAG, new DockerImageURI(NAME_TAG).getAbsoluteUri());
	}

	@Test
	public void testGetAbsoluteUriWithoutTag() {
		assertEquals("Exp. to get the full uri without user name", String.format("%s:%s", NAME, "latest"), new DockerImageURI(NAME).getAbsoluteUri());
	}
	
	@Test
	public void testName() {
		DockerImageURI tag = new DockerImageURI(NAME);
		assertEquals(NAME, tag.getName());
		assertEquals("Expected to toString to return the correct uri", String.format("%s:%s", NAME, "latest"), tag.toString());
	}

	@Test
	public void testNameWithTag() {
		DockerImageURI tag = new DockerImageURI(NAME_TAG);
		assertNameAndTag(tag);
		assertEquals("Expected to toString to return the correct uri", NAME_TAG, tag.toString());
	}
	
	@Test
	public void testUserWithNameAndTag() {
		DockerImageURI tag = new DockerImageURI(USER_NAME_TAG);
		assertNameAndTag(tag);
		assertUserName(tag);
		assertEquals("Expected to toString to return the correct uri", USER_NAME_TAG, tag.toString());
	}

	@Test
	public void testRepoWithUserWithNameAndTag() {
		DockerImageURI tag = new DockerImageURI(REPO_USER_NAME_TAG);
		assertNameAndTag(tag);
		assertUserName(tag);
		assertRepoHost(tag);
		assertEquals("Expected to toString to return the correct uri", REPO_USER_NAME_TAG, tag.toString());
	}
	
	private void assertRepoHost(DockerImageURI tag) {
		assertEquals("Expected to parse our the repo host", REPO_HOST, tag.getRepositoryHost());
	}

	private void assertUserName(DockerImageURI tag) {
		assertEquals("Expected to parse our the username", USERNAME, tag.getUserName());
	}

	private void assertNameAndTag(DockerImageURI tag){
		assertEquals("Expected to parse out the name", NAME, tag.getName());
		assertEquals("Expected to parse out the tage", TAG, tag.getTag());
	}
}
