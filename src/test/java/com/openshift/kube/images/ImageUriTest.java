package com.openshift.kube.images;

import static org.junit.Assert.*;

import org.junit.Test;

public class ImageUriTest {
	
	private static final String NAME = "foo";
	private static final String TAG = "bar";
	private static final String USERNAME = "openshift";
	private static final String REPO_HOST = "127.0.0.1";
	
	private static final String NAME_TAG = String.format("%s:%s", NAME, TAG);
	private static final String USER_NAME_TAG = String.format("%s/%s:%s", USERNAME, NAME, TAG);
	private static final String REPO_USER_NAME_TAG = String.format("%s/%s/%s:%s", REPO_HOST, USERNAME, NAME, TAG);;;
	
	@Test
	public void testGetUriWithoutHost(){
		assertEquals("Exp. to get the uri without tag", String.format("%s/%s:%s", USERNAME, NAME, TAG), new ImageUri(REPO_USER_NAME_TAG).getUriWithoutHost());
	}
	@Test
	public void testGetUriWithoutTag(){
		assertEquals("Exp. to get the uri without tag", String.format("%s/%s/%s", REPO_HOST, USERNAME, NAME), new ImageUri(REPO_USER_NAME_TAG).getUriWithoutTag());
	}
	@Test
	public void testGetBaseUri(){
		assertEquals("Exp. to get the uri without repo", USER_NAME_TAG, new ImageUri(REPO_USER_NAME_TAG).getBaseUri());
	}
	@Test
	public void testGetAbsoluteUri() {
		assertEquals("Exp. to get the full uri", REPO_USER_NAME_TAG, new ImageUri(REPO_USER_NAME_TAG).getAbsoluteUri());
	}

	@Test
	public void testGetAbsoluteUriWithoutRepo() {
		assertEquals("Exp. to get the full uri without rep", USER_NAME_TAG, new ImageUri(USER_NAME_TAG).getAbsoluteUri());
	}

	@Test
	public void testGetAbsoluteUriWithoutUserName() {
		assertEquals("Exp. to get the full uri without user name", NAME_TAG, new ImageUri(NAME_TAG).getAbsoluteUri());
	}

	@Test
	public void testGetAbsoluteUriWithoutTag() {
		assertEquals("Exp. to get the full uri without user name", String.format("%s:%s", NAME, "latest"), new ImageUri(NAME).getAbsoluteUri());
	}
	
	@Test
	public void testName() {
		ImageUri tag = new ImageUri(NAME);
		assertEquals(NAME, tag.getName());
		assertEquals("Expected to toString to return the correct uri", String.format("%s:%s", NAME, "latest"), tag.toString());
	}

	@Test
	public void testNameWithTag() {
		ImageUri tag = new ImageUri(NAME_TAG);
		assertNameAndTag(tag);
		assertEquals("Expected to toString to return the correct uri", NAME_TAG, tag.toString());
	}
	
	@Test
	public void testUserWithNameAndTag() {
		ImageUri tag = new ImageUri(USER_NAME_TAG);
		assertNameAndTag(tag);
		assertUserName(tag);
		assertEquals("Expected to toString to return the correct uri", USER_NAME_TAG, tag.toString());
	}

	@Test
	public void testRepoWithUserWithNameAndTag() {
		ImageUri tag = new ImageUri(REPO_USER_NAME_TAG);
		assertNameAndTag(tag);
		assertUserName(tag);
		assertRepoHost(tag);
		assertEquals("Expected to toString to return the correct uri", REPO_USER_NAME_TAG, tag.toString());
	}
	
	private void assertRepoHost(ImageUri tag) {
		assertEquals("Expected to parse our the repo host", REPO_HOST, tag.getRepositoryHost());
	}

	private void assertUserName(ImageUri tag) {
		assertEquals("Expected to parse our the username", USERNAME, tag.getUserName());
	}

	private void assertNameAndTag(ImageUri tag){
		assertEquals("Expected to parse out the name", NAME, tag.getName());
		assertEquals("Expected to parse out the tage", TAG, tag.getTag());
	}
}
