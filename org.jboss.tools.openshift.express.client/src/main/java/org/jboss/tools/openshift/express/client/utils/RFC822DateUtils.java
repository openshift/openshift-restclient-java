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
package org.jboss.tools.openshift.express.client.utils;

import java.util.Date;
import java.util.GregorianCalendar;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;

/**
 * @author André Dietisheim
 */
public class RFC822DateUtils {

	public static Date getDate(String rfc822DateString) throws DatatypeConfigurationException {
		// SimpleDateFormat can't handle RFC822 (-04:00 instead of GMT-04:00)
		// date formats
		//
		// SimpleDateFormat dateFormat = new
		// SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
		// return dateFormat.parse(propertyNode.asString());
		GregorianCalendar calendar =
				DatatypeFactory.newInstance().newXMLGregorianCalendar(rfc822DateString).toGregorianCalendar();
		return calendar.getTime();
	}

	public static String getString(Date date) throws DatatypeConfigurationException {
		GregorianCalendar calendar = (GregorianCalendar) GregorianCalendar.getInstance();
		return DatatypeFactory.newInstance().newXMLGregorianCalendar(calendar).toXMLFormat();
	}
}
