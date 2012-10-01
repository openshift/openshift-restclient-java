OpenShift Java Client
=============================

This is a client for OpenShift written in java. It pretty much offers all features that are currently available in the rhc-* command line tools 
(create/rename a domain, create/destroy applications, list all existing applications, available cartridges, embed cartridges etc.). 
This is the client that is used in JBoss Tools for OpenShift.

Usage
-----
An exemplary usage of the client may look like the following:

		final IOpenShiftConnection connection = new OpenShiftConnectionFactory().getConnection("myApplicationId", "user", "password");
		IUser user = connection.getUser();
		IDomain domain = user.createDomain("myDomain");
		IApplication as7Application = domain.createApplication("myApplication", ICartridge.JBOSSAS_7);
		IEmbeddedCartridge mySqlCartridge = as7Application.addEmbeddableCartridge(IEmbeddableCartridge.MYSQL_51);
		String unstructuredCredentials = mySqlCartridge.getCreationLog();
		String mySqlConnectionUrl = mySqlCartridge.getUrl();

Download
--------
You may either build from source using maven (mvn clean package) or get the prebuilt artifact from the maven central.