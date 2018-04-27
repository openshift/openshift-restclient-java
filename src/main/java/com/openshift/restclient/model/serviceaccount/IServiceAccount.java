package com.openshift.restclient.model.serviceaccount;

import java.util.Collection;

import com.openshift.restclient.model.IResource;

/**
 * @author David Simansky | dsimansk@redhat.com
 */
public interface IServiceAccount extends IResource {

    /**
     * Get the collection of all secrets
     * 
     */
    Collection<String> getSecrets();

    /**
     * Add new secret name
     * 
     * @param secret
     *            - secret name
     */
    void addSecret(String secret);

    /**
     * Get the collection of all imagePullSecrets
     * 
     */
    Collection<String> getImagePullSecrets();

    /**
     * Add new imagePullSecret name
     * 
     * @param imagePullSecret
     *            - imagePullSecretName
     */
    void addImagePullSecret(String imagePullSecret);

}
