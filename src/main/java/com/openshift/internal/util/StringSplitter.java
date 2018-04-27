/******************************************************************************* 
 * Copyright (c) 2018 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/

package com.openshift.internal.util;

import java.util.ArrayList;
import java.util.List;

public class StringSplitter {
    private StringSplitter() {
    }

    public static List<String> split(String str, List<String> result) {
        boolean inQuote = false;
        StringBuilder builder = new StringBuilder();

        for (int i = 0; i < str.length(); ++i) {
            char c = str.charAt(i);
            if (inQuote) {
                if (c == '"') {
                    inQuote = false;
                } else {
                    builder.append(c);
                }
            } else if (c == '"') {
                inQuote = true;
            } else if (c == ' ') {
                if (builder.length() > 0) {
                    result.add(builder.toString());
                    builder = new StringBuilder();
                }
            } else {
                builder.append(c);
            }
        }
        if (builder.length() > 0) {
            result.add(builder.toString());
        }
        return result;
    }

    public static List<String> split(String str) {
        List<String> result = new ArrayList<>();
        return split(str, result);
    }

}
