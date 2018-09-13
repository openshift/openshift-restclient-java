/*******************************************************************************
 * Copyright (c) 2015-2018 Red Hat, Inc. Distributed under license by Red Hat, Inc.
 * All rights reserved. This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Red Hat, Inc.
 ******************************************************************************/

package com.openshift.internal.restclient.capability;

import java.util.Map;

import com.openshift.internal.restclient.api.capabilities.PodExec;
import com.openshift.internal.restclient.api.capabilities.ScaleCapability;
import com.openshift.internal.restclient.apis.TypeMetaFactory;
import com.openshift.internal.restclient.capability.resources.BinaryBuildTrigger;
import com.openshift.internal.restclient.capability.resources.BuildCanceller;
import com.openshift.internal.restclient.capability.resources.BuildTrigger;
import com.openshift.internal.restclient.capability.resources.ClientCapability;
import com.openshift.internal.restclient.capability.resources.DeployCapability;
import com.openshift.internal.restclient.capability.resources.DeploymentConfigTraceability;
import com.openshift.internal.restclient.capability.resources.DeploymentTraceability;
import com.openshift.internal.restclient.capability.resources.DeploymentTrigger;
import com.openshift.internal.restclient.capability.resources.ImageStreamImportCapability;
import com.openshift.internal.restclient.capability.resources.OpenShiftBinaryPodLogRetrieval;
import com.openshift.internal.restclient.capability.resources.OpenShiftBinaryPortForwarding;
import com.openshift.internal.restclient.capability.resources.OpenShiftBinaryRSync;
import com.openshift.internal.restclient.capability.resources.PodLogRetrievalAsync;
import com.openshift.internal.restclient.capability.resources.ProjectTemplateListCapability;
import com.openshift.internal.restclient.capability.resources.ProjectTemplateProcessing;
import com.openshift.internal.restclient.capability.resources.PropertyAccessCapability;
import com.openshift.internal.restclient.capability.resources.TagCapability;
import com.openshift.internal.restclient.capability.resources.TemplateTraceability;
import com.openshift.internal.restclient.capability.resources.UpdateableCapability;
import com.openshift.internal.restclient.capability.server.ServerTemplateProcessing;
import com.openshift.internal.restclient.model.Service;
import com.openshift.internal.restclient.model.build.BuildConfigBuilder;
import com.openshift.restclient.IClient;
import com.openshift.restclient.api.capabilities.IPodExec;
import com.openshift.restclient.api.capabilities.IScalable;
import com.openshift.restclient.capability.ICapability;
import com.openshift.restclient.capability.resources.IBinaryBuildTriggerable;
import com.openshift.restclient.capability.resources.IBuildCancelable;
import com.openshift.restclient.capability.resources.IBuildTriggerable;
import com.openshift.restclient.capability.resources.IClientCapability;
import com.openshift.restclient.capability.resources.IDeployCapability;
import com.openshift.restclient.capability.resources.IDeploymentConfigTraceability;
import com.openshift.restclient.capability.resources.IDeploymentTraceability;
import com.openshift.restclient.capability.resources.IDeploymentTriggerable;
import com.openshift.restclient.capability.resources.IImageStreamImportCapability;
import com.openshift.restclient.capability.resources.IPodLogRetrieval;
import com.openshift.restclient.capability.resources.IPodLogRetrievalAsync;
import com.openshift.restclient.capability.resources.IPortForwardable;
import com.openshift.restclient.capability.resources.IProjectTemplateList;
import com.openshift.restclient.capability.resources.IProjectTemplateProcessing;
import com.openshift.restclient.capability.resources.IPropertyAccessCapability;
import com.openshift.restclient.capability.resources.IRSyncable;
import com.openshift.restclient.capability.resources.ITags;
import com.openshift.restclient.capability.resources.ITemplateTraceability;
import com.openshift.restclient.capability.resources.IUpdatable;
import com.openshift.restclient.capability.server.ITemplateProcessing;
import com.openshift.restclient.model.IBuild;
import com.openshift.restclient.model.IBuildConfig;
import com.openshift.restclient.model.IDeploymentConfig;
import com.openshift.restclient.model.IPod;
import com.openshift.restclient.model.IProject;
import com.openshift.restclient.model.IReplicationController;
import com.openshift.restclient.model.IResource;
import com.openshift.restclient.model.build.IBuildConfigBuilder;

/**
 * Convenience class to initialize capabilies. Only adds entry to underlying map
 * if the capability is supported
 * 
 */
