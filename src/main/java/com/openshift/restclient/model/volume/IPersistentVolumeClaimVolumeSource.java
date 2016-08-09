package com.openshift.restclient.model.volume;

/**
 * @author Ulf Lilleengen
 */
public interface IPersistentVolumeClaimVolumeSource extends IVolumeSource {
    String getClaimName();
    void setClaimName(String claimName);
    boolean isReadOnly();
    void setReadOnly(boolean readOnly);
}
