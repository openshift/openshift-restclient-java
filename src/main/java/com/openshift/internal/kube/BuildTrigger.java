package com.openshift.internal.kube;

public enum BuildTrigger {
	
	GitHub("github"),
	Generic("generic");
	
	private  String name;
	
	BuildTrigger(String name){
		this.name = name;
	}
	
	@Override
	public String toString() {
		return name;
	}
	
}
