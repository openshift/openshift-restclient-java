/******************************************************************************* 
 * Copyright (c) 2016-2018 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/

package com.openshift.restclient.api.capabilities;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.openshift.restclient.capability.ICapability;
import com.openshift.restclient.capability.IStoppable;

/**
 * Runs container exec
 */
public interface IPodExec extends ICapability {

    /**
     * Execute a command on a named container in this pod
     * 
     * @param listener
     *            Listener for command output
     * @param options
     *            Options for the exec
     * @param commands
     *            A command to run and any arguments
     * @return A Handle to allow termination of the connection
     */
    IStoppable start(IPodExecOutputListener listener, Options options, String... commands);

    /**
     * A callback for exec output
     *
     */
    interface IPodExecOutputListener {

        /**
         * Callback received on initial connection
         */
        void onOpen();

        /**
         * Exec received stdout message
         * 
         */
        void onStdOut(String message);

        /**
         * Exec received stderr message
         * 
         */
        void onStdErr(String message);

        /**
         * Exec (channel 3) error message
         * 
         */
        void onExecErr(String message);

        /**
         * Called by lower level errors
         * 
         * @param t
         *            Exception causing failure
         */
        void onFailure(Throwable t);

        /**
         * Callback received when the connection to the pod is terminated from the
         * server-side
         * 
         * @param code
         *            a valid http response code
         * @param reason
         *            a reason for termination, may be null
         */
        void onClose(int code, String reason);
    }

    /**
     * Options for exec
     */
    class Options {

        public static final String CONTAINER = "container";
        public static final String STDOUT = "stdout";
        public static final String STDERR = "stderr";
        private Map<String, String> options = new HashMap<>();
        private Map<String, String> secondaries = new HashMap<>();

        private Options storeSecondary(String key, Object v) {
            secondaries.put(key, v.toString());
            return this;
        }

        /**
         * The container from which to retrieve logs
         * 
         */
        public Options container(String container) {
            return storeSecondary(CONTAINER, container);
        }

        /**
         * Enable stdout
         * 
         */
        public Options stdOut(boolean value) {
            return storeSecondary(STDOUT, value);
        }

        /**
         * Enable stderr
         * 
         */
        public Options stdErr(boolean value) {
            return storeSecondary(STDERR, value);
        }

        /**
         * Add an option that is not explicitly defined. These will override any
         * explicit options if there are collisions
         *
         */
        public Options parameter(String name, String value) {
            options.put(name, value);
            return this;
        }

        /**
         * The collective options
         * 
         * @return a map of all the options
         */
        public Map<String, String> getMap() {
            HashMap combined = new HashMap<>(secondaries);
            combined.putAll(options);
            return Collections.unmodifiableMap(combined);
        }
    }

}
