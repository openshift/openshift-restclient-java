package com.openshift.kube;

public class OpenShiftKubeException extends RuntimeException {

	private static final long serialVersionUID = -7076942050102006278L;
	private Status status;

	public OpenShiftKubeException(String message, Throwable cause, Status status) {
		super(message, cause);
		this.status = status;
	}		
	
	public Status getStatus(){
		return this.status;
	}
}
