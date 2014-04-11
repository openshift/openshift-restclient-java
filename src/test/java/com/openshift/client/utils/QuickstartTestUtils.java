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
package com.openshift.client.utils;

import java.util.List;

import com.openshift.client.IQuickstart;

/**
 * @author André Dietisheim
 */
public class QuickstartTestUtils {

	public static final String AEROGEAR_PUSH_0X = "AeroGear Push 0.X";
	public static final String CACTI = "Cacti";
	public static final String CAKEPHP = "CakePHP";
	public static final String CAPEDWARF = "CapeDwarf";
	public static final String CARTRIDGE_DEVELELOPMENT_KIT = "Cartridge Development Kit";
	public static final String DJANGO = "Django";
	public static final String DRUPAL_8 = "Drupal 8";
	public static final String GO_LANGUAGE = "Go Language";
	public static final String JBOSS_FUSE_61 = "JBoss Fuse 6.1";
	public static final String LARAVEL_41 = "Laravel 4.1 Quickstart";
	public static final String REVEALJS = "Reveal.js";
	public static final String RUBY_ON_RAILS = "Ruby on Rails";
	public static final String TEXTPRESS = "TextPress";
	public static final String WILDFLY_8 = "WildFly 8";
	public static final String WORDPRESS_3X = "WordPress 3.x";
	
	public static IQuickstart getByName(String name, List<IQuickstart> quickstarts) {
		IQuickstart matchingQuickstart = null;
		for (IQuickstart quickstart : quickstarts) {
			if (name.equals(quickstart.getName())) {
				matchingQuickstart = quickstart;
				break;
			}
		}
		return matchingQuickstart;
	}


}
