OpenShift Java REST Client
===========================

This is the Java REST client for the version 3 architecture of [OpenShift](https://github.com/openshift/origin) based on Kubernetes.  The implementation is
a work in progress to provide functionality and features of the command-line interface and is used by JBoss Tools for OpenShift.

Usage
-----
TBD

Most insightful are the integration tests within the library which pretty much use the API in all details:

* ApplicationResourceIntegrationTest
* DomainResourceIntegrationTest
* EmbeddedCartridgeResourceIntegrationTest
* etc. 

Download
--------
You may either build from source using maven (mvn clean package) or get the prebuilt artifact from the maven central.
