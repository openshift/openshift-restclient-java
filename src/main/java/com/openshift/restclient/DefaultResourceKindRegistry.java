package com.openshift.restclient;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Default implementation of {@link ResourceKindRegistry} for which all {@link PredefinedResourceKind}s are already registered.
 */
public class DefaultResourceKindRegistry implements ConfigurableResourceKindRegistry {

    private final Map<String, ResourceKind> resourceKinds = new HashMap<>();

    public DefaultResourceKindRegistry() {
        register(PredefinedResourceKind.values());
    }

    @Override
    public void register(ResourceKind... resourceKinds) {
        this.resourceKinds.putAll(
                Arrays.stream(
                        PredefinedResourceKind.values())
                        .collect(Collectors.toMap(ResourceKind::getIdentifier, Function.identity()))
        );
    }

    @Override
    public Optional<ResourceKind> find(final String kind) {
        return Optional.ofNullable(resourceKinds.get(kind));
    }

}
