/******************************************************************************* 
 * Copyright (c) 2014 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/
package com.openshift.internal.client;

import static org.fest.assertions.Assertions.assertThat;

import java.net.URL;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.jboss.dmr.ModelNode;
import org.junit.Before;
import org.junit.Test;

import com.openshift.client.IOpenShiftConnection;
import com.openshift.client.IQuickstart;
import com.openshift.client.cartridge.ICartridge;
import com.openshift.client.utils.CartridgeTestUtils;
import com.openshift.client.utils.QuickstartAssert;
import com.openshift.client.utils.QuickstartTestUtils;
import com.openshift.client.utils.Samples;
import com.openshift.client.utils.TestConnectionBuilder;
import com.openshift.internal.client.cartridge.BaseCartridge;
import com.openshift.internal.client.response.QuickstartDTO;

/**
 * @author Andre Dietisheim
 */
public class QuickstartTest extends TestTimer {

	private IOpenShiftConnection connection;
	private HttpClientMockDirector mockDirector;

	@Before
	public void setup() throws Throwable {
		this.mockDirector = new HttpClientMockDirector();
		mockDirector.mockGetQuickstarts(Samples.GET_API_QUICKSTARTS);
		connection = new TestConnectionBuilder().defaultCredentials().create(mockDirector.client());
	}

	@Test
	public void shouldListQuickstarts() throws Throwable {
		// pre-conditions

		// operation
		List<IQuickstart> quickstarts = connection.getQuickstarts();

		// verification
		assertThat(quickstarts)
				.hasSize(78)
				.onProperty("name")
				.contains(QuickstartTestUtils.WORDPRESS_3X
						, QuickstartTestUtils.RUBY_ON_RAILS
						, QuickstartTestUtils.CAPEDWARF
						, QuickstartTestUtils.DJANGO
						, QuickstartTestUtils.CAKEPHP
						, QuickstartTestUtils.DRUPAL_8
						, QuickstartTestUtils.REVEALJS
						, QuickstartTestUtils.CARTRIDGE_DEVELELOPMENT_KIT
						, QuickstartTestUtils.GO_LANGUAGE
						, QuickstartTestUtils.AEROGEAR_PUSH_0X
						, QuickstartTestUtils.WILDFLY_8);
	}

	@Test
	public void shouldUnmarshallWildfly8Quickstart() throws Throwable {
		// pre-conditions
		// operation
		IQuickstart wilfly8Quickstart = QuickstartTestUtils.getByName(QuickstartTestUtils.WILDFLY_8,
				connection.getQuickstarts());

		// verification
		new QuickstartAssert(wilfly8Quickstart)
				.hasId("16766")
				.hasHref("https://www.openshift.com/quickstarts/wildfly-8")
				.hasName(QuickstartTestUtils.WILDFLY_8)
				.hasSummary("WildFly is a flexible, lightweight, managed application runtime "
						+ "that helps you build amazing applications.\n\nThis cartridge provides WIldFly 8.0.0.Final")
				.hasWebsite("http://www.wildfly.org")
				.hasTags("java", "java_ee", "jboss")
				.hasLanguage("Java")
				.hasProvider("trusted")
				.hasCartridges(Arrays.<ICartridge> asList(CartridgeTestUtils.wildfly8()));
	}

	@SuppressWarnings("unchecked")
	@Test
	public void shouldUnmarshallDjangoQuickstart() throws Throwable {
		// pre-conditions
		// operation
		IQuickstart djangoQuickstart = QuickstartTestUtils.getByName(QuickstartTestUtils.DJANGO,
				connection.getQuickstarts());

		// verification
		new QuickstartAssert(djangoQuickstart)
				.hasId("12730")
				.hasHref("https://www.openshift.com/quickstarts/django")
				.hasName(QuickstartTestUtils.DJANGO)
				.hasSummary("A high-level Python web framework that encourages rapid development and clean, "
						+ "pragmatic design.\n\nDuring application creation the Django admin username and password"
						+ " will be written to a file called CREDENTIALS in your data directory.  "
						+ "You will need to SSH into your application to access these credentials.")
				.hasWebsite("https://www.djangoproject.com/")
				.hasTags("framework", "python")
				.hasLanguage("Python")
				.hasProvider("openshift")
				// expression := python-3|python-2, availble := python-3.3 and
				// pythong-2.6
				.hasCartridgeNames(Arrays.<String> asList("python-3.3", "python-2.6", "python-2.7"));
	}

