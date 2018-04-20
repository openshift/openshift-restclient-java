package com.openshift.restclient.model.volume;

/**
 * @author Ulf Lilleengen
 */
public interface IEmptyDirVolumeSource extends IVolumeSource {
    String getMedium();

    void setMedium(String medium);
}
