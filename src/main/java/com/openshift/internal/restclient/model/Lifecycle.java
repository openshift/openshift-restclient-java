/*******************************************************************************
 * Copyright (c) 2016 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/

package com.openshift.internal.restclient.model;

import java.util.Optional;

import org.jboss.dmr.ModelNode;

import com.openshift.restclient.model.IHandler;
import com.openshift.restclient.model.ILifecycle;

/**
 * @author Ulf Lilleengen
 */
public class Lifecycle implements ILifecycle {
    private static final String PRESTOP = "preStop";
    private static final String POSTSTART = "postStart";

    private final Optional<IHandler> postStart;
    private final Optional<IHandler> preStop;

    private Lifecycle(Optional<IHandler> preStop, Optional<IHandler> postStart) {
        this.preStop = preStop;
        this.postStart = postStart;
    }

    @Override
    public Optional<IHandler> getPostStart() {
        return postStart;
    }

    @Override
    public Optional<IHandler> getPreStop() {
        return preStop;
    }

    @Override
    public String toJson() {
        ModelNode node = new ModelNode();
        preStop.ifPresent(
            handler -> node.get(PRESTOP).get(handler.getType()).set(ModelNode.fromJSONString(handler.toJson())));
        postStart.ifPresent(
            handler -> node.get(POSTSTART).get(handler.getType()).set(ModelNode.fromJSONString(handler.toJson())));
        return node.toJSONString(true);
    }

    public static ILifecycle fromJson(ModelNode json) {
        Builder builder = new Builder();
        if (json.has(PRESTOP)) {
            builder.preStop(parseHandler(json.get(PRESTOP)).orElse(null));
        }

        if (json.has(POSTSTART)) {
            builder.postStart(parseHandler(json.get(POSTSTART)).orElse(null));
        }
        return builder.build();
    }

    private static Optional<IHandler> parseHandler(ModelNode node) {
        if (node.has(IHandler.EXEC)) {
            return Optional.of(ExecAction.fromJson(node.get(IHandler.EXEC)));
        } else {
            return Optional.empty();
        }
    }

    public static class Builder implements IBuilder {

        private IHandler preStop = null;
        private IHandler postStart = null;

        public ILifecycle build() {
            return new Lifecycle(Optional.ofNullable(preStop), Optional.ofNullable(postStart));
        }

        public Builder postStart(IHandler handler) {
            this.postStart = handler;
            return this;
        }

        public Builder preStop(IHandler handler) {
            this.preStop = handler;
            return this;
        }
    }
}
