package com.openshift.client.fakes;

import com.openshift.client.OpenShiftException;
import com.openshift.client.configuration.IOpenShiftConfiguration;
import com.openshift.client.configuration.SystemProperties;

import java.io.IOException;

/**
 * @author Corey Daley
 */
public class SystemPropertiesFake extends SystemProperties {
	public SystemPropertiesFake(IOpenShiftConfiguration parentConfiguration) throws OpenShiftException, IOException {
		super(parentConfiguration);
	}
}
