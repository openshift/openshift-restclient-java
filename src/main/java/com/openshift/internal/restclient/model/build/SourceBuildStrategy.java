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

import com.openshift.restclient.model.build.BuildStrategyType;
import com.openshift.restclient.model.build.ISourceBuildStrategy;

/**
 * 
 * @author Jeff Cantrill
 *
 */
public class SourceBuildStrategy extends STIBuildStrategy implements ISourceBuildStrategy{

	public SourceBuildStrategy(String image, String scriptsLocation, boolean incremental, Map<String, String> envVars) {
		super(image, scriptsLocation, incremental, envVars);
	}

	@Override
	public String getType() {
		return BuildStrategyType.SOURCE;
	}
	
	
}
