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
package com.openshift.internal.client.response;

import static org.fest.assertions.Assertions.assertThat;
import static org.junit.Assert.assertNotNull;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.fest.assertions.Condition;
import org.junit.Test;

import com.openshift.client.ApplicationScale;
import com.openshift.client.GearState;
import com.openshift.client.HttpMethod;
import com.openshift.client.IField;
import com.openshift.client.IGear;
import com.openshift.client.IGearProfile;
import com.openshift.client.Message;
import com.openshift.client.Messages;
import com.openshift.client.utils.Cartridges;
import com.openshift.client.utils.MessageAssert;
import com.openshift.client.utils.ResourcePropertyAssert;
import com.openshift.client.utils.Samples;
import com.openshift.internal.client.CartridgeType;

public class OpenShiftJsonDTOFactoryTest {

	private static final String LINK_ADD_APPLICATION = "ADD_APPLICATION";

	private IRestResponseFactory factory = new OpenShiftJsonDTOFactory();
	
	private static final class ValidLinkCondition extends Condition<Map<?, ?>> {
		@Override
		public boolean matches(Map<?, ?> links) {
			for (Entry<?, ?> entry : links.entrySet()) {
				Link link = (Link) entry.getValue();
				if (link.getHref() == null || link.getHttpMethod() == null || link.getRel() == null) {
					return false;
				}
			}
			return true;
		}
	}

	@Test
	public void shouldUnmarshallGetUserResponseBody() throws Throwable {
		// pre-conditions
		String content = Samples.GET_USER.getContentAsString();
		assertNotNull(content);
		// operation
		RestResponse response = factory.get(content);
		// verifications
		assertThat(response.getDataType()).isEqualTo(EnumDataType.user);
		UserResourceDTO userResourceDTO = response.getData();
		assertThat(userResourceDTO.getRhLogin()).isEqualTo("foo@redhat.com");
		assertThat(userResourceDTO.getMaxGears()).isEqualTo(10);
		assertThat(userResourceDTO.getConsumedGears()).isEqualTo(3);
		assertThat(userResourceDTO.getLinks()).hasSize(2);
	}

	@Test
	public void shouldUnmarshallGetUserNoKeyResponseBody() throws Throwable {
		// pre-conditions
		String content = Samples.GET_USER_KEYS_NONE.getContentAsString();
		assertNotNull(content);
		// operation
		RestResponse response = factory.get(content);
		// verifications
		assertThat(response.getDataType()).isEqualTo(EnumDataType.keys);
		List<KeyResourceDTO> keys = response.getData();
		assertThat(keys).isEmpty();
	}

	@Test
	public void shouldUnmarshallGetUserSingleKeyResponseBody() throws Throwable {
		// pre-conditions
		String content = Samples.GET_USER_KEYS_1KEY.getContentAsString();
		assertNotNull(content);
		// operation
		RestResponse response = factory.get(content);
		// verifications
		assertThat(response.getDataType()).isEqualTo(EnumDataType.keys);
		List<KeyResourceDTO> keys = response.getData();
		assertThat(keys).hasSize(1);
		final KeyResourceDTO key = keys.get(0);
		assertThat(key.getLinks()).hasSize(3);
		assertThat(key.getName()).isEqualTo("somekey");
		assertThat(key.getType()).isEqualTo("ssh-rsa");
		assertThat(key.getContent()).isEqualTo("ABBA");
	}

	@Test
	public void shouldUnmarshallGetUserMultipleKeyResponseBody() throws Throwable {
		// pre-conditions
		String content = Samples.GET_USER_KEYS_2KEYS.getContentAsString();
		assertNotNull(content);
		// operation
		RestResponse response = factory.get(content);
		// verifications
		assertThat(response.getDataType()).isEqualTo(EnumDataType.keys);
		List<KeyResourceDTO> keys = response.getData();
		final KeyResourceDTO key = keys.get(0);
		assertThat(key.getLinks()).hasSize(3);
		assertThat(key.getName()).isEqualTo("default");
		assertThat(key.getType()).isEqualTo("ssh-rsa");
		assertThat(key.getContent()).isEqualTo("ABBA");
	}

