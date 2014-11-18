package com.openshift.kube;

import java.util.ArrayList;
import java.util.List;

import org.jboss.dmr.ModelNode;

import com.openshift.internal.kube.OpenshiftResource;
import com.openshift.internal.kube.Resource;

public class Project extends OpenshiftResource{


	public Project() {
		this(new ModelNode(), null);
		set("kind", ResourceKind.Project.toString());
	}
	
	public Project(ModelNode node, Client client) {
		super(node, client);
	}
	
	public String getDisplayName(){
		return asString("displayName");
	}
	
	public void setDisplayName(String name) {
		get("displayName").set(name);
	}

	public <T extends Resource> List<T> getResources(ResourceKind kind){
		if(getClient() == null) return new ArrayList<T>();
		return getClient().list(kind, getNamespace());
	}
}
