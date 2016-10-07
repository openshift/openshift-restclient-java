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

import java.util.Map;

import org.jboss.dmr.ModelNode;

import com.openshift.internal.restclient.model.KubernetesResource;
import com.openshift.restclient.IClient;
import com.openshift.restclient.model.build.IBuildRequest;

/**
 * 
 * @author Jeff Cantrill
 *
 */
public class BuildRequest extends KubernetesResource implements IBuildRequest{
	
	private static final String COMMIT = "commit";
	private static final String GIT = "git";
	private static final String BIGGIT = "Git";
	private static final String TYPE = "type";
	private static final String REVISION = "revision";
	private static final String REVISION_GIT_COMMIT = REVISION + "." + GIT + "." + COMMIT;
	private static final String REVISION_TYPE = REVISION + "." + TYPE;
	

	public BuildRequest(ModelNode node, IClient client, Map<String, String[]> propertyKeys) {
		super(node, client, propertyKeys);
	}


	@Override
	public void setCommitId(String commitId) {
		set(REVISION_TYPE, BIGGIT);
		set(REVISION_GIT_COMMIT, commitId);
	}

}