	@Test
	public void shouldUnmarshallGetRootAPIResponseBody() throws Throwable {
		// pre-conditions
		String content = Samples.GET_API.getContentAsString();
		assertNotNull(content);
		// operation
		RestResponse response = factory.get(content);
		// verifications
		assertThat(response.getDataType()).isEqualTo(EnumDataType.links);
		final Map<String, Link> links = response.getData();
		assertThat(links).hasSize(12);
		assertThat(links).satisfies(new ValidLinkCondition());

	}

	@Test
	public void shouldUnmarshallGetDomainsWith1ExistingResponseBody() throws Throwable {
		// pre-conditions
		String content = Samples.GET_DOMAINS.getContentAsString();
		assertNotNull(content);
		// operation
		RestResponse response = factory.get(content);
		// verifications
		assertThat(response.getDataType()).isEqualTo(EnumDataType.domains);
		final List<DomainResourceDTO> domainDTOs = response.getData();
		assertThat(domainDTOs).isNotEmpty();
		assertThat(domainDTOs).hasSize(1);
		final DomainResourceDTO domainDTO = domainDTOs.get(0);
		assertThat(domainDTO.getId()).isEqualTo("foobarz");
		assertThat(domainDTO.getLinks()).hasSize(5);
		final Link link = domainDTO.getLink(LINK_ADD_APPLICATION);
		assertThat(link).isNotNull();
		assertThat(link.getHref()).isEqualTo("https://openshift.redhat.com/broker/rest/domains/foobarz/applications");
		assertThat(link.getRel()).isEqualTo("Create new application");
		assertThat(link.getHttpMethod()).isEqualTo(HttpMethod.POST);
		final List<LinkParameter> requiredParams = link.getRequiredParams();
		assertThat(requiredParams).hasSize(1);
	}

	@Test
	public void shouldUnmarshallGetDomainsWithNoExistingResponseBody() throws Throwable {
		// pre-conditions
		String content = Samples.GET_DOMAINS_EMPTY.getContentAsString();
		assertNotNull(content);
		// operation
		RestResponse response = factory.get(content);
		// verifications
		assertThat(response.getDataType()).isEqualTo(EnumDataType.domains);
		final List<DomainResourceDTO> domains = response.getData();
		assertThat(domains).isEmpty();
	}

	@Test
	public void shouldUnmarshallGetDomainResponseBody() throws Throwable {
		// pre-conditions
		String content = Samples.GET_DOMAINS_FOOBARZ.getContentAsString();
		assertNotNull(content);
		// operation
		RestResponse response = factory.get(content);
		// verifications
		assertThat(response.getDataType()).isEqualTo(EnumDataType.domain);
		final DomainResourceDTO domain = response.getData();
		assertNotNull(domain);
		assertThat(domain.getId()).isEqualTo("foobarz");
		assertThat(domain.getLinks()).hasSize(5);
	}

	@Test
	public void shouldUnmarshallDeleteDomainKoNotFoundResponseBody() throws Throwable {
		// pre-conditions
		String content = Samples.DELETE_DOMAINS_FOOBAR_KO.getContentAsString();
		assertNotNull(content);
		// operation
		RestResponse response = factory.get(content);
		// verifications
		assertThat(response.getDataType()).isNull();
		assertThat(response.getMessages().size()).isEqualTo(1);
	}