	@Test
	public void shouldUnmarshallDrupal8Quickstart() throws Throwable {
		// pre-conditions
		// operation
		IQuickstart drupalQuickstart = QuickstartTestUtils.getByName(QuickstartTestUtils.DRUPAL_8,
				connection.getQuickstarts());

		// verification
		new QuickstartAssert(drupalQuickstart)
				.hasId("14942")
				.hasHref("https://www.openshift.com/quickstarts/drupal-8")
				.hasName(QuickstartTestUtils.DRUPAL_8)
				.hasSummary(
						"Try out the latest alpha releases of Drupal 8 on OpenShift. "
								+ "(Drupal is under active development, so any Drupal 8 sites should not be considered production-ready.)\n\n"
								+ "Drupal is an open source content management platform written in PHP powering millions of websites and applications. "
								+ "It is built, used, and supported by an active and diverse community of people around the world. "
								+ "Administrator user name and password are written to $OPENSHIFT_DATA_DIR/CREDENTIALS.")
				.hasWebsite("https://drupal.org")
				.hasTags("drupal")
				.hasLanguage("PHP")
				.hasProvider("community")
				.hasCartridges(
						Arrays.<ICartridge> asList(
								new BaseCartridge(
										new URL(
												"https://cartreflect-claytondev.rhcloud.com/reflect?github=phase2/openshift-php-fpm")),
								CartridgeTestUtils.mysql51(),
								new BaseCartridge(
										new URL(
												"https://cartreflect-claytondev.rhcloud.com/reflect?github=phase2/openshift-community-drush-master"))));
	}

	@Test
	public void shouldUnmarshallAerogearPushQuickstart() throws Throwable {
		// pre-conditions
		// operation
		IQuickstart aeroGarPushQuickstart = QuickstartTestUtils.getByName(QuickstartTestUtils.AEROGEAR_PUSH_0X,
				connection.getQuickstarts());

		// verification
		new QuickstartAssert(aeroGarPushQuickstart)
				.hasId("15549")
				.hasHref("https://www.openshift.com/quickstarts/aerogear-push-0x")
				.hasName(QuickstartTestUtils.AEROGEAR_PUSH_0X)
				.hasSummary(
						"The AeroGear UnifiedPush Server allows for sending native push messages to different mobile operation systems. "
								+ "This initial community version of the server supports Apple’s Push Notification Service (APNs), "
								+ "Google Cloud Messaging (GCM) and Mozilla’s SimplePush.\n\n"
								+ "It has a built in administrative console that makes it easy for developers of any type to create and manage "
								+ "push related aspects of their applications.")
				.hasWebsite("http://aerogear.org/")
				.hasTags("instant_app", "java", "messaging", "not_scalable", "xpaas")
				.hasLanguage("Java")
				.hasProvider("openshift")
				// expression :=
				// https://cartreflect-claytondev.rhcloud.com/reflect?github=aerogear/openshift-origin-cartridge-aerogear-push#AeroGear,
				// mysql-5
				.hasCartridges(
						Arrays.<ICartridge> asList(
								new BaseCartridge(
										new URL(
												"https://cartreflect-claytondev.rhcloud.com/reflect?github=aerogear/openshift-origin-cartridge-aerogear-push#AeroGear")),
								CartridgeTestUtils.mysql51()));
	}

	@SuppressWarnings("unchecked")
	@Test
	public void shouldUnmarshallCactiQuickstart() throws Throwable {
		// pre-conditions

		// operation
		IQuickstart cactiQuickstart = QuickstartTestUtils.getByName(QuickstartTestUtils.CACTI,
				connection.getQuickstarts());

		// verification
		new QuickstartAssert(cactiQuickstart)
				// expression := [
				// {&quot;name&quot;:&quot;php-5.3&quot;},
				// {&quot;name&quot;:&quot;mysql-5.1&quot;},
				// {&quot;name&quot;:&quot;cron-1.4&quot;}
				// ]
				.hasCartridgeNames(
						Collections.<String> singletonList(CartridgeTestUtils.PHP_53_NAME),
						Collections.<String> singletonList(CartridgeTestUtils.MYSQL_51_NAME),
						Collections.<String> singletonList(CartridgeTestUtils.CRON_14_NAME));
	}

	@Test
	public void shouldHave3TagsFromCommaDelimitedItems() throws Throwable {
		// pre-conditions
		String json = QuickstartTestUtils.createQuickstartJsonForTags("redhat, jboss, adietish");

		// operation
		QuickstartDTO quickstart = QuickstartTestUtils.getFirstQuickstartDTO(json);

		// verification
		assertThat(quickstart.getTags()).containsExactly("redhat", "jboss", "adietish");
	}

	@Test
	public void shouldHave3TagsFromArray() throws Throwable {
		// pre-conditions
		String json = QuickstartTestUtils.createQuickstartJsonForTags(ModelNode
				.fromJSONString("[ \"redhat\", \"jboss\", \"adietish\" ]"));

		// operation
		QuickstartDTO quickstart = QuickstartTestUtils.getFirstQuickstartDTO(json);

		// verification
		assertThat(quickstart.getTags()).containsExactly("redhat", "jboss", "adietish");
	}

