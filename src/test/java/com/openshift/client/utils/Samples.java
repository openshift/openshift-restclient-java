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

	// gear groups
	GET_DOMAINS_FOOBARZ_APPLICATIONS_SPRINGEAP6_GEARGROUPS("get-domains-foobarz-applications-springeap6-geargroups.json"), // 1.2

	// cartridges
	GET_DOMAINS_FOOBARZ_APPLICATIONS_SPRINGEAP6_CARTRIDGES_1EMBEDDED("get-domains-foobarz-applications-springeap6-cartridges_1embedded.json"), // 1.2
	GET_DOMAINS_FOOBARZ_APPLICATIONS_SPRINGEAP6_CARTRIDGES_2EMBEDDED("get-domains-foobarz-applications-springeap6-cartridges_2embedded.json"), // 1.2
	GET_DOMAINS_FOOBARZ_APPLICATIONS_SPRINGEAP6_CARTRIDGES_MYSQL("get-domains-foobarz-applications-springeap6-cartridges-mysql.json"), //1.2
	POST_MYSQL_DOMAINS_FOOBARZ_APPLICATIONS_SPRINGEAP6_CARTRIDGES("post-mysql-domains-foobarz-applications-springeap6-cartridges.json"), // 1.2
	
	// application
	GET_DOMAINS_FOOBARZ_APPLICATIONS("get-domains-foobarz-applications.json"), // 1.2
	GET_DOMAINS_FOOBARZ_APPLICATIONS_NOAPPS("get-domains-foobarz-applications_noapps.json"), // 1.2
	GET_DOMAINS_FOOBARZ_APPLICATIONS_SPRINGEAP6("get-domains-foobarz-applications-springeap6.json"), // 1.2
	GET_DOMAINS_FOOBARZ_APPLICATIONS_SPRINGEAP6_2ALIAS("get-domains-foobarz-applications-springeap6_2alias.json"), // 1.2
	GET_DOMAINS_FOOBARZ_APPLICATONS_SPRINGEAP6_0ALIAS("get-domains-foobarz-applications-springeap6_0alias.json"), // 1.2
	POST_STOP_DOMAINS_FOOBARZ_APPLICATIONS_SPRINGEAP6_EVENT("post-stop-domains-foobarz-applications-springeap6-events.json"), // 1.2
	POST_SCALABLE_DOMAINS_FOOBARZ_APPLICATIONS("post-scalable-domains-foobarz-applications.json"), // 1.2
	POST_JEKYLL_DOMAINS_FOOBARZ_APPLICATIONS("post-jekyll-domains-foobarz-applications.json"), // 1.2	
	
	// domain
	DELETE_DOMAINS_FOOBAR_KO("delete-domains-foobar_ko.json"), // 1.2
	DELETE_DOMAINS_FOOBARZ("delete-domains-foobarz.json"), // 1.2
	DELETE_DOMAINS_FOOBARZ_KO_EXISTINGAPPS("delete-domains-foobarz_ko-existingapps.json"), // 1.2
	GET_DOMAINS_FOOBAR_KO_NOTFOUND("get-domains-foobar_ko-notfound.json"), // 1.2
	GET_DOMAINS_FOOBARZ("get-domains-foobarz.json"), // 1.2
	GET_DOMAINS_FOOBARS("get-domains-foobars.json"), // 1.2
	GET_DOMAINS("get-domains.json"), // 1.2
	GET_DOMAINS_EMPTY("get-domains_empty.json"), // 1.2
	POST_FOOBAR_DOMAINS_KO_INUSE("post-foobar-domains_ko-inuse.json"), // 1.2	
	
	// user
	GET_API("get-api.json"), // 1.2 
	GET_USER_JSON("get-user.json"), // 1.2
	GET_USER_KEYS_2KEYS("get-user-keys_2keys.json"), // 1.2
	GET_USER_KEYS_NONE("get-user-keys_none.json"), // 1.2
	GET_USER_KEYS_1KEY("get-user-keys_1key.json"), // 1.2
	PUT_BBCC_DSA_USER_KEYS_SOMEKEY("put-bbcc-dsa-user-keys-somekey.json"), // 1.2

	// cartridges
	GET_CARTRIDGES("get-cartridges.json"); // 1.2
	
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