	@Test
	public void shouldUnmarshallGetApplicationsWith2AppsResponseBody() throws Throwable {
		// pre-conditions
		String content = Samples.GET_DOMAINS_FOOBARZ_APPLICATIONS_2EMBEDDED.getContentAsString();
		assertNotNull(content);
		// operation
		RestResponse response = factory.get(content);
		// verifications
		assertThat(response.getDataType()).isEqualTo(EnumDataType.applications);
		final List<ApplicationResourceDTO> applications = response.getData();
		assertThat(applications).hasSize(2);
		ApplicationResourceDTO applicationDTO = applications.get(1);
		assertThat(applicationDTO.getDomainId()).isEqualTo("foobarz");
		assertThat(applicationDTO.getCreationTime()).isEqualTo("2013-09-20T21:28:53Z");
		assertThat(applicationDTO.getApplicationUrl()).isEqualTo("http://springeap6-foobarz.rhcloud.com/");
		assertThat(applicationDTO.getSshUrl()).isEqualTo("ssh://523cbe15e0b8cd0a520001b6@springeap6-foobarz.rhcloud.com");
		assertThat(applicationDTO.getFramework()).isEqualTo(Cartridges.JBOSSEAP_6_NAME);
		assertThat(applicationDTO.getName()).isEqualTo("springeap6");
		assertThat(applicationDTO.getApplicationScale()).isEqualTo(ApplicationScale.NO_SCALE);
		assertThat(applicationDTO.getGitUrl()).isEqualTo(
				"ssh://523cbe15e0b8cd0a520001b6@springeap6-foobarz.rhcloud.com/~/git/springeap6.git/");
		assertThat(applicationDTO.getInitialGitUrl())
				.isEqualTo("git://github.com/openshift/spring-eap6-quickstart.git");
		assertThat(applicationDTO.getUuid()).isEqualTo("523cbe15e0b8cd0a520001b6");
		assertThat(applicationDTO.getGearProfile()).isEqualTo(IGearProfile.SMALL);
		assertThat(applicationDTO.getAliases()).containsExactly("jbosstools.org");
		assertThat(applicationDTO.getUuid()).isEqualTo("523cbe15e0b8cd0a520001b6");
		assertThat(applicationDTO.getCartridges().keySet()).containsOnly(
				Cartridges.JBOSSEAP_6_NAME,
				Cartridges.MONGODB_22_NAME,
				Cartridges.MYSQL_51_NAME);
		CartridgeResourceDTO cartridgeResourceDTO = 
				applicationDTO.getCartridges().get(Cartridges.MONGODB_22_NAME);
		assertThat(cartridgeResourceDTO).isNotNull();
		assertThat(cartridgeResourceDTO.getType()).isEqualTo(CartridgeType.EMBEDDED);
		assertThat(cartridgeResourceDTO.getName()).isEqualTo(Cartridges.MONGODB_22_NAME);
		assertThat(cartridgeResourceDTO.getDescription()).startsWith("MongoDB is a scalable, high-performance, "); 
		assertThat(cartridgeResourceDTO.getDisplayName()).startsWith("MongoDB NoSQL Database "); 
		assertThat(cartridgeResourceDTO.getMessages()).isNull(); // dito
		assertThat(cartridgeResourceDTO.getLinks()).hasSize(7); 
		List<CartridgeResourceProperty> properties = cartridgeResourceDTO.getProperties().getAll();
		assertThat(properties).onProperty("name").containsExactly(
				"username", "password", "database_name", "connection_url");
	}

	/**
	 * Should unmarshall get application response body.
	 * 
	 * @throws Throwable
	 */
	@Test
	public void shouldUnmarshallGetApplicationWithAliasesResponseBody() throws Throwable {
		// pre-conditions
		String content = Samples.GET_DOMAINS_FOOBARZ_APPLICATIONS_SPRINGEAP6_2ALIAS.getContentAsString();
		assertNotNull(content);
		// operation
		RestResponse response = factory.get(content);
		// verifications
		assertThat(response.getDataType()).isEqualTo(EnumDataType.application);
		final ApplicationResourceDTO application = response.getData();
		assertThat(application.getUuid()).hasSize(24);
		assertThat(application.getCreationTime()).startsWith("2013-");
		assertThat(application.getDomainId()).isEqualTo("foobarz");
		assertThat(application.getFramework()).isEqualTo(Cartridges.JBOSSEAP_6_NAME);
		assertThat(application.getName()).isEqualTo("springeap6");
		assertThat(application.getLinks()).hasSize(18);
		assertThat(application.getAliases()).contains("jbosstools.org", "redhat.com");
	}

