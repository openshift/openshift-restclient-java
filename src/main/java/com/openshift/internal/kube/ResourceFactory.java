package com.openshift.internal.kube;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import org.jboss.dmr.ModelNode;

import com.openshift.kube.Client;
import com.openshift.kube.Project;
import com.openshift.kube.ResourceKind;

/**
 * ResourceFactory creates a list of resources from a json string 
 */
public class ResourceFactory {
	
	private Client client;

	public ResourceFactory(Client client) {
		this.client = client;
	}

	public List<Resource> createList(String json, ResourceKind kind){
		ModelNode data = ModelNode.fromJSONString(json);
		String dataKind = data.get("kind").asString();
		if(ResourceKind.Project.toString().equals(dataKind)){
			return buildProjectListForSingleProject(data);
		}
		if(!(kind.toString() + "List").equals(dataKind)){
			throw new RuntimeException(String.format("Unexpected container type '%s' for desired kind: %s", dataKind, kind));
		}
		
		try{
			return buildList(data.get("items").asList(), kind.getResourceClass());
		}catch(Exception e){
			throw new RuntimeException(e);
		}
	}
	
	/*
	 * Project is apparently special as query for project with namespace returns a singular
	 * project
	 */
	private List<Resource> buildProjectListForSingleProject(ModelNode data) {
		ArrayList<Resource> projects = new ArrayList<Resource>(1);
		projects.add(new Project(data, client));
		return projects;
	}

	private <T extends Resource> List<Resource> buildList(List<ModelNode> items, Class<T> kind) throws NoSuchMethodException, SecurityException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		Constructor<T> constructor = kind.getConstructor(ModelNode.class, Client.class);
		List<Resource> resources = new ArrayList<Resource>(items.size());
		for (ModelNode item : items) {
			resources.add(constructor.newInstance(item, client));
		}
		return resources;
	}

	@SuppressWarnings("unchecked")
	public <T extends Resource> T create(String response) {
		// TODO Do we really want to allow direct creation of 'Resource'?
		Resource resource = new Resource(response);
		try {
			Constructor<? extends Resource> constructor = resource.getKind().getResourceClass().getConstructor(ModelNode.class, Client.class);
			return (T) constructor.newInstance(resource.getNode(), client);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

	}
}
