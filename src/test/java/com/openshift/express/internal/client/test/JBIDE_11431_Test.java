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
package com.openshift.express.internal.client.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import com.openshift.express.client.OpenShiftException;
import com.openshift.express.client.SSHKeyType;
import com.openshift.express.client.SSHPublicKey;
import com.openshift.express.internal.client.UserInfo;
import com.openshift.express.internal.client.response.OpenShiftResponse;
import com.openshift.express.internal.client.response.unmarshalling.UserInfoResponseUnmarshaller;

/**
 * @author Andre Dietisheim
 */
public class JBIDE_11431_Test {

	@Test
	public void canUnmarshallUserInfoWithSSHKeysInAlternativeProperty() throws OpenShiftException {
		String rsaKey = "AAAAB3NzaC1yc2EAAAADAQABAAABAQC789Mv+LVq2LLu+1f5FobFjYJIGYfMqiVsG3a0WwHz5fVGvccqsP3Oity7L5K9jKBP+o1sZwOL2x8ZOf8WsE0ZLXPgMIQURKUgES9uu3Of+PuWm0D3lylnxkMgT9bPolDjZxqBfZojrfl2oZ3Z+jdqUJg4a+1gReQHob4vM66Y0XCdWDEQDNiZpNQwCwmah8mE4F0CVO1ivd6wJpZtFstvjiQoB/7J2w7Po9BmNVzJMiUY3xiVLGuRhSEBO2DqLibrpB3kvD1cXUhqK0dwI5r5maMBeaaBTpPlcqD+1BbLplbDlJ+L5/90T9v58dBsYIKO3i6BkrbJn40dlGqmvo8f";
		String responseString = 
				"{"
						+"	\"messages\":\"\","
						+"	\"broker_c\":[\"namespace\","
						+"	\"rhlogin\","
						+"	\"ssh\","
						+"	\"app_uuid\","
						+"	\"debug\","
						+"	\"alter\","
						+"	\"cartridge\","
						+"	\"cart_type\","
						+"	\"action\","
						+"	\"app_name\","
						+"	\"api\"],"
						+"	\"api\":\"1.1.3\","
						+"	\"api_c\":[\"placeholder\"],"
						+"	\"exit_code\":0,"
						+"	\"result\":\"\","
						+"	\"debug\":\"\","
						+"	\"data\":{"
						+"		\"app_info\":{"
						+"			\"test1\":{"
						+"				\"framework\":\"php-5.3\","
						+"				\"embedded\":{"
						+"					\"mysql-5.1\":{"
						+"						\"info\":\"Connection URL: mysql://127.12.203.1:3306/\n\""
						+"					},"
						+"					\"phpmyadmin-3.4\":{"
						+"						\"info\":\"URL: https://test1-cpc.rhcloud.com/phpmyadmin/\n\""
						+"					}"
						+"				},"
						+"				\"aliases\":null,"
						+"				\"creation_time\":\"2012-03-26T11:11:29-04:00\","
						+"				\"uuid\":\"f86f404f81d94838a05816fd7fbabf30\""
						+"			}"
						+"		},"
						+"		\"user_info\":{"
						+"			\"ssh_type\":null,"
						+"			\"consumed_gears\":1,"
						+"			\"rhc_domain\":\"rhcloud.com\","
						+"			\"system_ssh_keys\":null,"
						+"			\"vip\":false,"
						+"			\"max_gears\":5,"
						+"			\"namespace\":\"cpc\","
						+"			\"ssh_key\":null,"
						+"			\"ssh_keys\":{"
						+"				\"uk\":{"
						+"					\"type\":\"ssh-rsa\","
						+"					\"key\":\"" + rsaKey + "\""
						+"				}"
						+"			},"
						+"			\"rhlogin\":\"USER@COMPANY\","
						+"			\"uuid\":\"eb6b8d6d25ab4961926215908a3fd474\","
						+"			\"env_vars\":null"
						+"		}"
						+"	}"
						+"}";
		
		OpenShiftResponse<UserInfo> userInfoResponse = new UserInfoResponseUnmarshaller().unmarshall(responseString);
		assertNotNull(userInfoResponse);
		UserInfo userInfo = userInfoResponse.getOpenShiftObject();
		assertNotNull(userInfo);
		SSHPublicKey key = userInfo.getSshPublicKey();
		assertNotNull(key);
		assertEquals(SSHKeyType.SSH_RSA, key.getKeyType());
		assertEquals(rsaKey, key.getPublicKey());
	}

}
