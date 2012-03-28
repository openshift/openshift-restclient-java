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
package com.openshift.express.internal.client.response.unmarshalling;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.datatype.DatatypeConfigurationException;

import org.jboss.dmr.ModelNode;

import com.openshift.express.client.Cartridge;
import com.openshift.express.client.ICartridge;
import com.openshift.express.client.OpenShiftUnknonwSSHKeyTypeException;
import com.openshift.express.internal.client.ApplicationInfo;
import com.openshift.express.internal.client.EmbeddableCartridgeInfo;
import com.openshift.express.internal.client.UserInfo;
import com.openshift.express.internal.client.utils.IOpenShiftJsonConstants;

/**
 * @author André Dietisheim
 */
public class UserInfoResponseUnmarshaller extends AbstractOpenShiftJsonResponseUnmarshaller<UserInfo> {

	private static final Pattern URL_REGEX = Pattern.compile(".*URL: (.+)");

	protected UserInfo createOpenShiftObject(ModelNode node) throws DatatypeConfigurationException, OpenShiftUnknonwSSHKeyTypeException {
		ModelNode dataNode = node.get(IOpenShiftJsonConstants.PROPERTY_DATA);
		if (!isSet(dataNode)) {
			return null;
		}

		ModelNode userInfoNode = dataNode.get(IOpenShiftJsonConstants.PROPERTY_USER_INFO);
		if (!isSet(userInfoNode)) {
			return null;
		}
		
		SSHKeyProperties keyProperties = getSSHKeyProperties(userInfoNode);
		String rhlogin = getString(IOpenShiftJsonConstants.PROPERTY_RHLOGIN, userInfoNode);
		String uuid = getString(IOpenShiftJsonConstants.PROPERTY_UUID, userInfoNode);
		String namespace = getString(IOpenShiftJsonConstants.PROPERTY_NAMESPACE, userInfoNode);
		String rhcDomain = getString(IOpenShiftJsonConstants.PROPERTY_RHC_DOMAIN, userInfoNode);
		long maxGears = this.getLong(IOpenShiftJsonConstants.PROPERTY_MAX_GEARS, userInfoNode);
		long consumedGears = this.getLong(IOpenShiftJsonConstants.PROPERTY_CONSUMED_GEARS, userInfoNode);

		List<ApplicationInfo> applicationInfos = createApplicationInfos(dataNode
				.get(IOpenShiftJsonConstants.PROPERTY_APP_INFO));

		return new UserInfo(rhlogin, uuid, keyProperties.sshPublicKey, rhcDomain, namespace, applicationInfos, keyProperties.sshKeyType, maxGears, consumedGears);
	}
	
	private SSHKeyProperties getSSHKeyProperties(ModelNode userInfoNode) {
		SSHKeyProperties keyProperties = new SSHKeyProperties();
		if (hasSshKeyProperties(userInfoNode)) {
			keyProperties.sshKeyType = getNonEmptyString(IOpenShiftJsonConstants.PROPERTY_SSH_TYPE, userInfoNode);
			keyProperties.sshPublicKey = getNonEmptyString(IOpenShiftJsonConstants.PROPERTY_SSH_KEY, userInfoNode);
		} else {
			ModelNode sshKeyNode = getFirstKeyOfSeveral(userInfoNode);
			if (isSet(sshKeyNode)) {
				keyProperties.sshKeyType = getNonEmptyString(IOpenShiftJsonConstants.PROPERTY_TYPE, sshKeyNode);
				keyProperties.sshPublicKey = getNonEmptyString(IOpenShiftJsonConstants.PROPERTY_KEY, sshKeyNode);
			}
		}

		return keyProperties;
	}

	private boolean hasSshKeyProperties(ModelNode userInfoNode) {
		return getNonEmptyString(IOpenShiftJsonConstants.PROPERTY_SSH_TYPE, userInfoNode) != null;
	}
	
	private ModelNode getFirstKeyOfSeveral(ModelNode userInfoNode) {
		ModelNode sshKeyNode = null;
		ModelNode sshKeysNode = getChild(IOpenShiftJsonConstants.PROPERTY_SSH_KEYS, userInfoNode);
		if (sshKeysNode != null) {
			Set<String> keys = sshKeysNode.keys();
			if (!keys.isEmpty()) {
				String firstKey = keys.iterator().next();
				sshKeyNode = sshKeysNode.get(firstKey);
			}
		}
		return sshKeyNode;
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
		List<EmbeddableCartridgeInfo> embeddedCartridges = createEmbeddedCartridges(appNode);
		ICartridge cartidge = new Cartridge(getString(IOpenShiftJsonConstants.PROPERTY_FRAMEWORK, appNode));
		Date creationTime = getDate(IOpenShiftJsonConstants.PROPERTY_CREATION_TIME, appNode);
		return new ApplicationInfo(name, uuid, embeddedCartridges, cartidge, creationTime);
	}

	protected List<EmbeddableCartridgeInfo> createEmbeddedCartridges(ModelNode appNode) {
		List<EmbeddableCartridgeInfo> cartridges = new ArrayList<EmbeddableCartridgeInfo>();
		ModelNode embeddedCartridgesNode = appNode.get(IOpenShiftJsonConstants.PROPERTY_EMBEDDED);
		if (!isSet(embeddedCartridgesNode)) {
			return cartridges;
		}
		for (String name : embeddedCartridgesNode.keys()) {
			cartridges.add(createEmbeddedCartridgeInfo(name, embeddedCartridgesNode.get(name)));
		}
		return cartridges;
	}

	private EmbeddableCartridgeInfo createEmbeddedCartridgeInfo(String name, ModelNode embeddedCartridgeNode) {
		String infoPropertyValue = getString(IOpenShiftJsonConstants.PROPERTY_INFO, embeddedCartridgeNode);
		return new EmbeddableCartridgeInfo(name, getUrl(infoPropertyValue));
	}

	private String getUrl(String infoPropertyValue) {
		Matcher matcher = URL_REGEX.matcher(infoPropertyValue);
		if (matcher.find()
				&& matcher.groupCount() >= 1) {
			return matcher.group(1);
		} else {
			return infoPropertyValue;
		}
	}

	private static class SSHKeyProperties {
		private String sshKeyType;
		private String sshPublicKey;
	}
}
