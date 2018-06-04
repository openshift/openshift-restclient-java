package com.openshift.restclient;

/**
 * A configurable version of {@link ResourceKindRegistry}.
 */
public interface ConfigurableResourceKindRegistry extends ResourceKindRegistry {

    /**
     * Register one or more {@link ResourceKind}s to be handled by the factory.
     *
     * @param resourceKinds the resource kinds to be handled
     */
    void register(final ResourceKind... resourceKinds);

}
