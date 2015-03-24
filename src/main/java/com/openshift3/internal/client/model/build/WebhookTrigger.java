/*******************************************************************************
 * Copyright (c) 2015 Red Hat, Inc. Distributed under license by Red Hat, Inc.
 * All rights reserved. This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Red Hat, Inc.
 ******************************************************************************/
package com.openshift3.internal.client.model.build;

import com.openshift.internal.client.utils.StringUtils;
import com.openshift3.client.model.build.BuildTriggerType;
import com.openshift3.client.model.build.IWebhookTrigger;

public class WebhookTrigger implements IWebhookTrigger {

	private BuildTriggerType type;
	private String secret;
	private String resourceName;
	private String baseURL;
	private String apiVersion;
	private String namespace;

	public WebhookTrigger(BuildTriggerType triggerType, String secret, String resourceName, String baseURL, String apiVersion, String namespace) {
		this.type = triggerType;
		this.secret = secret;
		this.resourceName = resourceName;
		this.baseURL = baseURL;
		this.apiVersion = apiVersion;
		this.namespace = namespace;
	}

	@Override
	public BuildTriggerType getType() {
		return type;
	}

	@Override
	public String getSecret() {
		return secret;
	}

	@Override
	public String getWebhookURL() {
		if(StringUtils.isEmpty(baseURL)){
			return "";
		}
		return String.format("%s/osapi/%s/buildConfigHooks/%s/%s/%s?namespace=%s",
					baseURL,
					apiVersion,
					resourceName,
					secret,
					type.toString(),
					namespace
				);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((secret == null) ? 0 : secret.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		WebhookTrigger other = (WebhookTrigger) obj;
		if (secret == null) {
			if (other.secret != null)
				return false;
		} else if (!secret.equals(other.secret))
			return false;
		if (type != other.type)
			return false;
		return true;
	}
	
}
