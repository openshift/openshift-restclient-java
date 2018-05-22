package com.openshift.restclient.utils;

public enum ResourceStatus {
    
    ACTIVE("Active"),
    TERMINATING("Terminating");
    
    private String value;
    
    private ResourceStatus(String value) {
        this.value = value;
    }
    
    public String getValue() {
        return this.value;
    }
    
    @Override
    public String toString() {
        return this.value;
    }
    
}