public class CapabilityInitializer {

    /**
     * Registers the capability if it is supported
     * 
     */
    private static void initializeCapability(Map<Class<? extends ICapability>, ICapability> capabilities,
            Class<? extends ICapability> capability, ICapability impl) {
        if (impl.isSupported()) {
            capabilities.put(capability, impl);
        }
    }

    /**
     * Initialize Build specific capabilities
     * 
     */
    public static void initializeCapabilities(Map<Class<? extends ICapability>, ICapability> capabilities, IBuild build,
            IClient client) {
        initializeCapability(capabilities, IBuildTriggerable.class, new BuildTrigger(build, client));
        initializeCapability(capabilities, IBuildCancelable.class, new BuildCanceller(build, client));
    }

    /**
     * Initialize BuildConfig specific capabilities
     * 
     */
    public static void initializeCapabilities(Map<Class<? extends ICapability>, ICapability> capabilities,
            IBuildConfig buildConfig, IClient client) {
        initializeCapability(capabilities, IBuildTriggerable.class, new BuildTrigger(buildConfig, client));
        initializeCapability(capabilities, IBinaryBuildTriggerable.class, new BinaryBuildTrigger(buildConfig, client));
    }

    /**
     * Initialize Pod specific capabilities
     * 
     */
    public static void initializeCapabilities(Map<Class<? extends ICapability>, ICapability> capabilities, IPod pod,
            IClient client) {
        initializeCapability(capabilities, IPortForwardable.class, new OpenShiftBinaryPortForwarding(pod, client));
        initializeCapability(capabilities, IPodLogRetrieval.class, new OpenShiftBinaryPodLogRetrieval(pod, client));
        initializeCapability(capabilities, IPodLogRetrievalAsync.class, new PodLogRetrievalAsync(pod, client));
        initializeCapability(capabilities, IPodExec.class, new PodExec(pod, client));
        initializeCapability(capabilities, IRSyncable.class, new OpenShiftBinaryRSync(client));
    }

    /**
     * Initialize Project specific capabilities
     * 
     */
    public static void initializeCapabilities(Map<Class<? extends ICapability>, ICapability> capabilities,
            IProject project, IClient client) {
        initializeCapability(capabilities, IProjectTemplateProcessing.class,
                new ProjectTemplateProcessing(project, client));
        initializeCapability(capabilities, IProjectTemplateList.class,
                new ProjectTemplateListCapability(project, client));
        initializeCapability(capabilities, IImageStreamImportCapability.class,
                new ImageStreamImportCapability(project, client));
    }

    public static void initializeCapabilities(Map<Class<? extends ICapability>, ICapability> capabilities,
            Service service, IClient client) {
    }

    public static void initializeCapabilities(Map<Class<? extends ICapability>, ICapability> capabilities,
            IDeploymentConfig config, IClient client) {
        initializeCapability(capabilities, IDeployCapability.class, new DeployCapability(config, client));
        initializeCapability(capabilities, IDeploymentTriggerable.class,
                new DeploymentTrigger(config, client, new TypeMetaFactory()));
    }

    public static void initializeCapabilities(Map<Class<? extends ICapability>, ICapability> capabilities,
            IReplicationController rc, IClient client) {
        initializeCapability(capabilities, IScalable.class, new ScaleCapability(rc, client, new TypeMetaFactory()));
    }

    public static void initializeCapabilities(Map<Class<? extends ICapability>, ICapability> capabilities,
            IResource resource, IClient client) {
        initializeCapability(capabilities, ITemplateTraceability.class, new TemplateTraceability(resource));
        initializeCapability(capabilities, IDeploymentConfigTraceability.class,
                new DeploymentConfigTraceability(resource, client));
        initializeCapability(capabilities, IDeploymentTraceability.class, new DeploymentTraceability(resource, client));
        initializeCapability(capabilities, ITags.class, new TagCapability(resource));
        initializeCapability(capabilities, IClientCapability.class, new ClientCapability(client));
        initializeCapability(capabilities, IUpdatable.class, new UpdateableCapability(resource));
        initializeCapability(capabilities, IPropertyAccessCapability.class, new PropertyAccessCapability(resource));
    }

    public static void initializeClientCapabilities(Map<Class<? extends ICapability>, ICapability> capabilities,
            IClient client) {
        initializeCapability(capabilities, ITemplateProcessing.class, new ServerTemplateProcessing(client));
        initializeCapability(capabilities, IBuildConfigBuilder.class, new BuildConfigBuilder(client));
    }
}
