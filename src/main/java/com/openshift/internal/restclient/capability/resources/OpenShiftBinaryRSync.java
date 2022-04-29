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

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.openshift.restclient.IClient;
import com.openshift.restclient.OpenShiftException;
import com.openshift.restclient.capability.resources.IRSyncable;
import com.openshift.restclient.model.IPod;

/**
 * Port forwarding implementation that wraps the OpenShift binary
 * 
 * @author Andre Dietisheim
 *
 */
public class OpenShiftBinaryRSync extends AbstractOpenShiftBinaryCapability implements IRSyncable {

    private static final Logger LOG = LoggerFactory.getLogger(OpenShiftBinaryRSync.class);
    public static final String RSYNC_COMMAND = "rsync";
    private static final long WAIT_FOR_EXIT_TIMEOUT = 5; // mins

    private Peer source;
    private Peer destination;
    private IPod pod;

    private final Executor executor = Executors.newCachedThreadPool();

    /**
     * Constructor.
     * 
     * @param client
     *            the client to connect to OpenShift.
     */
    public OpenShiftBinaryRSync(final IClient client) {
        super(client);
    }

    @Override
    public InputStream sync(final Peer source, final Peer destination, final OpenShiftBinaryOption... options)
            throws OpenShiftException {
        this.source = source;
        this.destination = destination;
        this.pod = getPod(source, destination);
        Process process = start(options);
        waitFor(process);
        if (process == null) {
            return null;
        }
        return process.getInputStream();
    }

    private IPod getPod(Peer source, Peer destination) {
        if (source.isPod()) {
            return source.getPod();
        } else if (destination.isPod()) {
            return destination.getPod();
        } else {
            return null;
        }
    }

    protected void waitFor(Process process) {
        if (process == null) {
            return;
        }

        this.executor.execute(() -> {
            try {
                process.waitFor();
            } catch (InterruptedException e) {
                throw new OpenShiftException("Error occurred while waiting for rsync operation to complete", e);
            }
        });
    }

    @Override
    public boolean isDone() {
        return !getProcess().isAlive();
    }

    @Override
    public int exitValue() {
        return getProcess().exitValue();
    }

    @Override
    public void await() throws InterruptedException {
        try {
            if (getProcess() == null) {
                throw new OpenShiftException("Could not sync %s to %s, no process was launched.", destination);
            }
            if (!getProcess().waitFor(WAIT_FOR_EXIT_TIMEOUT, TimeUnit.MINUTES)) {
                throw new OpenShiftException("Syncing %s to %s did not terminate within %d minutes.", source,
                        destination, WAIT_FOR_EXIT_TIMEOUT);
            }

            if (getProcess().exitValue() != 0) {
                String errorMessage = getErrorMessage(getProcess().getErrorStream());
                throw new OpenShiftException(
                        "Syncing %s to %s failed" + (StringUtils.isBlank(errorMessage) ? "" : ": %s"), source,
                        destination, errorMessage);
            }
        } catch (InterruptedException e) {
            throw new OpenShiftException(e, "Syncing %s to %s was interrupted.", source, destination);
        }
    }

    private static String getErrorMessage(InputStream errorStream) {
        try {
            return IOUtils.toString(errorStream, StandardCharsets.UTF_8);
        } catch (IOException e) {
            LOG.error("Could not retrieve error message from process", e);
            return null;
        }
    }

    @Override
    protected void cleanup() {
        this.source = null;
        this.destination = null;
    }

    @Override
    protected boolean validate() {
        return source != null && destination != null && hasPodPeer(source, destination);
    }

    private static boolean hasPodPeer(Peer source, Peer destination) {
        return source.isPod() || destination.isPod();
    }

    @Override
    public boolean isSupported() {
        return true;
    }

    @Override
    public String getName() {
        return OpenShiftBinaryRSync.class.getSimpleName();
    }

    @Override
    protected String buildArgs(final List<OpenShiftBinaryOption> options) {
        return new CommandLineBuilder(RSYNC_COMMAND).append(new Token(getClient())).append(new Server(getClient()))
                .append(new Namespace(pod)).append(options).append(source).append(destination).build();
    }
}
