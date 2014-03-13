package com.openshift.client.fakes;

import com.openshift.client.OpenShiftException;
import com.openshift.client.configuration.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Writer;
import java.util.Properties;

/**
 * @author Corey Daley
 */
public class OpenShiftConfigurationFake extends AbstractOpenshiftConfiguration {
	public OpenShiftConfigurationFake(final String systemConfigurationTimeout, final String userConfigurationTimeout, final String systemPropertiesTimeout, final String defaultConfigurationTimeout) throws FileNotFoundException, IOException, OpenShiftException {
		super(new SystemPropertiesFake(
				new UserConfigurationFake(
						new SystemConfigurationFake(
								new DefaultConfigurationFake(){
									@Override
									protected Properties getProperties(File file, Properties defaultProperties) {
										Properties properties = new Properties();
										properties.put(KEY_LIBRA_SERVER, LIBRA_SERVER);
										properties.put(KEY_LIBRA_DOMAIN, LIBRA_DOMAIN);
										if (defaultConfigurationTimeout != null) {
											properties.put(KEY_TIMEOUT, defaultConfigurationTimeout);
										} else {
											properties.put(KEY_TIMEOUT, DEFAULT_OPENSHIFT_TIMEOUT);
										}

										return properties;
									}
								}){
							//SystemConfigurationFake
							protected void init(Properties properties) {
								if (systemConfigurationTimeout != null) {
									properties.put(KEY_TIMEOUT, systemConfigurationTimeout);
								}
							}
						}){
					//UserConfigurationFake
					protected void initFile(Writer writer) throws IOException {
						if (userConfigurationTimeout != null) {
							writer.append(KEY_TIMEOUT).append('=').append(userConfigurationTimeout).append('\n');
						}
					}
				}){
			//SystemPropertiesFake
			@Override
			protected Properties getProperties(File file, Properties defaultProperties) {
				Properties properties = new Properties(defaultProperties);
				if (systemPropertiesTimeout != null) {
					properties.setProperty(KEY_TIMEOUT,systemPropertiesTimeout);
				}
				return properties;
			}
		});
	}
}