	@Test
	public void shouldHave3CartridgesFromCommaDelimitedItems() throws Throwable {
		// pre-conditions
		String json = QuickstartTestUtils.createQuickstartsJsonForCartridgeSpec("redhat, jboss, adietish");

		// operation
		QuickstartDTO quickstart = QuickstartTestUtils.getFirstQuickstartDTO(json);

		// verification
		assertThat(quickstart.getCartridges()).hasSize(3);
	}

	@Test
	public void shouldHave3CartridgesFromArray() throws Throwable {
		// pre-conditions
		String quickstartJson = QuickstartTestUtils.createQuickstartsJsonForCartridgeSpec(
				ModelNode.fromJSONString("[ \"redhat\", \"jboss\", \"adietish\" ]"));

		// operation
		QuickstartDTO dto = QuickstartTestUtils.getFirstQuickstartDTO(quickstartJson);

		// verification
		assertThat(dto.getCartridges()).hasSize(3);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void shouldOfferPhpMySqlCron() throws Throwable {
		// pre-conditions

		// operation
		IQuickstart cactiQuickstart = QuickstartTestUtils.getByName(QuickstartTestUtils.CACTI,
				connection.getQuickstarts());

		// verification
		new QuickstartAssert(cactiQuickstart)
				// expression := [
				// {&quot;name&quot;:&quot;php-5.3&quot;},
				// {&quot;name&quot;:&quot;mysql-5.1&quot;},
				// {&quot;name&quot;:&quot;cron-1.4&quot;}]
				.hasCartridgeNames(
						Collections.<String> singletonList(CartridgeTestUtils.PHP_53_NAME),
						Collections.<String> singletonList(CartridgeTestUtils.MYSQL_51_NAME),
						Collections.<String> singletonList(CartridgeTestUtils.CRON_14_NAME));
	}

	@Test
	public void shouldOfferDownloadableCartridge() throws Throwable {
		// pre-conditions

		// operation
		IQuickstart cactiQuickstart = QuickstartTestUtils.getByName(QuickstartTestUtils.JBOSS_FUSE_61,
				connection.getQuickstarts());

		// verification
		new QuickstartAssert(cactiQuickstart)
				// expression := https://bit.ly/1fYSzhk
				.hasCartridges(
				Collections.<ICartridge> singletonList(new BaseCartridge(new URL("https://bit.ly/1fYSzhk"))));
	}

	@SuppressWarnings("unchecked")
	@Test
	public void shouldOfferPhp53Php54NoMysqlAndHaveInitialGitUrl() throws Throwable {
		// pre-conditions

		// operation
		IQuickstart laravelQuickstart = 
				QuickstartTestUtils.getByName(QuickstartTestUtils.LARAVEL_41, connection.getQuickstarts());

		// verification
		new QuickstartAssert(laravelQuickstart)
				.hasInitialGitUrl("https://github.com/muffycompo/openshift-laravel4-quickstart-app.git")
				// expression := "php-5.3|php-5.4, mysql-5.5",
				.hasCartridgeNames(
						Arrays.<String> asList(CartridgeTestUtils.PHP_53_NAME),
						// mysql-5.5 is not present in get-cartridges.json
						Collections.<String>emptyList());
	}

	@Test
	public void shouldEmptyAlternativesForNonSuitableCartridge() throws Throwable {
		// pre-conditions

		// operation
		IQuickstart anahita = QuickstartTestUtils.getByName(QuickstartTestUtils.TEXTPRESS,
				connection.getQuickstarts());
		// expression:= php-5|zend-
		List<ICartridge> empty = anahita.getAlternativesFor(CartridgeTestUtils.as7());

		// verification
		assertThat(empty).isEmpty();
	}

	@Test
	public void shouldGiveZendAndPhpAlternativesForPhp53() throws Throwable {
		// pre-conditions

		// operation
		IQuickstart anahita = QuickstartTestUtils.getByName(QuickstartTestUtils.TEXTPRESS,
				connection.getQuickstarts());
		// expression:= php-5|zend-
		List<ICartridge> allPhp = anahita.getAlternativesFor(CartridgeTestUtils.php53());

		// verification
		// we have php-5.3 and zend-6.1, zend-5.3 (obsolete ones included)
		assertThat(allPhp).hasSize(3);
		assertThat(allPhp)
				.onProperty("name")
				.contains(CartridgeTestUtils.PHP_53_NAME, CartridgeTestUtils.ZEND_61_NAME);
	}

}
