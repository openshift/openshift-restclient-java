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

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.junit.Test;

import com.openshift.client.cartridge.ICartridge;
import com.openshift.client.cartridge.IEmbeddedCartridge;
import com.openshift.client.cartridge.query.ICartridgeQuery;
import com.openshift.client.utils.CartridgeAssert;
import com.openshift.client.utils.CartridgeTestUtils;
import com.openshift.internal.client.response.DownloadableCartridgeSpec;
import com.openshift.internal.client.response.NamedCartridgeSpec;
import com.openshift.internal.client.response.QuickstartDTO;
import com.openshift.internal.client.response.QuickstartJsonDTOFactory;
import com.openshift.internal.client.response.RestResponse;
import com.openshift.internal.client.utils.StringUtils;

/**
 * @author Andre Dietisheim
 */
public class QuickstartDTOCartridgeQueryTest {

	@Test
	public void shouldReturn1QueryForWildcardExpression() throws Throwable {
		// pre-conditions
		String cartridgesJson = createQuickstartJson("php-*");
		
		// operation
		List<ICartridgeQuery> queries = getCartridgeQueries(cartridgesJson);
		
		// verification
		assertThat(queries).hasSize(1);
	}

	@Test
	public void shouldReturn2Queries() throws Throwable {
		// pre-conditions
		String cartridgesJson = createQuickstartJson("php-*, mysql-*");
		
		// operation
		List<ICartridgeQuery> queries = getCartridgeQueries(cartridgesJson);
		
		// verification
		assertThat(queries).hasSize(2);
	}

	@Test
	public void shouldReturn1QueryForAlternativesExpression() throws Throwable {
		// pre-conditions
		String cartridgesJson = createQuickstartJson("jbosseap-|jbossas-"); 

		// operation
		List<ICartridgeQuery> queries = getCartridgeQueries(cartridgesJson); 

		// verification
		assertThat(queries).hasSize(1);
	}
	
	@Test
	public void shouldReturn1QueryForNameProperty() throws Throwable {
		// pre-conditions
		String cartridgesJson = createQuickstartJson(StringUtils.encodeQuotationMarks("[{ \"name\": \"mysql-5.2\" }]")); 

		// operation
		List<ICartridgeQuery> queries = getCartridgeQueries(cartridgesJson); 

		// verification
		assertThat(queries).hasSize(1);
	}
	
	@Test
	public void shouldReturn2QueriesForJsonArrayOfObjects() throws Throwable {
		// pre-conditions
		String cartridgesJson = createQuickstartJson(StringUtils.encodeQuotationMarks("[{ \"name\": \"mysql-5.2\" }, { \"url\": \"http://www.redhat.com\" }]")); 

		// operation
		List<ICartridgeQuery> queries = getCartridgeQueries(cartridgesJson); 

		// verification
		assertThat(queries).hasSize(2);
	}

	@Test
	public void shouldReturn2QueryForJsonArryOfObjectAndString() throws Throwable {
		// pre-conditions
		String cartridgesJson = createQuickstartJson(StringUtils.encodeQuotationMarks("[{ \"name\": \"mysql-5.2\" }, \"jbosstools\"]")); 

		// operation
		List<ICartridgeQuery> queries = getCartridgeQueries(cartridgesJson); 

		// verification
		assertThat(queries).hasSize(2);
	}

	@Test
	public void shouldReturnNoQueryForInvalidUrl() throws Throwable {
		// pre-conditions
		String cartridgesJson = createQuickstartJson(StringUtils.encodeQuotationMarks("[{ \"url\": \"bogusUrl\" }]")); 

		// operation
		List<ICartridgeQuery> queries = getCartridgeQueries(cartridgesJson); 

		// verification
		assertThat(queries).isEmpty();
	}

	@Test
	public void shouldReturnDownloadableCartridge() throws Throwable {
		// pre-conditions
		DownloadableCartridgeSpec query = new DownloadableCartridgeSpec("http://www.redhat.com");

		// operation
		ICartridge cartridge = query.get(Collections.<ICartridge> emptyList());
		Collection<ICartridge> cartridges = query.getAll(Collections.<ICartridge> emptyList());

		// verification
		assertThat(cartridge).isNotNull();
		assertThat(cartridge.isDownloadable()).isTrue();
		assertThat(cartridges).hasSize(1);
	}
	
