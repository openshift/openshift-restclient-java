/*******************************************************************************
 * Copyright (c) 2019 Red Hat, Inc. Distributed under license by Red Hat, Inc.
 * All rights reserved. This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Red Hat, Inc.
 ******************************************************************************/

package com.openshift.internal.restclient;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

public class KubernetesVersion {

    public static final int NO_VERSION = -1;
    private static final Pattern REGEX_KUBERVERSION = Pattern.compile("v(\\d{1,3})\\.(\\d{1,3})\\.(\\d{1,3})(\\+(\\p{ASCII}+)){0,1}");

    private boolean detected = false;
    private int major = NO_VERSION;
    private int minor = NO_VERSION;
    private int patch = NO_VERSION;
    private String git;

    public KubernetesVersion(String kubernetesVersion) {
        parse(kubernetesVersion);
    }

    private void parse(String kubernetesVersion) {
        reset();
        if (StringUtils.isEmpty(kubernetesVersion)) {
            return;
        }
        Matcher matcher = REGEX_KUBERVERSION.matcher(kubernetesVersion);
        if (!matcher.matches()
                || matcher.groupCount() < 5) {
            return;
        }
        try {
            detected = true;
            this.major = parseGroup(matcher.group(1));
            this.minor = parseGroup(matcher.group(2));
            this.patch = parseGroup(matcher.group(3));
            this.git = matcher.group(5);
        } catch(NumberFormatException e) {
            // stop when error encoutered
        }
        
    }

    private int parseGroup(String version) {
        return Integer.parseInt(version);
    }

    private void reset() {
        this.detected = false;
        this.major = NO_VERSION;
        this.minor = NO_VERSION;
        this.patch = NO_VERSION;
        this.git = null;
    }

    public boolean isDetected() {
        return detected;
    }

    public int getMajor() {
        return major;
    }

    public int getMinor() {
        return minor;
    }

    public int getPatch() {
        return patch;
    }

    public String getGit() {
        return git;
    }
}
