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

import static com.openshift.internal.client.response.ILinkNames.ADD_APPLICATION;
import static org.fest.assertions.Assertions.assertThat;
import static org.junit.Assert.assertNotNull;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.fest.assertions.Condition;
import org.junit.Test;

import com.openshift.client.GearState;
import com.openshift.client.HttpMethod;
import com.openshift.client.IGear;
import com.openshift.client.Message;
import com.openshift.client.utils.MessageAssert;
import com.openshift.client.utils.Samples;
import com.openshift.internal.client.CartridgeType;

public class ResourceDTOFactoryTest {

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
		String content = Samples.GET_USER_JSON.getContentAsString();
		assertNotNull(content);
		// operation
		RestResponse response = ResourceDTOFactory.get(content);
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
		RestResponse response = ResourceDTOFactory.get(content);
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
		RestResponse response = ResourceDTOFactory.get(content);
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
		RestResponse response = ResourceDTOFactory.get(content);
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
		RestResponse response = ResourceDTOFactory.get(content);
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
		RestResponse response = ResourceDTOFactory.get(content);
		// verifications
		assertThat(response.getDataType()).isEqualTo(EnumDataType.domains);
		final List<DomainResourceDTO> domainDTOs = response.getData();
		assertThat(domainDTOs).isNotEmpty();
		assertThat(domainDTOs).hasSize(1);
		final DomainResourceDTO domainDTO = domainDTOs.get(0);
		assertThat(domainDTO.getId()).isEqualTo("foobarz");
		assertThat(domainDTO.getLinks()).hasSize(5);
		final Link link = domainDTO.getLink(ADD_APPLICATION);
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
		RestResponse response = ResourceDTOFactory.get(content);
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
		RestResponse response = ResourceDTOFactory.get(content);
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
		RestResponse response = ResourceDTOFactory.get(content);
		// verifications
		assertThat(response.getDataType()).isNull();
		assertThat(response.getMessages()).hasSize(1);
	}

	@Test
	public void shouldUnmarshallGetApplicationsWith2AppsResponseBody() throws Throwable {
		// pre-conditions
		String content = Samples.GET_DOMAINS_FOOBARZ_APPLICATIONS.getContentAsString();
		assertNotNull(content);
		// operation
		RestResponse response = ResourceDTOFactory.get(content);
		// verifications
		assertThat(response.getDataType()).isEqualTo(EnumDataType.applications);
		final List<ApplicationResourceDTO> applications = response.getData();
		assertThat(applications).hasSize(2);
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
		RestResponse response = ResourceDTOFactory.get(content);
		// verifications
		assertThat(response.getDataType()).isEqualTo(EnumDataType.application);
		final ApplicationResourceDTO application = response.getData();
		assertThat(application.getUuid()).hasSize(24);
		assertThat(application.getCreationTime()).startsWith("2013-");
		assertThat(application.getDomainId()).isEqualTo("foobarz");
		assertThat(application.getFramework()).isEqualTo("jbosseap-6.0");
		assertThat(application.getName()).isEqualTo("springeap6");
		assertThat(application.getLinks()).hasSize(18);
		assertThat(application.getAliases()).contains("jbosstools.org", "redhat.com");
	}

	/**
	 * Should unmarshall get application response body.
	 * @throws Throwable 
	 */
	@Test
	public void shouldUnmarshallAddApplicationEmbeddedCartridgeResponseBody() throws Throwable {
		// pre-conditions
		String content = Samples.POST_MYSQL_DOMAINS_FOOBARZ_APPLICATIONS_SPRINGEAP6_CARTRIDGES.getContentAsString();
		assertNotNull(content);

		// operation
		RestResponse response = ResourceDTOFactory.get(content);

		// verifications
		Collection<Message> messages = response.getMessages().values();
		assertThat(messages).hasSize(3);
		Iterator<Message> it = messages.iterator();
		new MessageAssert(it.next())
				.hasField(Message.FIELD_DEFAULT)
				.hasExitCode(-1)
				.hasText("Added mysql-5.1 to application springeap6");
		new MessageAssert(it.next())
				.hasField(Message.FIELD_RESULT)
				.hasExitCode(0)
				.hasText(
						"\nMySQL 5.1 database added.  Please make note of these credentials:\n\n"
								+ "       Root User: adminnFC22YQ\n   Root Password: U1IX8AIlrEcl\n   Database Name: springeap6\n\n"
								+ "Connection URL: mysql://$OPENSHIFT_MYSQL_DB_HOST:$OPENSHIFT_MYSQL_DB_PORT/\n\n"
								+ "You can manage your new MySQL database by also embedding phpmyadmin-3.4.\n"
								+ "The phpmyadmin username and password will be the same as the MySQL credentials above.\n");
		new MessageAssert(it.next())
				.hasField(Message.FIELD_APPINFO)
				.hasExitCode(0)
				.hasText("Connection URL: mysql://127.13.125.1:3306/\n");

		assertThat(response.getDataType()).isEqualTo(EnumDataType.cartridge);
		final CartridgeResourceDTO cartridge = response.getData();
		assertThat(cartridge.getName()).isEqualTo("mysql-5.1");
		assertThat(cartridge.getType()).isEqualTo(CartridgeType.EMBEDDED);
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
		RestResponse response = ResourceDTOFactory.get(content);
		// verifications
		assertThat(response.getMessages()).hasSize(0);
		assertThat(response.getDataType()).isEqualTo(EnumDataType.cartridges);
		final List<CartridgeResourceDTO> cartridges = response.getData();
		assertThat(cartridges).hasSize(3); // mysql, mongo, jbosseap
		assertThat(cartridges).onProperty("name").contains("mongodb-2.2", "mysql-5.1", "jbosseap-6.0");
	}

	/**
	 * Should unmarshall get application response body.
	 * 
	 * @throws Throwable
	 */
	@Test
	public void shouldUnmarshallGetApplicationCartridgesWith3ElementsResponseBody() throws Throwable {
		// pre-conditions
		String content = Samples.GET_DOMAINS_FOOBARZ_APPLICATIONS_SPRINGEAP6_CARTRIDGES_2EMBEDDED.getContentAsString();
		assertNotNull(content);
		// operation
		RestResponse response = ResourceDTOFactory.get(content);
		// verifications
		assertThat(response.getMessages()).hasSize(0);
		assertThat(response.getDataType()).isEqualTo(EnumDataType.cartridges);
		final List<CartridgeResourceDTO> cartridges = response.getData();
		assertThat(cartridges).hasSize(3);
		assertThat(cartridges).onProperty("name").contains("mongodb-2.2", "mysql-5.1", "jbosseap-6.0");
	}

	@Test
	public void shouldUnmarshallGetApplicationGearGroupsResponseBody() throws Throwable {
		// pre-conditions
		String content = Samples.GET_DOMAINS_FOOBARZ_APPLICATIONS_SPRINGEAP6_GEARGROUPS.getContentAsString();
		assertNotNull(content);
		// operation
		RestResponse response = ResourceDTOFactory.get(content);
		// verifications
		Collection<GearGroupResourceDTO> gearGroups = response.getData();
		assertThat(gearGroups.size()).isEqualTo(3);
		GearGroupResourceDTO gearGroup = gearGroups.iterator().next();
		assertThat(gearGroup.getName()).isEqualTo("514207b84382ec1fef0000ab");
		assertThat(gearGroup.getUuid()).isEqualTo("514207b84382ec1fef0000ab");
		assertThat(gearGroup.getGears()).hasSize(2);
		final IGear gear = gearGroup.getGears().iterator().next();
		assertThat(gear.getId()).isEqualTo("514207b84382ec1fef000098");
		assertThat(gear.getState()).isEqualTo(GearState.IDLE);
	}

	@Test
	public void shouldUnmarshallSingleValidOptionInResponseBody() throws Throwable {
		// pre-conditions
		String content = Samples.POST_MYSQL_DOMAINS_FOOBARZ_APPLICATIONS_SPRINGEAP6_CARTRIDGES.getContentAsString();
		assertNotNull(content);
		// operation
		RestResponse response = ResourceDTOFactory.get(content);
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
		RestResponse response = ResourceDTOFactory.get(content);
		// verifications
		final KeyResourceDTO key = response.getData();
		final Link link = key.getLink("UPDATE");
		assertThat(link.getOptionalParams()).hasSize(0);
		assertThat(link.getRequiredParams().get(0).getValidOptions()).contains("ssh-rsa", "ssh-dss");
	}

}
