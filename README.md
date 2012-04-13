OpenShift Java Client
=============================

This is a client for OpenShift written in java. The client is an osgi bundle but may also be used in a non-osgi environment.

Downloads
---------

I have 2 prebuilt artifacts in the file section:

* jar with dependencies: `org.jboss.tools.openshift.express.client-[VERSION]-jar-with-dependencies.jar`
* osgi-bundle: `org.jboss.tools.openshift.express.client_[VERSION].jar`

Usage
-----
An exemplary usage of the client may look like the following:

    IUser user = new User(username, password);
    ISSHPublicKey sshKey = SSHKeyPair.create(passPhrase, privateKeyPath, publicKeyPath);
    IDomain domain = user.createDomain(domainName, sshKey);
    IApplication application = user.createApplication(applicationName, ICartridge.JBOSSAS_7); 
    application.destroy();
