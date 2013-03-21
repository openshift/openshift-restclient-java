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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

import javax.xml.datatype.DatatypeConfigurationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.openshift.client.ApplicationScale;
import com.openshift.client.IApplication;
import com.openshift.client.IApplicationPortForwarding;
import com.openshift.client.ICartridge;
import com.openshift.client.ICartridgeConstraint;
import com.openshift.client.IDomain;
import com.openshift.client.IEmbeddableCartridge;
import com.openshift.client.IEmbeddedCartridge;
import com.openshift.client.IGear;
import com.openshift.client.IGearGroup;
import com.openshift.client.IGearProfile;
import com.openshift.client.IOpenShiftConnection;
import com.openshift.client.OpenShiftException;
import com.openshift.client.OpenShiftSSHOperationException;
import com.openshift.client.utils.HostUtils;
import com.openshift.client.utils.RFC822DateUtils;
import com.openshift.internal.client.AbstractOpenShiftResource.ServiceRequest;
import com.openshift.internal.client.response.ApplicationResourceDTO;
import com.openshift.internal.client.response.CartridgeResourceDTO;
import com.openshift.internal.client.response.GearDTO;
import com.openshift.internal.client.response.GearGroupDTO;
import com.openshift.internal.client.response.Link;
import com.openshift.internal.client.response.Message;
import com.openshift.internal.client.ssh.ApplicationPortForwarding;
import com.openshift.internal.client.utils.Assert;
import com.openshift.internal.client.utils.CollectionUtils;
import com.openshift.internal.client.utils.IOpenShiftJsonConstants;

/**
 * The Class Application.
 * 
 * @author André Dietisheim
 */
public class GearGroupResource extends AbstractOpenShiftResource implements IGearGroup {

	private static final Logger LOGGER = LoggerFactory.getLogger(GearGroupResource.class);

	private String name;
	private String gearProfile;
	private List<IGear> gears = new ArrayList<IGear>();

	/**
	 * Constructor...
	 * 
	 * @param dto
	 * @param cartridge
	 * @param domain
	 */
	protected GearGroupResource(GearGroupDTO dto, ApplicationResource app) {
		this(dto.getName(), dto.getGearProfile(), dto.getGears(), app);
	}
	
	protected GearGroupResource(String name, String gearProfile, List<GearDTO> dtos, ApplicationResource app) {
		super(app.getService());
		this.name = name;
		this.gearProfile = gearProfile;
		for (GearDTO gear : dtos) {
			gears.add(new GearResource(gear, this));
		}
	}
	

	@Override
	public List<IGear> getGears() {
		// TODO Auto-generated method stub
		return gears;
	}

	@Override
	public void refresh() throws OpenShiftException {
		// TODO Auto-generated method stub
		
	}


	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return name;
	}

	@Override
	public String getGearProfile() {
		// TODO Auto-generated method stub
		return gearProfile;
	}
}
