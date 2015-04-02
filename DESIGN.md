Design
======

This document describes the high level design for the OpenShift Java 
Client (OSJC).  It is intended to cover aspects of this client
in support of the v3 architectual release of OpenShift which is based on
Kubernetes and Docker.  Design considerations for previous releases of OpenShift
are not discussed here.

Considerations & Motivations
----------------------------

The primary consideration for the current design is to isolate, as much as possible,
consumers of the library (e.g. JBoss Tools) from changes in the the API models.  Previous
experience has shown it can be challanging to maintain a client(e.g. UI) that supports both 
bleeding edge versions as well as a more stable code base such as enterprise deployments.
Possible scenarios that are specifically being addressed:

1. New properties and functionality added to existing types for in the latest API version
1. New properties and functionality back-ported to existing types in older API versions
1. Property and functionality deprecation for types in the latest API version

The general principle is to separate the models from functionality that supports them.
The result is a capabilities model where clients consume the capabilities offered
by the various resources instead of depending upon specific versions of the API model.
This should simply client code by:

1. Removing if/then checkes based on model versions
1. Allowing consumption of back-ported functionality by enabling the appropriate
capability in the library code.
1. "Removing" deprecated functionality by removing the capability in the library code.

Consumers of the library should depend upon the various model and capability interfaces
provided by the library.

API Models
----------
The model types are based upon the resources available for an OpenShift deployment.
This includes both OpenShift and Kubernetes types.  The OpenShift origin server allows
for interaction with the REST interface using several versions of the models. The OSJC
allows clients to interact with the server by:

1. Providing interfaces that expose only properties that are fundamental to a given type
(e.g. triggers for build) or to all types (e.g. name, labels)
1. Providing features or functional aspects as separate capabilites (as helper classes)
1. Limiting the possible types to those most likely to be of interest (i.e. Pod but not containers)

### Implementation
The current implementation is based upon the JBoss DMR library.  This library consumes a 
JSON string and can access specific parts of the content using a path syntax.  The default
implementation of the client uses a resource factory to consume a versioned JSON string and return an
instance of an API model that has the correct paths to the objects properties.  The paths are
registered in a versioned property map registry.

Capabilities
------------
Capabilities provide the functional features of a resource.  

### Implementation
One use case of capabilities is to retrieve resources that have a logical association.  
The OpenShift API model provides a loose association between various resource by adding known annotations to the OpenShift object.  A
resource is instantiated with a capability and a client can query a resource to determine
if it is able to provide the given capability.

Example
-------

Implementation Alternatives
------------
TBD
1. Versioned Packages vs Versioned Property registry?
1. Capabilities exposed via some Visitor pattern?
