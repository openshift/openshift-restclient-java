package com.openshift3.internal.client.model;

import static org.mockito.Mockito.*;

import java.util.ArrayList;

import org.junit.Test;

import com.openshift3.client.IClient;
import com.openshift3.client.ResourceKind;
import com.openshift3.client.model.IPod;
import com.openshift3.client.model.IService;
import com.openshift3.internal.client.IResourceFactory;
import com.openshift3.internal.client.ResourceFactory;

public class ServiceTest {

	@SuppressWarnings("unchecked")
	@Test
	public void testGetPods() {
		//setup
		IClient client = mock(IClient.class);
		when(client.list(any(ResourceKind.class), anyString(), anyMap()))
			.thenReturn(new ArrayList<IPod>());
		IResourceFactory factory = new ResourceFactory(client);
		IService service = factory.create("v1beta1", ResourceKind.Service);
		service.addLabel("bar","foo");
		service.setSelector("foo", "bar");
		
		//exectute
		service.getPods();
		
		//confirm called with selector and not something else
		verify(client, times(1)).list(eq(ResourceKind.Pod), anyString(), eq(service.getSelector()));
	}

}
