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
package com.openshift.client.cartridge;

/**
 * @author André Dietisheim
 */
public interface IStandaloneCartridge extends ICartridge {

	public static final String NAME_JBOSSEWS = "jbossews";
	public static final String NAME_JBOSSAS = "jbossas";
	public static final String NAME_JBOSSEAP = "jbosseap";
	public static final String NAME_JENKINS = "jenkins";
	public static final String NAME_PERL = "perl";
	public static final String NAME_PHP = "php";
	public static final String NAME_PYTHON = "python";
	public static final String NAME_RUBY = "ruby";
	public static final String NAME_ZEND = "zend";

}