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
	public void destroy() throws OpenShiftException;
	/**
	 * Checks if the GET link is available. 
	 * @return true if the GET link is available
	 * @throws OpenShiftException
	 */
	public boolean hasGetLink() throws OpenShiftException;
	/**
	 * Checks if the DELETE link is available.
	 * This link should be available to delete this 
	 * environment variable
	 * @return true if the DELETE link is available
	 * @throws OpenShiftException
	 */
	public boolean hasDeleteLink() throws OpenShiftException;
	/**
	 * Checks if the UPDATE link is available
	 * This link should be available to update this environment variable
	 * @return true if the UPDATE link is available
	 * @throws OpenShiftException
	 */
	public boolean hasUpdateLink() throws OpenShiftException;
	
	
}