	/**
	 * Should unmarshall get application response body.
	 * 
	 * @throws Throwable
	 */
	@Test
	public void shouldUnmarshallAddApplicationEmbeddedCartridgeResponseBody() throws Throwable {
		// pre-conditions
		String content = Samples.POST_MYSQL_DOMAINS_FOOBARZ_APPLICATIONS_SPRINGEAP6_CARTRIDGES.getContentAsString();
		assertNotNull(content);

		// operation
		RestResponse response = factory.get(content);

		// verifications
		Messages messages = response.getMessages();
		assertThat(messages.size()).isEqualTo(3);
		Collection<Message> defaultMessages = messages.getBy(IField.DEFAULT);
		assertThat(defaultMessages).isNotEmpty();
		new MessageAssert(defaultMessages.iterator().next())
				.hasField(IField.DEFAULT)
				.hasExitCode(-1)
				.hasText("Added mysql-5.1 to application springeap6");
		Collection<Message> resultMessages = messages.getBy(IField.RESULT);
		assertThat(resultMessages).isNotEmpty();
		new MessageAssert(resultMessages.iterator().next())
				.hasField(IField.RESULT)
				.hasExitCode(0)
				.hasText(
						"\nMySQL 5.1 database added.  Please make note of these credentials:\n\n"
								+ "       Root User: adminnFC22YQ\n   Root Password: U1IX8AIlrEcl\n   Database Name: springeap6\n\n"
								+ "Connection URL: mysql://$OPENSHIFT_MYSQL_DB_HOST:$OPENSHIFT_MYSQL_DB_PORT/\n\n"
								+ "You can manage your new MySQL database by also embedding phpmyadmin-3.4.\n"
								+ "The phpmyadmin username and password will be the same as the MySQL credentials above.\n");
		Collection<Message> appInfoMessages = messages.getBy(IField.APPINFO);
		assertThat(appInfoMessages).isNotEmpty();
		new MessageAssert(appInfoMessages.iterator().next())
				.hasField(IField.APPINFO)
				.hasExitCode(0)
				.hasText("Connection URL: mysql://127.13.125.1:3306/\n");

		assertThat(response.getDataType()).isEqualTo(EnumDataType.cartridge);
		final CartridgeResourceDTO cartridge = response.getData();
		assertThat(cartridge.getName()).isEqualTo(Cartridges.MYSQL_51_NAME);
		assertThat(cartridge.getDisplayName()).isEqualTo("MySQL Database 5.1");
		assertThat(cartridge.getDescription()).isEqualTo("MySQL is a multi-user, multi-threaded SQL database server.");
		assertThat(cartridge.getType()).isEqualTo(CartridgeType.EMBEDDED);
		assertThat(cartridge.getUrl()).isNull();
		CartridgeResourceProperties properties = cartridge.getProperties();
		assertThat(properties).isNotNull();
		assertThat(properties.size()).isEqualTo(4);
		new ResourcePropertyAssert(properties.getAll().get(0))
				.hasName("username")
				.hasDescription("Root user on mysql database")
				.hasType("cart_data")
				.hasValue("adminnFC22YQ");
		new ResourcePropertyAssert(properties.getAll().get(1))
				.hasName("password")
				.hasDescription("Password for root user on mysql database")
				.hasType("cart_data")
				.hasValue("U1IX8AIlrEcl");
		new ResourcePropertyAssert(properties.getAll().get(2))
				.hasName("database_name")
				.hasDescription("MySQL DB name")
				.hasType("cart_data")
				.hasValue("springeap6");
		new ResourcePropertyAssert(properties.getAll().get(3))
				.hasName("connection_url")
				.hasDescription("MySQL DB connection URL")
				.hasType("cart_data")
				.hasValue("mysql://$OPENSHIFT_MYSQL_DB_HOST:$OPENSHIFT_MYSQL_DB_PORT/");
		assertThat(cartridge.getLinks()).hasSize(7);

	}

	/**
	 * Should unmarshall get application response body.
	 * 
	 * @throws Throwable
	 */
	@Test
	public void shouldUnmarshallGetApplicationCartridgesWith1ElementResponseBody() throws Throwable {
		// pre-conditions
		String content = Samples.GET_DOMAINS_FOOBARZ_APPLICATIONS_SPRINGEAP6_CARTRIDGES_2EMBEDDED.getContentAsString();
		assertNotNull(content);
		// operation
		RestResponse response = factory.get(content);
		// verifications
		assertThat(response.getMessages().size()).isEqualTo(0);
		assertThat(response.getDataType()).isEqualTo(EnumDataType.cartridges);
		final Map<String, CartridgeResourceDTO> cartridges = response.getData();
		assertThat(cartridges).hasSize(3); // mysql, mongo, jbosseap
		assertThat(cartridges.values()).onProperty("name").containsOnly(
				Cartridges.MONGODB_22_NAME,
				Cartridges.MYSQL_51_NAME,
				Cartridges.JBOSSEAP_6_NAME);
	}

