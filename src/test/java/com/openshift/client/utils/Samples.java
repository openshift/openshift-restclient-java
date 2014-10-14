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

import com.openshift.internal.client.utils.StreamUtils;

/**
 * @author Andre Dietisheim
 */
public enum Samples {

	// gear groups
	GET_DOMAINS_FOOBARZ_APPLICATIONS_SPRINGEAP6_GEARGROUPS("get-domains-foobarz-applications-springeap6-geargroups.json"), // 1.2
	GET_DOMAINS_FOOBARZ_APPLICATIONS_SPRINGEAP6_GEARGROUPS_12ADDITIONALGEARSTORAGE("get-domains-foobarz-applications-springeap6-geargroups-12additionalgearstorage.json"), // 1.2
	
	// cartridges
	GET_DOMAINS_FOOBARZ_APPLICATIONS_SPRINGEAP6_CARTRIDGES_1EMBEDDED("get-domains-foobarz-applications-springeap6-cartridges_1embedded.json"), // 1.2
	GET_DOMAINS_FOOBARZ_APPLICATIONS_SPRINGEAP6_CARTRIDGES_2EMBEDDED("get-domains-foobarz-applications-springeap6-cartridges_2embedded.json"), // 1.2
	GET_DOMAINS_FOOBARZ_APPLICATIONS_SPRINGEAP6_CARTRIDGES_3EMBEDDED("get-domains-foobarz-applications-springeap6-cartridges_3embedded.json"), // 1.2
	GET_DOMAINS_FOOBARZ_APPLICATIONS_SPRINGEAP6_CARTRIDGES_MYSQL("get-domains-foobarz-applications-springeap6-cartridges-mysql.json"), //1.2
	POST_MYSQL_DOMAINS_FOOBARZ_APPLICATIONS_SPRINGEAP6_CARTRIDGES("post-mysql-domains-foobarz-applications-springeap6-cartridges.json"), // 1.2
	
	// application
	GET_DOMAINS_FOOBARZ_APPLICATIONS_1EMBEDDED("get-domains-foobarz-applications_1embedded.json"), // 1.2
	GET_DOMAINS_FOOBARZ_APPLICATIONS_NOENVVARS("get-domains-foobarz-applications_noenvvars.json"), // 1.2
	GET_DOMAINS_FOOBARZ_APPLICATIONS_2EMBEDDED("get-domains-foobarz-applications_2embedded.json"), // 1.2
	GET_DOMAINS_FOOBARZ_APPLICATIONS_3EMBEDDED("get-domains-foobarz-applications_3embedded.json"), // 1.2
	GET_DOMAINS_FOOBARZ_APPLICATIONS_SPRINGEAP_SCALABLE_DOWNLOADABLECART("get-domains-foobarz-applications_springeap_scalable_downloadablecart.json"), // 1.2
	GET_DOMAINS_FOOBARZ_APPLICATIONS_NOAPPS("get-domains-foobarz-applications_noapps.json"), // 1.2
	GET_DOMAINS_FOOBARZ_APPLICATIONS_DOWNLOADABLECART("get-domains-foobarz-applications-downloadablecart.json"), // 1.2
	GET_DOMAINS_FOOBARZ_APPLICATIONS_SPRINGEAP6_1EMBEDDED("get-domains-foobarz-applications-springeap6_1embedded.json"), // 1.2
	GET_DOMAINS_FOOBARZ_APPLICATIONS_SPRINGEAP6_2EMBEDDED("get-domains-foobarz-applications-springeap6_2embedded.json"), // 1.2
	GET_DOMAINS_FOOBARZ_APPLICATIONS_SPRINGEAP6_2ALIAS("get-domains-foobarz-applications-springeap6_2alias.json"), // 1.2
	GET_DOMAINS_FOOBARZ_APPLICATIONS_SPRINGEAP6_0ALIAS("get-domains-foobarz-applications-springeap6_0alias.json"), // 1.2
	POST_STOP_DOMAINS_FOOBARZ_APPLICATIONS_SPRINGEAP6_EVENT("post-stop-domains-foobarz-applications-springeap6-events.json"), // 1.2
	POST_SCALABLE_DOMAINS_FOOBARZ_APPLICATIONS("post-scalable-domains-foobarz-applications.json"), // 1.2
	POST_JEKYLL_DOMAINS_FOOBARZ_APPLICATIONS("post-jekyll-domains-foobarz-applications.json"), // 1.2
	POST_ADD_ENVIRONMENT_VARIABLE_FOO_TO_FOOBARZ_SPRINGEAP6("post_add_environment_variable_foo_to_foobarz_springeap6.json"),//1.2
	POST_ADD_2_ENVIRONMENT_VARIABLES_TO_FOOBARZ_SPRINGEAP6("post_add_2_environment_variables_to_foobarz_springeap6.json"),//1.2
	GET_2_ENVIRONMENT_VARIABLES_FOOBARZ_SPRINGEAP6("get_two_environment_variabls_foobarz_springeap6.json"),//1.2
	GET_1_ENVIRONMENT_VARIABLES_FOOBARZ_SPRINGEAP6("get_1_environment_variables_foobarz_springeap6.json"),
	GET_0_ENVIRONMENT_VARIABLES_FOOBARZ_SPRINGEAP6("get_0_environment_variables_foobarz_springeap6.json"),
	GET_4_ENVIRONMENT_VARIABLES_FOOBARZ_SPRINGEAP6("get_four_environment_variables_foobarz_springeap6.json"),
	PUT_FOO_ENVIRONMENT_VARIABLE_FOOBARZ_SPRINGEAP6("put_foo_environment_variable_foobarz_springeap6.json"),//1.2

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
	GET_API_QUICKSTARTS("get-api-quickstarts.json"),
	
	// user
	GET_USER("get-user.json"), // 1.2
	GET_USER_KEYS_2KEYS("get-user-keys_2keys.json"), // 1.2
	GET_USER_KEYS_NONE("get-user-keys_none.json"), // 1.2
	GET_USER_KEYS_1KEY("get-user-keys_1key.json"), // 1.2
	PUT_BBCC_DSA_USER_KEYS_SOMEKEY("put-bbcc-dsa-user-keys-somekey.json"), // 1.2

	// cartridges
	GET_CARTRIDGES("get-cartridges.json"), // 1.2
	
	// links
	LINKS_UNKNOWN_LINKPARAMETERTYPE("links-unknown-linkparametertype.json"), // 1.2
	LINKS_UNKNOWN_VERB("links-unknown-verb.json");
	
	private static final String SAMPLES_FOLDER = "/samples/";

	private String filePath;

	Samples(String fileName) {
		this.filePath = SAMPLES_FOLDER + fileName;
	}

	public String getContentAsString() {
		String content = null;
		try {
			final InputStream contentStream = Samples.class.getResourceAsStream(filePath);
			content = StreamUtils.readToString(contentStream, StreamUtils.UTF_8);
		} catch (Throwable e) {
			e.printStackTrace();
			throw new RuntimeException("Could not read file " + filePath + ": " + e.getMessage());
		}
		return content;
	}
}
