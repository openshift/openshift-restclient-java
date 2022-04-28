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

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.openshift.internal.util.StringSplitter;
import com.openshift.restclient.IClient;
import com.openshift.restclient.OpenShiftContext;
import com.openshift.restclient.OpenShiftException;
import com.openshift.restclient.capability.IBinaryCapability;
import com.openshift.restclient.capability.resources.LocationNotFoundException;
import com.openshift.restclient.model.IResource;

/**
 * Capability that wraps the OpenShift binary
 * 
 */
public abstract class AbstractOpenShiftBinaryCapability implements IBinaryCapability {

    private static final Logger LOG = LoggerFactory.getLogger(AbstractOpenShiftBinaryCapability.class);

    private static final boolean IS_MAC = StringUtils.isNotEmpty(System.getProperty("os.name"))
            && System.getProperty("os.name").toLowerCase().contains("mac");

    static class Server implements OpenShiftBinaryOption {

        private IClient client;

        public Server(IClient client) {
            this.client = client;
        }

        @Override
        public void append(StringBuilder commandLine) {
            commandLine.append(" --server=").append(client.getBaseURL()).append(" ");
        }
    }

    static class Token implements OpenShiftBinaryOption {

        private IClient client;

        Token(IClient client) {
            this.client = client;
        }

        @Override
        public void append(StringBuilder commandLine) {
            commandLine.append(" --token=").append(client.getAuthorizationContext().getToken());
        }
    }

    static class Namespace implements OpenShiftBinaryOption {

        private IResource resource;

        public Namespace(IResource resource) {
            this.resource = resource;
        }

        @Override
        public void append(StringBuilder commandLine) {
            if (resource == null) {
                return;
            }
            commandLine.append(" -n ").append(resource.getNamespaceName());
        }
    }

    protected static class CommandLineBuilder {

        private StringBuilder sb;

        public CommandLineBuilder(String command) {
            this.sb = new StringBuilder(command);
        }

        public CommandLineBuilder append(OpenShiftBinaryOption argument) {
            if (argument != null) {
                argument.append(sb);
            }
            return this;
        }

        public CommandLineBuilder append(Collection<OpenShiftBinaryOption> arguments) {
            if (arguments == null) {
                return this;
            }

            for (OpenShiftBinaryOption argument : arguments) {
                append(argument);
            }
            return this;
        }

        public String build() {
            return sb.toString();
        }
    }

    private Process process;
    private IClient client;

    protected AbstractOpenShiftBinaryCapability(IClient client) {
        this.client = client;
    }

    /**
     * Cleanup required when stopping the process
     */
    protected abstract void cleanup();

    /**
     * Validate arguments before starting process
     * 
     * @return true if start should continue; false otherwise;
     */
    protected abstract boolean validate();

    /**
     * Callback for building args to be sent to the {@code oc} command.
     * 
     * @return the String representation of all the arguments to use when running
     *         the {@code oc} command.
     */
    protected abstract String buildArgs(final List<OpenShiftBinaryOption> options);

    protected IClient getClient() {
        return client;
    }

    protected AbstractOpenShiftBinaryCapability() {
        addShutdownHook();
    }

    protected Process getProcess() {
        return process;
    }

    private void addShutdownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread(this::stop));
    }

    /**
     * Starts the {@link Process} to run the {@code oc} command.
     * 
     * @param arguments
     *            the command line options
     */
    public final Process start(final OpenShiftBinaryOption... arguments) {
        String location = getOpenShiftBinaryLocation();
        if (!validate()) {
            return null;
        }
        ProcessBuilder processBuilder = initProcessBuilder(location, arguments);
        return startProcess(processBuilder);
    }

    protected Process startProcess(ProcessBuilder builder) {
        try {
            process = builder.start();
            checkProcessIsAlive();
            return process;
        } catch (IOException e) {
            LOG.error("Could not start process for {}.", new Object[] { getName(), e });
            throw new OpenShiftException(e, "Does your OpenShift binary location exist? Error starting process: %s",
                    e.getMessage());
        }
    }

    private ProcessBuilder initProcessBuilder(String location, final OpenShiftBinaryOption... options) {
        List<String> args = new ArrayList<>();
        ProcessBuilder builder = null;
        // the condition is made in order to solve mac problem
        // with launching binaries containing spaces in its path
        // https://issues.jboss.org/browse/JBIDE-23862 - see the latest comments
        if (IS_MAC) {
            args.add(location);
            StringSplitter.split(buildArgs(Arrays.asList(options)), args);
            builder = new ProcessBuilder(args);
        } else {
            args.add(location);
            StringSplitter.split(buildArgs(Arrays.asList(options)), args);
            File oc = new File(location);
            builder = new ProcessBuilder(args);
            builder.directory(oc.getParentFile());
        }
        builder.environment().remove("KUBECONFIG");
        LOG.debug("OpenShift binary args: {}", builder.command());
        return builder;
    }

    private void checkProcessIsAlive() throws IOException {
        try {
            // TODO: replace fixed wait with wait for process to be running
            Thread.sleep(1000);
            if (!process.isAlive() && process.exitValue() != 0) {
                throw new OpenShiftException("OpenShiftBinaryCapability process exited: %s",
                        IOUtils.toString(process.getErrorStream(), StandardCharsets.UTF_8));
            }
        } catch (InterruptedException e) {
            if (!process.isAlive() && process.exitValue() != 0) {
                throw new OpenShiftException("OpenShiftBinaryCapability process exited: %s",
                        IOUtils.toString(process.getErrorStream(), StandardCharsets.UTF_8));
            }
        }
    }

    /**
     * Stops the {@link Process} running the {@code oc} command.
     */
    public final synchronized void stop() {
        if (process == null) {
            return;
        }
        cleanup();
        if (!process.isAlive()) {
            final int exitValue = process.exitValue();
            LOG.debug("OpenShiftBinaryCapability process exit code {}", exitValue);
            if (exitValue != 0) {
                try {
                    LOG.debug("OpenShiftBinaryCapability process error stream: {}",
                            IOUtils.toString(process.getErrorStream(), StandardCharsets.UTF_8));
                } catch (IOException e) {
                    LOG.debug("IOException trying to debug the process error stream", e);
                }
            }
            process = null;
            return;
        }
        process.destroyForcibly();
    }

    protected String getOpenShiftBinaryLocation() {
        // Check the ThreadLocal for oc binary
        String location = OpenShiftContext.get().get(OPENSHIFT_BINARY_LOCATION);
        if (StringUtils.isBlank(location)) {
            // Fall back to System property
            location = System.getProperty(OPENSHIFT_BINARY_LOCATION);
        }
        if (StringUtils.isBlank(location)) {
            throw new LocationNotFoundException(
                    String.format("The OpenShift 'oc' binary location was not specified. Set the property %s",
                            OPENSHIFT_BINARY_LOCATION));
        }
        return location;
    }

}
