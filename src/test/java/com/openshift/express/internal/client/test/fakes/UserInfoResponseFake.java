/*******************************************************************************
 * Copyright (c) 2011 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package com.openshift.express.internal.client.test.fakes;

import java.util.ArrayList;
import java.util.List;

import com.openshift.express.client.IEmbeddableCartridge;
import com.openshift.express.internal.client.EmbeddableCartridge;
import com.openshift.express.internal.client.EmbeddableCartridgeInfo;

/**
 * @author Andre Dietisheim
 */
public class UserInfoResponseFake {

	public static final String RHLOGIN = "jbosstools@redhat.com";
	public static final String PASSWORD = "$!445password%&";
	public static final String SSH_KEY_TYPE = "ssh-rsa";
	
	public static final String RHC_DOMAIN = "rhcloud.com";
	public static final String NAMESPACE = "1315839296868";
	public static final String UUID = "5f34b742db754cc9ab70fd1db2c9a2bd";
	public static final String SSH_KEY =
			"AAAAB3NzaC1yc2EAAAADAQABAAAAgQC6BGRDydfGsQHhnZgo43dEfLz"
					+ "SJBke/hE8MLBBG1+5ZwktsrE+f2VdVt0McRLVAO6rdJRyMUX0rTbm7"
					+ "SABRVSX+zeQjlfqbbUtYFc7TIfd4RQc3GaISG1rS3C4svRSjdWaG36"
					+ "vDY2KxowdFvpKj8i8IYNPlLoRA/7EzzyneS6iyw==";
	
	public static final long MAX_GEARS = 5;
	public static final long CONSUMED_GEARS = 0;
	
	public static final String APP1_NAME = "1315836963263";
	public static final String APP1_UUID = "810540bafc1c4b5e8cac830fb8ca786f";
	public static final String APP1_CARTRIDGE = "jbossas-7";
	public static final String APP1_CREATION_TIME = "2011-09-12T10:15:48-04:00";
	public static final List<EmbeddableCartridgeInfo> APP1_EMBEDDED = null;

	public static final String APP2_NAME = "1315903559289";
	public static final String APP2_UUID = "f5496311f43b42cd8fa5db5ecf83a352";
	public static final String APP2_CARTRIDGE = "jbossas-7";
	public static final String APP2_CREATION_TIME = "2011-09-13T04:45:44-04:00";
	public static final String APP2_EMBEDDED_NAME = "mysql-5.1";
	public static final String APP2_EMBEDDED_URL = "mysql://127.1.2.129:3306/";

	public static final String RESPONSE =
			"{"
					+ "	\"messages\":\"\","
					+ " 	\"debug\":\"\","
					+ "	\"data\":"
					+ ""
					+ "\"{"
					+ "		\\\"user_info\\\":"
					+ "		{"
					+ "			\\\"rhc_domain\\\":\\\"" + RHC_DOMAIN + "\\\"," //
					+ "			\\\"rhlogin\\\":\\\"" + RHLOGIN + "\\\","
					+ "			\\\"namespace\\\":\\\"" + NAMESPACE + "\\\","
					+ "			\\\"uuid\\\":\\\"" + UUID + "\\\","
					+ "			\\\"ssh_key\\\":\\\"" + SSH_KEY + "\\\","
					+ "         \\\"system_ssh_keys\\\":{},"
					+ "         \\\"ssh_type\":\\\"ssh-rsa\\\""
					+ "		},"
					+ "		\\\"app_info\\\":"
					+ "		{"
					+ "			\\\"" + APP1_NAME + "\\\":"
					+ "			{"
					+ "				\\\"embedded\\\":" + APP1_EMBEDDED + ","
					+ "				\\\"uuid\\\":\\\"" + APP1_UUID + "\\\","
					+ "				\\\"framework\\\":\\\"" + APP1_CARTRIDGE + "\\\","
					+ "				\\\"creation_time\\\":\\\"" + APP1_CREATION_TIME + "\\\""
					+ "			},"
					+ "			\\\"" + APP2_NAME + "\\\":"
					+ "			{"
					+ "				\\\"embedded\\\":"  
					+ "             {\"" 
					+                       APP2_EMBEDDED_NAME + "\" :  {\"info\" : \"Connection URL: " + APP2_EMBEDDED_URL + "/\"}"
					+"              }" + ","
					+ "				\\\"uuid\\\":\\\"" + APP2_UUID + "\\\","
					+ "				\\\"framework\\\":\\\"" + APP2_CARTRIDGE + "\\\","
					+ "				\\\"creation_time\\\":\\\"" + APP2_CREATION_TIME + "\\\""
					+ "			}"
					+ "		}"
					+ "	}\","
					+ "	\"api\":\"1.1.1\","
					+ "	\"api_c\":[\"placeholder\"],"
					+ "	\"result\":null,"
					+ "	\"broker\":\"1.1.1\","
					+ "	\"broker_c\":["
					+ "		\"namespace\","
					+ "		\"rhlogin\","
					+ "		\"ssh\","
					+ "		\"app_uuid\","
					+ "		\"debug\","
					+ "		\"alter\","
					+ "		\"cartridge\","
					+ "		\"cart_type\","
					+ "		\"action\","
					+ "		\"app_name\","
					+ "		\"api\""
					+ "		],"
					+ "	\"exit_code\":0"
					+ "}";
	
	public static IEmbeddableCartridge toEmbeddableCartridge(String name, String url) {
		return new EmbeddableCartridge(name, url);
	}

	public static List<IEmbeddableCartridge> toEmbeddableCartridges(IEmbeddableCartridge cartridge) {
		List<IEmbeddableCartridge> list = new ArrayList<IEmbeddableCartridge>();
		list.add(cartridge);
		return list;
	}

	public static List<IEmbeddableCartridge> toEmbeddableCartridges(List<EmbeddableCartridgeInfo> cartridgeInfos) {
		if (cartridgeInfos == null) {
			return null;
		}
		List<IEmbeddableCartridge> list = new ArrayList<IEmbeddableCartridge>();
		for (EmbeddableCartridgeInfo cartridgeInfo : cartridgeInfos) {
			list.add(new EmbeddableCartridge(cartridgeInfo.getName(), cartridgeInfo.getUrl()));
		}
		return list;
	}

}