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
package com.openshift.client.utils;

import java.io.InputStream;

import org.apache.commons.io.IOUtils;

/**
 * @author Andre Dietisheim
 */
public enum Samples {

	// application
	ADD_APPLICATION_CARTRIDGE_JSON("add-application-cartridge.json"),
	ADD_APPLICATION_CARTRIDGE_KO_JSON("add-application-cartridge-ko.json"),
	ADD_APPLICATION_ALIAS_JSON("add-application-alias.json"),
	ADD_APPLICATION_ALIAS_KO_JSON("add-application-alias-ko.json"),
	ADD_APPLICATION_JSON("add-application.json"),
	DELETE_APPLICATION_CARTRIDGE_JSON("delete-application-cartridge.json"),
	GET_APPLICATION_CARTRIDGE_JSON("get-application-cartridge.json"),
	GET_APPLICATION_CARTRIDGES_WITH1ELEMENT_JSON("get-application-cartridges-with1element.json"),
	GET_APPLICATION_CARTRIDGES_WITH2ELEMENTS_JSON("get-application-cartridges-with2elements.json"),
	GET_APPLICATION_GEARS_WITH1ELEMENT_JSON("get-application-gears-with1element.json"),
	GET_APPLICATION_GEARS_WITH2ELEMENTS_JSON("get-application-gears-with2elements.json"),
	GET_APPLICATIONS_WITHNOAPP_JSON("get-applications-withnoapp.json"),
	GET_APPLICATIONS_WITH1APP_JSON("get-applications-with1app.json"),
	GET_APPLICATIONS_WITH2APPS_JSON("get-applications-with2apps.json"),
	GET_APPLICATIONS_WITH2APPS_1LOCALHOST_JSON("get-applications-with2apps-1localhost.json"),
	GET_APPLICATION_WITH1CARTRIDGE1ALIAS_JSON("get-application-1cartridge-1alias.json"),
	GET_APPLICATION_WITH2CARTRIDGES2ALIASES_JSON("get-application-2cartridges-2aliases.json"),
	REMOVE_APPLICATION_ALIAS_JSON("remove-application-alias.json"),
	REMOVE_APPLICATION_ALIAS_KO_JSON("remove-application-alias-ko.json"),
	RESTART_APPLICATION_JSON("restart-application.json"),
	START_APPLICATION_JSON("start-application.json"),
	STOP_APPLICATION_JSON("stop-application.json"),
	STOP_FORCE_APPLICATION_JSON("stop-application-force.json"),

	// domain
	ADD_DOMAIN_JSON("add-domain.json"),
	ADD_DOMAIN_KO_JSON("add-domain-ko.json"),
	DELETE_DOMAIN_KO_NOTFOUND_JSON("delete-domain-ko-notfound.json"),
	DELETE_DOMAIN_EXISTING_OK_JSON("delete-domain-existing-ok.json"),
	DELETE_DOMAIN_JSON("delete-domain.json"),
	DELETE_DOMAIN_KO_EXISTING_APPS_JSON("delete-domain-ko-existingapps.json"),
	GET_DOMAIN("get-domain.json"),
	GET_DOMAIN_NOTFOUND_JSON("get-domain-notfound.json"),
	GET_DOMAINS_1329997507457_JSON("get-domains-1329997507457.json"),
	GET_DOMAINS_1329997507457_XML("get-domains-1329997507457.xml"),
	GET_DOMAINS_1EXISTING("get-domains-1existing.json"),
	GET_DOMAINS_JSON("get-domains.json"),
	GET_DOMAINS_NOEXISTING_JSON("get-domains-noexisting.json"),
	REMOVE_DOMAIN_EXISTING_JSON("remove-domain-existing.json"),
	REMOVE_DOMAIN_UNEXISTING_JSON("remove-domain-unexisting.json"),
	UPDATE_DOMAIN_ID("update-domain-id.json"),
	POST_DOMAINS_NEWDOMAIN_KO("post-domains-newdomain-ko.json"),	

	// user
	ADD_USER_KEY2_OK_JSON("add-user-key2-ok.json"),
	ADD_USER_KEY_KO_JSON("add-user-key-ko.json"),
	ADD_USER_KEY_OK_JSON("add-user-key-ok.json"),
	DELETE_USER_KEY_KO_JSON("delete-user-key-ko.json"),
	GET_REST_API_JSON("get-rest-api.json"),
	GET_USER_JSON("get-user.json"),
	GET_USER_KEY_DEFAULT_JSON("get-user-key-default.json"),
	GET_USER_KEYS_MULTIPLE_JSON("get-user-keys-multiple.json"),
	GET_USER_KEYS_NONE_JSON("get-user-keys-none.json"),
	GET_USER_KEYS_SINGLE_JSON("get-user-keys-single.json"),
	UPDATE_USER_KEY_JSON("update-user-key.json"),
	UPDATE_USER_KEY_RSA_JSON("update-user-key-rsa.json"),
	// pre-defined cartridges
	GET_CARTRIDGES("get-cartridges.json");
	
	private static final String SAMPLES_FOLDER = "/samples/";

	private String filePath;

	Samples(String fileName) {
		this.filePath = SAMPLES_FOLDER + fileName;
	}

	public String getContentAsString() throws Throwable {
		String content = null;
		try {
			final InputStream contentStream = Samples.class.getResourceAsStream(filePath);
			content = IOUtils.toString(contentStream);
		} catch (Throwable e) {
			e.printStackTrace();
			throw new RuntimeException("Could not read file " + filePath + ": " + e.getMessage());
		}
		return content;
	}
}
