package com.openshift.restclient.model.volume;

/**
 * @author Ulf Lilleengen
 */
public interface ISecretVolumeSource extends IVolumeSource {
    String getSecretName();
    void setSecretName(String secretName);
}
