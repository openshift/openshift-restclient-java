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

import com.openshift.restclient.model.IExecAction;
import org.jboss.dmr.ModelNode;

import java.util.ArrayList;
import java.util.Collections;

/**
 * @author Ulf Lilleengen
 */
public class ExecAction implements IExecAction {

    private static final String COMMAND = "command";
    private java.util.List<String> command;

    private ExecAction(java.util.List<String> command) {
        this.command = command;
    }

    @Override
    public java.util.List<String> getCommand() {
        return command;
    }

    @Override
    public String toJson() {
        ModelNode node = new ModelNode();
        ModelNode commandNode = node.get(COMMAND);
        for (String cmd : command) {
            commandNode.add().set(cmd);
        }
        return node.toJSONString(true);
    }

    public static IExecAction fromJson(ModelNode execNode) {
        Builder builder = new ExecAction.Builder();
        if (execNode.has(COMMAND)) {
            ModelNode commandNode = execNode.get(COMMAND);
            commandNode.asList().stream()
                    .map(ModelNode::asString)
                    .forEach(builder::command);
        }
        return builder.build();
    }

    @Override
    public String getType() {
        return EXEC;
    }

    public static class Builder implements IBuilder {
        private java.util.List<String> commands = new ArrayList<>();

        @Override
        public IBuilder command(String command) {
            commands.add(command);
            return this;
        }

        @Override
        public IExecAction build() {
            return new ExecAction(Collections.unmodifiableList(commands));
        }
    }
}
