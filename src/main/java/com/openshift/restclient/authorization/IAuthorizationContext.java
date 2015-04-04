package com.openshift.restclient.authorization;

public interface IAuthorizationContext {

	AuthorizationType getType();
	
	boolean isAuthorized();
	
	/**
	 * Token to use for authentication.  Will return non-null
	 * value if authorized
	 * @return
	 */
	String getToken();
	
	/**
	 * Time in ?? when the token expires. Will return
	 * non-null value if authorized
	 * @return
	 */
	String getExpiresIn();
	
	static enum AuthorizationType {
		Basic,
		Kerberos
	}
	
}
