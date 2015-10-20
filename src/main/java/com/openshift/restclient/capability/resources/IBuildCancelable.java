package com.openshift.restclient.capability.resources;

import com.openshift.restclient.capability.ICapability;
import com.openshift.restclient.model.IBuild;

public interface IBuildCancelable extends ICapability {
	
	IBuild cancel();

}
