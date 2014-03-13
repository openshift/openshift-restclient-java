package com.openshift.client.fakes;

import com.openshift.client.OpenShiftException;
import com.openshift.client.configuration.DefaultConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

/**
 * Created by cdaley on 3/13/14.
 */
public class DefaultConfigurationFake extends DefaultConfiguration {
	public static final String LIBRA_SERVER = "openshift.redhat.com";
	public static final String LIBRA_DOMAIN = "rhcloud.com";

	public DefaultConfigurationFake() throws OpenShiftException, IOException {
		super();
	}

	@Override
	protected Properties getProperties(File file, Properties defaultProperties) {
		Properties properties = new Properties();
		properties.put(KEY_LIBRA_SERVER, LIBRA_SERVER);
		properties.put(KEY_LIBRA_DOMAIN, LIBRA_DOMAIN);
		properties.put(KEY_TIMEOUT, DEFAULT_OPENSHIFT_TIMEOUT);
		return properties;
	}
}
