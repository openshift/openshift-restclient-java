/******************************************************************************* 
 * Copyright (c) 2011 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/
package com.openshift.express.client;


/**
 * @author André Dietisheim
 */
public interface ICartridge {

	@Deprecated
	public static final ICartridge JBOSSAS_7 = new Cartridge("jbossas-7");
	@Deprecated
	public static final ICartridge JENKINS_14 = new Cartridge("jenkins-1.4");
	@Deprecated
	public static final ICartridge PERL_51 = new Cartridge("perl-5.10");
	@Deprecated
	public static final ICartridge PYTHON_26 = new Cartridge("python-2.6");
	@Deprecated
	public static final ICartridge RUBY_18 = new Cartridge("ruby-1.8");
	@Deprecated
	public static final ICartridge PHP_53 = new Cartridge("php-5.3");
	
	@Deprecated
	public static final ICartridge RACK_11 = new Cartridge("ruby-1.8");
	@Deprecated
	public static final ICartridge WSGI_32 = new Cartridge("python-2.6");
	

	public abstract String getName();
	
	public abstract String getLogLocation();

}