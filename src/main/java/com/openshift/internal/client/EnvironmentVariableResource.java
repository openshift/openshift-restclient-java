package com.openshift.internal.client;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.openshift.client.IApplication;
import com.openshift.client.IEnvironmentVariable;
import com.openshift.client.Messages;
import com.openshift.client.OpenShiftException;
import com.openshift.internal.client.AbstractOpenShiftResource.Parameters;
import com.openshift.internal.client.AbstractOpenShiftResource.ServiceRequest;
import com.openshift.internal.client.response.EnvironmentVariableResourceDTO;
import com.openshift.internal.client.response.Link;
import com.openshift.internal.client.utils.IOpenShiftJsonConstants;

/**
 * 
 * @author siqbal
 *
 */
public class EnvironmentVariableResource  extends AbstractOpenShiftResource implements IEnvironmentVariable{
	
	private static final String LINK_GET = "GET";
	private static final String LINK_UPDATE = "UPDATE";
	private static final String LINK_DELETE = "DELETE";
	
	/** The name of the environment variable */
	private String name;
	
	/** The value of the environment variable*/
	private String value;
	
	/** The application this environment variable belongs to */
	private ApplicationResource application;
	
	protected EnvironmentVariableResource(EnvironmentVariableResourceDTO dto,ApplicationResource application){
		this(dto.getName(),dto.getValue(),dto.getMessages(),dto.getLinks(),application);
	}
	
	protected EnvironmentVariableResource(final String name,final String value,final Messages messages,final Map<String, Link> links,final ApplicationResource application){
		super(application.getService(), links, messages);
		this.name = name;
		this.value = value;
		this.application=application;
		
	}
	
	@Override
	public String getName(){
		return name;
	}
	
	@Override
	public String getValue(){
		return value;
	}
	@Override
	public void update(String value) throws OpenShiftException{
		 if(value==null){
	        	throw new OpenShiftException("Value for environment variable \"{0}\" not given.",name);
	      }
	      EnvironmentVariableResourceDTO environmentVariableResourceDTO = new UpdateEnvironmentVariableRequest().execute(value);
	      updateEnvironmentVariable(environmentVariableResourceDTO);
	      application.updateEnvironmentVariables();
	        
	}
	
	private void updateEnvironmentVariable(EnvironmentVariableResourceDTO dto){
		this.name = dto.getName();
		this.value = dto.getValue();
		this.getLinks().clear();
		this.getLinks().putAll(dto.getLinks());
	}
	
	@Override
	public void delete(){
		new DeleteEnvironmentVariableRequest().execute();
		application.updateEnvironmentVariables();
	}

	@Override
	public void refresh() throws OpenShiftException {
		EnvironmentVariableResourceDTO environmentVariableResourceDTO = new GetEnvironmentVariableRequest().execute();
		updateEnvironmentVariable(environmentVariableResourceDTO);
	}
	
	private class UpdateEnvironmentVariableRequest extends ServiceRequest {
		protected UpdateEnvironmentVariableRequest() {
			super(LINK_UPDATE);
		}
		protected EnvironmentVariableResourceDTO execute(String value){
			Parameters parameters = new Parameters()
			.add(IOpenShiftJsonConstants.PROPERTY_VALUE, value);
		return	super.execute(parameters.toArray());
		}
	}
	
	private class GetEnvironmentVariableRequest extends ServiceRequest {
		protected GetEnvironmentVariableRequest() {
			super(LINK_GET);
	}

	}

	private class DeleteEnvironmentVariableRequest extends ServiceRequest {
		protected DeleteEnvironmentVariableRequest() {
			super(LINK_DELETE);
	}

	}

}
