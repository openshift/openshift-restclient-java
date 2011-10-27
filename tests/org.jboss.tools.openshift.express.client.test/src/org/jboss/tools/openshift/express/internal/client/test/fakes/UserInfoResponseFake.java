package org.jboss.tools.openshift.express.internal.client.test.fakes;

public class UserInfoResponseFake {

	public static final String RHLOGIN = "jbosstools@redhat.com";
	public static final String PASSWORD = "$!445password%&";
	
	public static final String RHC_DOMAIN = "rhcloud.com";
	public static final String NAMESPACE = "1315839296868";
	public static final String UUID = "5f34b742db754cc9ab70fd1db2c9a2bd";
	public static final String SSH_KEY =
			"AAAAB3NzaC1yc2EAAAADAQABAAAAgQC6BGRDydfGsQHhnZgo43dEfLz"
					+ "SJBke/hE8MLBBG1+5ZwktsrE+f2VdVt0McRLVAO6rdJRyMUX0rTbm7"
					+ "SABRVSX+zeQjlfqbbUtYFc7TIfd4RQc3GaISG1rS3C4svRSjdWaG36"
					+ "vDY2KxowdFvpKj8i8IYNPlLoRA/7EzzyneS6iyw==";

	public static final String APP1_NAME = "1315836963263";
	public static final String APP1_EMBEDDED = null;
	public static final String APP1_UUID = "810540bafc1c4b5e8cac830fb8ca786f";
	public static final String APP1_CARTRIDGE = "jbossas-7.0";
	public static final String APP1_CREATION_TIME = "2011-09-12T10:15:48-04:00";

	public static final String APP2_NAME = "1315903559289";
	public static final String APP2_EMBEDDED = null;
	public static final String APP2_UUID = "f5496311f43b42cd8fa5db5ecf83a352";
	public static final String APP2_CARTRIDGE = "jbossas-7.0";
	public static final String APP2_CREATION_TIME = "2011-09-13T04:45:44-04:00";

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
					+ "			\\\"ssh_key\\\":\\\"" + SSH_KEY + "\\\""
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
					+ "				\\\"embedded\\\":" + APP2_EMBEDDED + ","
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
}
