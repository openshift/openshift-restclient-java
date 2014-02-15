OpenShift Java Client
===========================

Java client for the OpenShift REST API.  It pretty much offers all features that are currently available in the rhc-* command line tools 
(create/rename a domain, create/destroy applications, list applications, list available cartridges, add cartridges, etc.). 
This client is used by JBoss Tools for OpenShift.

Usage
-----
An exemplary usage of the client may look like the following:

		IOpenShiftConnection connection = 
				new OpenShiftConnectionFactory().getConnection("myApplicationId", "user", "password");
		IUser user = connection.getUser();
		IDomain domain = user.createDomain("myDomain");
		IApplication as7Application = domain.createApplication("myApplication", LatestVersionOf.jbossAs().get(user));
		IEmbeddedCartridge mySqlCartridge = as7Application.addEmbeddableCartridge(LatestVersionOf.mySQL().get(user));
		String unstructuredCredentials = mySqlCartridge.getCreationLog();
		String mySqlConnectionUrl = mySqlCartridge.getUrl();

There are also 2 blog posts on jboss.org which discuss the API in more details:

* [show-domain-info: openshift-java-client in a nutshell](http://planet.jboss.org/post/show_domain_info_openshift_java_client_in_a_nutshell)
* [enable-openshift-ci: full example using openshift-java-client](https://community.jboss.org/wiki/Enable-openshift-ciFullExampleUsingOpenshift-java-client)

Most insightful are the integration tests within the library which pretty much use the API in all details:

* ApplicationResourceIntegrationTest
* DomainResourceIntegrationTest
* EmbeddedCartridgeResourceIntegrationTest
* etc. 

Download
--------
You may either build from source using maven (mvn clean package) or get the prebuilt artifact from the maven central.
