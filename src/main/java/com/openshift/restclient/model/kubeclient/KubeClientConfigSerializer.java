/******************************************************************************* 
 * Copyright (c) 2016 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/
package com.openshift.restclient.model.kubeclient;

import java.beans.IntrospectionException;
import java.io.Reader;

import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.introspector.Property;
import org.yaml.snakeyaml.introspector.PropertyUtils;
import org.yaml.snakeyaml.representer.Representer;

import com.openshift.internal.restclient.model.kubeclient.KubeClientConfig;
import com.openshift.internal.restclient.model.kubeclient.KubeClientConfigConstructor;
import com.openshift.restclient.utils.BeanUtils;

/**
 * 
 * @author jeff.cantrill
 *
 */
public class KubeClientConfigSerializer {

	public IKubeClientConfig loadKubeClientConfig(Reader reader) {
		Representer representer = new Representer();
		Yaml parser = new Yaml(new KubeClientConfigConstructor(new YamlPropertyUtils()), representer);
		representer.getPropertyUtils().setSkipMissingProperties(true);
		return parser.loadAs(reader, KubeClientConfig.class);
	}
	
	private static class YamlPropertyUtils extends PropertyUtils {
		@Override
		public Property getProperty(Class<? extends Object> type, String name) throws IntrospectionException {
			if (name.indexOf('-') > -1) {
				name = BeanUtils.toCamelCase(name, "-");
			}
			return super.getProperty(type, name);
		}
	}
}
