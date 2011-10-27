package org.jboss.tools.openshift.express.internal.client.test.fakes;

/**
 * 
 * @author André Dietisheim
 *
 */
public class CartridgeResponseFake {

	public static final String CARTRIDGE_PERL5 = "perl-5.10";
	public static final String CARTRIDGE_JBOSSAS70 = "jbossas-7.0";
	public static final String CARTRIDGE_WSGI32 = "wsgi-3.2";
	public static final String CARTRIDGE_RACK11 = "rack-1.1";
	public static final String CARTRIDGE_PHP53 = "php-5.3";
	
	public static final String RESPONSE = 
			"{"
					+"	\"messages\":\"\","
					+"	\"debug\":\"\","
					+"	\"data\":\"{"
					+"		\\\"carts\\\":"
					+"			[\\\"" + CARTRIDGE_PERL5 +"\\\","
					+"			\\\"" + CARTRIDGE_JBOSSAS70 + "\\\","
					+"			\\\"" + CARTRIDGE_WSGI32+ "\\\","
					+"			\\\"" + CARTRIDGE_RACK11 + "\\\","
					+"			\\\"" + CARTRIDGE_PHP53 + "\\\"]"
					+"	}\","
					+"	\"api\":\"1.1.1\","
					+"	\"api_c\":[\"placeholder\"],"
					+"	\"result\":null,"
					+"	\"broker\":\"1.1.1\","
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
					+"	\"exit_code\":0"
					+ "}";	
}
