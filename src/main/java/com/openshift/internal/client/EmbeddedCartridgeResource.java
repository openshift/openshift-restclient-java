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
package com.openshift.internal.client;

import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.openshift.client.IApplication;
import com.openshift.client.IEmbeddableCartridge;
import com.openshift.client.IEmbeddedCartridge;
import com.openshift.client.OpenShiftException;
import com.openshift.internal.client.response.CartridgeResourceDTO;
import com.openshift.internal.client.response.Link;
import com.openshift.internal.client.response.Message;

/**
 * A cartridge that may be embedded into an application. This class is no enum
 * since we dont know all available types and they may change at any time.
 * 
 * @author André Dietisheim
 */
public class EmbeddedCartridgeResource extends AbstractOpenShiftResource implements IEmbeddedCartridge {

	private static final Pattern INFO_URL_PATTERN = Pattern.compile("URL: (.+)\\n*");

	protected static final String JENKINS_CLIENT = "jenkins-client";
	protected static final String MYSQL = "mysql";
	protected static final String PHPMYADMIN = "phpmyadmin";
	protected static final String METRICS = "metrics";
	protected static final String POSTGRES = "postgresql";
	protected static final String MONGO = "mongodb";
	protected static final String ROCKMONGO = "rockmongo";
	protected static final String CRON = "cron";
	protected static final String GEN_MMS_AGENT = "10gen-mms-agent";

	private static final String LINK_DELETE_CARTRIDGE = "DELETE";

	private final String name;
	private final CartridgeType type;
	private String url;
	private final ApplicationResource application;

	protected EmbeddedCartridgeResource(String info, final CartridgeResourceDTO dto, final ApplicationResource application) {
		this(dto.getName(), dto.getType(), info, dto.getLinks(), dto.getCreationLog(), application);
	}

	protected EmbeddedCartridgeResource(final String name, final CartridgeType type, String info, final Map<String, Link> links,
			final List<Message> creationLog, final ApplicationResource application) {
		super(application.getService(), links, creationLog);
		this.name = name;
		this.type = type;
		// TODO: fix this workaround once
		// https://bugzilla.redhat.com/show_bug.cgi?id=812046 is fixed
		this.url = extractUrl(info, creationLog);
		this.application = application;
	}
	
	/**
	 * @return the name
	 */
	public final String getName() {
		return name;
	}

	/**
	 * @return the type
	 */
	protected final CartridgeType getType() {
		return type;
	}

	/**
	 * @return the application
	 */
	public final IApplication getApplication() {
		return application;
	}

	private String extractUrl(String info, List<Message> messages) {
		if (info != null) {
			return extractUrl(info);
		} else {
			return extractUrl(messages);
		}
	}
	
	private String extractUrl(String string) {
		if (string == null) {
			return null;
		}
		Matcher matcher = INFO_URL_PATTERN.matcher(string);
		if (!matcher.find()
				|| matcher.groupCount() < 1) {
			return null;
		}
		
		return matcher.group(1);
	}
	
	private String extractUrl(List<Message> messages) {
		if (messages == null) {
			return null;
		}
		for (Message message : messages) {
			String url = extractUrl(message.getText());
			if (url != null) {
				return url;
			}
		}
		return null;
	}

	public String getUrl() throws OpenShiftException {
		return url;
	}

	@Override
	public void refresh() throws OpenShiftException {
	}
	
	public void destroy() throws OpenShiftException {
		new DeleteCartridgeRequest().execute();
		application.removeEmbeddedCartridge(this);
	}

	private class DeleteCartridgeRequest extends ServiceRequest {

		protected DeleteCartridgeRequest() {
			super(LINK_DELETE_CARTRIDGE);
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	/**
	 * TODO: implement fully correct #equals and #hashcode. The current
	 * implementation only ensures that {@link EmbeddedCartridgeResource} may be
	 * compared to {@link EmbeddableCartridge}.
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(IEmbeddableCartridge.class.isAssignableFrom(obj.getClass())))
			return false;
		IEmbeddableCartridge other = (IEmbeddableCartridge) obj;
		if (name == null) {
			if (other.getName() != null)
				return false;
		} else if (!name.equals(other.getName()))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "EmbeddedCartridgeResource [" +
				"name=" + name  
				+ ", url=" + url 
				+ ", type=" + type + ", url=" + url
				+ ", application=" + application 
				+ "]";
	}

}