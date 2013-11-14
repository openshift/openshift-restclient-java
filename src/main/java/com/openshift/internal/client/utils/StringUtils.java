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
package com.openshift.internal.client.utils;


/**
 * @author André Dietisheim
 */
public class StringUtils {

	public static String toLowerCase(String message) {
		if (message == null) {
			return null;
		}
		return message.toLowerCase();
	}

	public static String toLowerCase(Enum<?> aEnum) {
		if (aEnum == null) {
			return null;
		}
		return toLowerCase(aEnum.name());
	}

	public static boolean isEmpty(String string) {
		return string == null
				|| string.trim().isEmpty();
	}
	
	public static String prependIfNonEmpty(String prefix, String string) {
		if (isEmpty(prefix)) {
			return string;
		}
		
		return new StringBuilder(prefix).append(string).toString();
	}
	
	public static String nullToEmptyString(String string) {
		if (string == null) {
			return "";
		}
		return string;
	}
	
}
