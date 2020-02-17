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

package com.openshift.restclient.capability.resources;

import java.io.InputStream;

import org.apache.commons.lang.ArrayUtils;

import com.openshift.restclient.capability.IBinaryCapability;
import com.openshift.restclient.model.IPod;

public interface IRSyncable extends IBinaryCapability {

    /**
     * option to skip verifying the certificates during TLS connection
     * establishment.
     */
    static final OpenShiftBinaryOption EXCLUDE_GIT_FOLDER = new GitFolderExclude();

    /** option to exclude files/folders that match the given expressions **/
    static OpenShiftBinaryOption exclude(String... expressions) {
        return new Exclude(expressions);
    }

    /** option to not transfer file permissions. */
    static final OpenShiftBinaryOption NO_PERMS = new NoPerms();

    /** option to delete delete extraneous files from destination directories **/
    static final OpenShiftBinaryOption DELETE = new Delete();

    /**
     * Excludes some files/directories that match the given patterns when rsync'ing
     * the remote pod and the local deployment directory.
     * 
     * @see {@link https://github.com/openshift/origin/issues/8223}
     */
    static class Exclude implements OpenShiftBinaryOption {

        private String[] expressions;

        public Exclude(String... expressions) {
            this.expressions = expressions;
        }

        @Override
        public void append(StringBuilder arguments) {
            if (ArrayUtils.isEmpty(expressions)) {
                return;
            }
            for (String expression : expressions) {
                arguments.append(" --exclude=").append(expression);
            }
        }
    }

    /**
     * Does not sync .git folders when rsync'ing
     */
    static class GitFolderExclude extends Exclude {

        public GitFolderExclude() {
            super(".git");
        }
    }

    /**
     * Avoids transferring file permissions when rsync'ing
     */
    static class NoPerms implements OpenShiftBinaryOption {

        @Override
        public void append(StringBuilder arguments) {
            arguments.append(" --no-perms=true");
        }
    }

    /**
     * Deletes extraneous files from destination directories when rsync'ing.
     */
    static class Delete implements OpenShiftBinaryOption {

        @Override
        public void append(StringBuilder arguments) {
            arguments.append(" --delete");
        }
    }

    static class PodPeer extends Peer {

        private static final char POD_PATH_SEPARATOR = ':';

        private IPod pod;

        public PodPeer(String location, IPod pod) {
            super(location);
            this.pod = pod;
        }

        @Override
        public boolean isPod() {
            return true;
        }

        public IPod getPod() {
            return pod;
        }

        @Override
        protected String getParameter() {
            return new StringBuilder().append('"').append(pod.getName()).append(POD_PATH_SEPARATOR)
                    .append(getLocation()).append('"').toString();
        }
    }

    static class LocalPeer extends Peer {

        public LocalPeer(String location) {
            super(location);
        }

        @Override
        public boolean isPod() {
            return false;
        }

        @Override
        public IPod getPod() {
            return null;
        }

        protected String getParameter() {
            return new StringBuilder().append('"').append(getLocation()).append('"').toString();
        }
    }

    abstract static class Peer implements OpenShiftBinaryOption {

        private String location;

        private Peer(String path) {
            this.location = path;
        }

        protected String getLocation() {
            return location;
        }

        protected abstract String getParameter();

        public abstract boolean isPod();

        public abstract IPod getPod();

        @Override
        public void append(StringBuilder commandLine) {
            commandLine.append(" ").append(getParameter());
        }

        @Override
        public String toString() {
            return getParameter();
        }
    }

    /**
     * Synchronizes the give {@code destination} with the given {@code source}
     * 
     * @param source
     *            the source of the rsync
     * @param destination
     *            the destination of the rsync
     * @param options
     *            the options to pass to the underlying {@code oc rsync} command
     * @return the underlying {@link Process} streams to be displayed in a console.
     */
    InputStream sync(Peer source, Peer destination, OpenShiftBinaryOption... options);

    /**
     * Stops rsync'ing, forcibly if necessary.
     */
    void stop();

    /**
     * Indicates if the {@link Process} completed or not
     * 
     * @return <code>true</code> if the {@link Process} completed,
     *         <code>false</code> otherwise.
     */
    boolean isDone();

    /**
     * @return the {@link Process} exit value when it completed, {@code -1} if it's
     *         still running
     */
    int exitValue();

    /**
     * Blocks until the process is done.
     * 
     * @throws InterruptedException
     *             if the current thread is interrupted while waiting
     */
    void await() throws InterruptedException;
}
