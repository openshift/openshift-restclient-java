/******************************************************************************* 
 * Copyright (c) 2011 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/
package org.jboss.tools.openshift.express.internal.client.response.unmarshalling;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.datatype.DatatypeConfigurationException;

import org.jboss.dmr.ModelNode;
import org.jboss.tools.openshift.express.client.Cartridge;
import org.jboss.tools.openshift.express.client.ICartridge;
import org.jboss.tools.openshift.express.internal.client.ApplicationInfo;
import org.jboss.tools.openshift.express.internal.client.IOpenShiftJsonConstants;
import org.jboss.tools.openshift.express.internal.client.UserInfo;

/**
 * @author André Dietisheim
 */
public class UserInfoResponseUnmarshaller extends AbstractOpenShiftJsonResponseUnmarshaller<UserInfo> {

	@Override
	protected UserInfo createOpenShiftObject(ModelNode node) throws DatatypeConfigurationException {
		ModelNode dataNode = node.get(IOpenShiftJsonConstants.PROPERTY_DATA);
		if (!isSet(dataNode)) {
			return null;
		}

		ModelNode userInfoNode = dataNode.get(IOpenShiftJsonConstants.PROPERTY_USER_INFO);
		if (!isSet(userInfoNode)) {
			return null;
		}

		String sshPublicKey = getString(IOpenShiftJsonConstants.PROPERTY_SSH_KEY, userInfoNode);
		String rhlogin = getString(IOpenShiftJsonConstants.PROPERTY_RHLOGIN, userInfoNode);
		String uuid = getString(IOpenShiftJsonConstants.PROPERTY_UUID, userInfoNode);
		String namespace = getString(IOpenShiftJsonConstants.PROPERTY_NAMESPACE, userInfoNode);
		String rhcDomain = getString(IOpenShiftJsonConstants.PROPERTY_RHC_DOMAIN, userInfoNode);

		List<ApplicationInfo> applicationInfos = createApplicationInfos(dataNode.get(IOpenShiftJsonConstants.PROPERTY_APP_INFO));

		return new UserInfo(rhlogin, uuid, sshPublicKey, rhcDomain, namespace, applicationInfos);
	}

	private List<ApplicationInfo> createApplicationInfos(ModelNode appInfoNode) throws DatatypeConfigurationException {
		List<ApplicationInfo> applicationInfos = new ArrayList<ApplicationInfo>();
		if (!isSet(appInfoNode)) {
			return applicationInfos;
		}

		for (String name : appInfoNode.keys()) {
			applicationInfos.add(createApplicationInfo(name, appInfoNode.get(name)));
		}
		return applicationInfos;
	}

	private ApplicationInfo createApplicationInfo(String name, ModelNode appNode) throws DatatypeConfigurationException {
		String uuid = getString(IOpenShiftJsonConstants.PROPERTY_UUID, appNode);
		String embedded = getString(IOpenShiftJsonConstants.PROPERTY_EMBEDDED, appNode);
		ICartridge cartrdige = new Cartridge(getString(IOpenShiftJsonConstants.PROPERTY_FRAMEWORK, appNode));
		Date creationTime = getDate(IOpenShiftJsonConstants.PROPERTY_CREATION_TIME, appNode);
		return new ApplicationInfo(name, uuid, embedded, cartrdige, creationTime);
	}
}
