package org.jboss.tools.openshift.express.internal.client.response.unmarshalling;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

/**
 * @author André Dietisheim
 */
public class JsonSanitizer {

	private static final Pattern QUOTED_JSON_OBJECT_PATTERN = Pattern.compile("\"\\{(.+)\\}\"");
	private static final Pattern ESCAPED_QUOTES_PATTERN = Pattern.compile("\\\"");

	public static String sanitize(String json) {
		return correctEscapedJsonObjects(json);
	}

	/**
	 * Corrects erroneously quoted json objects in the given string.
	 * <p>
	 * corrects: "{ \"property\": \"value\" }" to { "propery" : "value" }
	 * 
	 * @param json
	 * @return
	 */
	protected static String correctEscapedJsonObjects(String json) {
		String sanitizedJson = json;
		Matcher matcher = QUOTED_JSON_OBJECT_PATTERN.matcher(json);
		if (matcher.find()
				&& matcher.groupCount() > 0) {
			sanitizedJson = matcher.replaceAll("{" + unescapeQuotes(matcher.group(1)) + "}");
		}
		return sanitizedJson;
	}

	private static String unescapeQuotes(String responseFragment) {
		return ESCAPED_QUOTES_PATTERN.matcher(responseFragment).replaceAll("\"");
	}

}
