/*******************************************************************************
 * Copyright (c) 2015-2018 Red Hat, Inc.
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
import java.io.SequenceInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.openshift.restclient.IClient;
import com.openshift.restclient.capability.IBinaryCapability.OpenShiftBinaryOption;
import com.openshift.restclient.capability.resources.IPodLogRetrieval;
import com.openshift.restclient.model.IPod;

public class OpenShiftBinaryPodLogRetrieval implements IPodLogRetrieval {

    static class PodName implements OpenShiftBinaryOption {

        private IPod pod;

        public PodName(IPod pod) {
            this.pod = pod;
        }

        @Override
        public void append(StringBuilder commandLine) {
            if (pod == null) {
                return;
            }
            commandLine.append(" ").append(pod.getName());
        }
    }

    static class ContainerName implements OpenShiftBinaryOption {

        private String name;

        public ContainerName(String name) {
            this.name = name;
        }

        @Override
        public void append(StringBuilder commandLine) {
            commandLine.append(" -c ").append(name);
        }
    }

    static class Follow implements OpenShiftBinaryOption {

        @Override
        public void append(StringBuilder commandLine) {
            commandLine.append(" -f");
        }
    }

    private static final Logger LOG = LoggerFactory.getLogger(IPodLogRetrieval.class);
    private IPod pod;
    private IClient client;
    private Map<String, PodLogs> cache = new HashMap<>();

    public OpenShiftBinaryPodLogRetrieval(IPod pod, IClient client) {
        this.pod = pod;
        this.client = client;
    }

    @Override
    public boolean isSupported() {
        return true;
    }

    @Override
    public String getName() {
        return OpenShiftBinaryPodLogRetrieval.class.getSimpleName();
    }

    @Override
    public InputStream getLogs(final boolean follow, final OpenShiftBinaryOption... options) {
        return getLogs(follow, null, options);
    }

    @Override
    public InputStream getLogs(final boolean follow, final String container, final OpenShiftBinaryOption... options) {
        final String normalizedContainer = StringUtils.defaultIfBlank(container, "");
        synchronized (cache) {
            if (cache.containsKey(normalizedContainer)) {
                return cache.get(normalizedContainer).getLogs();
            }
            PodLogs logs = null;
            try {
                logs = new PodLogs(client, follow, normalizedContainer, options);
                return logs.getLogs();
            } finally {
                if (logs != null) {
                    cache.put(normalizedContainer, logs);
                }
            }
        }
    }

    @Override
    public void stop() {
        new ArrayList<>(cache.keySet()).forEach(container -> stop(container));
    }

    @Override
    public synchronized void stop(String container) {
        if (!cache.containsKey(container)) {
            return;
        }
        try {
            PodLogs logs = cache.remove(container);
            logs.stop();
        } catch (Exception e) {
            LOG.warn("Unable to stop pod logs", e);
        }
    }

    protected class PodLogs extends AbstractOpenShiftBinaryCapability {

        public static final String LOGS_COMMAND = "logs";

        private String container;
        private boolean follow;
        private SequenceInputStream is;
        private OpenShiftBinaryOption[] options;

        protected PodLogs(IClient client, boolean follow, String container, OpenShiftBinaryOption... options) {
            super(client);
            this.follow = follow;
            this.container = container;
            this.options = options;
        }

        public synchronized InputStream getLogs() {
            if (is == null) {
                Process process = start(options);
                if (process != null) {
                    is = new SequenceInputStream(process.getInputStream(), process.getErrorStream());
                }
            }
            return is;
        }

        @Override
        public boolean isSupported() {
            return true;
        }

        @Override
        public String getName() {
            return "";
        }

        @Override
        protected void cleanup() {
            follow = false;
            if (getProcess() != null) {
                IOUtils.closeQuietly(getProcess().getInputStream());
                IOUtils.closeQuietly(getProcess().getErrorStream());
            }
            synchronized (cache) {
                cache.remove(this.container);
            }
        }

        @Override
        protected boolean validate() {
            return true;
        }

        @Override
        protected String buildArgs(final List<OpenShiftBinaryOption> options) {
            CommandLineBuilder builder = new CommandLineBuilder(LOGS_COMMAND).append(new Token(client))
                    .append(new Server(client)).append(options).append(new PodName(pod)).append(new Namespace(pod));
            if (follow) {
                builder.append(new Follow());
            }
            if (StringUtils.isNotBlank(container)) {
                builder.append(new ContainerName(container));
            }
            return builder.build();
        }
    }

}
