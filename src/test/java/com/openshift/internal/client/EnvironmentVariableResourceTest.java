package com.openshift.internal.client;

import static com.openshift.client.utils.Samples.GET_1_ENVIRONMENT_VARIABLES_FOOBARZ_SPRINGEAP6;
import static com.openshift.client.utils.Samples.GET_0_ENVIRONMENT_VARIABLES_FOOBARZ_SPRINGEAP6;
import static com.openshift.client.utils.Samples.GET_DOMAINS;
import static com.openshift.client.utils.Samples.GET_DOMAINS_FOOBARZ_APPLICATIONS_1EMBEDDED;
import static com.openshift.client.utils.Samples.GET_DOMAINS_FOOBARZ_APPLICATIONS_SPRINGEAP6_1EMBEDDED;
import static com.openshift.client.utils.Samples.PUT_FOO_ENVIRONMENT_VARIABLE_FOOBARZ_SPRINGEAP6;
import static org.fest.assertions.Assertions.assertThat;

import org.junit.Before;
import org.junit.Test;

import com.openshift.client.IApplication;
import com.openshift.client.IDomain;
import com.openshift.client.IEnvironmentVariable;

public class EnvironmentVariableResourceTest {
	
	private IApplication application;
	private IDomain domain;
	private HttpClientMockDirector mockDirector;
    @Before
	public void setup() throws Throwable{

		this.mockDirector = new HttpClientMockDirector()
				.mockGetDomains(GET_DOMAINS)
				.mockGetApplications("foobarz", GET_DOMAINS_FOOBARZ_APPLICATIONS_1EMBEDDED)
				.mockGetApplication("foobarz", "springeap6", GET_DOMAINS_FOOBARZ_APPLICATIONS_SPRINGEAP6_1EMBEDDED);
		this.application = mockDirector.getDomain("foobarz").getApplicationByName("springeap6");
		assertThat(application).isNotNull();
	
    }
    
    @Test
    public void shouldGetEnvironmentVariableName() throws Throwable{
    	//precondition
    	mockDirector.mockGetEnvironmentVariables("foobarz", "springeap6", GET_1_ENVIRONMENT_VARIABLES_FOOBARZ_SPRINGEAP6);
    	//operartion
    	IEnvironmentVariable environmentVariable = application.getEnvironmentVariableByName("FOO");
    	//verification
    	assertThat(environmentVariable.getName()).isEqualTo("FOO");
    }
    
    @Test
    public void shouldGetEnvironmentVariableValue() throws Throwable{
        //precondition
    	mockDirector.mockGetEnvironmentVariables("foobarz", "springeap6", GET_1_ENVIRONMENT_VARIABLES_FOOBARZ_SPRINGEAP6);
    	//operartion
    	IEnvironmentVariable environmentVariable = application.getEnvironmentVariableByName("FOO");
    	//verification
    	assertThat(environmentVariable.getValue()).isEqualTo("123");
    }
    
    @Test
    public void shouldDeleteEnvironmentVariable() throws Throwable{
    	//precondition
    	mockDirector.mockGetEnvironmentVariables("foobarz", "springeap6", GET_1_ENVIRONMENT_VARIABLES_FOOBARZ_SPRINGEAP6,GET_0_ENVIRONMENT_VARIABLES_FOOBARZ_SPRINGEAP6);
       //operation
      IEnvironmentVariable environmentVariable = application.getEnvironmentVariableByName("FOO");
      assertThat(environmentVariable).isNotNull();
      environmentVariable.delete();
      environmentVariable = application.getEnvironmentVariableByName("FOO");
      assertThat(environmentVariable).isNull();
    }
    
    @Test
    public void shouldUpdateEnvironmentVariableValue() throws Throwable{
    	//precondition
    	mockDirector.mockGetEnvironmentVariables("foobarz", "springeap6", GET_1_ENVIRONMENT_VARIABLES_FOOBARZ_SPRINGEAP6,GET_1_ENVIRONMENT_VARIABLES_FOOBARZ_SPRINGEAP6)
    	.mockUpdateEnvironmentVariableValue("foobarz", "springeap6","FOO", PUT_FOO_ENVIRONMENT_VARIABLE_FOOBARZ_SPRINGEAP6);
       //operation
      IEnvironmentVariable environmentVariable = application.getEnvironmentVariableByName("FOO");
      assertThat(environmentVariable.getValue()).isEqualTo("123");
      environmentVariable.update("321");;
      assertThat(environmentVariable.getValue()).isEqualTo("321");
    }
    
	
	
}
