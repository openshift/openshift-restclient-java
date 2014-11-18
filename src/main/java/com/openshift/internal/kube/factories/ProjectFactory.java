package com.openshift.internal.kube.factories;

import com.openshift.kube.Project;

/**
 * ProjectFactory is primary used to build instances of resources that can be submitted
 * to the server for creation.  These objects would have the minimum necessary structure
 * to pass validation.  These factories could either build up the object structure or possibly
 * load a template from disk and substitute the variable pieces.
 *
 */
public class ProjectFactory {
	
	private String version;
	
	public ProjectFactory(String version) {
		this.version = version;
	}

	public Project create(){
		Project p = new Project();
		p.setApiVersion(version);
		return p;
	}
}