	@Test
	public void shouldReturnNamedCartridge() throws Throwable {
		// pre-conditions
		NamedCartridgeSpec query = new NamedCartridgeSpec("jbosstools");

		// operation
		ICartridge cartridge = query.get(Collections.<ICartridge> emptyList());
		Collection<ICartridge> cartridges = query.getAll(Collections.<ICartridge> emptyList());

		// verification
		assertThat(cartridge).isNotNull();
		assertThat(cartridge.isDownloadable()).isFalse();;
		assertThat(cartridges).hasSize(1);
	}

	@Test
	public void shouldReturn2Cartridges() throws Throwable {
		// pre-conditions
		String cartridgesJson = createQuickstartJson("php-*");
		
		// operation
		ICartridgeQuery query = getFirstCartridgeQuery(cartridgesJson);
		
		// verification
		Collection<IEmbeddedCartridge> cartridges = query.getAll(CartridgeTestUtils.createEmbeddedCartridgeMocks("php-5.3", "php-5.4"));
		assertThat(cartridges).hasSize(2);
	}
	
	@Test
	public void shouldReturnEmptyResults() throws Throwable {
		// pre-conditions
		String cartridgesJson = createQuickstartJson("jboss-8");

		// operation
		ICartridgeQuery query = getFirstCartridgeQuery(cartridgesJson);

		// verification
		assertThat(query.getAll(CartridgeTestUtils.createEmbeddedCartridgeMocks("php-5.4", "jbossas-7"))).isEmpty();
	}


	@Test
	public void shouldReturn4AlternativesAnd1SingleCartridge() throws Throwable {
		// pre-conditions
		String cartridgesJson = createQuickstartJson("jboss*, mysql-*");

		// operation
		List<ICartridgeQuery> queries = getCartridgeQueries(cartridgesJson);
		assertThat(queries).hasSize(2);
		List<IEmbeddedCartridge> availableCartridges = CartridgeTestUtils.createEmbeddedCartridgeMocks(
				"jbossas-7", "jbosseap-6", "jbossews-1.0", "jbossews-2.0", "mysql-5.1", "php-5.4", "nodejs-0.10");
		Collection<IEmbeddedCartridge> cartridges1 = queries.get(0).getAll(availableCartridges);
		Collection<IEmbeddedCartridge> cartridges2 = queries.get(1).getAll(availableCartridges);
		
		// verification
		assertThat(cartridges1)
				.hasSize(4)
				.onProperty("name").contains("jbosseap-6", "jbossas-7", "jbossews-1.0", "jbossews-2.0");
		assertThat(cartridges2)
				.hasSize(1)
				.onProperty("name").contains("mysql-5.1");
	}
	
	@Test
	public void shouldReturnExactMatchPhp() throws Throwable {
		// pre-conditions
		List<ICartridgeQuery> queries = getCartridgeQueries(createQuickstartJson("php-5.4")); 
		assertThat(queries).hasSize(1);

		// operation
		List<ICartridge> availableCartridges = CartridgeTestUtils.createCartridges(
				"php-5.4", "php-5.3", "php-5.2");
		List<ICartridge> cartridges = queries.get(0).getAll(availableCartridges);

		// verification
		assertThat(cartridges).hasSize(1);
		new CartridgeAssert<ICartridge>(cartridges.get(0))
			.hasName("php-5.4");
	}

	@Test
	public void shouldReturnPhpOrMysql() throws Throwable {
		// pre-conditions
		String cartridgesJson = createQuickstartJson("php|mysql"); 
		List<ICartridgeQuery> queries = getCartridgeQueries(cartridgesJson); 
		assertThat(queries).hasSize(1);

		// operation
		List<ICartridge> availableCartridges = CartridgeTestUtils.createCartridges(
				"jbossas-7", "mysql-5.1", "php-5.4", "php-5.3");
		List<ICartridge> cartridges = queries.get(0).getAll(availableCartridges);

		// verification
		assertThat(cartridges)
				.hasSize(3)
				.onProperty("name").contains("php-5.3", "php-5.4", "mysql-5.1");
	}

