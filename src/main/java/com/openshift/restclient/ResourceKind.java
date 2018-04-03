package com.openshift.restclient;

import com.openshift.restclient.model.IResource;
import org.apache.commons.lang.StringUtils;

import java.util.Optional;

/**
 * Interface for defining a resource kind and an implementation for it.
 *
 * @author Christian Heike
 */
public interface ResourceKind {

    /**
     * @return the identifier for the resource kind
     */
    String getIdentifier();

    /**
     * @return the implementation class to be used for this resource kind
     */
    Optional<Class<? extends IResource>> getImplementationClass();

    static String pluralize(String kind) {
        return pluralize(kind, false, false);
    }

    static String pluralize(String kind, boolean lowercase, boolean uncapitalize) {
        if (StringUtils.isBlank(kind)) return "";
        if (kind.endsWith("y"))
            kind = kind.substring(0, kind.length() - 1).concat("ies");
        else if (!kind.endsWith("s")) {
            kind = kind.concat("s");
        }
        if (lowercase) {
            kind = kind.toLowerCase();
        }
        if (uncapitalize) {
            kind = StringUtils.uncapitalize(kind);
        }
        return kind;
    }

}
