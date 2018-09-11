/*******************************************************************************
 * Copyright (c) 2018 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/

package com.openshift.internal.restclient.capability.resources;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.openshift.restclient.IClient;
import com.openshift.restclient.ResourceKind;
import com.openshift.restclient.capability.resources.IBinaryBuildTriggerable;
import com.openshift.restclient.model.IBuild;
import com.openshift.restclient.model.IBuildConfig;
import com.openshift.restclient.model.IResource;
import com.openshift.restclient.model.build.IBinaryBuildSource;

public class BinaryBuildTrigger implements IBinaryBuildTriggerable {

    private static final String BUILDCONFIG_BINARY_SUBRESOURCE = "instantiatebinary";
    private static final String AS_FILE_PARAMETER = "asFile";
    private static final String AUTHOR_EMAIL_PARAMETER = "revision.authorEmail";
    private static final String AUTHOR_NAME_PARAMETER = "revision.authorName";
    private static final String COMMIT_PARAMETER = "revision.commit";
    private static final String COMMITTER_EMAIL_PARAMETER = "revision.committerEmail";
    private static final String COMMITTER_NAME_PARAMETER = "revision.committerName";
    private static final String MESSAGE_PARAMETER = "revision.message";
    private IResource resource;
    private IClient client;
    private final String subresource;
    private String asFile;
    private String authorEmail;
    private String authorName;
    private String commit;
    private String committerEmail;
    private String committerName;
    private String message;

    public BinaryBuildTrigger(IBuildConfig buildConfig, IClient client) {
        this.resource = buildConfig;
        this.client = client;
        this.subresource = BUILDCONFIG_BINARY_SUBRESOURCE;
    }

    @Override
    public boolean isSupported() {
        return resource != null && client != null && ResourceKind.BUILD_CONFIG.equals(resource.getKind())
                && ((IBuildConfig) resource).getBuildSource() instanceof IBinaryBuildSource;
    }

    @Override
    public String getName() {
        return BinaryBuildTrigger.class.getSimpleName();
    }

    @Override
    public IBuild triggerBinary(InputStream payload) {
        Map<String, String> parameters = new HashMap<>();
        if (StringUtils.isNotBlank(asFile)) {
            parameters.put(AS_FILE_PARAMETER, asFile);
        }
        if (StringUtils.isNotBlank(authorEmail)) {
            parameters.put(AUTHOR_EMAIL_PARAMETER, authorEmail);
        }
        if (StringUtils.isNotBlank(authorName)) {
            parameters.put(AUTHOR_NAME_PARAMETER, authorName);
        }
        if (StringUtils.isNotBlank(commit)) {
            parameters.put(COMMIT_PARAMETER, commit);
        }
        if (StringUtils.isNotBlank(committerEmail)) {
            parameters.put(COMMITTER_EMAIL_PARAMETER, committerEmail);
        }
        if (StringUtils.isNotBlank(committerName)) {
            parameters.put(COMMITTER_NAME_PARAMETER, committerName);
        }
        if (StringUtils.isNotBlank(message)) {
            parameters.put(MESSAGE_PARAMETER, message);
        }
        return client.create(resource.getKind(), resource.getApiVersion(), resource.getNamespaceName(), resource.getName(), subresource, payload, parameters);
    }

    @Override
    public void setAsFile(String asFile) {
        this.asFile = asFile;
    }

    @Override
    public String getAsFile() {
        return asFile;
    }

    @Override
    public void setCommit(String commit) {
        this.commit = commit;
    }

    @Override
    public String getCommit() {
        return commit;
    }

    @Override
    public void setAuthorEmail(String authorEmail) {
        this.authorEmail = authorEmail;
    }

    @Override
    public String getAuthorEmail() {
        return authorEmail;
    }

    @Override
    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    @Override
    public String getAuthorName() {
        return authorName;
    }

    @Override
    public void setCommitterEmail(String committerEmail) {
        this.committerEmail = committerEmail;
    }

    @Override
    public String getCommitterEmail() {
        return committerEmail;
    }

    @Override
    public void setCommitterName(String committerName) {
        this.committerName = committerName;
    }

    @Override
    public String getCommitterName() {
        return committerName;
    }

    @Override
    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
