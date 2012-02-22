package com.openshift.express.internal.client.request;

import com.openshift.express.client.ISSHPublicKey;

public class DestroyDomainRequest extends AbstractDomainRequest {

    public DestroyDomainRequest(String name, ISSHPublicKey sshKey,
            String username) {
        super(name, sshKey, username);
    }

    @Override
    public boolean isAlter() {
        return false;
    }

    @Override
    public boolean isDelete() {
        return true;
    }
    
    @Override
    public String getOperation() {
        return "destroy domain " + getName();
    }

}
