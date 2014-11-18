package com.openshift.kube.capability;

import static org.junit.Assert.*;

import java.net.MalformedURLException;
import java.net.URL;

import org.junit.Test;

import com.openshift.kube.OpenShiftKubeClient;

public class DefaultImageRegistryHostingTest {

	@Test
	public void testExistsWhenServiceExists() throws MalformedURLException {
		OpenShiftKubeClient client = new OpenShiftKubeClient(new URL("http://ec2-54-163-104-41.compute-1.amazonaws.com:8080"));
		DefaultImageRegistryHosting capability = new DefaultImageRegistryHosting(client);
		assertTrue("Exp. capability to be enabled", capability.exists());
	}

}
