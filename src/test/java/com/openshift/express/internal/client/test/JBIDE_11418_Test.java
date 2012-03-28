/******************************************************************************* 
 * Copyright (c) 2007 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/
package com.openshift.express.internal.client.test;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import com.openshift.express.client.OpenShiftException;
import com.openshift.express.internal.client.UserInfo;
import com.openshift.express.internal.client.response.OpenShiftResponse;
import com.openshift.express.internal.client.response.unmarshalling.UserInfoResponseUnmarshaller;

/**
 * @author Andre Dietisheim
 */
public class JBIDE_11418_Test {

	@Test
	public void canUnmarshallUserInfoWihtEmptySSHKey() throws OpenShiftException {
		String responseString =
				"{"
						+" 	\"api_c\":[\"placeholder\"],"
						+" 	\"result\":\"\","
						+" 	\"exit_code\":0,"
						+" 	\"api\":\"1.1.3\","
						+" 	\"data\":{"
						+" 		\"user_info\":{"
						+" 			\"domains\":[],"
						+" 			\"max_gears\":5,"
						+" 			\"vip\":false,"
						+" 			\"ssh_keys\":{},"
						+" 			\"uuid\":\"ab2cae3fb9f2462a83f83cfa26f1fb64\","
						+" 			\"rhlogin\":\"anagy+2012032703@redhat.com\","
						+" 			\"consumed_gears\":0,"
						+" 			\"ssh_type\":\"\","
						+" 			\"rhc_domain\":\"dev.rhcloud.com\","
						+" 			\"ssh_key\":\"\""
						+" 		},"
						+" 		\"app_info\":{"
						+" 		}"
						+" 	},"
						+"	\"broker_c\":["
						+"		\"namespace\","
						+"		\"rhlogin\","
						+"		\"ssh\","
						+"		\"app_uuid\","
						+"		\"debug\","
						+"		\"alter\","
						+"		\"cartridge\","
						+"		\"cart_type\","
						+"		\"action\","
						+"		\"app_name\","
						+"		\"api\"],"
						+"	\"messages\":\"\","
						+"	\"debug\":\"\""
						+"}";
		
		OpenShiftResponse<UserInfo> response = new UserInfoResponseUnmarshaller().unmarshall(responseString);
		assertNotNull(response);
	}
	
}
