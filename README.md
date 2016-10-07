OpenShift Java REST Client
===========================

[![Travis](https://travis-ci.org/openshift/openshift-restclient-java.svg?branch=master)](https://travis-ci.org/openshift/openshift-restclient-java) [![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.openshift/openshift-restclient-java/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.openshift/openshift-restclient-java)

This is the Java REST client for the version 3 architecture of [OpenShift](https://github.com/openshift/origin) based on Kubernetes.  The implementation is
a work in progress to provide similiar functionality and features of the command-line interface and is used by JBoss Tools for OpenShift.  For compatibility with
OpenShift 2.x see https://github.com/openshift/openshift-java-client/.

Download
--------
You may either build from source using maven (mvn clean package) which, using the master branch, will generate a snapshot build of the lastest updates.  You may also retrieve final released jars from [Maven Central](http://search.maven.org/#search%7Cgav%7C1%7Cg%3A%22com.openshift%22%20AND%20a%3A%22openshift-restclient-java%22).


Usage
-----

Creating a client:
 
	IClient client = new ClientBuilder("https://api.preview.openshift.com")
		.withUserName("openshiftdev")
		.withPassword("wouldntUlik3T0kn0w")
		.build();

This will authorize the client if the cluster is configured for basic authorization.  The alternative is to retrieve your OAUTH token and provide it to the client.  The token can be set with the builder or later by accessing the authorization context:

	client.getAuthorizationContext().setToken("asfdsfd8a70a3qrfafdsadsf786324");
	
Create a project to associate with your application by submitting a project request:

	IResource request = client.getResourceFactory().stub(ResourceKind.PROJECT_REQUEST, "myfirstproject");
	IProject project =  (IProject)client.create(request);

Resources can be created by stubbing which will instantiate and instance of the resource but not create it on the server:

	IService service = client.getResourceFactory().stub(ResourceKind.SERVICE, "myfirstservice", project.getName());
	service.setSelector(labelSelectors);
	service = client.create(service);
	
	
The client as well as resources supported by OpenShift may have certain capabilities that are instantiated when the resource is initialized.  The [capabilities](https://github.com/openshift/openshift-restclient-java/tree/master/src/main/java/com/openshift/restclient/capability) are implemented using an adapter pattern and used like the following to create a BuildConfig:

	IBuildConfig buildConfig = client.accept(new CapabilityVisitor<IBuildConfigBuilder, IBuildConfig>() {

			@Override
			public IBuildConfig visit(IBuildConfigBuilder builder) {
				return builder
						.named("mybuildconfig")
						.inNamespace(project.getName())
						.fromGitSource()
							.fromGitUrl("https://github.com/openshift/rails-example")
							.usingGitReference("master")
						.end()
						.usingSourceStrategy()
							.fromImageStreamTag("ruby:latest")
							.inNamespace("openshift")
							.withEnvVars(envVars)
						.end()
						.buildOnSourceChange(true)
						.buildOnConfigChange(true)
						.buildOnImageChange(true)
						.toImageStreamTag("mybuildconfig:latest")
					.build();
			}
		}, null);
 	
