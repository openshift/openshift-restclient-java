/*******************************************************************************
 * Copyright (c) 2016-2018 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/

package com.openshift.restclient.model.build;

import java.util.List;

import com.openshift.restclient.capability.ICapability;
import com.openshift.restclient.model.IBuildConfig;
import com.openshift.restclient.model.IEnvironmentVariable;
import com.openshift.restclient.model.IResourceBuilder;

public interface IBuildConfigBuilder extends IResourceBuilder<IBuildConfig, IBuildConfigBuilder>, ICapability {

    ISourceStrategyBuilder usingSourceStrategy();

    IJenkinsPipelineStrategyBuilder usingJenkinsPipelineStrategy();

    IGitSourceBuilder fromGitSource();
    
    IBinarySourceBuilder fromBinarySource();

    IBuildConfigBuilder toImageStreamTag(String tag);

    IBuildConfigBuilder buildOnSourceChange(boolean onSourceChange);

    IBuildConfigBuilder buildOnImageChange(boolean onImageChange);

    IBuildConfigBuilder buildOnConfigChange(boolean onConfigChange);

    interface ISourceBuilder<T extends ISourceBuilder> extends Endable {
        IBuildConfigBuilder end();
        
        T inContextDir(String contextDir);
    }
    
    interface IGitSourceBuilder extends ISourceBuilder<IGitSourceBuilder> {
        IGitSourceBuilder fromGitUrl(String url);

        IGitSourceBuilder usingGitReference(String ref);
    }
    
    interface IBinarySourceBuilder extends ISourceBuilder<IBinarySourceBuilder> {
        IBinarySourceBuilder fromAsFile(String asFile);
    }

    interface ISourceStrategyBuilder extends Endable {

        IBuildConfigBuilder end();

        /**
         * The imagestream tag in form of 'name:tag"
         * 
         * @param tag
         *            'name:tag'
         */
        ISourceStrategyBuilder fromImageStreamTag(String tag);

        ISourceStrategyBuilder inNamespace(String namespace);

        ISourceStrategyBuilder withEnvVars(List<IEnvironmentVariable> envVars);

        /**
         * @param tag
         *            docker pullspec
         */
        ISourceStrategyBuilder fromDockerImage(String tag);

    }

    interface IJenkinsPipelineStrategyBuilder extends Endable {

        IBuildConfigBuilder end();

        IJenkinsPipelineStrategyBuilder usingFile(String file);

        IJenkinsPipelineStrategyBuilder usingFilePath(String filePath);
    }

}
