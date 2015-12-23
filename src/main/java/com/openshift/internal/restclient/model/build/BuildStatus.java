/*******************************************************************************
 * Copyright (c) 2015 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package com.openshift.internal.restclient.model.build;

import static com.openshift.internal.util.JBossDmrExtentions.*;
import java.util.Map;

import org.jboss.dmr.ModelNode;

import com.openshift.internal.restclient.model.ModelNodeAdapter;
import com.openshift.restclient.images.DockerImageURI;
import com.openshift.restclient.model.build.IBuildStatus;

public class BuildStatus extends ModelNodeAdapter implements IBuildStatus {

	public BuildStatus(ModelNode node, Map<String, String[]> propertyKeys) {
		super(node, propertyKeys);
	}

	@Override
	public String getPhase() {
		return asString(getNode(), getPropertyKeys(), "phase");
	}

	@Override
	public String getStartTime() {
		return asString(getNode(), getPropertyKeys(), "startTimestamp");
	}

	@Override
	public long getDuration() {
		ModelNode node = get(getNode(), getPropertyKeys(), "duration");
		if(!node.isDefined()) {
			return 0L;
		}
		return node.asLong();
	}

	@Override
	public DockerImageURI getOutputDockerImage() {
		return new DockerImageURI(asString(getNode(), getPropertyKeys(), "outputDockerImageReference"));
	}

	
}