	@Test
	public void shouldUnmarshallGetApplicationCartridgesWith3ElementsResponseBody() throws Throwable {
		// pre-conditions
		String content = Samples.GET_DOMAINS_FOOBARZ_APPLICATIONS_SPRINGEAP6_CARTRIDGES_2EMBEDDED.getContentAsString();
		assertNotNull(content);
		// operation
		RestResponse response = factory.get(content);
		// verifications
		assertThat(response.getMessages().size()).isEqualTo(0);
		assertThat(response.getDataType()).isEqualTo(EnumDataType.cartridges);
		Map<String, CartridgeResourceDTO> cartridges = response.getData();
		assertThat(cartridges).hasSize(3);
		assertThat(cartridges.values()).onProperty("name").containsOnly(
				Cartridges.MONGODB_22_NAME,
				Cartridges.MYSQL_51_NAME,
				Cartridges.JBOSSEAP_6_NAME);
	}

	@Test
	public void shouldUnmarshallGetApplicationGearGroupsResponseBody() throws Throwable {
		// pre-conditions
		String content = Samples.GET_DOMAINS_FOOBARZ_APPLICATIONS_SPRINGEAP6_GEARGROUPS.getContentAsString();
		assertNotNull(content);
		// operation
		RestResponse response = factory.get(content);
		// verifications
		Collection<GearGroupResourceDTO> gearGroups = response.getData();
		assertThat(gearGroups.size()).isEqualTo(2);
		GearGroupResourceDTO gearGroup = gearGroups.iterator().next();
		assertThat(gearGroup.getName()).isEqualTo("514207b84382ec1fef0000ab");
		assertThat(gearGroup.getUuid()).isEqualTo("514207b84382ec1fef0000ab");
		assertThat(gearGroup.getGears()).hasSize(2);
		final GearResourceDTO gear = gearGroup.getGears().iterator().next();
		assertThat(gear.getUuid()).isEqualTo("514207b84382ec1fef000098");
		assertThat(gear.getState()).isEqualToIgnoringCase(GearState.IDLE.toString());
	}

	@Test
	public void shouldUnmarshallSingleValidOptionInResponseBody() throws Throwable {
		// pre-conditions
		String content = Samples.POST_MYSQL_DOMAINS_FOOBARZ_APPLICATIONS_SPRINGEAP6_CARTRIDGES.getContentAsString();
		assertNotNull(content);
		// operation
		RestResponse response = factory.get(content);
		// verifications
		final CartridgeResourceDTO cartridge = response.getData();
		final Link link = cartridge.getLink("RESTART");
		assertThat(link.getOptionalParams()).hasSize(0);
		assertThat(link.getRequiredParams().get(0).getValidOptions()).containsExactly("restart");
	}

	@Test
	public void shouldUnmarshallMultipleValidOptionInResponseBody() throws Throwable {
		// pre-conditions
		String content = Samples.PUT_BBCC_DSA_USER_KEYS_SOMEKEY.getContentAsString();
		assertNotNull(content);
		// operation
		RestResponse response = factory.get(content);
		// verifications
		final KeyResourceDTO key = response.getData();
		final Link link = key.getLink("UPDATE");
		assertThat(link.getOptionalParams()).hasSize(0);
		assertThat(link.getRequiredParams().get(0).getValidOptions()).contains("ssh-rsa", "ssh-dss");
	}

	@Test
	public void shouldUnmarshallLinksUnknwonLinkParameterType() throws Throwable {
		// pre-conditions
		String content = Samples.LINKS_UNKNOWN_LINKPARAMETERTYPE.getContentAsString();
		assertNotNull(content);
		// operation
		RestResponse response = factory.get(content);
		// verifications
		final Map<String, Link> links = response.getData();
		assertThat(links.size()).isEqualTo(1);
		final Link link = links.get("POST1");
		assertThat(link).isNotNull();
		assertThat(link.getHref()).isEqualTo("https://openshift.redhat.com/broker/rest/post1");
		assertThat(link.getHttpMethod()).isEqualTo(HttpMethod.POST);
		assertThat(link.getOptionalParams().size()).isEqualTo(0);
		assertThat(link.getRequiredParams().size()).isEqualTo(1);
		LinkParameter linkParameter = link.getRequiredParams().get(0);
		assertThat(linkParameter).isNotNull();
		assertThat(linkParameter.getName()).isEqualTo("post1Required1Name");
		assertThat(linkParameter.getDescription()).isEqualTo("post1Required1Description");
		assertThat(linkParameter.getType()).isNotNull().isEqualTo(new LinkParameterType("unknown"));
	}

}