	@Test
	public void shouldReturnRequiredDownloadableCartridge() throws Throwable {
		// pre-conditions
		String cartridgesJson = createQuickstartJson(StringUtils
				.encodeQuotationMarks("[{ \"url\" : \"" + CartridgeTestUtils.AEROGEAR_PUSH_URL + "\"}]"));

		// operation
		List<ICartridgeQuery> queries = getCartridgeQueries(cartridgesJson);
		assertThat(queries).hasSize(1);
		List<ICartridge> cartridges = queries.get(0).getAll(Collections.<ICartridge> emptyList());
		
		// verification
		assertThat(cartridges).hasSize(1);
		new CartridgeAssert<ICartridge>(cartridges.get(0))
			.isDownloadable()
			.hasUrl(CartridgeTestUtils.AEROGEAR_PUSH_URL);
	}

	@Test
	public void shouldReturn2PhpAnd1DownloadableCartridge() throws Throwable {
		// pre-conditions
		String cartridgesJson = createQuickstartJson(StringUtils.encodeQuotationMarks(
				"[ \"php*\", { \"url\": \"" + CartridgeTestUtils.FOREMAN_URL+ "\" }]")); 
		List<ICartridgeQuery> queries = getCartridgeQueries(cartridgesJson); 
		assertThat(queries).hasSize(2);

		// operation
		List<ICartridge> availableCartridges = CartridgeTestUtils.createCartridges(
				"jbossas-7", "mysql-5.1", "php-5.4", "php-5.3");
		List<ICartridge> cartridges1 = queries.get(0).getAll(availableCartridges);
		List<ICartridge> cartridges2 = queries.get(1).getAll(availableCartridges);

		// verification
		assertThat(cartridges1)
				.hasSize(2)
				.onProperty("name").contains("php-5.3", "php-5.4");
		new CartridgeAssert<ICartridge>(cartridges2.get(0))
				.isDownloadable()
				.hasUrl(CartridgeTestUtils.FOREMAN_URL);

	}

	@Test
	public void shouldReturnDownloadableCartridgeAndMysql() throws Throwable {
		// pre-conditions
		String cartridgesJson = createQuickstartJson(StringUtils
				.encodeQuotationMarks("[{ \"url\": \"" + CartridgeTestUtils.AEROGEAR_PUSH_URL+ "\" }, \"jboss\"]")); 
		List<ICartridgeQuery> queries = getCartridgeQueries(cartridgesJson); 
		assertThat(queries).hasSize(2);

		// operation
		List<ICartridge> availableCartridges = CartridgeTestUtils.createCartridges(
				"jbossas-7", "jbosseap-6", "jbossews-1.0", "jbossews-2.0", "mysql-5.1", "php-5.4", "nodejs-0.10");
		List<ICartridge> cartridges1 = queries.get(0).getAll(availableCartridges);
		List<ICartridge> cartridges2 = queries.get(1).getAll(availableCartridges);

		// verification
		assertThat(cartridges1).hasSize(1);
		new CartridgeAssert<ICartridge>(cartridges1.get(0))
			.isDownloadable()
			.hasUrl(CartridgeTestUtils.AEROGEAR_PUSH_URL);
		assertThat(cartridges2)
				.hasSize(4)
				.onProperty("name").contains("jbossas-7", "jbosseap-6", "jbossews-1.0", "jbossews-2.0");
	}

	
	private String createQuickstartJson(String cartridgesSpec) {
		return "{ \"data\": "
				+ "[ "
				+ "		{ "
				+ "			\"quickstart\": "
				+ "				{ "
				+ "					\"cartridges\": \"" + cartridgesSpec + "\" "
				+ "				} "
				+ "		} "
				+ "] "
				+ "}";
	}

	private List<ICartridgeQuery> getCartridgeQueries(String cartridgesJson) {
		RestResponse restResponse = new QuickstartJsonDTOFactory().get(cartridgesJson);

		assertThat(restResponse).isNotNull();
		assertThat(restResponse.getData()).isInstanceOf(List.class);

		List<QuickstartDTO> quickstartDTOs = restResponse.getData();
		assertThat(quickstartDTOs).hasSize(1);
		
		return quickstartDTOs.get(0).getCartridges();
	}

	private ICartridgeQuery getFirstCartridgeQuery(String cartridgesJson) {
		List<ICartridgeQuery> queries = getCartridgeQueries(cartridgesJson);
		assertThat(queries.size() > 0);
		return queries.get(0);
	}
}
