/******************************************************************************* 
 * Copyright (c) 2016 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/

package com.openshift.restclient.utils;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang.StringUtils;

public class BeanUtils {

    private BeanUtils() {
    }

    /**
     * Convert a delimited string to camelcase (e.g. foo-bar -> fooBar)
     * 
     * @param name
     *            the string to convert
     * @param delimiter
     *            the delimiter to use
     * @return the delimited string camelcased
     */
    public static String toCamelCase(String name, String delimiter) {
        String[] parts = name.split("-");
        List<String> capitalized = Stream.of(parts).map(p -> StringUtils.capitalize(p)).collect(Collectors.toList());
        return StringUtils.uncapitalize(StringUtils.join(capitalized, ""));
    }
}
