OpenShift Java REST Client
===========================

[![Travis](https://travis-ci.org/openshift/openshift-restclient-java.svg?branch=master)](https://travis-ci.org/openshift/openshift-restclient-java)

This is the Java REST client for the version 3 architecture of [OpenShift](https://github.com/openshift/origin) based on Kubernetes.  The implementation is
a work in progress to provide functionality and features of the command-line interface and is used by JBoss Tools for OpenShift.  For compatibility with
OpenShift 2.x see https://github.com/openshift/openshift-java-client/.

Usage
-----
    IClient client = new ClientFactory().create(url, sslCertCallback);
    client.setAuthorizationStrategy(new TokenAuthorizationStrategy("ADSASEAWRA-AFAEWAAA");
    List<IProject> projects = client.list(ResourceKind.PROJECTS, "test-namespace");

Download
--------
You may either build from source using maven (mvn clean package) or get the prebuilt artifact from the maven central.
