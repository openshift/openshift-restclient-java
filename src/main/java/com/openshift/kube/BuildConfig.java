package com.openshift.kube;

import org.jboss.dmr.ModelNode;

import com.openshift.internal.kube.BuildTrigger;
import com.openshift.internal.kube.OpenshiftResource;
import com.openshift.kube.images.ImageUri;

public class BuildConfig extends OpenshiftResource {

	public BuildConfig(ModelNode node, Client client) {
		super(node, client);
		set("kind",ResourceKind.BuildConfig.toString());
	}

	public BuildConfig() {
		this(new ModelNode(), null);
	}

	public String getSourceUri() {
		return getNode().get(new String[]{"parameters","source","git","uri"}).asString();
	}

	public void addTrigger(BuildTrigger type, String secret){
		ModelNode trigger = new ModelNode();
		trigger.get("type").set(type.toString());
		trigger.get(new String[]{type.toString(),"secret"}).set(secret);
		getNode().get("triggers").add(trigger);
	}

	public void setSource(String type, String uri){
		ModelNode params = getNode().get("parameters");
		params.get(new String[]{"source","type"}).set(type);
		params.get(new String[]{"source","git","uri"}).set(uri);
	}
	
	public void setStrategy(String type, String baseImage){
		ModelNode strategy = getNode().get(new String []{"parameters","strategy"});
		strategy.get("type").set(type);	
		strategy.get(new String[]{"stiStrategy","image"}).set(baseImage);
	}
	
	public void setOutput(ImageUri imageUri){
		ModelNode output = getNode().get(new String []{"parameters","output"});
		output.get("imageTag").set(imageUri.getUriWithoutHost());
		output.get("registry").set(imageUri.getRepositoryHost());
	}

}
