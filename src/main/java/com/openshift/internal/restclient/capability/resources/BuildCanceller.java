package com.openshift.internal.restclient.capability.resources;

import com.openshift.restclient.IClient;
import com.openshift.restclient.ResourceKind;
import com.openshift.restclient.capability.resources.IBuildCancelable;
import com.openshift.restclient.model.IBuild;

public class BuildCanceller implements IBuildCancelable {

    private IBuild build;
    private IClient client;

    public BuildCanceller(IBuild build, IClient client) {
        this.build = build;
        this.client = client;
    }

    @Override
    public boolean isSupported() {
        return build != null && client != null && ResourceKind.BUILD.equals(build.getKind());
    }

    @Override
    public String getName() {
        return BuildCanceller.class.getSimpleName();
    }

    @Override
    public IBuild cancel() {
        boolean cancelled = build.cancel();
        if (cancelled) {
            build = client.update(build);
        }
        return build;
    }

}
