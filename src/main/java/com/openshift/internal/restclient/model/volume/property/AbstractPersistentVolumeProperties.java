package com.openshift.internal.restclient.model.volume.property;

import org.jboss.dmr.ModelNode;

import com.openshift.restclient.model.volume.property.IPersistentVolumeProperties;

public abstract class AbstractPersistentVolumeProperties implements IPersistentVolumeProperties {

    public abstract void setProperties(ModelNode node);

}
