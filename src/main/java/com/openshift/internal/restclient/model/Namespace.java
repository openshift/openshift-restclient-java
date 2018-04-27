package com.openshift.internal.restclient.model;

import static com.openshift.internal.restclient.capability.CapabilityInitializer.initializeCapabilities;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.jboss.dmr.ModelNode;

import com.openshift.restclient.IClient;
import com.openshift.restclient.model.INamespace;
import com.openshift.restclient.model.IResource;

public class Namespace extends KubernetesResource implements INamespace {

    private static final String ANNOTATION_DISPLAY_NAME = "openshift.io/display-name";
    private static final String ANNOTATION_DESCRIPTION = "openshift.io/description";
    private static final String ANNOTATION_REQUESTER = "openshift.io/requester";

    public Namespace(ModelNode node, IClient client, Map<String, String []> propertyKeys) {
        super(node, client, propertyKeys);
        initializeCapabilities(getModifiableCapabilities(), this, getClient());
    }

    @Override
    public <T extends IResource> List<T> getResources(String kind) {
        if(getClient() == null) {
            return new ArrayList<>();
        }
        return getClient().list(kind, getName());
    }

    @Override
    public Namespace getNamespace() {
        return this;
    }

    @Override
    public String getNamespaceName() {
        return this.getName();
    }

    @Override
    public String getDisplayName() {
        return getAnnotation(ANNOTATION_DISPLAY_NAME);
    }

    @Override
    public void setDisplayName(String displayName) {
        setAnnotation(ANNOTATION_DISPLAY_NAME, displayName);
    }

    @Override
    public String getDescription() {
        return getAnnotation(ANNOTATION_DESCRIPTION);
    }

    @Override
    public void setDescription(String description) {
        setAnnotation(ANNOTATION_DESCRIPTION, description);
    }

    @Override
    public String getRequester() {
        return getAnnotation(ANNOTATION_REQUESTER);
    }

    @Override
    public void setRequest(String requester) {
        setAnnotation(ANNOTATION_REQUESTER, requester);
    }
}
