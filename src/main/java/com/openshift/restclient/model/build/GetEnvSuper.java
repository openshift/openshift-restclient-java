package com.openshift.restclient.model.build;

import com.openshift.restclient.model.IEnvironmentVariable;

import java.util.Collection;

public interface GetEnvSuper {

    Collection<IEnvironmentVariable> getEnvVars();
}
