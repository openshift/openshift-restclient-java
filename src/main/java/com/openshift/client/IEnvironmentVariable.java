package com.openshift.client;

/**
 * 
 * @author Syed Iqbal
 *
 */
public interface IEnvironmentVariable extends IOpenShiftResource{

	/**
	 * 
	 * @return Name of the environment variable
	 */
	public String getName();
	/**
	 * 
	 * @return Value of the environment variable
	 */
	public String getValue();
	/**
	 * 
	 * @param name Name of the environment variable
	 * @param value Value of the environment variable
	 * @throws OpenShiftException
	 */
	public void update(String value) throws OpenShiftException;
	/**
	 * Deletes the environment variable
	 * @throws OpenShiftException
	 */
	public void delete() throws OpenShiftException;
}
