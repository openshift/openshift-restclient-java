/******************************************************************************* 
 * Copyright (c) 2013 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/
package com.openshift.client.cartridge.selector;

import com.openshift.client.cartridge.IEmbeddableCartridge;
import com.openshift.client.cartridge.IStandaloneCartridge;


/**
 * @author Andre Dietisheim
 * 
 */
public class LatestVersionOf {

	public static  LatestEmbeddableCartridge mmsAgent() {
		return new LatestEmbeddableCartridge(IEmbeddableCartridge.NAME_10GEN_MMS_AGENT);
	}

	public static  LatestEmbeddableCartridge cron() {
		return new LatestEmbeddableCartridge(IEmbeddableCartridge.NAME_CRON);
	}

	public static  LatestEmbeddableCartridge haProxy() {
		return new LatestEmbeddableCartridge(IEmbeddableCartridge.NAME_HAPROXY);
	}

	public static  LatestEmbeddableCartridge jenkinsClient() {
		return new LatestEmbeddableCartridge(IEmbeddableCartridge.NAME_JENKINS_CLIENT);
	}

	public static  LatestEmbeddableCartridge metrics() {
		return new LatestEmbeddableCartridge(IEmbeddableCartridge.NAME_METRICS);
	}

	public static  LatestEmbeddableCartridge mongoDB() {
		return new LatestEmbeddableCartridge(IEmbeddableCartridge.NAME_MONGODB);
	}

	public static LatestEmbeddableCartridge phpMyAdmin() {
		return new LatestEmbeddableCartridge(IEmbeddableCartridge.NAME_PHPMYADMIN);
	}

	public static  LatestEmbeddableCartridge postgreSQL() {
		return new LatestEmbeddableCartridge(IEmbeddableCartridge.NAME_POSTGRESQL);
	}

	public static LatestEmbeddableCartridge mySQL() {
		return new LatestEmbeddableCartridge(IEmbeddableCartridge.NAME_MYSQL);
	}

	public static  LatestEmbeddableCartridge rockMongo() {
		return new LatestEmbeddableCartridge(IEmbeddableCartridge.NAME_ROCKMONGO);
	}

	public static LatestEmbeddableCartridge switchyard(){
		return new LatestEmbeddableCartridge(IEmbeddableCartridge.NAME_SWITCHYARD);
	}

	public static LatestStandaloneCartridge jbossEws(){
		return new LatestStandaloneCartridge(IStandaloneCartridge.NAME_JBOSSEWS);
	}

	public static LatestStandaloneCartridge jbossEap(){
		return new LatestStandaloneCartridge(IStandaloneCartridge.NAME_JBOSSEAP);
	}

	public static LatestStandaloneCartridge jbossAs(){
		return new LatestStandaloneCartridge(IStandaloneCartridge.NAME_JBOSSAS);
	}

	public static LatestStandaloneCartridge jenkins(){
		return new LatestStandaloneCartridge(IStandaloneCartridge.NAME_JENKINS);
	}
	
	public static LatestStandaloneCartridge perl(){
		return new LatestStandaloneCartridge(IStandaloneCartridge.NAME_PERL);
	}

	public static LatestStandaloneCartridge php(){
		return new LatestStandaloneCartridge(IStandaloneCartridge.NAME_PHP);
	}

	public static LatestStandaloneCartridge python(){
		return new LatestStandaloneCartridge(IStandaloneCartridge.NAME_PYTHON);
	}

	public static LatestStandaloneCartridge ruby(){
		return new LatestStandaloneCartridge(IStandaloneCartridge.NAME_RUBY);
	}

	public static LatestStandaloneCartridge zend(){
		return new LatestStandaloneCartridge(IStandaloneCartridge.NAME_ZEND);
	}
}
