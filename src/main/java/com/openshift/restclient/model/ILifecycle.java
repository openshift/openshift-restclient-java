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

package com.openshift.restclient.model;

import java.util.Optional;

/**
 * @author Ulf Lilleengen
 */
public interface ILifecycle extends JSONSerializeable {
    Optional<IHandler> getPostStart();

    Optional<IHandler> getPreStop();

    interface IBuilder {
        IBuilder preStop(IHandler handler);

        IBuilder postStart(IHandler handler);

        ILifecycle build();
    }
}
