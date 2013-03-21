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

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.fest.assertions.Condition;
import org.junit.Test;

import com.openshift.client.HttpMethod;
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
		assertThat(userResourceDTO.getLinks()).hasSize(3);
	}

	@Test
	public void shouldUnmarshallGetUserNoKeyResponseBody() throws Throwable {
		// pre-conditions
		String content = Samples.GET_USER_KEYS_NONE_JSON.getContentAsString();
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
		String content = Samples.GET_USER_KEYS_SINGLE_JSON.getContentAsString();
		assertNotNull(content);
		// operation
		RestResponse response = ResourceDTOFactory.get(content);
		// verifications
		assertThat(response.getDataType()).isEqualTo(EnumDataType.keys);
		List<KeyResourceDTO> keys = response.getData();
		assertThat(keys).hasSize(1);
		final KeyResourceDTO key = keys.get(0);
		assertThat(key.getLinks()).hasSize(3);
		assertThat(key.getName()).isEqualTo("default");
		assertThat(key.getType()).isEqualTo("ssh-rsa");
		assertThat(key.getContent()).isEqualTo("AAAA");
	}

	@Test
	public void shouldUnmarshallGetUserMultipleKeyResponseBody() throws Throwable {
		// pre-conditions
		String content = Samples.GET_USER_KEYS_MULTIPLE_JSON.getContentAsString();
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
		assertThat(key.getContent()).isEqualTo("AAAA");
	}

	@Test
	public void shouldUnmarshallGetRootAPIResponseBody() throws Throwable {
		// pre-conditions
		String content = Samples.GET_REST_API_JSON.getContentAsString();
		assertNotNull(content);
		// operation
		RestResponse response = ResourceDTOFactory.get(content);
		// verifications
		assertThat(response.getDataType()).isEqualTo(EnumDataType.links);
		final Map<String, Link> links = response.getData();
		assertThat(links).hasSize(6);
		assertThat(links).satisfies(new ValidLinkCondition());

	}

	@Test
	public void shouldUnmarshallGetDomainsWith1ExistingResponseBody() throws Throwable {
		// pre-conditions
		String content = Samples.GET_DOMAINS_1EXISTING.getContentAsString();
		assertNotNull(content);
		// operation
		RestResponse response = ResourceDTOFactory.get(content);
		// verifications
		assertThat(response.getDataType()).isEqualTo(EnumDataType.domains);
		final List<DomainResourceDTO> domainDTOs = response.getData();
		assertThat(domainDTOs).isNotEmpty();
		assertThat(domainDTOs).hasSize(1);
		final DomainResourceDTO domainDTO = domainDTOs.get(0);
		assertThat(domainDTO.getNamespace()).isEqualTo("foobar");
		assertThat(domainDTO.getLinks()).hasSize(6);
		final Link link = domainDTO.getLink(ADD_APPLICATION);
		assertThat(link).isNotNull();
		assertThat(link.getHref()).isEqualTo("/domains/foobar/applications");
		assertThat(link.getRel()).isEqualTo("Create new application");
		assertThat(link.getHttpMethod()).isEqualTo(HttpMethod.POST);
		final List<LinkParameter> requiredParams = link.getRequiredParams();
		assertThat(requiredParams).hasSize(2);
	}

	@Test
	public void shouldUnmarshallGetDomainsWithNoExistingResponseBody() throws Throwable {
		// pre-conditions
		String content = Samples.GET_DOMAINS_NOEXISTING_JSON.getContentAsString();
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
		String content = Samples.GET_DOMAIN.getContentAsString();
		assertNotNull(content);
		// operation
		RestResponse response = ResourceDTOFactory.get(content);
		// verifications
		assertThat(response.getDataType()).isEqualTo(EnumDataType.domain);
		final DomainResourceDTO domain = response.getData();
		assertNotNull(domain);
		assertThat(domain.getNamespace()).isEqualTo("foobar");
		assertThat(domain.getLinks()).hasSize(6);
	}

	@Test
	public void shouldUnmarshallDeleteDomainKoNotFoundResponseBody() throws Throwable {
		// pre-conditions
		String content = Samples.DELETE_DOMAIN_KO_NOTFOUND_JSON.getContentAsString();
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
		String content = Samples.GET_APPLICATIONS_WITH2APPS_JSON.getContentAsString();
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
	 * @throws Throwable 
	 */
	@Test
	public void shouldUnmarshallGetApplicationWithAliasesResponseBody() throws Throwable {
		// pre-conditions
		String content = Samples.GET_APPLICATION_WITH2CARTRIDGES2ALIASES_JSON.getContentAsString();
		assertNotNull(content);
		// operation
		RestResponse response = ResourceDTOFactory.get(content);
		// verifications
		assertThat(response.getDataType()).isEqualTo(EnumDataType.application);
		final ApplicationResourceDTO application = response.getData();
		assertThat(application.getUuid()).hasSize(32);
		assertThat(application.getCreationTime()).startsWith("2012-");
		assertThat(application.getDomainId()).isEqualTo("foobar");
		assertThat(application.getFramework()).isEqualTo("jbossas-7");
		assertThat(application.getName()).isEqualTo("sample");
		assertThat(application.getLinks()).hasSize(14);
		assertThat(application.getAliases()).contains("an_alias", "another_alias");
	}

	/**
	 * Should unmarshall get application response body.
	 * @throws Throwable 
	 */
	@Test
	public void shouldUnmarshallAddApplicationEmbeddedCartridgeResponseBody() throws Throwable {
		// pre-conditions
		String content = Samples.ADD_APPLICATION_CARTRIDGE_JSON.getContentAsString();
		assertNotNull(content);
		// operation
		RestResponse response = ResourceDTOFactory.get(content);
		// verifications
		assertThat(response.getMessages()).hasSize(3);
		assertThat(response.getDataType()).isEqualTo(EnumDataType.cartridge);
		final CartridgeResourceDTO cartridge = response.getData();
		assertThat(cartridge.getName()).isEqualTo("mysql-5.1");
		assertThat(cartridge.getType()).isEqualTo(CartridgeType.EMBEDDED);
		assertThat(cartridge.getLinks()).hasSize(6);

	}

	/**
	 * Should unmarshall get application response body.
	 * @throws Throwable 
	 */
	@Test
	public void shouldUnmarshallGetApplicationCartridgesWith1ElementResponseBody() throws Throwable {
		// pre-conditions
		String content = Samples.GET_APPLICATION_CARTRIDGES_WITH1ELEMENT_JSON.getContentAsString();
		assertNotNull(content);
		// operation
		RestResponse response = ResourceDTOFactory.get(content);
		// verifications
		assertThat(response.getMessages()).hasSize(0);
		assertThat(response.getDataType()).isEqualTo(EnumDataType.cartridges);
		final List<CartridgeResourceDTO> cartridges = response.getData();
		assertThat(cartridges).hasSize(1);
		assertThat(cartridges).onProperty("name").contains("mongodb-2.0");
	}

	/**
	 * Should unmarshall get application response body.
	 * @throws Throwable 
	 */
	@Test
	public void shouldUnmarshallGetApplicationCartridgesWith2ElementsResponseBody() throws Throwable {
		// pre-conditions
		String content = Samples.GET_APPLICATION_CARTRIDGES_WITH2ELEMENTS_JSON.getContentAsString();
		assertNotNull(content);
		// operation
		RestResponse response = ResourceDTOFactory.get(content);
		// verifications
		assertThat(response.getMessages()).hasSize(0);
		assertThat(response.getDataType()).isEqualTo(EnumDataType.cartridges);
		final List<CartridgeResourceDTO> cartridges = response.getData();
		assertThat(cartridges).hasSize(2);
		assertThat(cartridges).onProperty("name").contains("mongodb-2.0", "mysql-5.1");
	}

	/**
	 * Should unmarshall get application response body.
	 * @throws Throwable 
	 */
	//@Test
	public void shouldUnmarshallGetApplicationGearsResponseBody() throws Throwable {
		// pre-conditions
		String content = Samples.GET_APPLICATION_GEARS_WITH2ELEMENTS_JSON.getContentAsString();
		assertNotNull(content);
		// operation
		RestResponse response = ResourceDTOFactory.get(content);
		// verifications
		final List<GearDTO> gears = response.getData();
		assertThat(gears).hasSize(2);
		final GearDTO gear = gears.get(1); 
		assertThat(gear.getUuid()).isEqualTo("f936d82ee6b146adbb18e3f41d922006");
		//assertThat(gear.getGitUrl()).isEqualTo(
		//		"ssh://f936d82ee6b146adbb18e3f41d922006@scalable-foobar.stg.rhcloud.com/~/git/scalable.git/");
		//assertThat(gear.getComponents()).contains(
		//		new GearComponentDTO("jbossas-7", "8080", "proxy", "3128", null),
		//		new GearComponentDTO("mongodb-2.0", null, null, null, null), 
		//		new GearComponentDTO("mysql-5.1", null, null, null, null));
	}

	@Test
	public void shouldUnmarshallSingleValidOptionInResponseBody() throws Throwable {
		// pre-conditions
		String content = Samples.ADD_APPLICATION_CARTRIDGE_JSON.getContentAsString();
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
		String content = Samples.ADD_USER_KEY2_OK_JSON.getContentAsString();
		assertNotNull(content);
		// operation
		RestResponse response = ResourceDTOFactory.get(content);
		// verifications
		final KeyResourceDTO key = response.getData();
		final Link link = key.getLink("UPDATE");
		assertThat(link.getOptionalParams()).hasSize(0);
		assertThat(link.getRequiredParams().get(0).getValidOptions()).containsExactly("ssh-rsa", "ssh-dss");
	}

}
