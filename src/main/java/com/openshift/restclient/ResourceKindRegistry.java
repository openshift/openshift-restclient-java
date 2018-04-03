package com.openshift.restclient;

import java.util.Optional;

/**
 * A registry for {@link ResourceKind}s.
 * <p>
 * Note:
 * The 90s called me recently and wanted their I-prefix back.
 * I was a bit unsure whether I could give it to them, even though we have IDE support for
 * distinguishing interfaces from classes very easily, so I hesitated.
 * But then they got really mad and they shouted at me and then threatened to hire a very expensive lawyer.
 * You have to understand, neither do I have much money nor do I know a good lawyer, thus I had to give it back to them.
 *
 * Sorry ;)
 * </p>
 */
public interface ResourceKindRegistry {

    /**
     * Get the {@link ResourceKind} for a certain kind identifier by querying the registered
     * {@link ResourceKind}s using the {@link ResourceKind#getIdentifier()}.
     *
     * @param kind the identifier of the resource kind
     * @return an optional wrapping the descriptor or an empty optional
     */
    Optional<ResourceKind> find(String kind);

}
